package importer;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import db.Database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class CsvImporter {

    private final Map<String, Integer> genreCache = new HashMap<>();
    private final Map<String, Integer> actorCache = new HashMap<>();

    public int importCsv(String pathOrResource) throws SQLException, IOException, CsvValidationException {
        try (Reader reader = openReader(pathOrResource)) {
            return importFrom(reader);
        }
    }

    private Reader openReader(String pathOrResource) throws IOException {
        Path fs = Path.of(pathOrResource);
        if (Files.exists(fs)) {
            System.out.println("Reading CSV from filesystem: " + fs.toAbsolutePath());
            return Files.newBufferedReader(fs, StandardCharsets.UTF_8);
        }
        String resource = pathOrResource.startsWith("/") ? pathOrResource : "/" + pathOrResource;
        InputStream in = CsvImporter.class.getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("CSV not found at filesystem path or classpath: " + pathOrResource);
        }
        System.out.println("Reading CSV from classpath: " + resource);
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    private int importFrom(Reader reader) throws SQLException, IOException, CsvValidationException {
        int imported = 0;
        int skipped = 0;

        try (Connection conn = Database.getInstance().getConnection();
             CSVReaderHeaderAware csv = new CSVReaderHeaderAware(reader)) {

            boolean prevAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                Map<String, String> row;
                while ((row = csv.readMap()) != null) {
                    try {
                        importRow(conn, row);
                        imported++;
                    } catch (Exception rowErr) {
                        skipped++;
                        System.err.println("Skipping row (" + rowErr.getMessage() + "): " + row);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(prevAutoCommit);
            }
        }

        System.out.println("CSV import finished. imported=" + imported + " skipped=" + skipped);
        return imported;
    }

    private void importRow(Connection conn, Map<String, String> row) throws SQLException {
        String title = required(row, "title");
        String dateStr = required(row, "release_date");
        String duration = required(row, "duration");
        String score = required(row, "score");
        String genreName = required(row, "genre");
        String actorsField = row.getOrDefault("actors", "");

        LocalDate releaseDate;
        try {
            releaseDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("bad release_date: " + dateStr);
        }

        int genreId = upsertGenre(conn, genreName);
        int movieId = insertMovie(conn, title, releaseDate,
                Integer.parseInt(duration), Double.parseDouble(score), genreId);

        if (!actorsField.isBlank()) {
            String[] actors = actorsField.split("\\|");
            for (String a : actors) {
                String name = a.trim();
                if (name.isEmpty()) continue;
                int actorId = upsertActor(conn, name);
                linkMovieActor(conn, movieId, actorId);
            }
        }
    }

    private static String required(Map<String, String> row, String col) {
        String v = row.get(col);
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("missing column: " + col);
        }
        return v.trim();
    }

    private int upsertGenre(Connection conn, String name) throws SQLException {
        Integer cached = genreCache.get(name);
        if (cached != null) return cached;

        String sql = "INSERT INTO genres (name) VALUES (?) "
                + "ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name "
                + "RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int id = rs.getInt(1);
                genreCache.put(name, id);
                return id;
            }
        }
    }

    private int upsertActor(Connection conn, String name) throws SQLException {
        Integer cached = actorCache.get(name);
        if (cached != null) return cached;

        String sql = "INSERT INTO actors (name) VALUES (?) "
                + "ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name "
                + "RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int id = rs.getInt(1);
                actorCache.put(name, id);
                return id;
            }
        }
    }

    private int insertMovie(Connection conn, String title, LocalDate date,
                            int duration, double score, int genreId) throws SQLException {
        String sql = "INSERT INTO movies (title, release_date, duration, score, genre_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setInt(3, duration);
            stmt.setDouble(4, score);
            stmt.setInt(5, genreId);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Insert movie failed: " + title);
    }

    private void linkMovieActor(Connection conn, int movieId, int actorId) throws SQLException {
        String sql = "INSERT INTO movie_actors (movie_id, actor_id) VALUES (?, ?) "
                + "ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, actorId);
            stmt.executeUpdate();
        }
    }
}