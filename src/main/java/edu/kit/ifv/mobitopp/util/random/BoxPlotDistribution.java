package edu.kit.ifv.mobitopp.util.random;

public class BoxPlotDistribution {
	
	private final double min;
	private final double quartLow;
	private final double median;
	private final double quartHigh;
	private final double max;
	
	public BoxPlotDistribution(double min, double quartLow, double median, double quartHigh, double max) {
		this.min = min;
		this.quartLow = quartLow;
		this.median = median;
		this.quartHigh = quartHigh;
		this.max = max;
	}
	
	public double draw(double random) {
		double a;
		double b;
		
		if (random >= 0.75) {
			a = quartHigh;
			b = max;
			
		} else if (random >= 0.5) {
			a = median;
			b = quartHigh;

		} else if (random >= 0.25) {
			a = quartLow;
			b = median;
			
		} else {
			a = min;
			b= quartLow;
		}
		
		double scale = (random - a) / 0.25;
		
		return interpolate(a, b, scale);
	}
	
	private double interpolate(double a, double b, double scale) {
		return (b - a) * scale + a;
	}

}
