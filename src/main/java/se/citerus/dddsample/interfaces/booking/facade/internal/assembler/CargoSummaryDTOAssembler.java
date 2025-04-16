package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import se.citerus.dddsample.domain.model.cargo.CargoSummary;
import se.citerus.dddsample.domain.model.cargo.RoutingStatus;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoSummaryDTO;

/**
 * Assembler class for the CargoRoutingDTO.
 */
public class CargoSummaryDTOAssembler {

  /**
   *
   * @param cargo cargo
   * @return A cargo routing DTO
   */
  public CargoSummaryDTO toDTO(final CargoSummary cargo) {
    final CargoSummaryDTO dto = new CargoSummaryDTO(
        cargo.trackingId().idString(),
        cargo.origin().unLocode().idString(),
        cargo.routeSpecification().destination().unLocode().idString(),
        cargo.routeSpecification().arrivalDeadline(),
        cargo.delivery().routingStatus().sameValueAs(RoutingStatus.MISROUTED),
        !cargo.delivery().routingStatus().sameValueAs(RoutingStatus.NOT_ROUTED)
      );
    return dto;
  }

}
