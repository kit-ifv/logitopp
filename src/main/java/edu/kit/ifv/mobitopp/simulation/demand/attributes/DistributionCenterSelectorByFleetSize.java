package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

//This step evaluates the serviceProvider attribute, which should be set prior to this step!!!
//Can be evaluated during simulation
public class DistributionCenterSelectorByFleetSize<A extends ParcelAgent, P extends ParcelBuilder<A>>
	implements ParcelDemandModelStep<A, P, DistributionCenter> {

	@Override
	public boolean determinePreSimulation(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		return false;
	}
	
	@Override
	public DistributionCenter select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {

		Collection<DistributionCenter> distributionCenters = parcel.getServiceProvider()
																   .getValue()
																   .getDistributionCenters();
		
		if (distributionCenters.size() == 1) {
			return distributionCenters.iterator().next();
		}
		
		Map<DistributionCenter, Double> shares = distributionCenters.stream()
																	.filter(d -> d.getVehicleType() == VehicleType.TRUCK) //TODO: external distribution center identified by truck
																	.collect(toMap(d -> d, d -> (double) d.getTotalVehicles()));
		if (shares.isEmpty()) {
			shares = distributionCenters.stream()
					.collect(toMap(d -> d, d -> (double) d.getTotalVehicles()));
		}
		
		 return new ShareBasedSelector<>(shares).select(randomNumber);

	}
	
	
	
	

}
