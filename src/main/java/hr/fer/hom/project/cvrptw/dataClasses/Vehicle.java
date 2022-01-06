package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private int vehicleIndex;
    private int remainingCapacity;
    private int capacityLimit;
    //private Customer currentPosition; -> uvijek zadnji korisnik
    private List<Customer> route;
    private int totalRouteLength;
    private int totalRouteTime;

    public Vehicle(int vehicleIndex, int capacityLimit, Customer depot){
        this.vehicleIndex = vehicleIndex;
        this.capacityLimit = capacityLimit;
        this.route = new ArrayList<>();
        this.route.add(depot);
        //this.currentPosition = depot;
    }

    /*napraviti metodu addCustomerToEnd(Customer) za dodavanje korisnika na kraj rute
      dodati korisnika u listu, azurirati kapacitet vozila, duljinu rute, uk. vrijeme rute
      korisniku dodati parametre servedTime i positionOnRoute
     */

    /*Napraviti metodu checkAddingToEndPossible(Customer)
     Metoda provjerava da li je moguce dodati korisnika u rutu vozila
     Potrebno provjeriti ogranicenje kapaciteta vozila u odnosu na zahtjev korisnika
     te vremensko ogranicenje korisnika
     */



}
