package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.business.Business;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.BusinessParcelDemandModelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.ParcelDemandModel;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.PrivateParcelDemandModelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.model.BusinessParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.model.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.model.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPersonFactory;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;

/**
 * The Class DemandSimulatorDelivery extends the DemandSimulatorPassenger by
 * introducing parcel orders and delivery persons.
 */
public class DemandSimulatorDelivery extends DemandSimulatorPassenger {

	private final ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> privateOrderModel;
	private final Collection<ParcelBuilder<?>> parcels;
	private final DeliveryPersonFactory deliveryPersonFactory;
	private final Predicate<Person> personFilter;
	private final DeliveryResults deliveryResults;
	private final ParcelDemandModel<Business, BusinessParcelBuilder> businessOrderModel;

	/**
	 * Instantiates a new demand simulator delivery.
	 *
	 * @param destinationChoiceModel     the destination choice model
	 * @param modeChoiceModel            the mode choice model
	 * @param routeChoice                the route choice model
	 * @param activityPeriodFixer        the activity period fixer
	 * @param activityDurationRandomizer the activity duration randomizer
	 * @param tripFactory                the trip factory
	 * @param rescheduling               the rescheduling strategy
	 * @param modesInSimulation          the modes used in simulation
	 * @param initialState               the initial person state
	 * @param context                    the simulation context
	 * @param personFactory              the person factory
	 * @param privateOrderModel          the private order model
	 * @param businessOrderModel         the business order model
	 * @param results                    the delivery results
	 * @param personFilter               the person filter do determine which
	 *                                   persons should be simulated
	 */
	public DemandSimulatorDelivery(final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel, final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer, final TripFactory tripFactory,
			final ReschedulingStrategy rescheduling, final Set<Mode> modesInSimulation, final PersonState initialState,
			final SimulationContext context, final DeliveryPersonFactory personFactory,
			final ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> privateOrderModel,
			final ParcelDemandModel<Business, BusinessParcelBuilder> businessOrderModel, final DeliveryResults results,
			final Predicate<Person> personFilter) {

		super(destinationChoiceModel, modeChoiceModel, routeChoice, activityPeriodFixer, activityDurationRandomizer,
				tripFactory, rescheduling, modesInSimulation, initialState, context, personFactory.getDefaultFactory());

		this.privateOrderModel = privateOrderModel;
		this.businessOrderModel = businessOrderModel;
		this.parcels = new ArrayList<>();
		this.deliveryPersonFactory = personFactory;
		this.personFilter = personFilter;
		this.deliveryResults = results;
	}

	public DemandSimulatorDelivery(final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel, final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer, final TripFactory tripFactory,
			final ReschedulingStrategy rescheduling, final Set<Mode> modesInSimulation, final PersonState initialState,
			final SimulationContext context, final DeliveryPersonFactory personFactory,
			final ParcelDemandModel<Business, BusinessParcelBuilder> businessOrderModel, final DeliveryResults results,
			final Predicate<Person> personFilter) {
		this(destinationChoiceModel, modeChoiceModel, routeChoice, activityPeriodFixer, activityDurationRandomizer,
				tripFactory, rescheduling, modesInSimulation, initialState, context, personFactory,
				PrivateParcelDemandModelBuilder.nullPrivateParcelModel(results), businessOrderModel, results, personFilter);
	}

	public DemandSimulatorDelivery(final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel, final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer, final TripFactory tripFactory,
			final ReschedulingStrategy rescheduling, final Set<Mode> modesInSimulation, final PersonState initialState,
			final SimulationContext context, final DeliveryPersonFactory personFactory, final DeliveryResults results,
			final Predicate<Person> personFilter) {
		this(destinationChoiceModel, modeChoiceModel, routeChoice, activityPeriodFixer, activityDurationRandomizer,
				tripFactory, rescheduling, modesInSimulation, initialState, context, personFactory,
				PrivateParcelDemandModelBuilder.nullPrivateParcelModel(results),
				BusinessParcelDemandModelBuilder.nullBusinessParcelOrderModel(results), results,
				personFilter);
	}

