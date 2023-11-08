package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;
import lombok.Getter;

public class TransportPreferences {
	
	@Getter private final int choiceId;
	@Getter private final IParcel parcel;
	private final Map<TimedTransportChain, Double> probabilities;
	@Getter private TimedTransportChain selected;
	
	private final Random random;
	
	public TransportPreferences(int choiceId, IParcel parcel, Map<TimedTransportChain, Double> probaility, long seed) {
		this.choiceId = choiceId;
		this.parcel = parcel;
		this.probabilities = new LinkedHashMap<>(probaility);
		this.random = new Random(seed);
	}
	
	public TimedTransportChain selectNewPreference() {
		this.selected = new DiscreteRandomVariable<>(this.probabilities).realization(random.nextDouble());
		
		return this.selected;
	}
	
	public void removeOption(TimedTransportChain chain) {
		if (this.probabilities.containsKey(chain)) {
			
			this.probabilities.remove(chain);
			this.selectNewPreference();
		}
	}
	
	public List<TimedTransportChain> options() {
		return probabilities.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue())).map(e -> e.getKey()).collect(Collectors.toList());
	}

}
