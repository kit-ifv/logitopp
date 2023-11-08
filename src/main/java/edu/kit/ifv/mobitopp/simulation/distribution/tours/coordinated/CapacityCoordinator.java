package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChainFactory;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class CapacityCoordinator {
	private final static int BOX_CAPACITY = 50; //TODO make configurable
	
	private final Collection<DistributionCenter> distributionCenters;
	private final PreferredChainModel chainPreference;
	private final TimedTransportChainFactory timedChainFactory;
	private final TimeTable timeTable;
	private final Random random;
	
	private Time nextAssignment = Time.start;	
	private ChainAssignment currentAssignment;
	
	public CapacityCoordinator(
			Collection<DistributionCenter> distributionCenters , 
			PreferredChainModel chainPreference,
			TimedTransportChainFactory timedChainFactory, TimeTable timeTable
		) {
		this.distributionCenters = distributionCenters;
		this.chainPreference = chainPreference;
		this.timedChainFactory = timedChainFactory;
		this.timeTable = timeTable;
		this.random = new Random(42);//TODO seed in constructor
	}
	
	public ChainAssignment getAssignment(Time currentTime) {
		
		if (currentTime.isAfterOrEqualTo(nextAssignment)) {
			currentAssignment = coordinateCapacityAssignment(currentTime);
			nextAssignment = nextAssignment.plusDays(1);
		}
		
		return currentAssignment;
		
	}

	private ChainAssignment coordinateCapacityAssignment(Time date) {
		Time earliestDeparture = date.startOfDay().plusHours(7).plusMinutes(30); //TODO fix hard coded departure time

		ChainAssignment assignment = new ChainAssignment(distributionCenters);
		
		List<Request> requests = new ArrayList<>();
		for (DistributionCenter dc : distributionCenters) {
			
			List<TimedTransportChain> chains = getChainsFor(dc, earliestDeparture);
			assignment.assign(dc, getNonTramChains(chains, earliestDeparture));
			
			List<TransportPreferences> preferences = getPreferences(dc, chains); //TODO store preferences
			assignment.regiter(dc, preferences);
			
			requests.addAll(getRequests(dc, preferences));	
		}
		
		assignment.assignAll(assignRequests(requests));

		return assignment;
	}

	private List<TimedTransportChain> getNonTramChains(List<TimedTransportChain> chains, Time earliestDeparture) {
		List<TimedTransportChain> result = new ArrayList<>();
		
		chains.stream().filter(c -> !c.uses(VehicleType.TRAM)).forEach(chain -> {
			int count = chain.maxNumberOfTripsOnDayAfter(timeTable, earliestDeparture);
			for (int i=0; i < count; i++) {
				result.add(chain.copy());
			}
		});
		
		return result;
	}
		
	private Map<DistributionCenter, List<TimedTransportChain>> assignRequests(List<Request> requests) {
		Map<DistributionCenter, List<TimedTransportChain>> tramChains = new LinkedHashMap<>();
		
		List<Request> openRequests = new ArrayList<>(requests);
		while (!openRequests.isEmpty()) {
			Request req = openRequests.remove(random.nextInt(openRequests.size()));
			DistributionCenter dc = req.dc;
			
			if (req.chain.canBookConnections()) {
				TimedTransportChain bookedChain = req.chain.copy();
				bookedChain.bookConnections();
				
				if (!tramChains.containsKey(dc)) {
					tramChains.put(dc, new ArrayList<>());
				}
				
				tramChains.get(dc).add(bookedChain);
			}
			
		}
		
		
		return tramChains;
	}

	class Request {
		final TimedTransportChain chain;
		final DistributionCenter dc;
		
		Request(DistributionCenter dc, TimedTransportChain chain) {
			this.chain = chain;
			this.dc = dc;
		}
		
	}
	
	private List<Request> getRequests(DistributionCenter dc, List<TransportPreferences> preferences) {
		Map<TimedTransportChain, List<TimedTransportChain>> demandPerChain = preferences.stream()
				   .map(p -> p.getSelected())
				   .collect(Collectors.groupingBy(c -> c));
		
		List<Request> requests = new ArrayList<>();
		
		for (TimedTransportChain chain : demandPerChain.keySet()) {
			int demand = (int) Math.ceil(demandPerChain.get(chain).size() / BOX_CAPACITY);
			for (int i = 0; i < demand; i++) {
				requests.add(new Request(dc, chain));
			}			
		}
		
		return requests;
	}
	
	private List<TransportPreferences> getPreferences(DistributionCenter dc, List<TimedTransportChain> chains) {
		//only in delivery direction, returning boxes are handled first come first serve	
		
		List<TransportPreferences> preferences = new ArrayList<>();
	
		for (IParcel parcel: dc.getStorage().getParcels()) {
			preferences.add(
				chainPreference.selectPreference(parcel, chains, dc.getRandom().nextDouble())
			); 
		}
		
		return preferences;
	}

	private List<TimedTransportChain> getChainsFor(DistributionCenter dc, Time earliestDeparture) {
		return dc.getRegionalStructure()
				.getDeliveryChains()
				.stream()
				.map(c -> timedChainFactory.create(c, earliestDeparture))
				.collect(toList());
	}

}
