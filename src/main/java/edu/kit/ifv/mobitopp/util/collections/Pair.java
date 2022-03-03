package edu.kit.ifv.mobitopp.util.collections;

public class Pair<F,S> {

	private F first;
	private S second;
	
	public Pair(F first, S second) {
		this.setFirst(first);
		this.setSecond(second);
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		Pair<?,?> that = (Pair<?,?>) obj;

		return this.first.equals(that.first) && this.second.equals(that.second);
	}
	
}
