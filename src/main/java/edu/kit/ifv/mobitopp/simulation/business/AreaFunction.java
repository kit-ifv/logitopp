package edu.kit.ifv.mobitopp.simulation.business;

import java.util.Arrays;

public enum AreaFunction {
	
	SHOPPING(1),
	OFFICE(2),
	OTHER(3);
	
	private final int number;
	
	private AreaFunction(int number) {
		this.number = number;
	}
	
	public int asInt() {
		return this.number;
	}
	
	public String asString() {
		return this.name();
	}
		
	public static AreaFunction fromInt(int number) {
		return Arrays.stream(AreaFunction.values())
					 .filter(p -> p.asInt() == number)
					 .findFirst()
					 .orElseGet(() -> {
						 throw new IllegalArgumentException("Cannot parse " + number + " as area function!");
					 });
	}

}
