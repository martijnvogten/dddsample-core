package se.citerus.dddsample.interfaces.booking.facade.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * DTO for registering and routing a cargo.
 */
public final class CargoSummaryDTO implements Serializable {

  private final String trackingId;
  private final String origin;
  private final String finalDestination;
  private final Instant arrivalDeadline;
  private final boolean misrouted;
  private boolean routed;

  /**
   * Constructor.
   *
   * @param trackingId
   * @param origin
   * @param finalDestination
   * @param arrivalDeadline
   * @param misrouted
   */
  public CargoSummaryDTO(String trackingId, String origin, String finalDestination, Instant arrivalDeadline, boolean misrouted, boolean routed) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.arrivalDeadline = arrivalDeadline;
    this.misrouted = misrouted;
    this.routed = routed;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public String getOrigin() {
    return origin;
  }

  public String getFinalDestination() {
    return finalDestination;
  }

  public boolean isMisrouted() {
    return misrouted;
  }

  public boolean isRouted() {
    return routed;
  }

  public ZonedDateTime getArrivalDeadline() {
    return arrivalDeadline.atZone(ZoneOffset.UTC);
  }
}
