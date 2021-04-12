package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

public class GeoDistanceMetricTest extends AbstractDistanceMetricTest {

	@Override
	protected DistanceMetric createMetric() {
		return new GeoDistanceMetric();
	}

	@Override
	protected double getExpectedDistance() {
		return 133.387;
	}

}
