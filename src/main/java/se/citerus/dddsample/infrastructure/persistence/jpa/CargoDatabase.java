package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

public class CargoDatabase {
  
  @Autowired
  DataSource dataSource;
  
  @Autowired
  EntityManager entityManager;
  
  public interface Work<T> {
    T execute(Connection conn);
  }
  
  public <T> T doWork(Work<T> w) {
    try {
      return (T) entityManager.unwrap(Session.class).doReturningWork(new ReturningWork<T>() {
        @Override
        public T execute(Connection connection) throws SQLException {
          return w.execute(connection);
        }
      });
    } catch (HibernateException hibernateException) {
      throw new RuntimeException(hibernateException);
    }
  }
}
