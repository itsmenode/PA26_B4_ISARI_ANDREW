package dao;

import db.Database;
import model.Actor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorDAO {

    public Actor create(String name) throws SQLException {
        String sql = "INSERT INTO actors (name) VALUES (?)";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Actor(keys.getInt(1), name);
                }
            }
        }
        throw new SQLException("Creating actor failed, no ID obtained.");
    }

    public Actor findById(int id) throws SQLException {
        String sql = "SELECT id, name FROM actors WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Actor(rs.getInt("id"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    public Actor findByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM actors WHERE name = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Actor(rs.getInt("id"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    public List<Actor> findAll() throws SQLException {
        String sql = "SELECT id, name FROM actors ORDER BY name";
        List<Actor> actors = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                actors.add(new Actor(rs.getInt("id"), rs.getString("name")));
            }
        }
        return actors;
    }
}