package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import nl.pojoquery.PojoQuery;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

public class LocationRepositoryImpl implements LocationRepository {
	
	@Autowired
	DataSource db;

	@Override
	public Location find(UnLocode unLocode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location store(Location location) {
		PojoQuery.insert(db, location);
		return location;
	}

}
