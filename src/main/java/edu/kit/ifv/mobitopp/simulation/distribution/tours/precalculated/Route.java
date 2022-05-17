package edu.kit.ifv.mobitopp.simulation.distribution.tours.precalculated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import lombok.Getter;

public class Route {
	
	@Getter private int id;
	private LinkedHashMap<Integer, LinkDeliveryActivityBuilder> deliveryTour;
	private LinkedHashMap<Integer, Integer> expectedParcels;
	
	public Route(int id) {
		this.deliveryTour = new LinkedHashMap<>();
		this.expectedParcels = new LinkedHashMap<>();
	}
	
	public void planStop(int link, int expectedParcels) {
		this.deliveryTour.put(link, new LinkDeliveryActivityBuilder());
		this.expectedParcels.put(link, expectedParcels);
	}
	
	public boolean contains(DeliveryActivityBuilder delivery) {
		return this.deliveryTour.keySet().contains(Math.abs(delivery.getLocation().roadAccessEdgeId()));
	}
	
	public void addParcels(DeliveryActivityBuilder delivery) {
		if (contains(delivery)) {
			int stop = Math.abs(delivery.getLocation().roadAccessEdgeId());
			this.deliveryTour.put(stop, this.deliveryTour.get(stop).merge(delivery));
		}
	}
	
	private void verify() {
		for (int stop : deliveryTour.keySet()) {
			int parcels = this.deliveryTour.get(stop).getParcels().size();
			int expected = this.expectedParcels.get(stop);
			if (parcels != expected) {
				System.out.println("Tour " + id + " stop " + stop + " contains " + parcels + " parcels "+ " but " + expected + " were expected.");
			}
		}
	}
	
	public int lenght() {
		return this.deliveryTour.size();
	}
	
	public int size() {
		return this.deliveryTour.values().stream().mapToInt(d -> d.getParcels().size()).sum();
	}
	
	
	public Collection<LinkDeliveryActivityBuilder> getDeliveries() {
		verify();
		return this.deliveryTour.values();
	}
	
	
	private void parseStop(Row row) {
		int id = row.valueAsInteger("Id");

		if (id != -1) {
			int expected = row.valueAsInteger("Pakete");
			this.planStop(id, expected);	
		}
	}
	
	public static Collection<Route> parseRoutes(CsvFile csv, String alg, String name) {
		System.out.println("Parsing " + name);
		List<Route> routes = new ArrayList<>();
		
		csv.stream()
		   .filter(r -> r.get("Algorithmus").equals(alg))
		   .forEach(r -> {
			   String id = r.get("Id");
			   
			   if (!id.equals("")) {
				   int no = r.valueAsInteger("Tour Nr.");
				   
				   Route last;
				   if (routes.isEmpty() || (last = routes.get(routes.size()-1)).getId() != no) {
					   last = new Route(no);
					   routes.add(last);
				   }
				   
				   last.parseStop(r);

			   }

		   });
		
		return routes;
	}
	

}
