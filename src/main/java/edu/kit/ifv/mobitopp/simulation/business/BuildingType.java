package edu.kit.ifv.mobitopp.simulation.business;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static edu.kit.ifv.mobitopp.simulation.business.Branch.A;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.B;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.C;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.D;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.E;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.F;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.G;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.H;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.I;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.J;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.K;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.L;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.M;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.N;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.O;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.P;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.Q;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.R;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.S;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.T;
import static edu.kit.ifv.mobitopp.simulation.business.Branch.U;

public enum BuildingType {
	
	OTHER(0) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(B, J, T, U);
		}
	},
	
	STORE(1) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(G, C, Q);
		}
	},
	
	LEISURE(2) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(R, S);
		}
	},
	
	RESTAURANT(3) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(I);
		}
	},
	
	HOTEL(4) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(I);
		}
	},	
	
	OFFICE(5) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(K, L, M, N, O, P, Q, S);
		}
	},
	
	INDUSTRIAL(6) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(A, C, D, E, F, H);
		}
	},
	
	HOSPITAL(7) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(Q);
		}
	},
	WAREHOUSE(8) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(H);
		}
	},
	
	SCHOOL_UNIVERSITY(9) {
		@Override
		Collection<Branch> getBranches() {
			return List.of(P);
		}
	};
	
	private final int number;
	
	private BuildingType(int number) {
		this.number = number;
	}
	
	public int asInt() {
		return this.number;
	}
	
	public String asString() {
		return this.name();
	}
	
	abstract Collection<Branch> getBranches();
		
	public static BuildingType fromInt(int number) {
		return Arrays.stream(BuildingType.values())
					 .filter(p -> p.asInt() == number)
					 .findFirst()
					 .orElseGet(() -> {
						 throw new IllegalArgumentException("Cannot parse " + number + " as building type!");
					 });
	}
}
