package se.citerus.dddsample.infrastructure.persistence.jpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import nl.pojoquery.PojoQuery;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

public class HandlingEventRepositoryImpl implements HandlingEventRepository {
	
	@Autowired
	DataSource db;

	@Override
	public void store(HandlingEvent event) {
		PojoQuery.insert(db, event);
	}

	@Override
	public HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId) {
		// TODO Auto-generated method stub
		return null;
	}
}
