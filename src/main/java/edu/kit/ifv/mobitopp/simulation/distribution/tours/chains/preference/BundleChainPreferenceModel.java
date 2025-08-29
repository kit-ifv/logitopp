package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BundleChainPreferenceModel implements PreferredChainModel {

    private final Map<Integer, TransportPreferences> bundleChains = new HashMap<>();
    private final PreferredChainModel delegate;

    public BundleChainPreferenceModel(PreferredChainModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public TransportPreferences selectPreference(IParcel parcel, Collection<TimedTransportChain> choiceSet, double randomNumber, Time time) {
        if (bundleChains.containsKey(parcel.getBundleId())) {
            return bundleChains.get(parcel.getBundleId()).copy(parcel);
        } else {
            TransportPreferences preferences = delegate.selectPreference(parcel, choiceSet, randomNumber, time);
            bundleChains.put(parcel.getBundleId(), preferences);
            return preferences;
        }
    }

    @Override
    public Collection<TimedTransportChain> filterChoiceSet(IParcel parcel, Collection<TimedTransportChain> choiceSet) {
        return delegate.filterChoiceSet(parcel, choiceSet);
    }
}
