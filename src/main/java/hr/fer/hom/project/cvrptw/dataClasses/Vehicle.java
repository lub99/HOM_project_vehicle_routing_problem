package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private Integer vehicleIndex;
    private Integer remainingCapacity;
    private Integer capacityLimit;
    private List<Customer> route;
    private Double routeLength;
    private Integer routeTime;

    public Vehicle(int vehicleIndex, int capacityLimit, Customer depot){
        this.vehicleIndex = vehicleIndex;
        this.capacityLimit = capacityLimit;
        this.remainingCapacity = capacityLimit;
        this.route = new ArrayList<>();
        this.route.add(depot);
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

    public List<Customer> getRoute() {
        return route;
    }

    public Customer getLastCustomerInRoute() {
        return route.get(route.size()-1);
    }

    public void setRouteTime(int time) {
        this.routeTime = time;
    }
    public void setRouteLength(double length) {
        this.routeLength = length;
    }

    public void addCustomerToEnd(Customer c, double[][] distances){
        Customer lastCustomer = getLastCustomerInRoute();
        this.route.add(c);
        this.remainingCapacity -= c.getDemand();
        this.setRouteLength(calculateRoutePositionOfNextCustomer(lastCustomer, c, distances));
        this.setRouteTime(calculateArrivalTimeToNextCustomer(lastCustomer, c, distances));
        c.setArrivalTime(this.getRouteTime());
        c.setPositionOnRoute(this.getRouteLength());
        c.setServed(true);
    }

    public double calculateRoutePositionOfNextCustomer(Customer previousCustomer,
                                                     Customer nextCustomer, double[][] distances){
        return previousCustomer.getPositionOnRoute()
                + distances[previousCustomer.getCustomerIndex()][nextCustomer.getCustomerIndex()];
    }

    public int calculateArrivalTimeToNextCustomer(Customer previousCustomer,
                                                 Customer nextCustomer, double[][] distances){
        return Math.max(nextCustomer.getReadyTime(), previousCustomer.getArrivalTime()
                + previousCustomer.getServiceTime()
                + (int)(distances[previousCustomer.getCustomerIndex()][nextCustomer.getCustomerIndex()]+1));
    }

    public void returnToGarage(double[][] distances) {
        Customer depot = this.route.get(0).copy();
        addCustomerToEnd(depot, distances);
    }
}
