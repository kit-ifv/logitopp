package edu.kit.ifv.mobitopp.simulation.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

public abstract class DeliveryAgent {

	@Getter	private final Collection<ParcelActivity> currentTour;
	@Getter private final Collection<IParcel> retour;
	@Getter private final Collection<IParcel> pickups;
	private final Random random;
	@Getter	private final DistributionCenter distributionCenter;

	public DeliveryAgent(DistributionCenter distributionCenter, long seed) {
		this.currentTour = new ArrayList<ParcelActivity>();
		this.retour = new ArrayList<>();
		this.pickups = new ArrayList<>();
		this.random = new Random(seed);
		this.distributionCenter = distributionCenter;
	}

	/**
	 * Loads the given parcels and updates their state (now on delivery).
	 *
	 * @param deliveries  the deliveries
	 * @param currentTime the current time
	 */
	public void planDeliveries(Collection<ParcelActivity> deliveries, Time currentTime) {
		this.currentTour.addAll(deliveries);
	}

	/**
	 * Unloads parcels from the current tour and updates their state (now
	 * undefined).
	 *
	 * @param currentTime the current time
	 * @return the unloaded parcels
	 */
	public Collection<IParcel> unload(Time currentTime) {
		retour.forEach(p -> p.unloaded(currentTime, this));
		pickups.forEach(p -> p.tryDelivery(currentTime, this));
		
		List<IParcel> parcels = new ArrayList<>();
		parcels.addAll(retour); //only retour parcels are returned
		
		this.retour.clear();
		this.pickups.clear();
		this.currentTour.clear();
		
		return parcels;
	}
	
	public void retour(IParcel parcel) {
		this.retour.add(parcel);
	}
	
	public void pickup(IParcel parcel) {
		this.pickups.add(parcel);
	}

	/**
	 * Removes the delivered parcel from the current tour.
	 *
	 * @param delivery the delivery
	 */
	public void remove(ParcelActivity delivery) {
		this.currentTour.remove(delivery);
	}

	/**
	 * Gets the next random number.
	 *
	 * @return the next random number
	 */
	public double getNextRandom() {
		return this.random.nextDouble();
	}

	public abstract int getOid();
	
	public ParcelPolicyProvider getPolicyProvider() {
		return this.distributionCenter.getPolicyProvider();
	}
}
