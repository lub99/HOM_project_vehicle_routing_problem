package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Vehicle {

    private Integer vehicleIndex;
    private Integer remainingCapacity;
    private Integer capacityLimit;
    //private Customer currentPosition; -> uvijek zadnji korisnik
    private List<Customer> route;
    private Double routeLength;
    private Integer routeTime;

    public Vehicle(int vehicleIndex, int capacityLimit, Customer depot){
        this.vehicleIndex = vehicleIndex;
        this.capacityLimit = capacityLimit;
        this.route = new ArrayList<>();
        this.route.add(depot);
        this.routeTime = 0;
        this.routeLength = 0.0;
        //this.currentPosition = depot;
    }

    /*Napraviti metodu checkAddingToEndPossible(Customer)
     Metoda provjerava da li je moguce dodati korisnika u rutu vozila
     Potrebno provjeriti ogranicenje kapaciteta vozila u odnosu na zahtjev korisnika
     te vremensko ogranicenje korisnika
     */

    /*dodati gettere i settere za neke atribute
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

    /*napraviti metodu addCustomerToEnd(Customer) za dodavanje korisnika na kraj rute
      dodati korisnika u listu, azurirati kapacitet vozila, duljinu rute, uk. vrijeme rute
      korisniku dodati parametre servedTime i positionOnRoute
     */
    public void addCustomerToEnd(Customer c, double[][] distances){
        this.route.add(c);
        this.remainingCapacity -= c.getDemand();
        Customer lastCustomer = this.route.get(this.route.size()-1);
        this.setRouteLength(this.getRouteLength() +
                distances[lastCustomer.getCustomerIndex()][c.getCustomerIndex()]);
        //this.setRouteTime(this.getRouteTime() + t);  //vidjeti kako se racuna t
        c.setServedTime(this.getRouteTime());
        c.setPositionOnRoute(this.getRouteLength());
        c.setServed(true);
    }

}
