package org.shishkov;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Демонстрация работы с БД через JDBC + HikariCP.
 * JDBC устанавливает соединение с БД.
 * HikariCP управляет пулом соединений и их жизненным циклом.
 */
public class JDBCAndHikariApproach {
    static String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    static String dbUsername = "postgres";
    static String dbPassword = "postgres";
    static HikariDataSource dataSource;

    static CountDownLatch latch = new CountDownLatch(50);
    private static final ReentrantLock lock = new ReentrantLock();

    static {
        configHikariCP();
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(50);

        for (int i = 0; i < 50; i++) {
            executor.execute(() -> {
                try {
                    latch.countDown();
                    latch.await();
                    getDataFromDBThroughConnectionPool();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
    }

    public static void getDataFromDBThroughConnectionPool() {
        try (Connection connection = dataSource.getConnection()) {
            // Задержка для того чтобы была видна параллельная работа и использование соединений из пула
            // Иначе задачи выполняются слишком быстро и хватает 1-2 соединения из пула
            Thread.sleep(300);

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM person WHERE age = ?");
            ps.setInt(1, 25);
            ResultSet rs = ps.executeQuery();

            lock.lock();
            System.out.println("ActiveConnections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("IdleConnections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("TotalConnections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
            lock.unlock();

            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.printf("Person: name = %s, age = %d\n", name, age);
            }
        } catch (SQLException e) {
            System.out.println("database interaction failed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void configHikariCP() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(7);
        config.setMinimumIdle(3);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
        System.out.println("HikariCP инициализирован!");
    }
}

