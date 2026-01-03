package se.citerus.dddsample.config;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.pojoquery.DB;
import org.pojoquery.DbContext;
import org.pojoquery.SqlExpression;
import org.pojoquery.schema.SchemaGenerator;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.infrastructure.sampledata.SampleLocations;
import se.citerus.dddsample.infrastructure.sampledata.SampleVoyages;

public class ResetDatabaseBean {
  
  @Autowired
  EntityManager entityManager;

  @PostConstruct
  public void clearDatabase() {
    entityManager.unwrap(Session.class).doWork((conn) -> {
      if (isMySQL(conn)) { // MySQL
        truncateMySQLDatabase(conn);
      } else if (isHSQLDB(conn)) { // HSQLDB
        createSchemaFromEntityClasses(conn);
      } else {
        throw new RuntimeException("Unsupported database engine: " + conn.getMetaData().getDatabaseProductName());
      }
    });
  }

  private static void createSchemaFromEntityClasses(Connection conn) throws SQLException {
    // First drop all tables in reverse dependency order
    dropAllTables(conn);
    
    DbContext hsqlDbContext = DbContext.forDialect(DbContext.Dialect.HSQLDB);
    List<String> statements = SchemaGenerator.generateCreateTableStatements(
        hsqlDbContext,
        Location.class,
        Voyage.class,
        CarrierMovement.class,
        Cargo.class,
        Leg.class,
        HandlingEvent.class
    );
    for (String statement : statements) {
      System.out.println("DDL: " + statement);
      DB.update(conn, SqlExpression.sql(statement));
    }
    conn.commit();
  }

  /**
   * Drop all tables in the correct order (respecting foreign key dependencies).
   */
  private static void dropAllTables(Connection conn) {
    // Drop in reverse dependency order to avoid FK constraint violations
    String[] tablesToDrop = {
        "handling_event",
        "leg",
        "cargo",
        "carrier_movement",
        "voyage",
        "location"
    };
    for (String table : tablesToDrop) {
      try {
        DB.update(conn, SqlExpression.sql("DROP TABLE " + table + " IF EXISTS CASCADE"));
      } catch (Exception e) {
        // Ignore errors if table doesn't exist
      }
    }
  }

  private static void truncateMySQLDatabase(Connection conn) {
    List<String> tableNames = DB.queryColumns(conn, "SHOW TABLES").get(0);
    DB.update(conn, SqlExpression.sql("SET foreign_key_checks = 0"));
    try {
      for (String table : tableNames) {
        DB.update(conn, SqlExpression.sql("TRUNCATE TABLE " + table));
        List<String> fieldNames = DB.queryColumns(conn, "DESCRIBE " + table).get(0);
        if (fieldNames.contains("id")) {
          DB.update(conn, SqlExpression.sql("ALTER TABLE " + table + " MODIFY COLUMN id BIGINT auto_increment NOT NULL"));
        }
      }
    } finally {
      DB.update(conn, SqlExpression.sql("SET foreign_key_checks = 1"));
    }
  }

  public void truncateDatabase() {
    entityManager.unwrap(Session.class).doWork((conn) -> {
      String url = conn.getMetaData().getURL();
      if (url.startsWith("jdbc:mysql:")) { // MySQL
        truncateMySQLDatabase(conn);
      } else { // HSQLDB
        DB.update(conn, SqlExpression.sql("TRUNCATE SCHEMA PUBLIC AND COMMIT"));
      }
      
      resetIdField(SampleLocations.getAll());
      resetIdField(SampleVoyages.getAll());
    });
  }

  private static <T> void resetIdField(Collection<T> instances) {
    try {
      Field idField = instances.iterator().next().getClass().getDeclaredField("id");
      idField.setAccessible(true);
      for (T instance : instances) {
        idField.set(instance, null);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  private static boolean isHSQLDB(Connection conn) throws SQLException {
    return conn.getMetaData().getURL().startsWith("jdbc:hsqldb:");
  }

  private static boolean isMySQL(Connection conn) throws SQLException {
    return conn.getMetaData().getURL().startsWith("jdbc:mysql:");
  }

}
