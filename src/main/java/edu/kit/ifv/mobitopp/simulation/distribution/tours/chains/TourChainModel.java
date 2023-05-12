package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.TourPlanningStrategy;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class TourChainModel implements TourPlanningStrategy {
	
//	private final ImpedanceIfc impedanceIfc;
//	private final DeliveryDurationModel duration;
	private final DistributionCenter distributionCenter;
	private final TransportChainPreferenceModel preferenceModel;
	private final DeliveryResults results;
	private final Random random;
	
	private final TourPlanningStrategy delegate;
	
	public TourChainModel(TourPlanningStrategy delegate, DeliveryResults results, TransportChainPreferenceModel preferenceModel, DistributionCenter distributionCenter, long seed) {
//		ImpedanceIfc impedanceIfc, DeliveryDurationModel duration,
		
//		this.impedanceIfc = impedanceIfc;
//		this.duration = duration;
		
		this.distributionCenter = distributionCenter;
		this.preferenceModel = preferenceModel;
		this.results = results;
		this.random = new Random(seed);
		
		this.delegate = delegate;
	}

	@Override
	public List<PlannedDeliveryTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet, Time time) {
		
		Collection<TransportChain> deliveryChains = distributionCenter.getRegionalStructure().getDeliveryChains();
		Collection<TransportChain> pickupChains = distributionCenter.getRegionalStructure().getPickUpChains();
		
		for (IParcel parcel: deliveries) {
			preferenceModel.selectPreference(parcel, deliveryChains, time, random.nextDouble()); //TODO build transport chain departures with fixed departure times
		}
		
		for (IParcel parcel: pickUps) {
			preferenceModel.selectPreference(parcel, pickupChains, time, random.nextDouble());
		}
		
		return delegate.planTours(deliveries, pickUps, fleet, time);
	}

	@Override
	public boolean shouldReplanTours(DistributionCenter center, Time time) {
		return delegate.shouldReplanTours(center, time);
	}
	
}
