package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

import java.util.List;
import java.util.Map;

public class CopyBundleTransportPreference implements TransportPreferences {

    private final TransportPreferences delegate;
    private final IParcel parcel;

    public CopyBundleTransportPreference(TransportPreferences delegate, IParcel parcel) {
        this.delegate = delegate;
        this.parcel = parcel;
    }

    @Override
    public TimedTransportChain selectNewPreference() {
        return delegate.selectNewPreference();
    }

    @Override
    public void removeOption(TimedTransportChain chain) {
        delegate.removeOption(chain);
    }

    @Override
    public List<TimedTransportChain> options() {
        return delegate.options();
    }

    @Override
    public int getChoiceId() {
        return delegate.getChoiceId();
    }

    @Override
    public IParcel getParcel() {
        return parcel;
    }

    @Override
    public Map<TimedTransportChain, Double> getProbabilities() {
        return delegate.getProbabilities();
    }

    @Override
    public TimedTransportChain getSelected() {
        return delegate.getSelected();
    }
}
