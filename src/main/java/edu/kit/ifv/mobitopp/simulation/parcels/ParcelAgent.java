package edu.kit.ifv.mobitopp.simulation.parcels;

public interface ParcelAgent {
	
	public void setPlannedProductionQuantity(int quantity);
	
	public int getPlannedProductionQuantity();
	
	public int getCurrentProductionQuantity();
	
	public int getRemainingProductionQuantity();

}
