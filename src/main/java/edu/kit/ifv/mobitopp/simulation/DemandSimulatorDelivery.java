package edu.kit.ifv.mobitopp.simulation;

import static java.util.Comparator.comparingInt;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.business.partners.BusinessPartnerSelector;
import edu.kit.ifv.mobitopp.simulation.demand.BusinessParcelDemandModelBuilder;
import edu.kit.ifv.mobitopp.simulation.demand.ParcelDemandModel;
import edu.kit.ifv.mobitopp.simulation.demand.PrivateParcelDemandModelBuilder;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.person.ParcelPersonFactory;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import lombok.Setter;

/**
 * The Class DemandSimulatorDelivery extends the DemandSimulatorPassenger by
 * introducing parcel orders and delivery persons.
 */
public class DemandSimulatorDelivery extends DemandSimulatorPassenger {

	private final Predicate<Person> personFilter;

	private final Collection<Business> businesses;
	private final Predicate<Business> businessDemandFilter;
	private final Predicate<Business> businessProductionFilter;
	private final BusinessPartnerSelector consumptionPartnerSelector;
	private final BusinessPartnerSelector productionPartnerSelector;

	private final ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> privateDemandModel;
	private final ParcelDemandModel<Business, BusinessParcelBuilder> businessDemandModel;
	private final ParcelDemandModel<Business, BusinessParcelBuilder> businessProductionModel;

	private final ParcelSchedulerHook schedulerHook;
	private final DeliveryResults deliveryResults;

	@Setter
	private boolean skipSimulation = false;

	private ParcelPersonFactory parcelPersonFactory;

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
	 * @param privateDemandModel         the private order model
	 * @param businessDemandModel        the business order model
	 * @param businessProductionModel    the business production model
	 * @param results                    the delivery results
	 * @param personFilter               the person filter do determine which
	 *                                   persons should be simulated
	 * @param businessDemandFilter       the business demand filter
	 * @param businessProductionFilter   the business production filter
	 * @param businesses                 the businesses
	 * @param consumptionPartnerSelector the delivery cep-partner selector for businesses
	 * @param productionPartnerSelector  the shipping cep-partner selector for businesses
	 * @param distributionCenters 		 the distribution centers
	 */
	public DemandSimulatorDelivery(final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel, final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer, final TripFactory tripFactory,
			final ReschedulingStrategy rescheduling, final Set<Mode> modesInSimulation, final PersonState initialState,
			final SimulationContext context, final SimulationPersonFactory personFactory,
			
			final ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> privateDemandModel,
			final ParcelDemandModel<Business, BusinessParcelBuilder> businessDemandModel,
			final ParcelDemandModel<Business, BusinessParcelBuilder> businessProductionModel,
			final DeliveryResults results, final Predicate<Person> personFilter,
			final Predicate<Business> businessDemandFilter, final Predicate<Business> businessProductionFilter,
			final Collection<Business> businesses, final BusinessPartnerSelector consumptionPartnerSelector,
			final BusinessPartnerSelector productionPartnerSelector,
			final Collection<DistributionCenter> distributionCenters) {

		super(destinationChoiceModel, modeChoiceModel, routeChoice, activityPeriodFixer, activityDurationRandomizer,
				tripFactory, rescheduling, modesInSimulation, initialState, context, personFactory);
		
		this.parcelPersonFactory = new ParcelPersonFactory(personFactory());
		
		this.businessDemandFilter = businessDemandFilter;
		this.businessProductionFilter = businessProductionFilter;

		this.privateDemandModel = privateDemandModel;
		this.businessDemandModel = businessDemandModel;
		this.businessProductionModel = businessProductionModel;
		this.personFilter = personFilter;
		this.deliveryResults = results;
		this.schedulerHook = new ParcelSchedulerHook(false);
		this.businesses = businesses;
		this.consumptionPartnerSelector = consumptionPartnerSelector;
		this.productionPartnerSelector = productionPartnerSelector;

		this.schedulerHook.register(this);
		distributionCenters.forEach(this::addBeforeTimeSliceHook);
	}

