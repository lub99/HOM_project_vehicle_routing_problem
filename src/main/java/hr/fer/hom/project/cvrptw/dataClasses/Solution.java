package hr.fer.hom.project.cvrptw.dataClasses;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private Integer numberOfIter;
    private Double finalTempSA;

    public Integer getNumberOfIter() {
        return numberOfIter;
    }

    public void setNumberOfIter(Integer numberOfIter) {
        this.numberOfIter = numberOfIter;
    }

    public Double getFinalTempSA() {
        return finalTempSA;
    }

    public void setFinalTempSA(Double finalTempSA) {
        this.finalTempSA = finalTempSA;
    }

    public Solution() {
    }

    public Solution(List<Vehicle> vehiclesUsed) {
        this.vehiclesUsed = vehiclesUsed;
        this.totalDistance = this.getTotalDistance();
    }

    public Double getTotalDistance() {
        return vehiclesUsed.stream().mapToDouble(Vehicle::getRouteLength).sum();
    }

    public List<Vehicle> getVehiclesUsed() {
        return vehiclesUsed;
    }

    public Integer getVehiclesUsedCount() {
        return vehiclesUsed.size();
    }

    public void setTotalDistance(Double distance) {
        this.totalDistance = distance;
    }

    public void setVehiclesUsed(List<Vehicle> vehicles) {
        this.vehiclesUsed = vehicles;
    }

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

    public boolean checkIfNewSolutionIsBetterPlus(Solution newSolution){
        return newSolution.getVehiclesUsedCount() < this.getVehiclesUsedCount()
                || newSolution.getLowestNumOfCustomerInOneVehicle() < this.getLowestNumOfCustomerInOneVehicle()
                || (newSolution.getTotalDistance() < this.getTotalDistance());
    }

    public boolean checkIfNewSolutionIsBetterPlus2(Solution newSolution){
        return newSolution.getVehiclesUsedCount() < this.getVehiclesUsedCount()
                || newSolution.getLowestNumOfCustomerInOneVehicle() < this.getLowestNumOfCustomerInOneVehicle()
                || (newSolution.getTotalDistance() < this.getTotalDistance())
                && newSolution.getLowestNumOfCustomerInOneVehicle() < 5;
    }

    public void replaceVehicles(List<Vehicle> oldVehicles, List<Vehicle> newVehicles) {
        vehiclesUsed.removeAll(oldVehicles);
        vehiclesUsed.addAll(newVehicles);
        totalDistance = getTotalDistance();
    }

    public int getLowestNumOfCustomerInOneVehicle(){
        Collections.sort(this.vehiclesUsed, Comparator.comparing(Vehicle::getNumberOfCustomersInRoute));
        return this.vehiclesUsed.get(0).getNumberOfCustomersInRoute();
    }

    public String toString2() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(vehiclesUsed.size()).append("\n");

        int index = 1;
        for (var vehicle : vehiclesUsed) {
            stringBuilder.append(index).append(": v_id=")
                    .append(vehicle.getVehicleIndex()).append(" -> ");
            var oneRoute = vehicle.getRoute()
                    .stream()
                    .map(CustomerCalc::printToString)
                    .collect(Collectors.joining("->"));
            stringBuilder.append(oneRoute).append("\n");
            index++;
        }

        stringBuilder.append(getTotalDistance()).append("\n");

        return stringBuilder.toString();
    }

    public double[] getParameters() {
        double[] parameters = new double[2];
        parameters[0] = Double.valueOf(this.getVehiclesUsedCount());
        parameters[1] = this.getTotalDistance();
        return  parameters;
    }
}
