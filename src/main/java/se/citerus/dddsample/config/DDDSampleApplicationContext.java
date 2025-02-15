package se.citerus.dddsample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import com.pathfinder.api.GraphTraversalService;

import jakarta.persistence.EntityManager;
import nl.pojoquery.DbContext;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.application.impl.BookingServiceImpl;
import se.citerus.dddsample.application.impl.CargoInspectionServiceImpl;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.model.cargo.CargoFactory;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.infrastructure.messaging.jms.InfrastructureMessagingJmsConfig;
import se.citerus.dddsample.infrastructure.persistence.jpa.CargoRepositoryImpl;
import se.citerus.dddsample.infrastructure.persistence.jpa.HandlingEventRepositoryImpl;
import se.citerus.dddsample.infrastructure.persistence.jpa.LocationRepositoryImpl;
import se.citerus.dddsample.infrastructure.persistence.jpa.VoyageRepositoryImpl;
import se.citerus.dddsample.infrastructure.routing.ExternalRoutingService;
import se.citerus.dddsample.infrastructure.sampledata.SampleDataGenerator;
import se.citerus.dddsample.interfaces.InterfacesApplicationContext;


@Configuration
@Import({InterfacesApplicationContext.class, InfrastructureMessagingJmsConfig.class})
public class DDDSampleApplicationContext {
	
	static {
		DbContext pojoqueryConfig = DbContext.getDefault();
		pojoqueryConfig.setQuoteStyle(nl.pojoquery.DbContext.QuoteStyle.MYSQL);
		pojoqueryConfig.setQuoteObjectNames(false);
	}

	@Autowired
	private GraphTraversalService graphTraversalService;
	
	@Autowired
	private ApplicationEvents applicationEvents;
	
	@Autowired
	private EntityManager entityManager;
	
	@Bean
    public LocationRepository locationRepository() {
    	return new LocationRepositoryImpl();
    }

    @Bean
    public HandlingEventRepository handlingEventRepository() {
    	return new HandlingEventRepositoryImpl();
    }

    @Bean
    public CargoRepository cargoRepository() {
    	return new CargoRepositoryImpl();
    }

    @Bean
    public VoyageRepository voyageRepository() {
    	return new VoyageRepositoryImpl();
    }
    
    @Bean
    public CargoFactory cargoFactory() {
        return new CargoFactory(locationRepository(), cargoRepository());
    }

    @Bean
    public BookingService bookingService(CargoFactory cargoFactory,RoutingService routingService) {
        return new BookingServiceImpl(cargoRepository(), locationRepository(), routingService, cargoFactory);
    }

    @Bean
    public CargoInspectionService cargoInspectionService() {
        return new CargoInspectionServiceImpl(applicationEvents, cargoRepository(), handlingEventRepository());
    }

    @Bean
    public HandlingEventService handlingEventService(HandlingEventFactory handlingEventFactory) {
        return new HandlingEventServiceImpl(handlingEventRepository(), applicationEvents, handlingEventFactory);
    }

    @Bean
    public HandlingEventFactory handlingEventFactory() {
        return new HandlingEventFactory(cargoRepository(), voyageRepository(), locationRepository());
    }

    @Bean
    public RoutingService routingService() {
        return new ExternalRoutingService(graphTraversalService, locationRepository(), voyageRepository());
    }

    @Bean
    public ResetDatabaseBean resetDatabaseBean() {
      return new ResetDatabaseBean();
    }
    
    @Bean
    public SampleDataGenerator sampleDataGenerator(CargoRepository cargoRepository,
                                                   VoyageRepository voyageRepository,
                                                   LocationRepository locationRepository,
                                                   HandlingEventRepository handlingEventRepository,
                                                   PlatformTransactionManager platformTransactionManager) {
        return new SampleDataGenerator(cargoRepository,
            voyageRepository,
            locationRepository,
            handlingEventRepository,
            platformTransactionManager);
    }
}
