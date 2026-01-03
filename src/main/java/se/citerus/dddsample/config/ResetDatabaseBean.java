package se.citerus.dddsample.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Session;
import org.pojoquery.DB;
import org.pojoquery.DbContext;
import org.pojoquery.SqlExpression;
import org.pojoquery.schema.SchemaGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;

public class ResetDatabaseBean {
  
  @Autowired
  EntityManager entityManager;

  public void clearDatabase() {
    entityManager.unwrap(Session.class).doWork((conn) -> {
      createSchemaFromEntityClasses(conn);
    });
  }

  private static void createSchemaFromEntityClasses(Connection conn) throws SQLException {
    // First drop all tables in reverse dependency order
    dropAllTables(conn);
    
    List<String> statements = SchemaGenerator.generateCreateTableStatements(
        DbContext.getDefault(),
        Location.class,
        Voyage.class,
        CarrierMovement.class,
        Cargo.class,
        Leg.class,
        HandlingEvent.class
    );
    for (String statement : statements) {
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

}
