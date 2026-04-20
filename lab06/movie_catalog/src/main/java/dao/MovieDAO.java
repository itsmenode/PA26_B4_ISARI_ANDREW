package dao;

import db.Database;
import model.Genre;
import model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public Movie create(Movie movie) throws SQLException {
        String sql = "INSERT INTO movies (title, release_date, duration, score, genre_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, movie.getTitle());
            stmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
            stmt.setInt(3, movie.getDuration());
            stmt.setDouble(4, movie.getScore());
            stmt.setInt(5, movie.getGenre().getId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    movie.setId(keys.getInt(1));
                }
            }
        }
        return movie;
    }

    public Movie findById(int id) throws SQLException {
        String sql = "SELECT m.id, m.title, m.release_date, m.duration, m.score, "
                + "       g.id AS genre_id, g.name AS genre_name "
                + "FROM movies m "
                + "LEFT JOIN genres g ON m.genre_id = g.id "
                + "WHERE m.id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Movie> findByGenre(int genreId) throws SQLException {
        String sql = "SELECT m.id, m.title, m.release_date, m.duration, m.score, "
                + "       g.id AS genre_id, g.name AS genre_name "
                + "FROM movies m "
                + "LEFT JOIN genres g ON m.genre_id = g.id "
                + "WHERE m.genre_id = ? "
                + "ORDER BY m.title";
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, genreId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapRow(rs));
                }
            }
        }
        return movies;
    }

    public List<Movie> findAll() throws SQLException {
        String sql = "SELECT m.id, m.title, m.release_date, m.duration, m.score, "
                + "       g.id AS genre_id, g.name AS genre_name "
                + "FROM movies m "
                + "LEFT JOIN genres g ON m.genre_id = g.id "
                + "ORDER BY m.title";
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(mapRow(rs));
            }
        }
        return movies;
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        Date d = rs.getDate("release_date");
        if (d != null) {
            movie.setReleaseDate(d.toLocalDate());
        }
        movie.setDuration(rs.getInt("duration"));
        movie.setScore(rs.getDouble("score"));

        int genreId = rs.getInt("genre_id");
        if (!rs.wasNull()) {
            movie.setGenre(new Genre(genreId, rs.getString("genre_name")));
        }
        return movie;
    }
}