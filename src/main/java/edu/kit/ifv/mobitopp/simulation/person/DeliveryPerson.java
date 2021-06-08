package edu.kit.ifv.mobitopp.simulation.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

/**
 * The Class DeliveryPerson decorates {@link SimulationPerson} by adding the
 * functionality of loading, delivering and unloading {@link PrivateParcel}s.
 */
public class DeliveryPerson implements SimulationPerson {

	private final SimulationPerson person;
	@Getter
	private final Collection<IParcel> currentTour;
	private final Random random;
	@Getter
	private final DeliveryEfficiencyProfile efficiency;
	@Getter
	private final DistributionCenter distributionCenter;

	/**
	 * Instantiates a new {@link DeliveryPerson} with the given
	 * {@link DeliveryEfficiencyProfile}.
	 *
	 * @param person             the person
	 * @param distributionCenter the distribution center
	 * @param efficiency         the efficiency
	 * @param seed               the seed
	 */
	public DeliveryPerson(SimulationPerson person, DistributionCenter distributionCenter,
			DeliveryEfficiencyProfile efficiency, long seed) {
		this.person = person;
		this.currentTour = new ArrayList<IParcel>();
		this.random = new Random(seed);
		this.efficiency = efficiency;
		this.distributionCenter = distributionCenter;
	}

	/**
	 * Loads the given parcels and updates their state (now on delivery).
	 *
	 * @param parcels     the parcels
	 * @param currentTime the current time
	 */
	public void load(Collection<IParcel> parcels, Time currentTime) {
		this.currentTour.addAll(parcels);
		parcels.forEach(p -> p.loaded(currentTime, this));
	}

	/**
	 * Unloads parcels from the current tour and updates their state (now
	 * undefined).
	 *
	 * @param currentTime the current time
	 * @return the unloaded parcels
	 */
	public Collection<IParcel> unload(Time currentTime) {
		Collection<IParcel> parcels = new ArrayList<>(currentTour);
		parcels.forEach(p -> p.unloaded(currentTime, this));
		this.currentTour.clear();
		return parcels;
	}

	/**
	 * Removes the delivered parcel from the current tour.
	 *
	 * @param parcel the parcel
	 */
	public void delivered(IParcel parcel) {
		this.currentTour.remove(parcel);
	}

	/**
	 * Gets the next random number.
	 *
	 * @return the next random number
	 */
	public double getNextRandom() {
		return this.random.nextDouble();
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
	public void initSchedule(TourFactory tourFactory, ActivityPeriodFixer fixer,
			ActivityStartAndDurationRandomizer activityDurationRandomizer, List<Time> days) {

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
	public SimulationOptions options() {
		return this.person.options();
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

}
