package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.RideSharingOffers;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulationPersonDecorator extends PersonDecorator implements SimulationPerson {

	private static final long serialVersionUID = 533306482717926568L;
	private final SimulationPerson person;

	public SimulationPersonDecorator(SimulationPerson person) {
		super(person);
		this.person = person;
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
	public boolean hasNextActivity() {
		return this.person.hasNextActivity();
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