	/**
	 * Initiates a fraction of households. Creates a SimulatedPerson for each person
	 * in a household. Creates parcel orders for each person in a household.
	 *
	 * @param queue             the event queue
	 * @param boarder           the public transport behavior
	 * @param seed              the seed
	 * @param listener          the person listener
	 * @param modesInSimulation the modes used in simulation
	 * @param initialState      the initial person state
	 */
	@Override
	protected void initFractionOfHouseholds(EventQueue queue, PublicTransportBehaviour boarder, long seed,
			PersonListener listener, Set<Mode> modesInSimulation, PersonState initialState) {

		Function<Person, PickUpParcelPerson> createAgent = p -> createSimulatedPerson(queue, boarder, seed, p, listener,
				modesInSimulation, initialState);

		List<PickUpParcelPerson> ppps = personLoader().households().flatMap(Household::persons).filter(personFilter)
				.map(createAgent).collect(Collectors.toList());

		createParcelOrders(ppps);
	}

	private void createParcelOrders(List<PickUpParcelPerson> ppps) {

		Function<PickUpParcelPerson, Collection<PrivateParcelBuilder>> createParcelOrders = p -> createParcelOrder(p);

		int[] counts = new int[11];
		ppps.forEach(ppp -> {
			counts[createParcelOrders.apply(ppp).size()]++;
		});

		printDistribution(counts, "private");

		int[] countsBusiness = new int[11];
//		context().zoneRepository().getZones().forEach(
//				z -> z.getDemandData().opportunities().forEach(o -> countsBusiness[createBusinessOrders(o).size()]++));
		
		printDistribution(countsBusiness, "business");
	}
	
	private void printDistribution(int[] orderSizes, String label) {
		int sum = IntStream.range(0, orderSizes.length).map(i -> i * orderSizes[i]).sum();
		int amount = IntStream.of(orderSizes).sum();

		System.out.println("Generated " + sum + " " + label +" parcels for " + amount + " potential recipients");

		System.out.println("Number of " + label + " parcels distribution: ");
		for (int i = 0; i < orderSizes.length; i++) {
			System.out.println("Order size " + i + ": " + orderSizes[i]);
		}
	}

	/**
	 * Creates the simulated person.
	 *
	 * @param queue             the queue
	 * @param boarder           the boarder
	 * @param seed              the seed
	 * @param p                 the p
	 * @param listener          the listener
	 * @param modesInSimulation the modes in simulation
	 * @param initialState      the initial state
	 * @return the pick up parcel person
	 */
	protected PickUpParcelPerson createSimulatedPerson(EventQueue queue, PublicTransportBehaviour boarder, long seed,
			Person p, PersonListener listener, Set<Mode> modesInSimulation, PersonState initialState) {
		return deliveryPersonFactory.create(p, queue, simulationOptions(), simulationDays(), modesInSimulation,
				tourFactory, tripFactory(), initialState, boarder, seed, listener, this.deliveryResults);
	}

	/**
	 * Creates the parcel orders for the given person by applying the simulator's
	 * parcelOrderModel.
	 *
	 * @param p the person
	 * @return the collection of parcels ordered by the given person
	 */
	protected Collection<PrivateParcelBuilder> createParcelOrder(PickUpParcelPerson p) {
		Collection<PrivateParcelBuilder> parcels = this.privateOrderModel.createParcelDemand(p);
		this.parcels.addAll(parcels);

		return parcels;
	}

	/**
	 * Creates the business parcel orders for the given opportunity by applying the
	 * simulator's businessParcelOrderModel.
	 *
	 * @param business the business
	 * @return the collection of parcels ordered by the given opportunity/business
	 */
	protected Collection<BusinessParcelBuilder> createBusinessOrders(Business business) {
		Collection<BusinessParcelBuilder> parcels = this.businessOrderModel.createParcelDemand(business);
		this.parcels.addAll(parcels);

		return parcels;
	}

}
