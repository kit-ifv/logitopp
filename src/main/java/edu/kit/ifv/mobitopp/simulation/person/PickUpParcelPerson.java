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
import edu.kit.ifv.mobitopp.simulation.parcels.NullParcelProducer;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.model.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.model.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class PickUpParcelPerson decorates a {@link SimulationPerson}
 * by adding the functionalyity of ordering, receiving and picking up {@link PrivateParcel}s.
 */
public class PickUpParcelPerson extends SimulationPersonDecorator implements NullParcelProducer {

	private static final long serialVersionUID = 8116806011994669529L;
	private SimulationPerson person;
	private Collection<IParcel> ordered;
	private Collection<IParcel> received;
	private Collection<IParcel> inPackstation;
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
		super(person);
		
		this.person = person;
		
		this.ordered = new ArrayList<IParcel>();
		this.received = new ArrayList<IParcel>();
		this.inPackstation = new ArrayList<IParcel>();
		
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
	public void order(PrivateParcel parcel) {
		this.ordered.add(parcel);
	}
	
	/**
	 * Cancels the given parcel order.
	 *
	 * @param parcel the parcel order to be canceled
	 */
	public void cancelOrder(PrivateParcel parcel) {
		this.ordered.remove(parcel);
	}

	/**
	 * Receive the given parcel.
	 *
	 * @param parcel the parcel
	 */
	public void receive(PrivateParcel parcel) {
		this.received.add(parcel);
		parcel.getDistributionCenter().getDelivered().add(parcel);
	}
	
	/**
	 * Notify the person about a new parcel in pack station.
	 *
	 * @param parcel the parcel
	 */
	public void notifyParcelInPackStation(PrivateParcel parcel) {
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
	
	
	
	/**
	 * Gets the next random number.
	 *
	 * @return the next random number
	 */
	public double getNextRandom() {
		return this.random.nextDouble();
	}


}
