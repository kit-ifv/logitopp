package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Person;


public class DefaultEmploymentStrategy implements DeliveryEmploymentStrategy {
	
	private Employment employmentType;	
	
	public DefaultEmploymentStrategy(Employment employmentType) {
		this.employmentType = employmentType;
	}
	
	@Override
	public boolean isPotentialEmployee(Person person) {
		return person.employment().equals(employmentType);
	}

}
