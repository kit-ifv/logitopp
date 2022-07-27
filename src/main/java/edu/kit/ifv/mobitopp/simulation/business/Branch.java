package edu.kit.ifv.mobitopp.simulation.business;

import static edu.kit.ifv.mobitopp.simulation.business.Sector.INDUSTRY;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.OTHER;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.SERVICE;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.TRADE;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.HOSPITALITY;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.ADMINISTRATION;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.OTHER;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.STORE;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.LEISURE;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.RESTAURANT;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.HOTEL;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.OFFICE;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.INDUSTRIAL;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.HOSPITAL;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.WAREHOUSE;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.SCHOOL_UNIVERSITY;

public enum Branch {//According to NACE Rev. 2
	
	OTHER(0) {
		@Override
		Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(BuildingType.OTHER);
		}
	},
	
	A(1) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	B(2) {//
		@Override
		Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {

			return List.of(BuildingType.OTHER);
		}
	},
	
	C(3) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE, INDUSTRIAL);
		}
	},
	
	D(4) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	E(5) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	F(6) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	
	G(7) {
		@Override
		Sector getSector() {
			return TRADE;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE);
		}
	},
	
	H(8) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(WAREHOUSE);
		}
	},
	
	I(9) {
		@Override
		Sector getSector() {
			return HOSPITALITY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(RESTAURANT, HOTEL);
		}
	},
	
	J(10) {
		@Override
		Sector getSector() {
			return SERVICE;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {

			return List.of(BuildingType.OTHER);
		}
	},
	
	K(11) {
		@Override
		Sector getSector() {
			return SERVICE;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	L(12) {
		@Override
		Sector getSector() {
			return SERVICE;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	M(13) {
		@Override
		Sector getSector() {
			return SERVICE;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	N(14) {
		@Override
		Sector getSector() {
			return SERVICE;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	O(15) {
		@Override
		Sector getSector() {
			return ADMINISTRATION;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	P(16) {
		@Override
		Sector getSector() {
			return ADMINISTRATION;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(SCHOOL_UNIVERSITY);
		}
	},
	
	Q(17) {
		@Override
		Sector getSector() {
			return ADMINISTRATION;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE, OFFICE, HOSPITAL);
		}
	},
	
	R(18) {
		@Override
		Sector getSector() {
			return HOSPITALITY;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(LEISURE);
		}
	},
	
	S(19) {
		@Override
		Sector getSector() {
			return SERVICE;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(LEISURE, OFFICE);
		}
	},
	
	T(20) {//
		@Override
		Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(BuildingType.OTHER);
		}
	},
	
	U(21) {//
		@Override
		Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(BuildingType.OTHER);
		}
	};	
	
	private final int number;
	
	private Branch(int number) {
		this.number = number;
	}
	
	public int asInt() {
		return this.number;
	}
	
	public String asString() {
		return this.name();
	}
	
	abstract Sector getSector();
	abstract Collection<BuildingType> getBuildingTypes();
	
	public static Branch fromInt(int number) {
		return Arrays.stream(Branch.values())
					 .filter(p -> p.asInt() == number)
					 .findFirst()
					 .orElseGet(() -> {
						 throw new IllegalArgumentException("Cannot parse " + number + " as trade!");
					 });
	}
}
