package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionCenter;

/**
 * The Interface DeliveryEmploymentStrategy.
 * Decides if a person is a potential employee for a {@link DistributionCenter}.
 */
public interface DeliveryEmploymentStrategy {
	
	/**
	 * Checks if the given {@link Person} is potential employee
	 * for a {@link DistributionCenter}.
	 *
	 * @param person the person
	 * @return true, if the person is a potential employee
	 */
	public boolean isPotentialEmployee(Person person);

}
