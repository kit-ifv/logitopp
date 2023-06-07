package edu.kit.ifv.mobitopp.simulation.parcels;

public class ParcelUnit {
	
	private final int units;
	
	private ParcelUnit(int units) {
		this.units = units;
	}
	
	public static ParcelUnit of(int units) {
		return new ParcelUnit(units);
	}
	
	public static ParcelUnit of(IParcel parcel) {
		return ParcelUnit.of(parcel.getShipmentSize());
	}
	
	public static ParcelUnit of(ShipmentSize size) {
		return size.toParcelUnits();
	}
	
	public ParcelUnit add(ParcelUnit unit) {
		return new ParcelUnit(this.units + unit.units);
	}
	
	public ParcelUnit add(IParcel parcel) {
		return this.add(ParcelUnit.of(parcel));
	}
	
	public ParcelUnit add(ShipmentSize size) {
		return this.add(ParcelUnit.of(size));
	}

}
