package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

public class LocationRepositoryImpl implements LocationRepository {

  @Autowired
  private CargoDatabase db;

  @Override
  public Location find(UnLocode unLocode) {
    List<Location> results = db.query(Location.class)
        .addWhere("{location.unlocode} = ?", unLocode.idString())
        .execute();
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public List<Location> getAll() {
    return db.query(Location.class).execute();
  }

  @Override
  public Location store(Location location) {
    db.insert(location);
    return location;
  }

}
