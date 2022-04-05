package edu.kit.ifv.mobitopp.simulation.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;

public class BusinessBuilder {

//	private static int ID_CNT;
	
	private int id;
	private String name;
	private Branch branch;
	private int employees;
	private double area;
	private Map<AreaFunction,Double> functionShares;
	private Random random;
	private ZoneAndLocation location;
	private Fleet fleet;
	private ParcelPolicyProvider policyProvider;
	
	public BusinessBuilder(long seed) {
		this.functionShares = new HashMap<>();
		this.random = new Random(seed);
//		this.id = ID_CNT++;
	}
	
	public BusinessBuilder id(int id) {
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
	
	public BusinessBuilder with(int employees) {
		this.employees = employees;
		return this;
	}
	
	public BusinessBuilder with(double area) {
		this.area = area;
		return this;
	}
	
	public BusinessBuilder with(double share, AreaFunction function) {
		this.functionShares.put(function, share);
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
		
	public BusinessBuilder at(ZoneAndLocation location) {
		this.location = location;
		return this;
	}
	
	
	private void validate() {

	}
	
	public Business build() {
		validate();
		return new Business(id, name, location, branch, employees, area, functionShares, fleet, random, policyProvider);
	}
	

	
	
}
