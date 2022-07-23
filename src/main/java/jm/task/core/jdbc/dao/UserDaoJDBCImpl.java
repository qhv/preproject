package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private static final UserDaoJDBCImpl INSTANCE = new UserDaoJDBCImpl();
    private static final String CREATE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS preproject;";
    private static final String CREAETE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS preproject.users
            (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(45),
                last_name VARCHAR(45),
                age TINYINT
            )
            """;
    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS preproject.users";
    private static final String SAVE_USER_SQL = """
            INSERT INTO preproject.users(name, last_name, age)
            VALUES (?, ?, ?)
            """;
    private static final String REMOVE_USER_SQL = """
            DELETE FROM preproject.users
            WHERE id = ?
            """;
    private static final String GET_ALL_USERS_SQL = """
            SELECT id,
                   name,
                   last_name,
                   age
            FROM preproject.users
            """;
    private static final String CLEAN_TABLE_SQL = "TRUNCATE TABLE preproject.users";

    private UserDaoJDBCImpl() {
    }

    public static UserDaoJDBCImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void createUsersTable() {
        Connection connection = null;
        PreparedStatement createSchemaStatement = null;
        PreparedStatement createTableStatement = null;
        Exception exception = null;

        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false);

            createSchemaStatement = connection.prepareStatement(CREATE_SCHEMA_SQL);
            createTableStatement = connection.prepareStatement(CREAETE_TABLE_SQL);

            createSchemaStatement.executeUpdate();
            createTableStatement.executeUpdate();

            connection.commit();
        } catch (Exception e) {
            exception = e;

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException sqlException) {
                e.addSuppressed(sqlException);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }

                if (createSchemaStatement != null) createSchemaStatement.close();
                if (createTableStatement != null) createTableStatement.close();
            } catch (SQLException sqlException) {
                if (exception != null) {
                    exception.addSuppressed(sqlException);
                } else {
                    exception = sqlException;
                }
            }

            if (exception != null) throw new RuntimeException(exception);
        }

    }

    @Override
    public void dropUsersTable() {
        try (var connection = Util.getConnection();
             var prepareStatement = connection.prepareStatement(DROP_TABLE_SQL)) {

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (var connection = Util.getConnection();
             var ps = connection.prepareStatement(SAVE_USER_SQL)) {

            ps.setString(1, name);
            ps.setString(2, lastName);
            ps.setByte(3, age);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (var connection = Util.getConnection();
             var ps = connection.prepareStatement(REMOVE_USER_SQL)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (var connection = Util.getConnection();
             var prepareStatement = connection.prepareStatement(GET_ALL_USERS_SQL);
             var resultSet = prepareStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (var connection = Util.getConnection();
             var prepareStatement = connection.prepareStatement(CLEAN_TABLE_SQL)) {

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
