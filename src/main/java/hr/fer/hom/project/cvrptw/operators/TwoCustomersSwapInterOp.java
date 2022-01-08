package hr.fer.hom.project.cvrptw.operators;

import hr.fer.hom.project.cvrptw.dataClasses.CustomerCalc;
import hr.fer.hom.project.cvrptw.dataClasses.Problem;
import hr.fer.hom.project.cvrptw.dataClasses.Solution;
import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TwoCustomersSwapInterOp {

    private Solution solution;

    public TwoCustomersSwapInterOp(Solution solution) {
        this.solution = solution;
    }

    /**
     * finds two the nearest vehicles by centroid and try to swap random customers some number of times.
     */
    public Solution run() {
        List<Vehicle> vehicles = new ArrayList<>();
        Random random = new Random();
        var usedVehicles = solution.getVehiclesUsed();
        var usedVehiclesSize = solution.getVehiclesUsedCount();
        while (vehicles.size() < 5) {
            var vehicle = usedVehicles.get(random.nextInt(usedVehiclesSize));
            if (!vehicles.contains(vehicle)) {
                vehicles.add(vehicle);
            }
        }

        List<Vehicle> twoNearestVehiclesByCentroid = findTwoNearestVehiclesByCentroid(vehicles);
        var vehicle1 = twoNearestVehiclesByCentroid.get(0);
        var vehicle2 = twoNearestVehiclesByCentroid.get(1);
        Vehicle[] vehiclesCrossed = {vehicle1, vehicle2};
        for (int i = 0; i < 5; i++) {
            CustomerCalc randomCustomerFromV1 = vehicle1.chooseRandomCustomerFromRouteNotDepot();
            CustomerCalc randomCustomerFromV2 = vehicle2.chooseRandomCustomerFromRouteNotDepot();
            vehiclesCrossed = Problem.twoCustomerInterSwap(vehicle1, vehicle2, randomCustomerFromV1, randomCustomerFromV2);
            // if cross succeed finish
            if (!twoNearestVehiclesByCentroid.contains(vehiclesCrossed[0]) && !twoNearestVehiclesByCentroid.contains(vehiclesCrossed[1])) {
                break;
            }
        }

        solution.replaceVehicles(twoNearestVehiclesByCentroid, Arrays.asList(vehiclesCrossed));
        return solution;
    }


    private List<Vehicle> findTwoNearestVehiclesByCentroid(List<Vehicle> vehicles) {
        var vehiclesCount = vehicles.size();
        double minDistance = Double.MAX_VALUE;
        int[] nearestVehiclesIndices = new int[2];
        for (int i = 0; i < vehiclesCount - 1; i++) {
            for (int j = i + 1; j < vehiclesCount; j++) {
                double distance = calcEuclidianDistanceBetweenCentroids(vehicles.get(i), vehicles.get(j));
                if (distance < minDistance) {
                    nearestVehiclesIndices[0] = i;
                    nearestVehiclesIndices[1] = j;
                    minDistance = distance;
                }
            }
        }
        return List.of(vehicles.get(nearestVehiclesIndices[0]), vehicles.get(nearestVehiclesIndices[1]));
    }

    private double calcEuclidianDistanceBetweenCentroids(Vehicle vehicle1, Vehicle vehicle2) {
        var centroid1 = vehicle1.calculateRouteCentroid();
        var centroid2 = vehicle2.calculateRouteCentroid();
        int x1 = centroid1[0];
        int y1 = centroid1[1];
        int x2 = centroid2[0];
        int y2 = centroid2[1];

        var firsSummand = Math.pow(x1 - x2, 2);
        var secondSummand = Math.pow(y1 - y2, 2);
        return Math.sqrt(firsSummand + secondSummand);
    }
}
