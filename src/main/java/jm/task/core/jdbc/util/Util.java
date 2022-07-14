package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {
    public static Connection getConnection() {
        Connection connection;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }
}
