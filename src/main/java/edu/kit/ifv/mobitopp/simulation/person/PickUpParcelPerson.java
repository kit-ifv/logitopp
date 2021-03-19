package edu.kit.ifv.mobitopp.simulation.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonAttributes;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.RideSharingOffers;
import edu.kit.ifv.mobitopp.simulation.SimulationOptionsCustomization;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.activityschedule.PickUpParcelReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class PickUpParcelPerson decorates a {@link SimulationPerson}
 * by adding the functionalyity of ordering, receiving and picking up {@link Parcel}s.
 */
public class PickUpParcelPerson implements SimulationPerson {
	private SimulationPerson person;
	private Collection<Parcel> ordered;
	private Collection<Parcel> received;
	private Collection<Parcel> inPackstation;
	private Random random;
	
	/**
	 * Instantiates a new {@link PickUpParcelPerson}
	 * decorating the given {@link SimulationPerson}.
	 * 
	 * @param person the person
	 * @param options the options
	 * @param seed the seed
	 */
	public PickUpParcelPerson(SimulationPerson person, SimulationOptionsCustomization options, long seed) {	
		this.person = person;
		
		this.ordered = new ArrayList<Parcel>();
		this.received = new ArrayList<Parcel>();
		this.inPackstation = new ArrayList<Parcel>();
		
		this.random = new Random(getOid() + seed);
		
		PickUpParcelReschedulingStrategy pickUpReschedulingStrategy = new PickUpParcelReschedulingStrategy(this, options.rescheduling());
		options.customize(pickUpReschedulingStrategy);
		
	}
	
	
	
	/**
	 * Checks for parcels in the pack station.
	 *
	 * @return true, if the person has parcels in the pack station
	 */
	public boolean hasParcelInPackstation() {
		return !this.inPackstation.isEmpty();
	}
	
	/**
	 * Adds the given parcel to the person's parcel orders.
	 *
	 * @param parcel the parcel
	 */
	public void order(Parcel parcel) {
		this.ordered.add(parcel);
	}
	
	/**
	 * Cancels the given parcel order.
	 *
	 * @param parcel the parcel order to be canceled
	 */
	public void cancelOrder(Parcel parcel) {
		this.ordered.remove(parcel);
	}

	/**
	 * Receive the given parcel.
	 *
	 * @param parcel the parcel
	 */
	public void receive(Parcel parcel) {
		this.received.add(parcel);
		parcel.getDistributionCenter().getDelivered().add(parcel);
	}
	
	/**
	 * Notify the person about a new parcel in pack station.
	 *
	 * @param parcel the parcel
	 */
	public void notifyParcelInPackStation(Parcel parcel) {
		this.inPackstation.add(parcel);
		System.out.println("Person " + this.getOid() + " is notified about parcel " + parcel.getOId() + " being added to the pack station.");
	}

	/**
	 * Pick up parcels from the pack station.
	 */
	public void pickUpParcels() {
		System.out.println("Person " + this.getOid() + " picks up their parcels at a pack station. Parcel ids: " + this.inPackstation.stream().map(p -> "" + p.getOId()).collect(Collectors.joining(",")) );
		
		this.received.addAll(this.inPackstation);
		this.inPackstation.clear();
	}
	
	@Override
	public SimulationOptions options() {
		return this.person.options();
	}
	
	@Override
	public boolean isCarDriver() {
		return this.person.isCarDriver();
	}

	@Override
	public boolean isCarPassenger() {
		return this.person.isCarPassenger();
	}

	@Override
	public void useCar(Car car, Time time) {
		this.person.useCar(car, time);
	}

	@Override
	public Car whichCar() {
		return this.person.whichCar();
	}

	@Override
	public Car releaseCar(Time time) {
		return this.person.releaseCar(time);
	}

	@Override
	public Car parkCar(Zone zone, Location location, Time time) {
		return this.person.parkCar(zone, location, time);
	}

	@Override
	public boolean hasParkedCar() {
		return this.person.hasParkedCar();
	}

	@Override
	public void takeCarFromParking() {
		this.person.takeCarFromParking();
	}

	@Override
	public boolean isCycling() {
		return this.person.isCycling();
	}

	@Override
	public void useBike(Bike bike, Time time) {
		this.person.useBike(bike, time);
	}

	@Override
	public Bike whichBike() {
		return this.person.whichBike();
	}

	@Override
	public Bike releaseBike(Time time) {
		return this.person.releaseBike(time);
	}

	@Override
	public Bike parkBike(Zone zone, Location location, Time time) {
		return this.person.parkBike(zone, location, time);
	}

