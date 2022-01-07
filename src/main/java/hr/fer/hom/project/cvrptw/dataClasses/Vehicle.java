package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.ArrayList;
import java.util.List;
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

    /*Napraviti metodu checkAddingToEndPossible(Customer)
     Metoda provjerava da li je moguce dodati korisnika u rutu vozila
     Potrebno provjeriti ogranicenje kapaciteta vozila u odnosu na zahtjev korisnika
     te vremensko ogranicenje korisnika
     */

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
    public int getNumberOfCustomersInRoute() { return route.size(); }

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
        if (index > this.getNumberOfCustomersInRoute()) return null;
        int[] addingPossible;
        Vehicle newVehicle = new Vehicle(this.vehicleIndex, this.capacityLimit, this.getDepot(), this.distances);
        for (int i=1; i<=this.route.size(); i++){
            if (i < index){  //dodaj sve prethodne korisnike iz stare u novu rutu
                newVehicle.addCustomerToEnd(this.route.get(i).getCustomer());
            }else if (i == index){  //dodaj novog korisnika
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(customer);
                if (addingPossible[0] == 0) return null;
                newVehicle.addCustomerToEnd(customer);
            }else{  //dodaj ostale korisnike
                addingPossible = newVehicle.checkIfCustomerCanBeAddedToEnd(this.route.get(i-1).getCustomer());
                if (addingPossible[0] == 0) return null;
                newVehicle.addCustomerToEnd(this.route.get(i-1).getCustomer());
            }
        }
        return newVehicle;
    }

    /*
    Two customers swap intra operator -> za minimizacije duljine rute
    Zamjena dva random korisnika u ruti -> azurirati sve potrebne info
     */

    /*
    Add customer from route with min num of customers to some short route if possible
      -> za minimizaciju broja vozila
     */

    /*
    Racunanje centroida od jedne rute
     */

    /*
    Two customers swap inter operator
    Zamjena dva korisnika koji su u razlicitim rutama
    Uzeti dvije rute koje imaju bliske centroide td je prva ruta random odabrana npr
     */

    /*
    ...druge ideje
     */


    public boolean onlyDepotsInRoute(){
        return false;
    }
}
