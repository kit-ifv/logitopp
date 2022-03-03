package edu.kit.ifv.mobitopp.simulation;

public interface ParcelAgent {
	
	public void setPlannedProductionQuantity(int quantity);
	
	public void addActualProductionQuantity(int quantity);
	
	public int getRemainingProductionQuantity();

}
