package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.*;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference.PreferredChainModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference.TransportPreferences;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

import static java.util.stream.Collectors.*;

public class CapacityCoordinator {
	private final static double BOX_CAPACITY = 57.0d; //TODO make configurable
	
	private final Collection<DistributionCenter> distributionCenters;
	private final PreferredChainModel chainPreference;
	private final TimedTransportChainFactory timedChainFactory;

	private final ConnectionChainsFactory connectionChainsFactory;

	private final TimeTable timeTable;
	private final Random random;
	
	private Time nextAssignment = Time.start;	
	private ChainAssignment currentAssignment;
	
	public CapacityCoordinator(
			Collection<DistributionCenter> distributionCenters ,
			PreferredChainModel chainPreference,
			TimedTransportChainFactory timedChainFactory,
			ConnectionChainsFactory connectionChainsFactory,
			TimeTable timeTable
		) {
		this.distributionCenters = distributionCenters;
		this.chainPreference = chainPreference;
		this.timedChainFactory = timedChainFactory;
		this.connectionChainsFactory = connectionChainsFactory;
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
		System.out.println("Coordinate capacity of trams: " + date);

		Time earliestTramDeparture = date.startOfDay().plusHours(4); //TODO fix hard coded departure time
		Time earliestNonTramDeparture = date.startOfDay().plusHours(7);

		ChainAssignment assignment = new ChainAssignment(distributionCenters);
		
		//First collect delivery chains, and delivery chain preferences
		//for tram chains, assign slots to distribution centers
		List<Request> requests = new ArrayList<>();
		Map<TimedTransportChain, List<TimedTransportChain>> tramChainCopiesOfDay = new LinkedHashMap<>();
		for (DistributionCenter dc : distributionCenters) {
			System.out.println("Collect chains and requests for " + dc.getName() + "[" + dc.getId() + "]");
			
			List<TimedTransportChain> chains = getChainsFor(dc, earliestTramDeparture);
			System.out.println(chains.stream().map(TimedTransportChain::forLogging).collect(joining("\n")));


			List<TimedTransportChain> nonTramChains = getNonTramChains(chains, earliestNonTramDeparture);
			assignment.assign(dc, nonTramChains);

			List<TransportPreferences> preferences = getPreferences(dc, chains, date);
			assignment.register(dc, preferences);

			List<Request> dcRequests = getRequests(dc, preferences);
			requests.addAll(dcRequests);

			System.out.println("    - requests: " + dcRequests.size());

			System.out.println("    - non-tram chains: " + nonTramChains.size());


			Map<TimedTransportChain, List<TimedTransportChain>> dcTramChains = getTramChainCopiesOfDay(earliestTramDeparture, chains);
			tramChainCopiesOfDay.putAll(dcTramChains);

			System.out.println("    - tram chains: " + dcTramChains.size());
			System.out.println("    - tram chains copies over time: " + dcTramChains.values().stream().mapToInt(List::size).sum());
		}


		assignment.assignAll(assignRequests(requests, tramChainCopiesOfDay));
		
		//for assigned delivery chains, obtain reversed direction
		// (here first come, first served applies)
		// also determine preferences for pickup requests
//		for (DistributionCenter dc : distributionCenters) {
//			System.out.println("Collect return chains for: " + dc.getName() + "[" + dc.getId() + "]");
//
//			Map<TimedTransportChain, TimedTransportChain> returnChainsWithCorrespondence = getReturnChainsFor(dc, assignment);
//			assignment.register(dc, getReturnPreferences(dc, returnChainsWithCorrespondence, date));
//
//			System.out.println("    - return chains: " + returnChainsWithCorrespondence.size());
//		}

		for (DistributionCenter dc: distributionCenters) {
			System.out.println("    - " + dc  + ": ");
			System.out.println("        - all chains: " + assignment.getChains(dc).size());
			System.out.println("        - bike chains: " + assignment.getChains(dc).stream().filter(c -> c.lastMileVehicle().equals(VehicleType.BIKE)).count());
			System.out.println("        - truck chains: " + assignment.getChains(dc).stream().filter(c -> c.lastMileVehicle().equals(VehicleType.TRUCK)).count());
		}
		
		return assignment;
	}