	/**
	 * Instantiates a new demand simulator delivery.
	 *
	 * @param destinationChoiceModel     the destination choice model
	 * @param modeChoiceModel            the mode choice model
	 * @param routeChoice                the route choice
	 * @param activityPeriodFixer        the activity period fixer
	 * @param activityDurationRandomizer the activity duration randomizer
	 * @param tripFactory                the trip factory
	 * @param rescheduling               the rescheduling
	 * @param modesInSimulation          the modes in simulation
	 * @param initialState               the initial state
	 * @param context                    the context
	 * @param personFactory              the person factory
	 * @param businessDemandModel        the business demand model
	 * @param businessProductionModel    the business production model
	 * @param results                    the results
	 * @param personFilter               the person filter
	 * @param businesses                 the businesses
	 * @param consumptionPartnerSelector the consumption partner selector
	 * @param productionPartnerSelector  the production partner selector
	 * @param distributionCenters        the distribution centers
	 */
	public DemandSimulatorDelivery(final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel, final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer, final TripFactory tripFactory,
			final ReschedulingStrategy rescheduling, final Set<Mode> modesInSimulation, final PersonState initialState,
			final SimulationContext context,

			final SimulationPersonFactory personFactory,
			final ParcelDemandModel<Business, BusinessParcelBuilder> businessDemandModel,
			final ParcelDemandModel<Business, BusinessParcelBuilder> businessProductionModel,
			final DeliveryResults results, final Predicate<Person> personFilter, final Collection<Business> businesses,
			final BusinessPartnerSelector consumptionPartnerSelector, final BusinessPartnerSelector productionPartnerSelector,
			final Collection<DistributionCenter> distributionCenters) {

		this(destinationChoiceModel, modeChoiceModel, routeChoice, activityPeriodFixer, activityDurationRandomizer,
				tripFactory, rescheduling, modesInSimulation, initialState, context, personFactory,
				PrivateParcelDemandModelBuilder.nullPrivateParcelModel(results), businessDemandModel,
				businessProductionModel, results, personFilter, b -> true, b -> true, businesses, consumptionPartnerSelector, productionPartnerSelector, distributionCenters);
	}

