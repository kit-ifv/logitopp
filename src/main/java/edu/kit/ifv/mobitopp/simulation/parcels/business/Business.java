package edu.kit.ifv.mobitopp.simulation.parcels.business;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelAgent;

public class Business implements ParcelAgent {

	private int plannedProductionQuantitiy;
	private int coveredProductionQuantitiy;
	
	@Override
	public void setPlannedProductionQuantity(int quantity) {
		this.plannedProductionQuantitiy = quantity;
	}

	@Override
	public int getPlannedProductionQuantity() {
		
		return this.plannedProductionQuantitiy;
	}

	@Override
	public int getCurrentProductionQuantity() {

		return this.coveredProductionQuantitiy;
	}

	@Override
	public int getRemainingProductionQuantity() {
		
		return this.plannedProductionQuantitiy - this.coveredProductionQuantitiy;
	}
	
	public void addProducts(int number) {
		
		this.coveredProductionQuantitiy += number;
	}

	
	
	
	public ZoneAndLocation location() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double getNextRandom() {
		return 42.0;
	}

}
