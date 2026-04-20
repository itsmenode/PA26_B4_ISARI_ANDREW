package dao;

import db.Database;
import model.Movie;
import model.MovieList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieListDAO {

    private final MovieDAO movieDAO = new MovieDAO();

    public MovieList create(String name) throws SQLException {
        String sql = "INSERT INTO movie_lists (name) VALUES (?)";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1));
                }
            }
        }
        throw new SQLException("Creating movie list failed, no ID obtained.");
    }

    public MovieList findById(int id) throws SQLException {
        String sql = "SELECT id, name, created_at FROM movie_lists WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MovieList list = mapRow(rs);
                    list.setMovies(findMoviesInList(id));
                    return list;
                }
            }
        }
        return null;
    }

    public MovieList findByName(String name) throws SQLException {
        String sql = "SELECT id, name, created_at FROM movie_lists WHERE name = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MovieList list = mapRow(rs);
                    list.setMovies(findMoviesInList(list.getId()));
                    return list;
                }
            }
        }
        return null;
    }

    public List<MovieList> findAll() throws SQLException {
        String sql = "SELECT id, name, created_at FROM movie_lists ORDER BY created_at";
        List<MovieList> lists = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lists.add(mapRow(rs));
            }
        }
        for (MovieList list : lists) {
            list.setMovies(findMoviesInList(list.getId()));
        }
        return lists;
    }

    public void addMovieToList(int listId, int movieId) throws SQLException {
        String sql = "INSERT INTO movie_list_items (list_id, movie_id) VALUES (?, ?) "
                + "ON CONFLICT DO NOTHING";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM movie_lists WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private List<Movie> findMoviesInList(int listId) throws SQLException {
        String sql = "SELECT movie_id FROM movie_list_items WHERE list_id = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("movie_id"));
                }
            }
        }
        List<Movie> movies = new ArrayList<>();
        for (int id : ids) {
            Movie m = movieDAO.findById(id);
            if (m != null) {
                movies.add(m);
            }
        }
        return movies;
    }

    private MovieList mapRow(ResultSet rs) throws SQLException {
        MovieList list = new MovieList();
        list.setId(rs.getInt("id"));
        list.setName(rs.getString("name"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            list.setCreatedAt(ts.toLocalDateTime());
        }
        return list;
    }
}
