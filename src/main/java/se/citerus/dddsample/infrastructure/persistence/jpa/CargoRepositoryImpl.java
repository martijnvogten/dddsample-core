package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import nl.pojoquery.PojoQuery;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

public class CargoRepositoryImpl implements CargoRepository {
	
	@Autowired
	DataSource db;
	
	@Autowired
	EntityManager entityManager;

	@Override
	public Cargo find(TrackingId trackingId) {
		Session session = entityManager.unwrap(Session.class);
		return session.doReturningWork(conn -> {
			List<Cargo> list = PojoQuery.build(Cargo.class).addWhere("tracking_id=?", trackingId).execute(conn);
			return list.size() > 0 ? list.get(0) : null;
		});
	}

	@Override
	public List<Cargo> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void store(Cargo cargo) {
		PojoQuery.insert(db, cargo);
	}

	@Override
	public TrackingId nextTrackingId() {
		// TODO Auto-generated method stub
		return null;
	}

}
