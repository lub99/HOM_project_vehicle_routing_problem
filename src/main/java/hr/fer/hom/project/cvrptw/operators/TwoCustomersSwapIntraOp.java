package hr.fer.hom.project.cvrptw.operators;

import java.util.List;
import java.util.Random;

import hr.fer.hom.project.cvrptw.dataClasses.CustomerCalc;
import hr.fer.hom.project.cvrptw.dataClasses.Solution;
import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

public class TwoCustomersSwapIntraOp {

    private Solution solution;

    public TwoCustomersSwapIntraOp(Solution solution){
        this.solution = solution;
    }

    public Solution run(){
        List<Vehicle> vehiclesUsed = solution.getVehiclesUsed();
        int vehiclesCount = vehiclesUsed.size();
        Random random = new Random();
        int vehicleIndex = random.nextInt(vehiclesCount);
        Vehicle vehicle = solution.getVehiclesUsed().get(vehicleIndex);

        int customersInVehicleCount = vehicle.getNumberOfCustomersInRoute();
        Vehicle bestNewVehicle = vehicle;
        for(int i=0; i<5; i++){
            int customer1Index = random.nextInt(customersInVehicleCount-1)+1;
            int customer2Index = random.nextInt(customersInVehicleCount-1)+1;
            if (customer1Index == customer2Index) continue;
            CustomerCalc customer1 = vehicle.getRoute().get(customer1Index);
            CustomerCalc customer2 = vehicle.getRoute().get(customer2Index);
            Vehicle newVehicle = vehicle.twoCustomersIntraSwap(customer1, customer2);
            /*boolean addingSuccessful = vehicle.replaceSuccessful(newVehicle);
            if (addingSuccessful){
                System.out.println("promjena uspjesna");
                bestNewVehicle = newVehicle;
            }*/
            if (newVehicle.getRouteLength() < bestNewVehicle.getRouteLength()){
                bestNewVehicle = newVehicle;  //onemogucava losija rjesenja po pitanju udaljenosti
                //System.out.println("poboljsanje rjesenja");
            }
        }
        vehiclesUsed.remove(vehicle);
        vehiclesUsed.add(bestNewVehicle);
        solution.setVehiclesUsed(vehiclesUsed);
        return solution;
    }
}
