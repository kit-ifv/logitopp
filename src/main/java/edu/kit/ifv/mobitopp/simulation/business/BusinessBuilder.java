package edu.kit.ifv.mobitopp.simulation.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.fleet.Fleet;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.Pair;

public class BusinessBuilder {
	
	private long id;
	private String name;
	
	private Branch branch;
	private BuildingType buildingType;
	
	private int employees;
	private double area;
	private Map<DayOfWeek, Pair<Time, Time>> openingHours;
	private ZoneAndLocation location;
	private Fleet fleet;
	
	private ParcelPolicyProvider policyProvider;
	private Random random;	
	
	public BusinessBuilder(long seed) {
		this.openingHours = new HashMap<>();
		this.random = new Random(seed);
	}
	
	public BusinessBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public BusinessBuilder called(String name) {
		this.name = name;
		return this;
	}
	
	public BusinessBuilder with(Branch branch) {
		this.branch = branch;
		return this;
	}
	
	public BusinessBuilder with(BuildingType buildingType) {
		this.buildingType = buildingType;
		return this;
	}
	
	public BusinessBuilder with(int employees) {
		this.employees = employees;
		return this;
	}
	
	public BusinessBuilder with(double area) {
		this.area = area;
		return this;
	}
	
	public BusinessBuilder openBetween(DayOfWeek day, Pair<Time, Time> interval) {
		this.openingHours.put(day, interval);
		return this;
	}
	
	public BusinessBuilder at(ZoneAndLocation location) {
		this.location = location;
		return this;
	}
	
	public BusinessBuilder with(Fleet fleet) {
		this.fleet = fleet;
		return this;
	}
	
	public BusinessBuilder with(ParcelPolicyProvider policyProvider) {
		this.policyProvider = policyProvider;
		return this;
	}
	
	private void validate() {

	}
	
	public Business build() {
		validate();
		return new Business(id, name, branch, buildingType, employees, area, openingHours, location, fleet, policyProvider, random);
	}
	

	
	
}
