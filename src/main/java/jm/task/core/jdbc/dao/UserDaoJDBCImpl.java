package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private static final UserDaoJDBCImpl INSTANCE = new UserDaoJDBCImpl();
    private Connection connection;

    private UserDaoJDBCImpl() {
        Util.openConnection();
        connection = Util.getConnection();
    }

    @Override
    public void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS preproject;");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS preproject.users
                    (id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(45),
                    last_name VARCHAR(45),
                    age TINYINT);
                    """);
        } catch (SQLException e) {
            Util.closeConnection();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void dropUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS preproject.users;");
        } catch (SQLException e) {
            Util.closeConnection();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (PreparedStatement ps = Util.getConnection().prepareStatement(
                "INSERT INTO preproject.users(name, last_name, age) VALUES (?, ?, ?);")) {

            ps.setString(1, name);
            ps.setString(2, lastName);
            ps.setByte(3, age);

            ps.executeUpdate();
        } catch (SQLException e) {
            Util.closeConnection();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (PreparedStatement ps =
                     Util.getConnection().prepareStatement("DELETE FROM preproject.users WHERE id=?;")) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            Util.closeConnection();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Statement statement = Util.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM preproject.users;")) {

            while (resultSet.next()) {
                users.add(new User(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age")));
            }
        } catch (SQLException e) {
            Util.closeConnection();
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (Statement statement = Util.getConnection().createStatement()) {
            statement.execute("TRUNCATE TABLE preproject.users;");
        } catch (SQLException e) {
            Util.closeConnection();
            throw new RuntimeException(e);
        }
    }

    public static UserDaoJDBCImpl getInstance() {
        return INSTANCE;
    }
}
