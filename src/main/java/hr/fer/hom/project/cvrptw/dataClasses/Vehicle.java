package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Vehicle {

    private Integer vehicleIndex;
    private Integer remainingCapacity;
    private Integer capacityLimit;
    //private List<Customer> route;
    private List<CustomerCalc> route;
    private Double routeLength;
    private Integer routeTime;
    private double[][] distances;

    public Vehicle(int vehicleIndex, int capacityLimit, Customer depot, double[][] distances){
        this.vehicleIndex = vehicleIndex;
        this.capacityLimit = capacityLimit;
        this.remainingCapacity = capacityLimit;
        this.distances = distances;
        this.route = new ArrayList<>();
        CustomerCalc d = new CustomerCalc(depot, 0, 0.0);
        this.route.add(d);
        this.routeTime = 0;
        this.routeLength = 0.0;
    }

    public Integer getVehicleIndex() {
        return vehicleIndex;
    }

    public Integer getRemainingCapacity() {
        return remainingCapacity;
    }

    public Double getRouteLength() {
        return routeLength;
    }

    public Integer getRouteTime() {
        return routeTime;
    }

    public List<CustomerCalc> getRoute() {
        return route;
    }

    public CustomerCalc getLastCustomerCalcInRoute() {
        return route.get(route.size()-1);
    }

    public Customer getLastCustomerInRoute() {
        return route.get(route.size()-1).getCustomer();
    }

    public Customer getDepot() { return route.get(0).getCustomer(); }

    //skladiste se broji samo na pocetku
    public int getNumberOfCustomersInRoute() { return route.size()-1; }

    public void setRouteTime(int time) {
        this.routeTime = time;
    }
    public void setRouteLength(double length) {
        this.routeLength = length;
    }

    public void print(){
        String s = this.getRoute()
                .stream()
                .map(CustomerCalc::printToString)
                .collect(Collectors.joining("->"));
        System.out.println(s);
    }

    public void addCustomerToEnd(Customer c){
        CustomerCalc lastCustomer = getLastCustomerCalcInRoute();
        this.remainingCapacity -= c.getDemand();
        this.setRouteLength(calculateRoutePositionOfNextCustomer(lastCustomer, c));
        this.setRouteTime(calculateArrivalTimeToNextCustomer(lastCustomer, c));
        c.setServed(true);
        CustomerCalc nextCustomer = new CustomerCalc(c);
        nextCustomer.setArrivalTime(this.getRouteTime());
        nextCustomer.setPositionOnRoute(this.getRouteLength());
        this.route.add(nextCustomer);
    }

    public double calculateRoutePositionOfNextCustomer(CustomerCalc previousCustomer,
                                                     Customer nextCustomer){
        return previousCustomer.getPositionOnRoute()
                + distances[previousCustomer.getCustomer().getCustomerIndex()][nextCustomer.getCustomerIndex()];
    }

    public int calculateArrivalTimeToNextCustomer(CustomerCalc previousCustomer,
                                                 Customer nextCustomer){
        return Math.max(nextCustomer.getReadyTime(), previousCustomer.getArrivalTime()
                + previousCustomer.getCustomer().getServiceTime()
                + (int)(distances[previousCustomer.getCustomer().getCustomerIndex()][nextCustomer.getCustomerIndex()]+1));
    }

    public void returnToGarage() {
        Customer depot = this.route.get(0).getCustomer();
        addCustomerToEnd(depot);
    }

    public int[] checkIfCustomerCanBeAddedToEnd(Customer nextCustomer){
        int[] returnStatement = new int[2];
        returnStatement[0] = 0;
        if (this.getRemainingCapacity() < nextCustomer.getDemand()){
            return returnStatement;
        }
        CustomerCalc previousCustomer = this.getLastCustomerCalcInRoute();
        int potentialArrivalToNextCustomer = this.calculateArrivalTimeToNextCustomer(
                previousCustomer, nextCustomer);
        int diff = nextCustomer.getDueDate() - potentialArrivalToNextCustomer;
        if (diff < 0){
            return returnStatement;
        }
        int timeOfReturnToDepot = potentialArrivalToNextCustomer + nextCustomer.getServiceTime()
                + (int) (distances[nextCustomer.getCustomerIndex()][this.getDepot().getCustomerIndex()] + 1);
        if (timeOfReturnToDepot > this.getDepot().getDueDate()){
            return returnStatement;
        }
        returnStatement[0] = 1;
        returnStatement[1] = timeOfReturnToDepot;
        return returnStatement;
    }
    /*
    Insert customer after customer on index
    eg. index 1 -> inserts customer after depot
    eg. index 2 -> inserts customer after customer no1 in route
    Prihvacaju se indexi od 1 do duljine rute?
     */
    public Vehicle insertCustomerAtIndex(Customer customer, int index){
        if (index > this.getRoute().size()) return this;
        int[] addingPossible;
        Vehicle newVehicle = new Vehicle(this.vehicleIndex, this.capacityLimit, this.getDepot(), this.distances);
        for (int i=1; i<=this.route.size(); i++){
            if (i < index){  //dodaj sve prethodne korisnike iz stare u novu rutu
                newVehicle.addCustomerToEnd(this.route.get(i).getCustomer());
            }else if (i == index){  //dodaj novog korisnika
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(customer);
                if (addingPossible[0] == 0) return this;
                newVehicle.addCustomerToEnd(customer);
            }else{  //dodaj ostale korisnike
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(this.route.get(i-1).getCustomer());
                if (addingPossible[0] == 0) return this;
                newVehicle.addCustomerToEnd(this.route.get(i-1).getCustomer());
            }
        }
        return newVehicle;
    }

    /*
    Remove customer from route
    Metoda prima CustomerCalc objekt i brise ga iz rute
    Svi korisnici nakon izbrisanog korisnika moraju se azurirati
    Vracamo novo vozilo
     */
    public Vehicle removeFromRoute(CustomerCalc customer){
        if (customer.getCustomer().getCustomerIndex() == 0) return this; //pokusaj brisanja skladista, nista ne radimo
        Vehicle newVehicle = new Vehicle(this.vehicleIndex, this.capacityLimit, this.getDepot(), this.distances);
        for (int i=1; i<this.route.size(); i++){
            CustomerCalc currentCustomer = this.route.get(i);
            if (currentCustomer.getCustomer().getCustomerIndex() == customer.getCustomer().getCustomerIndex()) continue;
            newVehicle.addCustomerToEnd(currentCustomer.getCustomer());
        }
        return newVehicle;
    }

    /*
    Two customers swap intra operator -> za minimizaciju duljine rute
    Zamjena dva random korisnika u ruti -> azurirati sve potrebne info
     */
    public Vehicle twoCustomersIntraSwap(CustomerCalc customer1, CustomerCalc customer2){
        if (customer1.getCustomer().getCustomerIndex() == 0) return this;
        if (customer2.getCustomer().getCustomerIndex() == 0) return this;
        Vehicle newVehicle = new Vehicle(this.vehicleIndex, this.capacityLimit, this.getDepot(), this.distances);
        int[] addingPossible;
        for (int i=1; i<this.route.size(); i++){
            CustomerCalc currentCustomer = this.route.get(i);
            if (currentCustomer.equals(customer1)){
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(customer2.getCustomer());
                if (addingPossible[0] == 0) return this;
                newVehicle.addCustomerToEnd(customer2.getCustomer());
            }else if(currentCustomer.equals(customer2)){
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(customer1.getCustomer());
                if (addingPossible[0] == 0) return this;
                newVehicle.addCustomerToEnd(customer1.getCustomer());
            }else{
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(currentCustomer.getCustomer());
                if (addingPossible[0] == 0) return this;
                newVehicle.addCustomerToEnd(currentCustomer.getCustomer());
            }
        }
        return newVehicle;
    }

    /*
    Relocate intra operator
     */
    public Vehicle relocateCustomer(CustomerCalc customer, int index){
        if (customer.getCustomer().getCustomerIndex() == 0) return this;
        if (index > this.route.size()-1) return this;
        Vehicle vehicleWithoutCustomer = this.removeFromRoute(customer);
        Vehicle newVehicle = vehicleWithoutCustomer.insertCustomerAtIndex(customer.getCustomer(), index);
        boolean vehicleAdded = newVehicle.replaceSuccessful(vehicleWithoutCustomer);
        if (!vehicleAdded) return this;
        return newVehicle;
    }

    /*
    Racunanje centroida od jedne rute
     */
    public int[] calculateRouteCentroid(){
        int[] centroid = new int[2];
        int xSum = this.route.stream().mapToInt(o -> o.getCustomer().getxCoordinate()).sum();
        int ySum = this.route.stream().mapToInt(o -> o.getCustomer().getyCoordinate()).sum();
        centroid[0] = (int) (xSum / this.route.size());
        centroid[1] = (int) (ySum / this.route.size());
        return centroid;
    }

    public Vehicle replaceCustomer(CustomerCalc oldCustomer, CustomerCalc newCustomer) {
        if (newCustomer.getCustomer().getCustomerIndex() == 0) return this;
        if (oldCustomer.getCustomer().getCustomerIndex() == 0) return this;
        Vehicle newVehicle = new Vehicle(this.vehicleIndex, this.capacityLimit, this.getDepot(), this.distances);
        int[] addingPossible;
        for (int i = 1; i < this.route.size(); i++) {
            CustomerCalc currentCustomer = this.route.get(i);
            if (currentCustomer.equals(oldCustomer)) {
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(newCustomer.getCustomer());
                if (addingPossible[0] == 0) return this;
                newVehicle.addCustomerToEnd(newCustomer.getCustomer());
            }else {
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(currentCustomer.getCustomer());
                if (addingPossible[0] == 0) return this;
                newVehicle.addCustomerToEnd(currentCustomer.getCustomer());
            }
        }
        return newVehicle;
    }

    public boolean replaceSuccessful(Vehicle other){
        return !this.equals(other);
    }

    public boolean equals(Vehicle other){
        if (this.route.size() != other.route.size()) return false;
        for (int i=0; i<this.route.size(); i++){
            if (!this.route.get(i).equals(other.route.get(i))) return false;
        }
        return true;
    }

    public CustomerCalc chooseRandomCustomerFromRouteNotDepot() {
        var customerIndex = new Random().nextInt(route.size() - 2) + 1;
        return route.get(customerIndex);
    }

}
