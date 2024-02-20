package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChainFactory;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference.PreferredChainModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference.TransportPreferences;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class CapacityCoordinator {
	private final static double BOX_CAPACITY = 50.0d; //TODO make configurable
	
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
		
		//First collect delivery chains, and delivery chain preferences
		//for tram chains, assign slots to distribution centers
		List<Request> requests = new ArrayList<>();
		for (DistributionCenter dc : distributionCenters) {
			
			List<TimedTransportChain> chains = getChainsFor(dc, earliestDeparture);
			assignment.assign(dc, getNonTramChains(chains, earliestDeparture));
			
			List<TransportPreferences> preferences = getPreferences(dc, chains);
			assignment.register(dc, preferences);
			
			requests.addAll(getRequests(dc, preferences));	
		}
		assignment.assignAll(assignRequests(requests));
		
		//for assigned delivery chains, obtain reversed direction
		// (here first come first serve applies)
		// also determine preferences for pickup requests
		for (DistributionCenter dc : distributionCenters) {
			Map<TimedTransportChain, TimedTransportChain> returnChainsWithCorrespondence = getReturnChainsFor(dc, assignment);
			
			assignment.register(dc, getReturnPreferences(dc, returnChainsWithCorrespondence));
		}
		
		return assignment;
	}

	//create reverse of each available deliver chain ( mapped to the delivery chain itself)
	private Map<TimedTransportChain, TimedTransportChain> getReturnChainsFor(DistributionCenter dc, ChainAssignment assignment) {
		Map<TimedTransportChain, TimedTransportChain> result = new LinkedHashMap<>();
		
		assignment.getChains(dc).stream().distinct().forEach(deliveryChain -> {
			
			int tourDurHours = (deliveryChain.lastMileVehicle().equals(VehicleType.BIKE)) ? 2 : 8 ; //TODO assumed tour duration??
			Time returnDeparture = deliveryChain.getArrival(deliveryChain.last()).plusHours(tourDurHours);
			
			result.put(
				createReturnChain(deliveryChain, returnDeparture),
				deliveryChain
			);
			
		});
		
		return result;
	}

	public TimedTransportChain createReturnChain(TimedTransportChain deliveryChain, Time returnDeparture) {
		TransportChain oppositeDirection = deliveryChain.getOppositeDirection();
		System.out.println("reverse :" + deliveryChain.getHubs());
		System.out.println(" ==> " + oppositeDirection.getHubs());
		return timedChainFactory.create(oppositeDirection, returnDeparture);
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
		// pick random request until all requests have been handled
		// 
		Map<DistributionCenter, List<TimedTransportChain>> tramChains = new LinkedHashMap<>();
		
		List<Request> openRequests = new ArrayList<>(requests);
		while (!openRequests.isEmpty()) {
			Request req = openRequests.remove(random.nextInt(openRequests.size()));
			DistributionCenter dc = req.dc;
			
			if (req.chain.canBookConnections()) {
				TimedTransportChain bookedChain = req.chain.copy(); //Why copy here?
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
			int demand = (int) Math.ceil(demandPerChain.get(chain).size()*1.0d / BOX_CAPACITY);
			for (int i = 0; i < demand; i++) {
				requests.add(new Request(dc, chain));
			}			
		}
		
		return requests;
	} //TODO requests for secondary choices
	
	private List<TransportPreferences> getPreferences(DistributionCenter dc, List<TimedTransportChain> chains) {
		//only in delivery direction, returning boxes are handled first come first serve
		//BUT: picked up parcels may also have preferences and hard rule based filter might apply (xxl not in bike)!!
		
		List<TransportPreferences> preferences = new ArrayList<>();
	
		for (IParcel parcel: dc.getStorage().getParcels()) {
			preferences.add(
				chainPreference.selectPreference(parcel, chains, dc.getRandom().nextDouble())
			); 
		}
		
		return preferences;
	}
	
	private List<TransportPreferences> getReturnPreferences(DistributionCenter dc, Map<TimedTransportChain, TimedTransportChain> returnChainsWithCorrespondence) {
		List<TransportPreferences> preferences = new ArrayList<>();
		
		for (IParcel parcel: dc.getStorage().getRequests()) {
			
			//get preferred pickup chains for pickup parcel
			TransportPreferences returnPreferences = chainPreference.selectPreference(parcel, returnChainsWithCorrespondence.keySet(), dc.getRandom().nextDouble());
			
			//then map probabilities to corresponding delivery chains
			Map<TimedTransportChain, Double> probabilities = returnChainsWithCorrespondence.keySet().stream().collect(Collectors.toMap(
				c -> returnChainsWithCorrespondence.get(c),
				c -> returnPreferences.getProbabilities().getOrDefault(c, 0.0)
			));

			preferences.add(
				new TransportPreferences(returnPreferences.getChoiceId(), parcel, probabilities, dc.getRandom().nextLong())
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
