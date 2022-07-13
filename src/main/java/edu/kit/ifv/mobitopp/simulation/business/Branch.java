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
		Collection<Sector> getSectors() {
			return List.of(Sector.OTHER);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(BuildingType.OTHER);
		}
	},
	
	A(1) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(INDUSTRY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	B(2) {//
		@Override
		Collection<Sector> getSectors() {
			return List.of(Sector.OTHER);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {

			return List.of(BuildingType.OTHER);
		}
	},
	
	C(3) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(TRADE, INDUSTRY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE, INDUSTRIAL);
		}
	},
	
	D(4) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(INDUSTRY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	E(5) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(INDUSTRY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	F(6) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(INDUSTRY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},
	
	
	G(7) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(TRADE);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE);
		}
	},
	
	H(8) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(INDUSTRY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(WAREHOUSE);
		}
	},
	
	I(9) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(HOSPITALITY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(RESTAURANT, HOTEL);
		}
	},
	
	J(10) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(SERVICE);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {

			return List.of(BuildingType.OTHER);
		}
	},
	
	K(11) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(SERVICE);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	L(12) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(SERVICE);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	M(13) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(SERVICE);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	N(14) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(SERVICE);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	O(15) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(ADMINISTRATION);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},
	
	P(16) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(SERVICE, ADMINISTRATION);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(SCHOOL_UNIVERSITY);
		}
	},
	
	Q(17) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(ADMINISTRATION);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE, OFFICE, HOSPITAL);
		}
	},
	
	R(18) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(HOSPITALITY);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(LEISURE);
		}
	},
	
	S(19) {
		@Override
		Collection<Sector> getSectors() {
			return List.of(SERVICE, ADMINISTRATION);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(LEISURE, OFFICE);
		}
	},
	
	T(20) {//
		@Override
		Collection<Sector> getSectors() {
			return List.of(Sector.OTHER);
		}

		@Override
		Collection<BuildingType> getBuildingTypes() {
			return List.of(BuildingType.OTHER);
		}
	},
	
	U(21) {//
		@Override
		Collection<Sector> getSectors() {
			return List.of(Sector.OTHER);
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
	
	abstract Collection<Sector> getSectors();
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
