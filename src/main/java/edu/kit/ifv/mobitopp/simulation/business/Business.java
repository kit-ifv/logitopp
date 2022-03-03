package edu.kit.ifv.mobitopp.simulation.business;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

public class Business implements ParcelAgent {

	private int plannedProductionQuantitiy;
	private int coveredProductionQuantitiy;
	
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

	
	
	
	public ZoneAndLocation location() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double getNextRandom() {
		return 42.0;
	}



}
