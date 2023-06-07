package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.BIKE;
import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.TRAM;
import static edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize.EXTRA_LARGE;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class LogitChainPreferenceModel implements PreferedChainModel {
	
	private final TimeTable timeTable;
	private final ImpedanceIfc impedance;
	
	public LogitChainPreferenceModel(TimeTable timeTable, ImpedanceIfc impedance) {
		this.timeTable = timeTable;
		this.impedance = impedance;
	}

	@Override
	public TransportPreference selectPreference(IParcel parcel, Collection<TransportChain> choiceSet, Time currentTime,
			double randomNumber) {
		return null;
	}

	@Override
	public Collection<TimedTransportChain> filterChoiceSet(IParcel parcel, Collection<TimedTransportChain> choiceSet, Time time) {
		return choiceSet.stream()
				.filter(chain -> canBeUsed(chain, parcel, time))
				.collect(toList());
	}
	
	private boolean canBeUsed(TransportChain chain, IParcel parcel, Time time) {
		return chain.canTransport(parcel) && !(isXL(parcel) && chain.uses(BIKE));
	}

	private boolean isXL(IParcel parcel) {
		return parcel.getShipmentSize().equals(EXTRA_LARGE);
	}

}