	@Override
	public boolean hasParkedBike() {
		return this.person.hasParkedBike();
	}

	@Override
	public void takeBikeFromParking() {
		this.person.takeBikeFromParking();
	}

	@Override
	public boolean isMobilityProviderCustomer(String company) {
		return this.person.isMobilityProviderCustomer(company);
	}

	@Override
	public Map<String, Boolean> mobilityProviderCustomership() {
		return this.person.mobilityProviderCustomership();
	}

	@Override
	public Household household() {
		return this.person.household();
	}

	@Override
	public boolean hasPersonalCar() {
		return this.person.hasPersonalCar();
	}

	@Override
	public int getOid() {
		return this.person.getOid();
	}

	@Override
	public ActivityScheduleWithState activitySchedule() {
		return this.person.activitySchedule();
	}

	@Override
	public Zone nextFixedActivityZone(ActivityIfc activity) {
		return this.person.nextFixedActivityZone(activity);
	}

	@Override
	public void useCarAsPassenger(Car car) {
		this.person.useCarAsPassenger(car);
	}

	@Override
	public void leaveCar() {
		this.person.leaveCar();
	}

	@Override
	public void assignPersonalCar(PrivateCar personalCar) {
		this.person.assignPersonalCar(personalCar);
	}

	@Override
	public boolean hasPersonalCarAssigned() {
		return this.person.hasPersonalCarAssigned();
	}

	@Override
	public PrivateCar personalCar() {
		return this.person.personalCar();
	}

	@Override
	public Optional<TourBasedActivityPattern> tourBasedActivityPattern() {
		return this.person.tourBasedActivityPattern();
	}

	@Override
	public ActivityIfc nextHomeActivity() {
		return this.person.nextHomeActivity();
	}

	@Override
	public void initSchedule(TourFactory tourFactory,ActivityPeriodFixer fixer, ActivityStartAndDurationRandomizer activityDurationRandomizer,
			List<Time> days) {
		this.person.initSchedule(tourFactory, fixer, activityDurationRandomizer, days);
	}

	@Override
	public String forLogging(ImpedanceIfc impedance) {
		return this.person.forLogging(impedance);
	}

	@Override
	public void startActivity(Time currentDate, ActivityIfc activity, Trip precedingTrip,
			ReschedulingStrategy rescheduling) {
		this.person.startActivity(currentDate, activity, precedingTrip, rescheduling);
	}

	@Override
	public PersonAttributes attributes() {
		return this.person.attributes();
	}

	@Override
	public boolean hasAccessToCar() {
		return this.person.hasAccessToCar();
	}

	@Override
	public boolean hasBike() {
		return this.person.hasBike();
	}

	@Override
	public boolean hasCommuterTicket() {
		return this.person.hasCommuterTicket();
	}

	@Override
	public boolean hasDrivingLicense() {
		return this.person.hasDrivingLicense();
	}

	@Override
	public PersonId getId() {
		return this.person.getId();
	}

	@Override
	public Gender gender() {
		return this.person.gender();
	}

	@Override
	public Employment employment() {
		return this.person.employment();
	}

	@Override
	public int age() {
		return this.person.age();
	}

	@Override
	public Graduation graduation() {
		return this.person.graduation();
	}

	@Override
	public int getIncome() {
		return this.person.getIncome();
	}

	@Override
	public Zone homeZone() {
		return this.person.homeZone();
	}

	@Override
	public boolean hasFixedZoneFor(ActivityType activityType) {
		return this.person.hasFixedZoneFor(activityType);
	}

	@Override
	public Zone fixedZoneFor(ActivityType activityType) {
		return this.person.fixedZoneFor(activityType);
	}

	@Override
	public boolean hasFixedActivityZone() {
		return this.person.hasFixedActivityZone();
	}

	@Override
	public Zone fixedActivityZone() {
		return this.person.fixedActivityZone();
	}

	@Override
	public Location fixedDestinationFor(ActivityType activityType) {
		return this.person.fixedDestinationFor(activityType);
	}

	@Override
	public boolean isFemale() {
		return this.person.isFemale();
	}

	@Override
	public boolean isMale() {
		return this.person.isMale();
	}

	@Override
	public Stream<FixedDestination> getFixedDestinations() {
		return this.person.getFixedDestinations();
	}

	@Override
	public ModeChoicePreferences modeChoicePrefsSurvey() {
		return this.person.modeChoicePrefsSurvey();
	}

	@Override
	public ModeChoicePreferences modeChoicePreferences() {
		return this.person.modeChoicePreferences();
	}

	@Override
	public ModeChoicePreferences travelTimeSensitivity() {
		return this.person.travelTimeSensitivity();
	}

