package edu.kit.ifv.mobitopp.simulation.distribution.timetable;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.*;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class TimeTableParser {

	Map<Integer, DistributionCenter> depotIds;

	public TimeTableParser(Collection<DistributionCenter> depots) {
		this.depotIds = new LinkedHashMap<>();
		depots.forEach(c -> depotIds.put(c.getId(), c));
	}

	public TimeTable parse(String filePath) {
		return this.parse(CsvFile.createFrom(filePath));
	}

	public TimeTable parse(File file) {
		return this.parse(CsvFile.createFrom(file));
	}

	public TimeTable parse(CsvFile file) {
		List<Connection> connections = file.stream().map(this::parseConnection).filter(Objects::nonNull).collect(toList());
		return new TimeTable(connections);
	}

	public Connection parseConnection(Row row) {
		int from_id = row.valueAsInteger("from_depot");
		DistributionCenter origin = depotIds.get(from_id);

		int to_id = row.valueAsInteger("to_depot");
		DistributionCenter destination = depotIds.get(to_id);

		int depart_day = row.valueAsInteger("depart_day");
		int depart_hour = row.valueAsInteger("depart_hour");
		int depart_min = row.valueAsInteger("depart_min");
		Time departure = Time.start
				.plusDays(depart_day)
				.plusHours(depart_hour)
				.plusMinutes(depart_min);

		int trip_duration = row.valueAsInteger("trip_duration");

		int capacity = row.valueAsInteger("capacity");

		if (origin == null) {
			System.out.println("Could not find origin " + from_id + " in loaded distribution centers: " + depotIds.values());
			System.out.println("Data: "  + row );
			return null;
		}

		if (destination == null) {
			System.out.println("Could not find destination " + to_id + " in loaded distribution centers: " + depotIds.values());
			System.out.println("Data: "  + row );
			return null;
		}

		return new Connection(origin, destination, departure, trip_duration, capacity);
	}



}
