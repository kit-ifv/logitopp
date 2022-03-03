package edu.kit.ifv.mobitopp.util.collections;

public class Triple<F,S, T> {

	private F first;
	private S second;
	private T third;
	
	public Triple(F first, S second, T third) {
		this.setFirst(first);
		this.setSecond(second);
		this.setThird(third);
	}

	public F getFirst() {
		return first;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public S getSecond() {
		return second;
	}

	public void setSecond(S second) {
		this.second = second;
	}
	
	public T getThird() {
		return third;
	}

	public void setThird(T third) {
		this.third = third;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		Triple<?,?,?> that = (Triple<?,?,?>) obj;

		return this.first.equals(that.first) && this.second.equals(that.second) && this.third.equals(that.third);
	}

	
	
}
