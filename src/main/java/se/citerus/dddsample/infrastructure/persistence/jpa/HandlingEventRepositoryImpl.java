package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.beans.factory.annotation.Autowired;

import nl.pojoquery.PojoQuery;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

public class HandlingEventRepositoryImpl implements HandlingEventRepository {
	
	@Autowired
	CargoDatabase db;

	@Override
	public void store(HandlingEvent event) {
	  db.doWork(conn -> {
	    return PojoQuery.insert(conn, event);
	  });
	}

	@Override
	//   @Query("select he from HandlingEvent he where he.cargo.trackingId = :trackingId and he.location is not NULL")
	public HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId) {
		return new HandlingHistory(db.doWork(conn -> PojoQuery.build(HandlingEvent.class)
		    .addWhere("{cargo}.tracking_id = ?", trackingId.idString())
		    .addWhere("{location}.id IS NOT NULL")
		    .execute(conn)
		));
	}
}
