package edu.kit.ifv.mobitopp.simulation.business;

import static edu.kit.ifv.mobitopp.simulation.business.Sector.INDUSTRY;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.OTHER;
import static edu.kit.ifv.mobitopp.simulation.business.Sector.SERVICE;

import java.util.Arrays;

public enum Branch {

	A(1) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}
	},
	
	B(2) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}
	},
	
	C(3) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}
	},
	
	D(4) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}
	},
	
	E(5) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}
	},
	
	F(6) {
		@Override
		Sector getSector() {
			return INDUSTRY;
		}
	},
	
	
	G(7) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	H(8) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	I(9) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	J(10) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	K(11) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	L(12) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	M(13) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	N(14) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	O(15) {
		@Override
		Sector getSector() {
			return OTHER;
		}
	},
	
	P(16) {
		@Override
		Sector getSector() {
			return OTHER;
		}
	},
	
	Q(17) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	R(18) {
		@Override
		Sector getSector() {
			return OTHER;
		}
	},
	
	S(19) {
		@Override
		Sector getSector() {
			return SERVICE;
		}
	},
	
	T(20) {
		@Override
		Sector getSector() {
			return OTHER;
		}
	},
	
	U(21) {
		@Override
		Sector getSector() {
			return OTHER;
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
	
	public static Branch fromInt(int number) {
		return Arrays.stream(Branch.values())
					 .filter(p -> p.asInt() == number)
					 .findFirst()
					 .orElseGet(() -> {
						 throw new IllegalArgumentException("Cannot parse " + number + " as trade!");
					 });
	}
}
