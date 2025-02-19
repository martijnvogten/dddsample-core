package se.citerus.dddsample.domain.model.voyage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import nl.pojoquery.annotations.FieldName;
import nl.pojoquery.annotations.Link;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainEntity;

/**
 * A Voyage.
 */
// @Entity(name = "Voyage")
@Table(name = "Voyage")
@nl.pojoquery.annotations.Table("voyage")
public class Voyage implements DomainEntity<Voyage> {
  
  @nl.pojoquery.annotations.Table("voyage")
  public static class VoyageRef {
    @nl.pojoquery.annotations.Id
    private Long id;
    
    @FieldName("voyage_number")
    private String voyageNumber;
    
    public VoyageRef(Long id, String voyageNumber) {
      this.id = id;
      this.voyageNumber = voyageNumber;
    }

    public VoyageNumber voyageNumber() {
      return new VoyageNumber(voyageNumber);
    }
    
    public Long id() {
      return id;
    }
    
    @Override
    public int hashCode() {
      return voyageNumber.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null) return false;
      if (!(o instanceof VoyageRef)) return false;

      final VoyageRef that = (VoyageRef) o;

      return sameIdentityAs(that);
    }

    public boolean sameIdentityAs(VoyageRef other) {
      return other != null && this.voyageNumber().sameValueAs(other.voyageNumber());
    }
    
    @SuppressWarnings("unused")
    private VoyageRef() {
    }
  }

  @nl.pojoquery.annotations.Id
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @FieldName("voyage_number")
  @Column(name = "voyage_number", unique = true)
  private String voyageNumber;

  @Link(foreignlinkfield = "voyage_id")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "voyage_id")
  private List<CarrierMovement> carrierMovements;

  protected Voyage() {
    // Needed by Hibernate
  }

  // Null object pattern
  @Transient
  public static final Voyage NONE = new Voyage(new VoyageNumber(""), Schedule.EMPTY);

    public Voyage(final VoyageNumber voyageNumber, final Schedule schedule) {
    Objects.requireNonNull(voyageNumber, "Voyage number is required");
    Objects.requireNonNull(schedule, "Schedule is required");

    this.voyageNumber = voyageNumber.idString();
    this.carrierMovements = schedule.carrierMovements();
  }

  /**
   * @return Voyage number.
   */
  public VoyageNumber voyageNumber() {
    return new VoyageNumber(voyageNumber);
  }

  /**
   * @return Schedule.
   */
  public Schedule schedule() {
    return new Schedule(carrierMovements);
  }

  @Override
  public int hashCode() {
    return voyageNumber.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (!(o instanceof Voyage)) return false;

    final Voyage that = (Voyage) o;

    return sameIdentityAs(that);
  }

  @Override
  public boolean sameIdentityAs(Voyage other) {
    return other != null && this.voyageNumber().sameValueAs(other.voyageNumber());
  }

  @Override
  public String toString() {
    return "Voyage " + voyageNumber;
  }


  public Long id() {
    return id;
  }
  
  public VoyageRef getRef() {
    return new VoyageRef(id, voyageNumber);
  }

  /**
   * Builder pattern is used for incremental construction
   * of a Voyage aggregate. This serves as an aggregate factory.
   */
  public static final class Builder {

    private final List<CarrierMovement> carrierMovements = new ArrayList<>();
    private final VoyageNumber voyageNumber;
    private Location departureLocation;

    public Builder(final VoyageNumber voyageNumber, final Location departureLocation) {
      Objects.requireNonNull(voyageNumber, "Voyage number is required");
      Objects.requireNonNull(departureLocation, "Departure location is required");

      this.voyageNumber = voyageNumber;
      this.departureLocation = departureLocation;
    }

    public Builder addMovement(Location arrivalLocation, Instant departureTime, Instant arrivalTime) {
      carrierMovements.add(new CarrierMovement(departureLocation, arrivalLocation, departureTime, arrivalTime));
      // Next departure location is the same as this arrival location
      this.departureLocation = arrivalLocation;
      return this;
    }

    public Voyage build() {
      return new Voyage(voyageNumber, new Schedule(carrierMovements));
    }

  }

}
