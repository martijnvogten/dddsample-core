package se.citerus.dddsample.infrastructure.persistence.jpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Indexed;

import nl.pojoquery.PojoQuery;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

@Indexed
public class VoyageRepositoryImpl implements VoyageRepository {
	
	@Autowired
	DataSource db;

	@Override
	public Voyage find(VoyageNumber voyageNumber) {
		return PojoQuery.build(Voyage.class).addWhere("voyage_number=?", voyageNumber).execute(db).get(0);
	}

	@Override
	public void store(Voyage voyage) {
		PojoQuery.insert(db, voyage);
		for (CarrierMovement carrierMovement : voyage.schedule().carrierMovements()) {
			PojoQuery.insert(db, carrierMovement);
		}
	}

}
