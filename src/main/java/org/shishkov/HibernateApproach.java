package org.shishkov;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.shishkov.entities.Person;

import java.util.List;
import java.util.Properties;

public class HibernateApproach {
    static String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    static String dbUsername = "postgres";
    static String dbPassword = "postgres";
    static SessionFactory sessionFactory = initializeHibernate();

    public static void main(String[] args) {
        Session session = sessionFactory.openSession();
        List<Person> persons = session.createNativeQuery("SELECT * FROM person", Person.class).getResultList();
        persons.forEach(System.out::println);
        session.close();
    }

    // в Spring Boot конфигурируется проще
    // если добавить зависимость Spring JPA, то Bean для SessionFactory создастся автоматически
    @SuppressWarnings("deprecation")
    public static SessionFactory initializeHibernate() {
        Configuration configuration = new Configuration();

        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "org.postgresql.Driver");
        settings.put(Environment.URL, dbUrl);
        settings.put(Environment.USER, dbUsername);
        settings.put(Environment.PASS, dbPassword);
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        settings.put(Environment.SHOW_SQL, "false");
        settings.put(Environment.FORMAT_SQL, "false");
        settings.put(Environment.HBM2DDL_AUTO, "update");

        configuration.setProperties(settings);
        configuration.addAnnotatedClass(Person.class);
        return configuration.buildSessionFactory();
    }
}