	/**
	 * Instantiates a new demand simulator delivery.
	 *
	 * @param destinationChoiceModel     the destination choice model
	 * @param modeChoiceModel            the mode choice model
	 * @param routeChoice                the route choice
	 * @param activityPeriodFixer        the activity period fixer
	 * @param activityDurationRandomizer the activity duration randomizer
	 * @param tripFactory                the trip factory
	 * @param rescheduling               the rescheduling
	 * @param modesInSimulation          the modes in simulation
	 * @param initialState               the initial state
	 * @param context                    the context
	 * @param personFactory              the person factory
	 * @param results                    the results
	 * @param personFilter               the person filter
	 * @param businesses                 the businesses
	 * @param consumptionPartnerSelector the consumption partner selector
	 * @param productionPartnerSelector  the production partner selector
	 * @param distributionCenters        the distribution centers
	 */
	public DemandSimulatorDelivery(final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel, final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer, final TripFactory tripFactory,
			final ReschedulingStrategy rescheduling, final Set<Mode> modesInSimulation, final PersonState initialState,
			final SimulationContext context,

			final SimulationPersonFactory personFactory, final DeliveryResults results,
			final Predicate<Person> personFilter, Collection<Business> businesses,
			final BusinessPartnerSelector consumptionPartnerSelector, final BusinessPartnerSelector productionPartnerSelector,
			final Collection<DistributionCenter> distributionCenters) {

		this(destinationChoiceModel, modeChoiceModel, routeChoice, activityPeriodFixer, activityDurationRandomizer,
				tripFactory, rescheduling, modesInSimulation, initialState, context, personFactory,
				PrivateParcelDemandModelBuilder.nullPrivateParcelModel(results),
				BusinessParcelDemandModelBuilder.nullBusinessParcelOrderModel(results),
				BusinessParcelDemandModelBuilder.nullBusinessParcelOrderModel(results), results, personFilter,
				b -> true, b -> true, businesses, consumptionPartnerSelector, productionPartnerSelector, distributionCenters);
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

		Consumer<Person> createAgent = p -> {
			PickUpParcelPerson ppp = createSimulatedPerson(queue, boarder, seed, p, listener, modesInSimulation,
					initialState);
			createPrivateParcelDemand(ppp).forEach(schedulerHook::addParcel);
		};

		personLoader().households().flatMap(Household::persons).filter(personFilter).forEach(createAgent);
		this.privateDemandModel.printStatistics("private");

		personLoader().clearInput();

		initBusinessDemand();

		if (skipSimulation) {
			this.schedulerHook.flushAllParcels();
			System.out.println("Generated Demand Only");
			System.exit(0);
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
		
		PickUpParcelPerson ppp = parcelPersonFactory.create(p, queue, simulationOptions(), simulationDays(),
				modesInSimulation, tourFactory, tripFactory(), initialState, boarder, seed, listener);
		personLoader().removePerson(ppp.getOid());
		return ppp;
	}

	/**
	 * Initiates the business parcel demand. Applies the business demand model and
	 * the business production model.
	 */
	protected void initBusinessDemand() {
		
		this.businesses.stream().filter(businessDemandFilter).forEach(b -> {
			createBusinessParcelDemand(b).forEach(schedulerHook::addParcel);
		});
		this.businessDemandModel.printStatistics("business");

		
		this.businesses.stream().filter(businessProductionFilter).forEach(b -> {
			createBusinessParcelProduction(b).forEach(schedulerHook::addParcel);
		});
		this.businessProductionModel.printStatistics("produced");

		
		Comparator<Business> compareConsumptionDemand = comparingInt(b -> b.getDemandQuantity().getConsumption());
		this.businesses.stream()
					   .filter(businessDemandFilter)
					   .filter(b -> b.getDemandQuantity().getConsumption() > 0)
					   .sorted(compareConsumptionDemand.reversed())
					   .forEach(b -> consumptionPartnerSelector.select(b).forEach(b::addDeliveryPartner));

		Comparator<Business> compareProductionDemand = comparingInt(b -> b.getDemandQuantity().getProduction());
		this.businesses.stream()
					   .filter(businessProductionFilter)
					   .filter(b -> b.getDemandQuantity().getProduction() > 0)
					   .sorted(compareProductionDemand.reversed())
					   .forEach(b -> productionPartnerSelector.select(b).forEach(b::addShippingPartner));

		consumptionPartnerSelector.printStatistics();
		productionPartnerSelector.printStatistics();
	}

	/**
	 * Creates the parcel orders for the given person by applying the simulator's
	 * parcelOrderModel.
	 *
	 * @param p the person
	 * @return the collection of parcels ordered by the given person
	 */
	protected Collection<PrivateParcelBuilder> createPrivateParcelDemand(PickUpParcelPerson p) {
		return this.privateDemandModel.createParcelDemand(p);
	}

	/**
	 * Creates the business parcel orders for the given opportunity by applying the
	 * simulator's businessParcelOrderModel.
	 *
	 * @param business the business
	 * @return the collection of parcels ordered by the given opportunity/business
	 */
	protected Collection<BusinessParcelBuilder> createBusinessParcelDemand(Business business) {
		return this.businessDemandModel.createParcelDemand(business);
	}

	/**
	 * Creates the business parcel production for the given opportunity by applying
	 * the simulator's businessParcelProductionModel.
	 *
	 * @param business the business
	 * @return the collection of parcels produced by the given opportunity/business
	 */
	protected Collection<BusinessParcelBuilder> createBusinessParcelProduction(Business business) {
		return this.businessProductionModel.createParcelDemand(business);
	}

}
