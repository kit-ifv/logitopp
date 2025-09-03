package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toList;


public class DeterministicChainSelector implements PreferredChainModel {

    private static int choiceCount = 0;
    @Override
    public TransportPreferences selectPreference(IParcel parcel, Collection<TimedTransportChain> choiceSet, double randomNumber, Time time) {
        Collection<TimedTransportChain> filterChoiceSet = filterChoiceSet(parcel, choiceSet);

        if (filterChoiceSet.isEmpty()) {
            throw new IllegalArgumentException("No transport chains in filtered choice set for parcel (" +parcel.getOId() + "): " + parcel);
        }

        return new TransportPreferenceProbabilities(
                choiceCount++,
                parcel,
                Map.of(filterChoiceSet.iterator().next(), 1.0),
                (long) (randomNumber * Long.MAX_VALUE)
            );
    }

    @Override
    public Collection<TimedTransportChain> filterChoiceSet(IParcel parcel, Collection<TimedTransportChain> choiceSet) {
        return choiceSet.stream()
                .filter(chain -> canBeUsed(chain, parcel))
                .collect(toList());
    }

    private boolean canBeUsed(TransportChain chain, IParcel parcel) {
        return chain.canTransport(parcel)
                && (chain.last().getOrganization().equals("ALL")
                        || chain.last().getOrganization().equals(chain.first().getOrganization()
                )
        );
    }
}
