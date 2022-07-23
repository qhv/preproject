package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        try {
            UserService users = new UserServiceImpl();

            users.dropUsersTable();
            users.createUsersTable();

            users.saveUser("Ivan", "Dow", (byte) 3);
            users.saveUser("Tony", "Smith", (byte) 22);
            users.saveUser("Catherine", "James", (byte) 18);
            users.saveUser("Matthew", "Brown", (byte) 107);

            System.out.println(users.getAllUsers());

            users.cleanUsersTable();
            users.dropUsersTable();
        } finally {
            Util.closeConnection();
        }
    }
}
