package dao;

import db.Database;
import model.MovieReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieReportDAO {

    public List<MovieReport> getAll() throws SQLException {
        String sql = "SELECT movie_id, title, release_date, duration, score, genre, actors "
                + "FROM movie_report";
        List<MovieReport> rows = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MovieReport r = new MovieReport();
                r.setMovieId(rs.getInt("movie_id"));
                r.setTitle(rs.getString("title"));
                Date d = rs.getDate("release_date");
                if (d != null) {
                    r.setReleaseDate(d.toLocalDate());
                }
                r.setDuration(rs.getInt("duration"));
                r.setScore(rs.getDouble("score"));
                r.setGenre(rs.getString("genre"));
                r.setActors(rs.getString("actors"));
                rows.add(r);
            }
        }
        return rows;
    }
}