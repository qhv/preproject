package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Util {

    private final static String URL = "jdbc:mysql://0.0.0.0:55002";
    private final static String USER_NAME = "pp_user";
    private final static String PASSWORD = "pa$$";

    private static Connection proxyConnection;
    private static Connection connection;

    /**
     * JDBC connection functions
     */
    static {
        initConnection();
    }

    private static void initConnection() {
        connection = openConnection();
        proxyConnection = (Connection) Proxy.newProxyInstance(Util.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> method.getName().equals("close")
                        ? reinstateConnection((Connection) proxy)
                        : method.invoke(connection, args));
    }

    private static Void reinstateConnection(Connection connection) {
        proxyConnection = connection;
        return null;
    }

    private static Connection openConnection() {
        try {
            return DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Connection getConnection() {
        while (proxyConnection == null) {
            // Здесь должен быть await(), но я пока не до конца понял как это реализовать
        }
        Connection storedProxyConnection = proxyConnection;
        proxyConnection = null;
        return storedProxyConnection;
    }

    /**
     * Hibernate connection functions
     */
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) return sessionFactory;

        try {
            Map<String, Object> settings = new HashMap<>();
            settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
            settings.put(Environment.URL, URL);
            settings.put(Environment.USER, USER_NAME);
            settings.put(Environment.PASS, PASSWORD);
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");

            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
            registryBuilder.applySettings(settings);
            StandardServiceRegistry registry = registryBuilder.build();

            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(User.class);

            Metadata metadata = sources.getMetadataBuilder().build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();

            StandardServiceRegistryBuilder.destroy(registry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sessionFactory;

    }
}
