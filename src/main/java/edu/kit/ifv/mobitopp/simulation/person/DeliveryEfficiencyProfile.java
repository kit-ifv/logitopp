package edu.kit.ifv.mobitopp.simulation.person;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DeliveryEfficiencyProfile {
	@Getter private int loadDuration;
	@Getter private int unloadDuration;
	@Getter private int tripDuration;
	
	@Getter private float deliveryDurAdd;
	@Getter private float deliveryDurMul;
	
}
