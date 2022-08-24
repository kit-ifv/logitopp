package edu.kit.ifv.mobitopp.simulation.business;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TestBranchSectorAndBuilding {
	
	@Test
	public void sectorFromInt() {
		for (Sector s : Sector.values()) {
			assertEquals(s, Sector.fromInt(s.asInt()));
		}
	}
	
	@Test
	public void sectorFromIllegalInt() {
		assertThrows(IllegalArgumentException.class, () -> {
			Sector.fromInt(-1);
		});
	}
	
	@Test
	public void branchFromInt() {
		for (Branch b : Branch.values()) {
			assertEquals(b, Branch.fromInt(b.asInt()));
		}
	}
	
	@Test
	public void branchFromIllegalInt() {
		assertThrows(IllegalArgumentException.class, () -> {
			Branch.fromInt(-1);
		});
	}
	
	@Test
	public void buildingFromInt() {
		for (BuildingType b : BuildingType.values()) {
			assertEquals(b, BuildingType.fromInt(b.asInt()));
		}
	}
	
	@Test
	public void buildingFromIllegalInt() {
		assertThrows(IllegalArgumentException.class, () -> {
			BuildingType.fromInt(-1);
		});
	}
	
	@Test
	public void getSector() {
		for (Branch b : Branch.values()) {
			assertTrue(b.getSector().getContainedBranches().contains(b));
		}
	}
	
	@Test
	public void getContainedBranches() {
		for (Sector s : Sector.values()) {
			s.getContainedBranches().forEach(b -> 
				assertEquals(s, b.getSector())
			);
		}
	}
	
	@Test
	public void getBuildingTypes() {
		for (Branch b : Branch.values()) {
			b.getBuildingTypes().forEach(bt -> 
				assertTrue(bt.getBranches().contains(b))
			);
		}
	}
	
	@Test
	public void getBranchces() {
		for (BuildingType bt : BuildingType.values()) {
			bt.getBranches().forEach(b ->
				assertTrue(b.getBuildingTypes().contains(bt))
			);
		}
	}

	
	@Test
	public void sectorAsString() {
		for (Sector s : Sector.values()) {
			assertEquals(s.name(), s.asString());
		}
	}
	
	@Test
	public void branchAsString() {
		for (Branch b : Branch.values()) {
			assertEquals(b.name(), b.asString());
		}
	}
	
	@Test
	public void buildingAsString() {
		for (BuildingType b : BuildingType.values()) {
			assertEquals(b.name(), b.asString());
		}
	}
	
	
}
