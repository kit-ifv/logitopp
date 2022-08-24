package edu.kit.ifv.mobitopp.simulation;

public class DemandQuantity {

	private int production;
	private int consumption;
	
	public int getConsumption() {
		return consumption;
	}
	
	public void setConsumption(int consumption) {
		this.consumption = consumption;
	}
	
	public void addConsumption(int increment) {
		this.consumption += increment;
	}

	public int getProduction() {
		return production;
	}

	public void setProduction(int production) {
		this.production = production;
	}
	
	public void addProduction(int increment) {
		this.production += increment;
	}
	
}