	//create reverse of each available deliver chain ( mapped to the delivery chain itself)
	private Map<TimedTransportChain, TimedTransportChain> getReturnChainsFor(DistributionCenter dc, ChainAssignment assignment) {
		Map<TimedTransportChain, TimedTransportChain> result = new LinkedHashMap<>();
		
		assignment.getChains(dc).stream().distinct().forEach(deliveryChain -> {
			
			int tourDurHours = (deliveryChain.lastMileVehicle().equals(VehicleType.BIKE)) ? 2 : 8 ; //TODO assumed tour duration??
			Time returnDeparture = deliveryChain.getArrival(deliveryChain.last()).plusHours(tourDurHours);
			
			createReturnChain(deliveryChain, returnDeparture).ifPresent( returnChain -> 
				result.put(
					returnChain,
					deliveryChain
				)
			);
			
		});
		
		return result;
	}

	public Optional<TimedTransportChain> createReturnChain(TimedTransportChain deliveryChain, Time returnDeparture) {
		TransportChain oppositeDirection = deliveryChain.getOppositeDirection();
		return timedChainFactory.create(oppositeDirection, returnDeparture);
	}

	private List<TimedTransportChain> getNonTramChains(List<TimedTransportChain> chains, Time earliestDeparture) {
		List<TimedTransportChain> result = new ArrayList<>();
		
		chains.stream().filter(c -> !c.uses(VehicleType.TRAM)).forEach(chain -> {
			int count = chain.maxNumberOfTripsOnDayAfter(timeTable, earliestDeparture);
			for (int i=0; i < count; i++) {
				timedChainFactory.create(chain, earliestDeparture).map(c -> c.copyWithId(chain.getId())).ifPresent(result::add);
			}
		});
		
		return result;
	}

