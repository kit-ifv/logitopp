package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;

@Getter
public class CEPServiceProvider {//TODO ParcelAgent?
	private static int idCnt = 0;
	
	private final int id;
	private final String name;
	private final Collection<DistributionCenter> distributionCenters;
	
	public CEPServiceProvider(String name) {
		this.id = idCnt++;
		this.name = name;
		this.distributionCenters = new ArrayList<>();
	}
	
	public void addDistributionCenter(DistributionCenter distributionCenter) {
		this.distributionCenters.add(distributionCenter);
	}

	public int getNumVehicles() {
		return this.distributionCenters.stream().mapToInt(DistributionCenter::getNumVehicles).sum();
	}

	public int currentShippingDemand() {
		return this.distributionCenters.stream().mapToInt(DistributionCenter::currentShippingDemand).sum();
	}

	public int currentDeliveryDemand() {
		return this.distributionCenters.stream().mapToInt(DistributionCenter::currentDeliveryDemand).sum();
	}
	
	@Override
		public String toString() {
			return this.name;
		}

}
