package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.List;

public class Vehicle {

    private int vehicleIndex;
    private int remainingCapacity;
    private int capacityLimit;
    private Customer currentPosition;
    private List<Customer> route;
    private int totalRouteLength;
    private int totalRouteTime;

    public Vehicle(int vehicleIndex, int capacityLimit, Customer depot){
        this.vehicleIndex = vehicleIndex;
        this.capacityLimit = capacityLimit;
        this.currentPosition = depot;
    }

    //napraviti metodu za dodavanje korisnika u rutu



}
