package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

public class EuclidDistanceMetricTest extends AbstractDistanceMetricTest {

	@Override
	protected DistanceMetric createMetric() {
		return new EuclidDistanceMetric();
	}

	@Override
	protected double getExpectedDistance() {
		return 1.4142;
	}

}
