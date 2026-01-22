import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String JDBC_URL = dotenv.get("JDBC_URL");
    private static final String USERNAME = dotenv.get("USER");
    private static final String PASSWORD = dotenv.get("PASSWORD");
    public Connection getConnection() {

        try {
            if (JDBC_URL == null || USERNAME == null || PASSWORD == null) {
                throw new SQLException("Variables JDBC manquantes dans le fichier .env");
            }

            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

