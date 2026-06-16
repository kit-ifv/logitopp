package edu.kit.ifv.mobitopp.simulation.person;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.*;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.demand.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import lombok.Getter;

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
	
	@Getter private final DemandQuantity demandQuantity;

	private final ZoneAndLocation packStation;
	
	/**
	 * Instantiates a new {@link PickUpParcelPerson}
	 * decorating the given {@link SimulationPerson}.
	 * 
	 * @param person the person
	 * @param seed the seed
	 */
	public PickUpParcelPerson(SimulationPerson person, ZoneAndLocation packStation, long seed) {
		super(person);
		
		this.person = person;
		this.packStation = packStation;

		this.ordered = new ArrayList<IParcel>();
		this.received = new ArrayList<IParcel>();
		this.inPackstation = new ArrayList<IParcel>();

		this.random = new Random(getOid() + seed);
		this.demandQuantity = new DemandQuantity();
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
		parcel.getProducer().addDelivered(parcel);
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



	@Override
	public ZoneAndLocation getZoneAndLocation() {
		return new ZoneAndLocation(person.homeZone(), person.household().homeLocation());
	}

	
	@Override
	public String carrierTag() {
		return "PrivatePerson";
	}



	@Override
	public boolean hasFixedZoneFor(ActivityType activityType) {
		if (activityType == ActivityType.PICK_UP_PARCEL) {
			return true;
		}
		return person.hasFixedZoneFor(activityType);
	}

	@Override
	public Zone fixedZoneFor(ActivityType activityType) {
		if (activityType == ActivityType.PICK_UP_PARCEL) {
			return packStation.zone();
		}
		return person.fixedZoneFor(activityType);
	}

	@Override
	public Zone nextFixedActivityZone(ActivityIfc activity) {
		if (activity.activityType() == ActivityType.PICK_UP_PARCEL) {
			return packStation.zone();
		}
		return person().nextFixedActivityZone(activity);
	}

	@Override
	public Location fixedDestinationFor(ActivityType activityType) {
		if (activityType == ActivityType.PICK_UP_PARCEL) {
			return packStation.location();
		}
		return person().fixedDestinationFor(activityType);
	}

	private List<FixedDestination> fixedDestinations = null;

	@Override
	public Stream<FixedDestination> getFixedDestinations() {

		if (fixedDestinations == null) {
			fixedDestinations = new ArrayList<>(person.getFixedDestinations().collect(Collectors.toList()));
			fixedDestinations.add(new FixedDestination(ActivityType.PICK_UP_PARCEL, packStation.zone(), packStation.location()));
		}

		return fixedDestinations.stream();
	}

}
