package org.shishkov;

import java.sql.*;

/**
 * Демонстрация работы с БД через JDBC
 */
public class JDBCApproach {
    static String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    static String dbUsername = "postgres";
    static String dbPassword = "postgres";

    public static void main(String[] args) {
        getDataFromDBWithJDBC();
    }

    public static void getDataFromDBWithJDBC() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM person WHERE age = ?");
            ps.setInt(1, 25);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.printf("Person: name = %s, age = %d\n", name, age);
            }
        } catch (SQLException e) {
            System.out.println("database interaction failed");
        }
    }
}
