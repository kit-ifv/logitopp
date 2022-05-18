package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.populationsynthesis.neighborhood.NeighborhoodRelationship;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.time.Time;



/**
 * The Class DummyDeliveryPolicy is an exemplary implementation of the ParcelDeliveryPolicy interface.
 */
public class NeighborhoodDeliveryPolicy implements ParcelDeliveryPolicy<PrivateParcel> {

	private final ParcelDeliveryPolicy<PrivateParcel> policy;
	private final NeighborhoodRelationship neighborhood;
	private final DeliveryResults results;

	public NeighborhoodDeliveryPolicy(ParcelDeliveryPolicy<PrivateParcel> policy, NeighborhoodRelationship neighborhood, DeliveryResults results) {
		this.policy = policy;
		this.neighborhood = neighborhood;
		this.results = results;
	}
	
	public NeighborhoodDeliveryPolicy(ParcelDeliveryPolicy<PrivateParcel> policy, NeighborhoodRelationship neighborhood) {
		this(policy, neighborhood, null);
	}
	
	/**
	 * Checks whether the given parcel can be delivered.
	 * Home delivery: check if the recipient, another household member or a neighbor is at home.
	 * (If not uses the wrapped policy, to check whether the parcel can be delivered anyway).
	 * For other delivery types uses wrapped policy.
	 * 
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel can be delivered
	 */
	@Override
	public Optional<RecipientType> canDeliver(PrivateParcel parcel, Time currentTime) {

		Optional<RecipientType> canDeliver = policy.canDeliver(parcel, currentTime);
		
		if (canDeliver.isEmpty() && parcel.getDestinationType().equals(HOME)) {
			Collection<Household> neighbors = neighborhood.getNeighborsOf(parcel.getPerson().household());
			System.out.println("Found " + neighbors.size() + " neighbors. incl? " + (neighbors.contains(parcel.getPerson().household())));
			
			
			boolean anybodyHome = neighbors
									.stream()
									.flatMap(Household::persons)
									.filter(p -> {
										try {
											return p.currentActivity() != null;
										} catch (NullPointerException e) {
											return false;
										}
									})
									
									.anyMatch(p -> p.currentActivity().activityType().equals(ActivityType.HOME));
			if (results != null) {
				this.logNeighbors(parcel, currentTime, neighbors, anybodyHome);
			}
			
			
			return optionalRecipient(anybodyHome, RecipientType.NEIGHBOR);
		}
 
		
		return canDeliver;
	}

	private void logNeighbors(PrivateParcel parcel, Time currentTime, Collection<Household> neighbors, boolean anybodyHome) {
		Set<Household> checked = new HashSet<>();
		neighbors
			.stream()
			.flatMap(Household::persons)
			.anyMatch(p -> {checked.add(p.household()); return p.currentActivity().activityType().equals(ActivityType.HOME);});
		
		this.results.logNeighborDelivery(parcel.getOId(), parcel.getZone(), currentTime, anybodyHome, neighbors.size(), checked.size());
	}
	
	
	/**
	 * Update the parcel delivery.
	 * Uses the wrapped policy.
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel order was updated
	 */
	@Override
	public boolean updateParcelDelivery(PrivateParcel parcel, Time currentTime) {
		return policy.updateParcelDelivery(parcel, currentTime);
	}
	
	private Optional<RecipientType> optionalRecipient(boolean check, RecipientType recipientType) {
		if (check) {
			return Optional.of(recipientType);
		} else {
			return Optional.empty();
		}
	}

}
