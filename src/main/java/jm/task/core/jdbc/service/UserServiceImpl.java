package jm.task.core.jdbc.service;

import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import jm.task.core.jdbc.model.User;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserDaoJDBCImpl users = new UserDaoJDBCImpl();

    @Override
    public void createUsersTable() {
        users.createUsersTable();
    }

    @Override
    public void dropUsersTable() {
        users.dropUsersTable();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        users.saveUser(name, lastName, age);
        System.out.println("User '" + name + "' has been added to table.");
    }

    @Override
    public void removeUserById(long id) {
        users.removeUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return users.getAllUsers();
    }

    @Override
    public void cleanUsersTable() {
        users.cleanUsersTable();
    }
}