	private Map<DistributionCenter, List<TimedTransportChain>> assignRequests(
			List<Request> requests,
			Map<TimedTransportChain, List<TimedTransportChain>> tramChainCopiesOfDay
	) {
		// pick random request until all requests have been handled
		Map<DistributionCenter, List<TimedTransportChain>> tramChains = new LinkedHashMap<>();



//		Map<Integer, List<Request>> requestsByChainId = requests.stream().collect(groupingBy(r -> r.chain.getId()));
//
//
//		if (requests.stream().anyMatch(c -> !tramChainCopiesOfDay.containsKey(c.chain))) {
//			System.out.println("    - !! some requested chains are not available!!!");
//		}
//
//
//		for (TimedTransportChain chain: tramChainCopiesOfDay.keySet()) {
//
//
//			List<Request> openRequests = new ArrayList<>(requestsByChainId.getOrDefault(chain.getId(), List.of()));
//			List<TimedTransportChain> availableChains = tramChainCopiesOfDay.getOrDefault(chain, List.of())
//																			.stream()
//																			.filter(TimedTransportChain::canBookConnections)
//																			.sorted(Comparator.comparing(c -> c.getFirstMileDeparture().toMinutes()))
//																			.collect(Collectors.toList());
//
//			System.out.println("    - handle requests for " + chain.forLogging());
//			System.out.println("        - requests " + openRequests.size());
//			System.out.println("        - available chains over day " + availableChains.size());
//			String indent = "            ";
//			System.out.println(indent + availableChains.stream().map(TimedTransportChain::forLogging).collect(joining("\n" + indent)));
//
//			while (!openRequests.isEmpty() && !availableChains.isEmpty()) {
//				Request req = openRequests.remove(random.nextInt(openRequests.size()));
//
//				TimedTransportChain selected = availableChains.get(0);
//
//				if (selected.canBookConnections()) {
//					if (!tramChains.containsKey(req.dc)) {
//						tramChains.put(req.dc, new ArrayList<>());
//					}
//
//					selected.bookConnections();
//
//					tramChains.get(req.dc).add(selected.copyWithId(req.chain.getId()));
//				}
//
//				if (!selected.canBookConnections()) {
//					availableChains.remove(selected);
//				}
//
//
//			}
//
//			System.out.println("        - remaining requests " + openRequests.size());
//			System.out.println("        - remaining chains " + availableChains.size());
//
//		}

		Map<TimedTransportChain, List<TimedTransportChain>> avaliableChains = new LinkedHashMap<>();

		requests.forEach(r -> {
			if (!avaliableChains.containsKey(r.chain)) {
				avaliableChains.put(r.chain, new ArrayList<>(
					tramChainCopiesOfDay.getOrDefault(r.chain, List.of()).stream().filter(TimedTransportChain::canBookConnections).sorted(Comparator.comparing(c -> c.getFirstMileDeparture().toMinutes())).collect(toList())
				));
			}
		});

		System.out.println("Requested chains " + avaliableChains.size());
		if (requests.stream().anyMatch(c -> !tramChainCopiesOfDay.containsKey(c.chain))) {
			System.out.println("    - !! some requested chains are not available!!!");
		}

		List<Request> openRequests = new ArrayList<>(requests);
		while (!openRequests.isEmpty()) {
			Request req = openRequests.remove(random.nextInt(openRequests.size()));
			DistributionCenter dc = req.dc;

			List<TimedTransportChain> availableRequestChains = avaliableChains.get(req.chain);

			TimedTransportChain selected = null;
			while(
				!availableRequestChains.isEmpty()
			) {
				TimedTransportChain toCheck = availableRequestChains.get(0);

				if (toCheck.canBookConnections()) {
					selected = toCheck;
					break;
				}

				availableRequestChains.remove(toCheck);
			}


			if (selected != null) {

				selected.bookConnections();
				if(!selected.canBookConnections()) {
					availableRequestChains.remove(selected);
				}

				if (!tramChains.containsKey(dc)) {
					tramChains.put(dc, new ArrayList<>());
				}
				tramChains.get(dc).add(selected.copyWithId(req.chain.getId()));
				System.out.println("    - dc " + dc + " receives " + selected.forLogging());
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
				   .map(TransportPreferences::getSelected)
				   .filter(c -> c.uses(VehicleType.TRAM))
				   .collect(Collectors.groupingBy(c -> c));
		
		List<Request> requests = new ArrayList<>();
		
		for (TimedTransportChain chain : demandPerChain.keySet()) {
			int demand = (int) Math.ceil(demandPerChain.get(chain).size()*2.0d / BOX_CAPACITY);
			for (int i = 0; i < demand; i++) {
				requests.add(new Request(dc, chain));
			}			
		}
		
		return requests;
	} //TODO requests for secondary choices
	
	private List<TransportPreferences> getPreferences(DistributionCenter dc, List<TimedTransportChain> chains, Time time) {
		//only in delivery direction, returning boxes are handled first come first serve
		//BUT: picked up parcels may also have preferences and hard rule based filter might apply (xxl not in bike)!!
		
		List<TransportPreferences> preferences = new ArrayList<>();
	
		for (IParcel parcel: dc.getStorage().getParcels()) {
			preferences.add(
				chainPreference.selectPreference(parcel, chains, dc.getRandom().nextDouble(), time)
			); 
		}
		
		return preferences;
	}
	
	private List<TransportPreferences> getReturnPreferences(DistributionCenter dc, Map<TimedTransportChain, TimedTransportChain> returnChainsWithCorrespondence, Time time) {
		List<TransportPreferences> preferences = new ArrayList<>();
		
		for (IParcel parcel: dc.getStorage().getRequests()) {
			
			//get preferred pickup chains for pickup parcel
			TransportPreferences returnPreferences = chainPreference.selectPreference(parcel, returnChainsWithCorrespondence.keySet(), dc.getRandom().nextDouble(), time);
			
			//then map probabilities to corresponding delivery chains
			Map<TimedTransportChain, Double> probabilities = returnChainsWithCorrespondence.keySet().stream().collect(Collectors.toMap(
					returnChainsWithCorrespondence::get,
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
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(toList());
	}

	private Map<TimedTransportChain, List<TimedTransportChain>> getTramChainCopiesOfDay(Time earliestDeparture, List<TimedTransportChain> chains) {
		List<TimedTransportChain> tramChains =
					chains.stream()
					.filter(c -> c.uses(VehicleType.TRAM))
					.collect(toList());

		Map<TimedTransportChain, List<TimedTransportChain>> timedTramChains = new LinkedHashMap<>();

		for (TimedTransportChain chain : tramChains) {

			timedTramChains.put(chain, new ArrayList<>(
				connectionChainsFactory.create(chain, earliestDeparture)
			));

//			Optional<List<Connection>> connections = chain.legsOfType(VehicleType.TRAM)
//					.stream()
//					.findFirst()
//					.map(l ->
//							timeTable.getConnectionsOnDay(l.getFirst(), l.getSecond(), earliestDeparture).collect(toList())
//					);
//
//			if (connections.isPresent()) {
//				for (Connection conn : connections.get()) {
//					Time dep = conn.getDeparture().minusMinutes(30);
//					dep = (dep.isBefore(Time.start)) ? Time.start : dep;
//					timedChainFactory.create(chain, dep).ifPresent(
//						timedTramChains.get(chain)::add
//					);
//				}
//			}

		}

		return timedTramChains;
	}

}
