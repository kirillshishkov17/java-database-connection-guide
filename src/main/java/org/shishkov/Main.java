package org.shishkov;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

@SuppressWarnings("CallToPrintStackTrace")
public class Main {

    static String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    static String dbUsername = "postgres";
    static String dbPassword = "postgres";
    static HikariDataSource dataSource;

    static {
        configHikariCP();   // ConnectionPool настраивается один раз при старте приложения
    }

    public static void main(String[] args) {
        getDataFromDBWithJDBC();                // низкоуровневое подключение к БД с помощью JDBC
        getDataFromDBThroughConnectionPool();   // добавил HikariCP для управления ConnectionPool
    }

    public static void getDataFromDBWithJDBC() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM person WHERE name = ?");
            ps.setString(1, "Kirill");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.printf("Row content: name = %s, age = %d\n", name, age);
            }
        } catch (SQLException e) {
            System.out.println("database interaction failed");
            e.printStackTrace();
        }
    }

    public static void configHikariCP() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
        System.out.println("HikariCP инициализирован");
    }

    public static void getDataFromDBThroughConnectionPool() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM person");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.printf("Row content: name = %s, age = %d\n", name, age);
            }
        } catch (SQLException e) {
            System.out.println("database interaction failed");
            e.printStackTrace();
        }
    }
}