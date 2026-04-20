package db;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/movies_db";
    private static final String USER = "postgres";
    private static final String PASS = "0745493242";

    private static Database instance;
    private final BasicDataSource dataSource;

    private Database() {
        dataSource = new BasicDataSource();
        dataSource.setUrl(URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASS);

        dataSource.setInitialSize(2);
        dataSource.setMaxTotal(5);
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(5);
        dataSource.setMaxWaitMillis(3000);

        System.out.println("Connection pool initialized: " + URL);
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        try {
            dataSource.close();
            System.out.println("Connection pool closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}