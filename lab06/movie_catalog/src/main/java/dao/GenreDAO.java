package dao;

import db.Database;
import model.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    public Genre create(String name) throws SQLException {
        String sql = "INSERT INTO genres (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            int affected = stmt.executeUpdate();

            if (affected == 0) {
                return findByName(name);
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Genre(keys.getInt(1), name);
                }
            }
        }
        throw new SQLException("Creating genre failed, no ID obtained.");
    }

    public Genre findById(int id) throws SQLException {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Genre(rs.getInt("id"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    public Genre findByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM genres WHERE name = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Genre(rs.getInt("id"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    public List<Genre> findAll() throws SQLException {
        String sql = "SELECT id, name FROM genres ORDER BY name";
        List<Genre> genres = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        }
        return genres;
    }
}