	@Override
	public void arriveAtStop(EventQueue queue, Time currentDate) {
		this.person.arriveAtStop(queue, currentDate);
	}

	@Override
	public void vehicleArriving(EventQueue queue, Vehicle vehicle, Time currentDate) {
		this.person.vehicleArriving(queue, vehicle, currentDate);
	}

	@Override
	public ActivityIfc currentActivity() {
		return this.person.currentActivity();
	}

	@Override
	public Trip currentTrip() {
		return this.person.currentTrip();
	}

	@Override
	public void currentTrip(Trip trip) {
		this.person.currentTrip(trip);
	}

	@Override
	public boolean hasNextActivity() {
		return this.person.hasNextActivity();
	}

	@Override
	public ActivityIfc nextActivity() {
		return this.person.nextActivity();
	}

	@Override
	public boolean nextActivityStartsAfterSimulationEnd() {
		return this.person.nextActivityStartsAfterSimulationEnd();
	}

	@Override
	public boolean rideOfferAccepted() {
		return this.person.rideOfferAccepted();
	}

	@Override
	public void acceptRideOffer() {
		this.person.acceptRideOffer();
	}

	@Override
	public PersonState currentState() {
		return this.person.currentState();
	}

	@Override
	public void startTrip(ImpedanceIfc impedance, Trip trip, Time date) {
		this.person.startTrip(impedance, trip, date);
	}

	@Override
	public void endTrip(ImpedanceIfc impedance, ReschedulingStrategy rescheduling, Time currentDate) {
		this.person.endTrip(impedance, rescheduling, currentDate);
	}

	@Override
	public void startActivity(Trip previousTrip, ReschedulingStrategy rescheduling, Time currentDate) {
		this.person.startActivity(previousTrip, rescheduling, currentDate);
	}

	@Override
	public void endActivity() {
		this.person.endActivity();
	}

	@Override
	public void selectDestinationAndMode(DestinationChoiceModel targetSelector,
			TourBasedModeChoiceModel modeChoiceModel, ImpedanceIfc impedance, boolean passengerAsOption) {
		this.person.selectDestinationAndMode(targetSelector, modeChoiceModel, impedance, passengerAsOption);
	}

	@Override
	public void offerRide(Time currentDate, SimulationOptions options) {
		this.person.offerRide(currentDate, options);
	}

	@Override
	public void revokeRideOffer(RideSharingOffers rideOffers, Trip trip, Time currentTime) {
		this.person.revokeRideOffer(rideOffers, trip, currentTime);
	}

	@Override
	public boolean findAndAcceptBestMatchingRideOffer(RideSharingOffers rideOffers, Trip trip,
			int max_difference_minutes) {
		return this.person.findAndAcceptBestMatchingRideOffer(rideOffers, trip, max_difference_minutes);
	}

	@Override
	public void selectRoute(ZoneBasedRouteChoice routeChoice, Trip trip, Time date) {
		this.person.selectRoute(routeChoice, trip, date);
	}

	@Override
	public void prepareTrip(ImpedanceIfc impedance, Trip trip, Time time) {
		this.person.prepareTrip(impedance, trip, time);
	}

	@Override
	public void notify(EventQueue queue, DemandSimulationEventIfc event, Time currentDate) {
		this.person.notify(queue, event, currentDate);
	}

	@Override
	public void enterFirstStop(Time time) {
		this.person.enterFirstStop(time);
	}

	@Override
	public boolean isPublicTransportVehicleAvailable(Time time) {
		return this.person.isPublicTransportVehicleAvailable(time);
	}

	@Override
	public boolean hasPlaceInPublicTransportVehicle() {
		return this.person.hasPlaceInPublicTransportVehicle();
	}

	@Override
	public void changeToNewTrip(Time time) {
		this.person.changeToNewTrip(time);
	}

	@Override
	public void boardPublicTransportVehicle(Time time) {
		this.person.boardPublicTransportVehicle(time);
	}

	@Override
	public void getOffPublicTransportVehicle(Time time) {
		this.person.getOffPublicTransportVehicle(time);
	}

	@Override
	public boolean hasArrivedAtNextActivity() {
		return this.person.hasArrivedAtNextActivity();
	}

	@Override
	public void wait(Time currentTime) {
		this.person.wait(currentTime);
	}

	@Override
	public boolean hasPublicTransportVehicleDeparted(Time time) {
		return this.person.hasPublicTransportVehicleDeparted(time);
	}

	/**
	 * Gets the next random number.
	 *
	 * @return the next random number
	 */
	public double getNextRandom() {
		return this.random.nextDouble();
	}

}
