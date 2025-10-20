package org.shishkov;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        getDataFromDBWithJDBC();
    }

    public static void getDataFromDBWithJDBC() {
        String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
        String dbUsername = "postgres";
        String dbPassword = "postgres";

        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
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
}