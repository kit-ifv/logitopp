package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;

/**
 * The Class DefaultEmploymentStrategy is an exemplary implementation of {@link DeliveryEmploymentStrategy}.
 * Selects persons with a certain {@link Employment employment type}.
 */
public class DefaultEmploymentStrategy implements DeliveryEmploymentStrategy {
	
	private Employment employmentType;	
	
	/**
	 * Instantiates a new {@link DefaultEmploymentStrategy}
	 * looking for persons with the given {@link Employment employment type}.
	 *
	 * @param employmentType the employment type
	 */
	public DefaultEmploymentStrategy(Employment employmentType) {
		this.employmentType = employmentType;
	}
	
	/**
	 * Checks if the given {@link Person} is potential employee
	 * for a {@link DistributionCenter}.
	 *
	 * @param person the person
	 * @return true, if the person is a potential employee
	 */
	@Override
	public boolean isPotentialEmployee(Person person) {
		return person.employment().equals(employmentType);
	}

}
