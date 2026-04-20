package dao;

import db.Database;
import model.Genre;
import model.Movie;
import model.MovieList;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MovieListDAO {

    public MovieList create(String name) throws SQLException {
        String sql = "INSERT INTO movie_lists (name) VALUES (?) RETURNING id, created_at";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MovieList list = new MovieList();
                    list.setId(rs.getInt("id"));
                    list.setName(name);
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        list.setCreatedAt(ts.toLocalDateTime());
                    }
                    return list;
                }
            }
        }
        throw new SQLException("Creating movie list failed.");
    }

    public void addMovies(int listId, Collection<Integer> movieIds) throws SQLException {
        if (movieIds.isEmpty()) return;

        String sql = "INSERT INTO movie_list_movies (list_id, movie_id) VALUES (?, ?) "
                + "ON CONFLICT DO NOTHING";

        try (Connection conn = Database.getInstance().getConnection()) {
            boolean prevAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int movieId : movieIds) {
                    stmt.setInt(1, listId);
                    stmt.setInt(2, movieId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(prevAutoCommit);
            }
        }
    }

    public MovieList findById(int id) throws SQLException {
        String listSql = "SELECT id, name, created_at FROM movie_lists WHERE id = ?";
        String moviesSql =
                "SELECT m.id, m.title, m.release_date, m.duration, m.score, "
                        + "       g.id AS genre_id, g.name AS genre_name "
                        + "FROM movie_list_movies mlm "
                        + "JOIN movies m ON mlm.movie_id = m.id "
                        + "LEFT JOIN genres g ON m.genre_id = g.id "
                        + "WHERE mlm.list_id = ? "
                        + "ORDER BY m.title";

        try (Connection conn = Database.getInstance().getConnection()) {
            MovieList list;
            try (PreparedStatement stmt = conn.prepareStatement(listSql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) return null;
                    list = new MovieList();
                    list.setId(rs.getInt("id"));
                    list.setName(rs.getString("name"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) list.setCreatedAt(ts.toLocalDateTime());
                }
            }

            List<Movie> movies = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(moviesSql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Movie m = new Movie();
                        m.setId(rs.getInt("id"));
                        m.setTitle(rs.getString("title"));
                        Date d = rs.getDate("release_date");
                        if (d != null) m.setReleaseDate(d.toLocalDate());
                        m.setDuration(rs.getInt("duration"));
                        m.setScore(rs.getDouble("score"));
                        int gid = rs.getInt("genre_id");
                        if (!rs.wasNull()) {
                            m.setGenre(new Genre(gid, rs.getString("genre_name")));
                        }
                        movies.add(m);
                    }
                }
            }
            list.setMovies(movies);
            return list;
        }
    }

    public List<MovieList> findAll() throws SQLException {
        String sql = "SELECT id, name, created_at FROM movie_lists ORDER BY id";
        List<MovieList> lists = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MovieList l = new MovieList();
                l.setId(rs.getInt("id"));
                l.setName(rs.getString("name"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) l.setCreatedAt(ts.toLocalDateTime());
                lists.add(l);
            }
        }
        return lists;
    }

    public void deleteAll() throws SQLException {
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM movie_lists");
        }
    }
}