package hr.fer.hom.project.cvrptw.dataClasses;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Solution cuva informacije o trenutnom rjesenju
 * totalDistance - ukupna prijedena udaljenost svih vozila
 * totalTime - ukupno iskoristeno vrijeme svih vozila
 * vehiclesUsed - iskoristena vozila
 */
public class Solution {

    private Double totalDistance;
    private List<Vehicle> vehiclesUsed;

    public Solution() {
    }

    public Solution(List<Vehicle> vehiclesUsed) {
        this.vehiclesUsed = vehiclesUsed;
        this.totalDistance = this.getTotalDistance();
    }

    public Double getTotalDistance() {
        /*if (totalDistance != null) {
            return totalDistance;
        } else {
            return vehiclesUsed.stream().mapToDouble(Vehicle::getRouteLength).sum();
        }*/
        return vehiclesUsed.stream().mapToDouble(Vehicle::getRouteLength).sum();
    }

    public List<Vehicle> getVehiclesUsed() {
        return vehiclesUsed;
    }

    public Integer getVehiclesUsedCount() {
        return vehiclesUsed.size();
    }

    //public boolean getFeasible() {return feasible;}

    public void setTotalDistance(Double distance) {
        this.totalDistance = distance;
    }

    public void setVehiclesUsed(List<Vehicle> vehicles) {
        this.vehiclesUsed = vehicles;
    }

    //public void setFeasible(boolean feasible) { this.feasible = feasible;}

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(vehiclesUsed.size()).append("\n");

        int index = 1;
        for (var vehicle : vehiclesUsed) {
            var oneRoute = vehicle.getRoute()
                    .stream()
                    .map(CustomerCalc::printToString)
                    .collect(Collectors.joining("->"));
            stringBuilder.append(index).append(": ").append(oneRoute).append("\n");
            index++;
        }

        stringBuilder.append(getTotalDistance()).append("\n");

        return stringBuilder.toString();
    }

    public void printToFile(String outputFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(this.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Solution copy(){
        return new Solution(new ArrayList<>(this.getVehiclesUsed()));
    }

    public boolean checkIfNewSolutionIsBetter(Solution newSolution){
        return newSolution.getVehiclesUsedCount() < this.getVehiclesUsedCount()
                || (newSolution.getTotalDistance() < this.getTotalDistance());
    }
}
