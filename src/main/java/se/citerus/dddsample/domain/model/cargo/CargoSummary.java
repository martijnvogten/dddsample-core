package se.citerus.dddsample.domain.model.cargo;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import nl.pojoquery.annotations.FieldName;
import nl.pojoquery.annotations.Link;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainEntity;

/**
 * A Cargo. This is the central class in the domain model,
 * and it is the root of the Cargo-Itinerary-Leg-Delivery-RouteSpecification aggregate.
 *
 * A cargo is identified by a unique tracking id, and it always has an origin
 * and a route specification. The life cycle of a cargo begins with the booking procedure,
 * when the tracking id is assigned. During a (short) period of time, between booking
 * and initial routing, the cargo has no itinerary.
 *
 * The booking clerk requests a list of possible routes, matching the route specification,
 * and assigns the cargo to one route. The route to which a cargo is assigned is described
 * by an itinerary.
 *
 * A cargo can be re-routed during transport, on demand of the customer, in which case
 * a new route is specified for the cargo and a new route is requested. The old itinerary,
 * being a value object, is discarded and a new one is attached.
 *
 * It may also happen that a cargo is accidentally misrouted, which should notify the proper
 * personnel and also trigger a re-routing procedure.
 *
 * When a cargo is handled, the status of the delivery changes. Everything about the delivery
 * of the cargo is contained in the Delivery value object, which is replaced whenever a cargo
 * is handled by an asynchronous event triggered by the registration of the handling event.
 *
 * The delivery can also be affected by routing changes, i.e. when the route specification
 * changes, or the cargo is assigned to a new route. In that case, the delivery update is performed
 * synchronously within the cargo aggregate.
 *
 * The life cycle of a cargo ends when the cargo is claimed by the customer.
 *
 * The cargo aggregate, and the entire domain model, is built to solve the problem
 * of booking and tracking cargo. All important business rules for determining whether
 * or not a cargo is misdirected, what the current status of the cargo is (on board carrier,
 * in port etc), are captured in this aggregate.
 *
 */
// @Entity(name = "Cargo")
@Table(name = "Cargo")
@nl.pojoquery.annotations.Table("cargo")
public class CargoSummary implements DomainEntity<CargoSummary> {
  
  
  @nl.pojoquery.annotations.Table("cargo")
  public static class CargoRef {
    @nl.pojoquery.annotations.Id
    private Long id;
    
    @FieldName("tracking_id")
    private String trackingId;
    
    public CargoRef(Long id, String trackingId) {
      this.id = id;
      this.trackingId = trackingId;
    }
    
    protected CargoRef() {
    };
    
    public Long id() {
      return id;
    }
    
    public TrackingId trackingId() {
      return new TrackingId(trackingId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(trackingId);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CargoRef other = (CargoRef) obj;
      return Objects.equals(trackingId, other.trackingId);
    }
  }

  @nl.pojoquery.annotations.Id
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @FieldName("tracking_id")
  @Column(name = "tracking_id", unique = true)
  protected String trackingId;

  @Link(linkfield = "origin_id")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "origin_id")
  protected Location origin;

  @nl.pojoquery.annotations.Embedded(prefix="")
  @Embedded
  protected RouteSpecification routeSpecification;

  @nl.pojoquery.annotations.Embedded(prefix="")
  @Embedded
  protected Delivery delivery;

  /**
   * The tracking id is the identity of this entity, and is unique.
   * 
   * @return Tracking id.
   */
  public TrackingId trackingId() {
    return new TrackingId(trackingId);
  }

  /**
   * @return Origin location.
   */
  public Location origin() {
    return origin;
  }

  /**
   * @return The delivery. Never null.
   */
  public Delivery delivery() {
    return delivery;
  }

  /**
   *
   * @return the id of the cargo, note that the id is not the tracking id.
   */
  public Long id(){
    return id;
  }

  /**
   * @return The route specification.
   */
  public RouteSpecification routeSpecification() {
    return routeSpecification;
  }
  
  @Override
  public boolean sameIdentityAs(final CargoSummary other) {
    return other != null && trackingId.equals(other.trackingId);
  }

  /**
   * @param object to compare
   * @return True if they have the same identity
   * @see #sameIdentityAs(CargoSummary)
   */
  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    final CargoSummary other = (CargoSummary) object;
    return sameIdentityAs(other);
  }

  /**
   * @return Hash code of tracking id.
   */
  @Override
  public int hashCode() {
    return trackingId.hashCode();
  }

  @Override
  public String toString() {
    return trackingId;
  }
  
  public CargoRef getRef() {
    return new CargoRef(id, trackingId);
  }

  protected CargoSummary() {
    // Needed by Hibernate
  }

}
