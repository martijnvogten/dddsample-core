package se.citerus.dddsample.infrastructure.persistence.pojoquery;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.pojoquery.DB;
import org.pojoquery.PojoQuery;
import org.pojoquery.SqlExpression;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

public class CargoRepositoryImpl implements CargoRepository {

  @Autowired
  CargoDatabase db;

  @Override
  public Cargo find(TrackingId trackingId) {
    List<Cargo> list = db.query(Cargo.class)
        .addWhere("{cargo.trackingId} = ?", trackingId.toString())
        .execute();
    return list.size() > 0 ? list.get(0) : null;
  }

  @Override
  public List<Cargo> getAll() {
    return db.query(Cargo.class).execute();
  }

  @Override
  public void store(Cargo cargo) {
    if (cargo.id() == null) {
      db.insert(cargo);
      insertLegRecords(cargo);
    } else {
      db.update(cargo);
      db.update(SqlExpression.sql("DELETE FROM leg WHERE cargo_id = ?", cargo.id()));
      insertLegRecords(cargo);
    }
  }

  private void insertLegRecords(Cargo cargo) {
    for (Leg leg : cargo.itinerary().legs()) {
      Map<String, Object> legValues = PojoQuery.extractValues(Leg.class, leg);
      legValues.remove("id");
      legValues.put("cargo_id", cargo.id());
      db.insert("leg", legValues);
    }
  }

  @Override
  public TrackingId nextTrackingId() {
    return db.doReturningWork(conn -> {
      String select = db.isMySQL() ? 
          "SELECT UPPER(SUBSTR(CAST(UUID() AS CHAR(38)), 1, 8))" 
          :
          // HSQLDB
          "SELECT UPPER(SUBSTR(CAST(UUID() AS VARCHAR(38)), 0, 9)) AS id FROM (VALUES(0))";

      List<String> result = DB.queryColumns(conn, select).get(0);
      return new TrackingId(result.get(0));
    });
  }

}
