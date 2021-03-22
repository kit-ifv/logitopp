# logiTopp
logiTopp is an extension of the agent-based travel demand model [mobiTopp](http://mobitopp.ifv.kit.edu/) developed at the [Institute for transport studies at the Karlsruhe Institute of Technology](http://www.ifv.kit.edu/english/index.php) for modeling parcel orders and last-mile deliveries.

## New ActivityTypes in mobiTopp
Code | Description
--- | ---
23 | DELIVER_PARCEL
71 | PICK_UP_PARCEL


## Parcel Orders
Prior to the simulation, a set of parcel orders is generated for each person of the population. The delivery of these parcels is then simulated alongside the the usual mobiTopp simulation.

The details of each parcel order are determined by the following choice models. 

### Number of Parcels
As a first step, the number of parcel orders (per person) is determined.
This can also result to 0, in which case that person has no parels ordered during the simulated week.
LogiTopp provides a generic NumberOfParcelsSelector which draws a number from a normal distribution (e.g. mean 0.75, stdDev 0.5). This number is capped by a lower and upper bound (e.g. [0,10]).

### Parcel Destination
Each parcel is assigned a destination type (HOME, WORK or PACKSTATION) which determines where the parcel will be delivered to. Each type determines the delivery location by the recipients fixed destination for ActivityType Home, Work resp. Pick-Up-Parcel. The fixed destination for this third ActivityType has to be set during population synthesis (e.g. to the centroid location of a persons home zone). 

LogiTopp provides a generic, share based ParcelDestinationSelector. A share of each type can be specified. However, since we want to simulate the deliveries in the survey area, WORK deliveries are not allowed if a persons work zone is outside the survey area. Therefore, a zone filter can be provided. 


### Planned Delivery Date
Parcels  can arrive at distribution centers on different days. So the third model step determines a planned delivery date from which on the parcel is available for distribution. 
LogiTopp provides a generic DeliveryDateSelector which selects a random (uniform distribution) date between two given dates. This can be used to select planned delivery dates between Monday and Saturday but skip Sunday. The arrival at the distribution center can either be at the start of the day (day precision) or at the start of an hour (hour precision).

### Delivery Service
Parcels can be transported by different delivery services. In the fourth step, each parcel can be assigned a delivery service tag.
LogiTopp provides a generic, share based DeliveryServiceSelector.

### Distribution Center
Additionally to the delivery service tag, each parcel is assigned to a distribution center, from where it will be delivered to the recipient.
LogiTopp provides a generic, share based DistributionCenterSelector.

### Parcel Size (Planned)
A shipment size model is planned but not implemented.



## Distribution Centers
Distribution Centers are modeled explicitly. Each Distribution Center has a number of employees that are selected from the population. Furthermore, each Distribution Center is assigned a relative share that can be used to assign the ordered parcels to one the Distribution Centers.

Distribution centers also have a name and organization tag.

### Employment Strategy
When selecting persons from the population as employees for distribution centers, a few conditions need to be fulfilled:

  1. If the predefined number of employees is reached, a distribution center cannot hire any additional delivery persons.

  2. A person can only be employed by a distribution center that is located in the persons fixed work destination zone.
  
  3. Additionally, an EmploymentStrategy can be defined to introduce custom employment conditions. LogiTopp provides a generic DefaultEmploymentStrategy to filter for a certain employment type (e.g. FULLTIME).

### Delivery Policy
Each distribution center has a DeliveryPolicy. A delivery policy is applied at each parcel deliery attempt to determine whether a parcel can be delivered.
This can be used to decide whether the recipient has to receive the parcel personally or if a neighbor or other household member may receive it.
Additionally, an update routine can be provided, which can update the parcel after a failed delivery attempt. 
This can be used to change e.g. the destination of the parcel to PACKSTATION after the nth failed delivery attempt.
LogiTopp provides a DummyDeliveryPolicy with the following rules:

  1. Parcels with destination PACKSTATION can always be delivered. (Currently packstations are not modeled explicitly and are assumed to have an infinite capacity).

  2. Parcels with destination WORK can only be delivered, if the recipient is currently performing a work activity.

  3. Parcels with destination HOME can be delivered, if the recipient ore one of the other household members is currently performing a home activity.

  4. After 3 failed delivery attempts, a parcel is sent to the packstation.


### Tour Assignment
When a delivery person starts to work, they are assigned a set of parcels to deliver on their tour. The distribution center applies a TourStrategy to determine the parcels for each delivery person.
LogiTopp provides a simple tour strategy: 
This tour strategy plans a route through all zones using a 2 approximation TSP algorithm. Inside each zone, parcels are grouped by the delivery location and delivery type (PACKSTATION, WORK, HOME).


## Delivery Person
Delivery persons wrap a simulation person and add additional functionality like storing the parcels of the current tour or the persons efficiency profile.

### Rescheduling
The activity schedule of delivery persons is currently replaced by a simplified work-home-work-home... schedule.

### Efficiency Model
When delivery persons are created, an efficiency model can be applied, to determine the efficiency profile for the delivery person. An efficiency profile provides a loading/unloading duration, an estimate trip duration as well as a delivery duration.
The delivery duration is split into two parts: a base duration and a duration for each additional parcel.
This efficiency model can be used to model different delivery types like on bike or truck. The provided durations can also be used when planning the delivery tour of a person.

## Pick Up Parcel Person
PickUpParcelPerson wraps a simulated person and adds the functionality of picking up parcels at a packstation.

### Fixed Destination
These persons have a fixed destination for the Pick-Up-Parcel ActivityType, which is currently set to their home zone.

### Rescheduling
When a PickUpParcelPerson is notified about a parcel being added to the packstation, they will try to add a Pick-Up-Parcel activity to their activity schedule.

## Results
The simulation produces several result files concerning the simulated deliveries:

  1. delivery-employees.csv: a list of persons that work as delivery agents and the distribution center they are employed by.
  
  2. parcel-orders.csv: the parcels orders generated before the simulation.
  
  3. delivery-rescheduling.csv: Rescheduling events of delivery persons indication their work activities.
  
  4. parcel-states.csv: all state changes of parcels (UNDEFINED, ON_DELIVERY, RETURNING, DELIVERED).
