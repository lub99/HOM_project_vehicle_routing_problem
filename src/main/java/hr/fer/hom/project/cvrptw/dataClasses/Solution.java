package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.List;

/**
 * Solution cuva informacije o trenutnom rjesenju
 * @totalDistance - ukupna prijedena udaljenost svih vozila
 * @totalTime - ukupno iskoristeno vrijeme svih vozila
 * @vehiclesUsed - iskoristena vozila
 */
public class Solution {

    private int totalDistance;
    private int totalTime;
    //private int vehiclesUsedCount;
    private List<Vehicle> vehiclesUsed;

    public Solution(){}

    public Solution(int totalDistance, int totalTime, List<Vehicle> vehiclesUsed){
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.vehiclesUsed = vehiclesUsed;
        //this.vehiclesUsedCount = vehiclesUsed.size();
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public List<Vehicle> getVehiclesUsed() { return vehiclesUsed; }

    public Integer getVehiclesUsedCount() {
        return vehiclesUsed.size();
    }

    public void setTotalDistance(int distance) {
        this.totalDistance = distance;
    }
    public void setTotalTime(int time) {
        this.totalTime = time;
    }
    public void setVehiclesUsed(List<Vehicle> vehicles) {
        this.vehiclesUsed = vehicles;
    }

    /*
    Dodati ispis rjesenja kao u pdf-u projekta
    - ispis broja vozila
    - iterirati po vozilima, ispisati indeks vozila, indeks korisnika i njegov servedTime
    - ispisati totalDistance rjesenja
     */
}
