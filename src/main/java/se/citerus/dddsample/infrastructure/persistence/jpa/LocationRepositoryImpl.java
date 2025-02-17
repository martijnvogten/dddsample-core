package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import nl.pojoquery.PojoQuery;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

public class LocationRepositoryImpl implements LocationRepository {

  @Autowired
  private CargoDatabase db;

  @Override
  public Location find(UnLocode unLocode) {
    return db.doWork((conn) -> { 
      List<Location> results = PojoQuery.build(Location.class)
          .addWhere("{this}.unlocode = ?", unLocode.idString()).execute(conn);
      return results.size() > 0 ? results.get(0) : null;
    });
  }

  @Override
  public List<Location> getAll() {
    return db.doWork(conn -> PojoQuery.build(Location.class).execute(conn));
  }

  @Override
  public Location store(Location location) {
    return db.doWork(conn -> {
      PojoQuery.insert(conn, location);
      return location;
    });
  }

}
