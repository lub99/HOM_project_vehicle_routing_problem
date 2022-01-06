package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Solution cuva informacije o trenutnom rjesenju
 * totalDistance - ukupna prijedena udaljenost svih vozila
 * totalTime - ukupno iskoristeno vrijeme svih vozila
 * vehiclesUsed - iskoristena vozila
 */
public class Solution {

    private int totalDistance;
    private int totalTime;
    //private int vehiclesUsedCount;
    private List<Vehicle> vehiclesUsed;
    private boolean feasible;

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

    public Integer getVehiclesUsedCount() { return vehiclesUsed.size(); }

    public boolean getFeasible() { return feasible; }

    //impl racunanje uk udaljenosti i vremena iz info o vozilima
    public void setTotalDistance(int distance) {
        this.totalDistance = distance;
    }
    public void setTotalTime(int time) {
        this.totalTime = time;
    }
    public void setVehiclesUsed(List<Vehicle> vehicles) {
        this.vehiclesUsed = vehicles;
    }
    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public void print(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(vehiclesUsed.size()).append("\n");

        for (var vehicle : vehiclesUsed){
            var oneRoute = vehicle.getRoute()
                    .stream()
                    .map(Customer::printToString)
                    .collect(Collectors.joining("->"));
            stringBuilder.append(vehicle.getVehicleIndex()).append(": ").append(oneRoute).append("\n");
        }

        //stringBuilder.append(totalDistance).append("\n");

        System.out.println(stringBuilder);
    }
}
