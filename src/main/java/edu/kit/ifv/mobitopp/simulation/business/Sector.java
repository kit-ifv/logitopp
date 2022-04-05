package edu.kit.ifv.mobitopp.simulation.business;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum Sector {
	
	INDUSTRY(1) {
		@Override
		Collection<Branch> getContainedTrades() {
			return List.of(A, B, C, D, E, F);
		}
	},
	
	SERVICE(2) {
		@Override
		Collection<Branch> getContainedTrades() {
			return List.of(G, H, I, J, K, L, M, N, Q, S);
		}
	},
	
	OTHER(3) {
		@Override
		Collection<Branch> getContainedTrades() {
			return List.of(O, P, R, T, U);
		}
	};
	
	private final int number;
	
	private Sector(int number) {
		this.number = number;
	}
	
	public int asInt() {
		return this.number;
	}
	
	public String asString() {
		return this.name();
	}
	
	abstract Collection<Branch> getContainedTrades();
	
	public static Sector fromInt(int number) {
		return Arrays.stream(Sector.values())
					 .filter(p -> p.asInt() == number)
					 .findFirst()
					 .orElseGet(() -> {
						 throw new IllegalArgumentException("Cannot parse " + number + " as sector!");
					 });
	}
}
