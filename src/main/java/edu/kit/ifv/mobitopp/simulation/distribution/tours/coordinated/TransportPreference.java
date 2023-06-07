package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;
import lombok.Getter;

public class TransportPreference {
	
	@Getter private final IParcel parcel;
	private final Map<TimedTransportChain, Double> probabilities;
	@Getter private TimedTransportChain preference;
	
	private final Random random;
	
	public TransportPreference(IParcel parcel, Map<TimedTransportChain, Double> probaility, long seed) {
		this.parcel = parcel;
		this.probabilities = new LinkedHashMap<>(probaility);
		this.random = new Random(seed);
	}
	
	public TimedTransportChain selectNewPreference() {
		this.preference = new DiscreteRandomVariable<>(this.probabilities).realization(random.nextDouble());
		
		return this.preference;
	}
	
	public void removeOption(TimedTransportChain chain) {
		if (this.probabilities.containsKey(chain)) {
			
			this.probabilities.remove(chain);
			this.selectNewPreference();
		}
	}

}
