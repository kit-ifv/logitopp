package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

import java.util.List;
import java.util.Map;

public interface TransportPreferences {
    public TimedTransportChain selectNewPreference();

    public void removeOption(TimedTransportChain chain);

    public List<TimedTransportChain> options();

    public int getChoiceId();

    public IParcel getParcel();

    public Map<TimedTransportChain, Double> getProbabilities();

    public TimedTransportChain getSelected();

    public default TransportPreferences copy(IParcel newParcel) {
        return new CopyBundleTransportPreference(this, newParcel);
    }
}
