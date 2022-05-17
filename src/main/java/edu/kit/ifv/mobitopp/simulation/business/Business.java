package edu.kit.ifv.mobitopp.simulation.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import lombok.Getter;

@Getter
public class Business implements ParcelAgent {

	private final int id;
	private final String name;
	private final Branch branch;
	private final int employees;
	private final double area;
	private final ZoneAndLocation location;
	private final Map<AreaFunction, Double> functionShares;
	private final Random random;
	private final Fleet fleet;
	
	private final ParcelPolicyProvider policyProvider;

	private int plannedProductionQuantitiy;
	private int coveredProductionQuantitiy;
	private final Collection<IParcel> currentParcels;
	private final Collection<IParcel> delivered;
	

	public Business(int id, String name, ZoneAndLocation location, Branch branch,
			int employees, double area, Map<AreaFunction, Double> functionShares,
			Fleet fleet, Random random, ParcelPolicyProvider policyProvider) {
		this.id = id;
		this.name = name;
		this.branch = branch;
		this.employees = employees;
		this.area = area;
		this.location = location;
		this.functionShares = functionShares;
		this.random = random;
		this.fleet = fleet;
		this.policyProvider = policyProvider;
		this.currentParcels = new ArrayList<>();
		this.delivered = new ArrayList<>();
	}


	
	
	
	@Override
	public void setPlannedProductionQuantity(int quantity) {
		this.plannedProductionQuantitiy = quantity;
	}

	@Override
	public int getRemainingProductionQuantity() {

		return this.plannedProductionQuantitiy - this.coveredProductionQuantitiy;
	}

	@Override
	public void addActualProductionQuantity(int quantity) {
		this.coveredProductionQuantitiy += quantity;
	}

	@Override
	public int getPlannedProductionQuantity() {
		return this.plannedProductionQuantitiy;
	}

	public ZoneAndLocation location() {
		return this.getLocation();
	}

	public double getNextRandom() {
		return this.random.nextDouble();
	}





	@Override
	public void removeParcel(IParcel parcel) {
		this.currentParcels.remove(parcel);
	}

	@Override
	public void addParcel(IParcel parcel) {
		this.currentParcels.add(parcel);		
	}

	@Override
	public void addDelivered(PrivateParcel parcel) {
		this.delivered.add(parcel);
	}
	
	@Override
	public ParcelPolicyProvider getPolicyProvider() {
		return this.policyProvider;
	}

	
	@Override
	public String toString() {
		return this.name;
	}
}
