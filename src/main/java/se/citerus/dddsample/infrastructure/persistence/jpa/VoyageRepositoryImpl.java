package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Indexed;

import org.pojoquery.PojoQuery;
import org.pojoquery.SqlExpression;
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
    return db.query(Voyage.class)
        .addWhere("{voyage.voyageNumber} = ?", voyageNumber.idString())
        .execute().get(0);
  }

  @Override
  public void store(Voyage voyage) {
    if (voyage.id() == null) {
      db.insert(voyage);
      insertCarrierMovementRecords(voyage);
    } else {
      db.update(voyage);
      db.update(SqlExpression.sql("DELETE FROM carrier_movement WHERE voyage_id = ?", voyage.id()));
      insertCarrierMovementRecords(voyage);
    }
  }

  private void insertCarrierMovementRecords(Voyage voyage) {
    for (CarrierMovement movement : voyage.schedule().carrierMovements()) {
      Map<String, Object> cmValues = PojoQuery.extractValues(CarrierMovement.class, movement);
      cmValues.put("voyage_id", voyage.id());
      db.insert("carrier_movement", cmValues);
    }
  }

}
