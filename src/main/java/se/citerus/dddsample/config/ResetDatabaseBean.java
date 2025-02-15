package se.citerus.dddsample.config;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import nl.pojoquery.DB;
import nl.pojoquery.SqlExpression;

public class ResetDatabaseBean {
  
  @Autowired
  EntityManager entityManager;

  @PostConstruct
  public void clearDatabase() {
    entityManager.unwrap(Session.class).doWork((conn) -> {
      List<String> tableNames = DB.queryColumns(conn, "SHOW TABLES").get(0);
      DB.update(conn, SqlExpression.sql("SET foreign_key_checks = 0"));
      for (String table : tableNames) {
        DB.update(conn, SqlExpression.sql("TRUNCATE TABLE " + table));
        List<String> fieldNames = DB.queryColumns(conn, "DESCRIBE " + table).get(0);
        if (fieldNames.contains("id")) {
          DB.update(conn, SqlExpression.sql("ALTER TABLE " + table + " MODIFY COLUMN id BIGINT auto_increment NOT NULL"));
        }
      }
    });
  }
}
