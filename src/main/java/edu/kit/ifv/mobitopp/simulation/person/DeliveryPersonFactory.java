package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.SimulationOptionsCustomization;
import edu.kit.ifv.mobitopp.simulation.SimulationPersonFactory;
import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public class DeliveryPersonFactory {

	private Collection<DistributionCenter> distributionCenters;
	private SimulationPersonFactory defaultFactory;
	private DeliveryEfficiencyModel efficiencyModel;
	private DeliveryEmploymentStrategy employmentStrategy;

	public DeliveryPersonFactory(Collection<DistributionCenter> distributionCenters,
		SimulationPersonFactory defaultFactory, DeliveryEfficiencyModel efficiencyModel,
		DeliveryEmploymentStrategy employmentStrategy) {
		
		this.distributionCenters = distributionCenters;
		this.defaultFactory = defaultFactory;
		this.efficiencyModel = efficiencyModel;
		this.employmentStrategy = employmentStrategy;
	}

	public PickUpParcelPerson create(Person person, EventQueue queue,
		SimulationOptions simulationOptions, List<Time> simulationDays, Set<Mode> modesInSimulation,
		TourFactory tourFactory, TripFactory tripFactory, PersonState initialState,
		PublicTransportBehaviour boarder, long seed, PersonListener listener, DeliveryResults results) {

		List<DistributionCenter> nonSaturatedDistributionCenters = new ArrayList<DistributionCenter>();
		if (person.hasFixedZoneFor(WORK)) {
			Zone work = person.fixedZoneFor(ActivityType.WORK);
			nonSaturatedDistributionCenters = getNonSaturatedDistributionCenters(work);
		}

		if (nonSaturatedDistributionCenters.isEmpty()
			|| !employmentStrategy.isPotentialEmployee(person)) {// All distribution centers have
																	// enough employees
			return createPickUpParcelPerson(person, queue, simulationOptions, simulationDays,
				modesInSimulation, tourFactory, tripFactory, initialState, boarder, seed, listener);

		} else {
			DistributionCenter dc = nonSaturatedDistributionCenters.get(0);
			return createDeliveryPerson(person, dc, queue, simulationOptions, simulationDays,
				modesInSimulation, tourFactory, tripFactory, boarder, seed, listener, results);

		}

	}

	private List<DistributionCenter> getNonSaturatedDistributionCenters(Zone zone) {
		return this.distributionCenters
			.stream()
			.filter(dc -> dc.getZone().equals(zone))
			.filter(dc -> !dc.hasEnoughEmployees())
			.collect(Collectors.toList());
	}

	private PickUpParcelPerson createPickUpParcelPerson(Person person, EventQueue queue,
		SimulationOptions simulationOptions, List<Time> simulationDays, Set<Mode> modesInSimulation,
		TourFactory tourFactory, TripFactory tripFactory, PersonState initialState,
		PublicTransportBehaviour boarder, long seed, PersonListener listener) {

		SimulationOptionsCustomization customSimulationOptions = new SimulationOptionsCustomization(
			simulationOptions);
		SimulationPerson simPerson = this.defaultFactory
			.create(person, queue, customSimulationOptions, simulationDays, modesInSimulation,
				tourFactory, tripFactory, initialState, boarder, seed, listener);
		
		long pickUpParcelSeed = new Random(seed).nextLong();
		return new PickUpParcelPerson(simPerson, customSimulationOptions, pickUpParcelSeed);
	}

	private PickUpParcelPerson createDeliveryPerson(Person person,
		DistributionCenter distributionCenter, EventQueue queue,
		SimulationOptions simulationOptions, List<Time> simulationDays, Set<Mode> modesInSimulation,
		TourFactory tourFactory, TripFactory tripFactory, PublicTransportBehaviour boarder,
		long seed, PersonListener listener, DeliveryResults results) {

		SimulationOptionsCustomization customSimulationOptions = new SimulationOptionsCustomization(
			simulationOptions);
		PersonState initialState = PersonStateDelivery.UNINITIALIZED;
		SimulationPerson simPerson = this.defaultFactory
			.create(person, queue, customSimulationOptions, simulationDays, modesInSimulation,
				tourFactory, tripFactory, initialState, boarder, seed, listener);

		DeliveryEfficiencyProfile efficiency = this.efficiencyModel
			.select(distributionCenter, person);
		DeliveryPerson deliveryPerson = new DeliveryPerson(simPerson, distributionCenter, efficiency, seed);

		ReschedulingStrategy customRescheduling = new DeliveryReschedulingStrategy(
			distributionCenter, deliveryPerson, simulationOptions.rescheduling(), results);
		customSimulationOptions.customize(customRescheduling);

		// Add as employee of distributionCenter
		distributionCenter.addEmployee(deliveryPerson);
		System.out
			.println("New employee " + deliveryPerson.getOid() + " of "
				+ distributionCenter.getName() + ": " + distributionCenter.getEmployees().size()
				+ "/" + distributionCenter.getNumEmployees());
		results.logEmployee(deliveryPerson, distributionCenter);		

		long pickUpParcelSeed = new Random(seed).nextLong();
		return new PickUpParcelPerson(deliveryPerson, customSimulationOptions, pickUpParcelSeed);
	}

	public SimulationPersonFactory getDefaultFactory() {
		return this.defaultFactory;
	}

}
