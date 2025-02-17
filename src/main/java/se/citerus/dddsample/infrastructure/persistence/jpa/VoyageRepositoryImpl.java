package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Indexed;

import nl.pojoquery.DB;
import nl.pojoquery.PojoQuery;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

@Indexed
public class VoyageRepositoryImpl implements VoyageRepository {
	
	@Autowired
	CargoDatabase db;

	@Override
	public Voyage find(VoyageNumber voyageNumber) {
	  return db.doWork(conn -> {
	    return PojoQuery.build(Voyage.class).addWhere("voyage_number=?", voyageNumber.idString()).execute(conn).get(0);
	  });
	}

	@Override
	public void store(Voyage voyage) {
	  db.doWork(conn -> {
	    PojoQuery.insert(conn, voyage);
	    for (CarrierMovement carrierMovement : voyage.schedule().carrierMovements()) {
	      Map<String, Object> cmValues = PojoQuery.extractValues(CarrierMovement.class, carrierMovement);
	      cmValues.put("voyage_id", voyage.id());
	      DB.insert(conn, "carrier_movement", cmValues);
	    }
	    return voyage;
	  });
	}

}
