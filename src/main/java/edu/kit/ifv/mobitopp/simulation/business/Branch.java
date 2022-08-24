package edu.kit.ifv.mobitopp.simulation.business;

import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.HOSPITAL;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.HOTEL;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.INDUSTRIAL;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.LEISURE;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.OFFICE;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.RESTAURANT;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.SCHOOL_UNIVERSITY;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.STORE;
import static edu.kit.ifv.mobitopp.simulation.business.BuildingType.WAREHOUSE;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.ADMINISTRATION;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.HOSPITALITY;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.INDUSTRY;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.SERVICE;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.RETAIL;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum Branch {// According to NACE Rev. 2

	OTHER(0) {
		@Override
		public Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(BuildingType.OTHER);
		}
	},

	A(1) {
		@Override
		public Sector getSector() {
			return INDUSTRY;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},

	B(2) {//
		@Override
		public Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {

			return List.of(BuildingType.OTHER);
		}
	},

	C(3) {
		@Override
		public Sector getSector() {
			return INDUSTRY;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE, INDUSTRIAL);
		}
	},

	D(4) {
		@Override
		public Sector getSector() {
			return INDUSTRY;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},

	E(5) {
		@Override
		public Sector getSector() {
			return INDUSTRY;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},

	F(6) {
		@Override
		public Sector getSector() {
			return INDUSTRY;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL);
		}
	},

	G(7) {
		@Override
		public Sector getSector() {
			return RETAIL;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE);
		}
	},

	H(8) {
		@Override
		public Sector getSector() {
			return INDUSTRY;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(INDUSTRIAL, WAREHOUSE);
		}
	},

	I(9) {
		@Override
		public Sector getSector() {
			return HOSPITALITY;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(RESTAURANT, HOTEL);
		}
	},

	J(10) {
		@Override
		public Sector getSector() {
			return SERVICE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {

			return List.of(BuildingType.OTHER);
		}
	},

	K(11) {
		@Override
		public Sector getSector() {
			return SERVICE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},

	L(12) {
		@Override
		public Sector getSector() {
			return SERVICE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},

	M(13) {
		@Override
		public Sector getSector() {
			return SERVICE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},

	N(14) {
		@Override
		public Sector getSector() {
			return SERVICE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},

	O(15) {
		@Override
		public Sector getSector() {
			return ADMINISTRATION;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(OFFICE);
		}
	},

	P(16) {
		@Override
		public Sector getSector() {
			return ADMINISTRATION;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(SCHOOL_UNIVERSITY);
		}
	},

	Q(17) {
		@Override
		public Sector getSector() {
			return SERVICE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(STORE, OFFICE, HOSPITAL);
		}
	},

	R(18) {
		@Override
		public Sector getSector() {
			return Sector.LEISURE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(LEISURE);
		}
	},

	S(19) {
		@Override
		public Sector getSector() {
			return SERVICE;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(LEISURE, OFFICE);
		}
	},

	T(20) {//
		@Override
		public Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
			return List.of(BuildingType.OTHER);
		}
	},

	U(21) {//
		@Override
		public Sector getSector() {
			return Sector.OTHER;
		}

		@Override
		public Collection<BuildingType> getBuildingTypes() {
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

	public abstract Sector getSector();

	public abstract Collection<BuildingType> getBuildingTypes();

	public static Branch fromInt(int number) {
		return Arrays.stream(Branch.values()).filter(p -> p.asInt() == number).findFirst().orElseGet(() -> {
			throw new IllegalArgumentException("Cannot parse " + number + " as trade!");
		});
	}
}
