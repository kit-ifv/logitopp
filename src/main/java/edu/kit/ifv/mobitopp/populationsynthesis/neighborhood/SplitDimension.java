package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.geom.Point2D;

public enum SplitDimension {
	X {
		@Override
		public double getValue(Point2D point) {
			return point.getX();
		}
		
		@Override
		public Point2D createSplitPoint(double value) {
			return new Point2D.Double(value, 0.0);
		}
	},
	
	Y {
		@Override
		public double getValue(Point2D point) {
			return point.getY();
		}
		
		@Override
		public Point2D createSplitPoint(double value) {
			return new Point2D.Double(0.0, value);
		}
	},
	
	NONE;
	
	public SplitDimension getOpposite() {
		switch (this) {
			case X:
				return Y;
			case Y:
				return X;
			case NONE:
				return NONE;
			default:
				return NONE;
		}
		
	}
	
	public double getValue(Point2D point) {
		throw new UnsupportedOperationException("Retreiving values from 2D points is not supported for SplitDimension " + this.name());
	}
	
	public Point2D createSplitPoint(double value) {
		throw new UnsupportedOperationException("Creating 2D split points is not supported for SplitDimension " + this.name());
	}
}
