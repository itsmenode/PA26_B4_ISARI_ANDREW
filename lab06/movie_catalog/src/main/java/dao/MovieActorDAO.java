package dao;

import db.Database;
import model.Actor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieActorDAO {

    public void addActorToMovie(int movieId, int actorId) throws SQLException {
        String sql = "INSERT INTO movie_actors (movie_id, actor_id) VALUES (?, ?) "
                + "ON CONFLICT DO NOTHING";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, actorId);
            stmt.executeUpdate();
        }
    }

    public void removeActorFromMovie(int movieId, int actorId) throws SQLException {
        String sql = "DELETE FROM movie_actors WHERE movie_id = ? AND actor_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, actorId);
            stmt.executeUpdate();
        }
    }

    public List<Actor> findActorsByMovie(int movieId) throws SQLException {
        String sql = "SELECT a.id, a.name "
                + "FROM actors a "
                + "JOIN movie_actors ma ON a.id = ma.actor_id "
                + "WHERE ma.movie_id = ? "
                + "ORDER BY a.name";
        List<Actor> actors = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actors.add(new Actor(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return actors;
    }
}