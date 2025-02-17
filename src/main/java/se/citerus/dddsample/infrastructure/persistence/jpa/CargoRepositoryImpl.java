package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import nl.pojoquery.DB;
import nl.pojoquery.PojoQuery;
import nl.pojoquery.SqlExpression;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

public class CargoRepositoryImpl implements CargoRepository {

  @Autowired
  CargoDatabase db;

  @Override
  public Cargo find(TrackingId trackingId) {
    return db.doWork(conn -> {
      List<Cargo> list = PojoQuery.build(Cargo.class).addWhere("{this}.tracking_id=?", trackingId.toString())
          .execute(conn);
      return list.size() > 0 ? list.get(0) : null;
    });
  }

  @Override
  public List<Cargo> getAll() {
    return db.doWork(conn -> PojoQuery.build(Cargo.class).execute(conn));
  }

  @Override
  public void store(Cargo cargo) {
    db.doWork(conn -> {
      if (cargo.id() == null) {
        PojoQuery.insert(conn, cargo);
        insertLegRecords(conn, cargo);
      } else {
        PojoQuery.update(conn, cargo);
        DB.update(conn, SqlExpression.sql("DELETE FROM leg WHERE cargo_id = ?", cargo.id()));
        insertLegRecords(conn, cargo);
      }
      return null;
    });
  }

  private void insertLegRecords(Connection conn, Cargo cargo) {
    for (Leg leg : cargo.itinerary().legs()) {
      Map<String, Object> legValues = PojoQuery.extractValues(Leg.class, leg);
      legValues.remove("id");
      legValues.put("cargo_id", cargo.id());
      DB.insert(conn, "leg", legValues);
    }
  }

  @Override
  public TrackingId nextTrackingId() {
    String trackingIDString = db.doWork(conn -> {
      List<String> result = DB.queryColumns(conn, "SELECT UPPER(SUBSTR(CAST(UUID() AS CHAR(38)), 1, 8))").get(0);
      return result.get(0);
    });
    return new TrackingId(trackingIDString);
  }

}
