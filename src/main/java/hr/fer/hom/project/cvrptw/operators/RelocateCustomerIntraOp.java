package hr.fer.hom.project.cvrptw.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hr.fer.hom.project.cvrptw.dataClasses.CustomerCalc;
import hr.fer.hom.project.cvrptw.dataClasses.Solution;
import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

public class RelocateCustomerIntraOp {

    private Solution solution;

    public RelocateCustomerIntraOp(Solution solution){
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
        int customerIndex = random.nextInt(customersInVehicleCount-1)+1;
        CustomerCalc customer = vehicle.getRoute().get(customerIndex);
        for(int i=0; i<5; i++){
            int relocationIndex = random.nextInt(customersInVehicleCount-1)+1;
            if (relocationIndex == customerIndex) continue;
            Vehicle newVehicle = vehicle.relocateCustomer(customer, relocationIndex);
            //boolean addingSuccessful = vehicle.replaceSuccessful(newVehicle);
            if (newVehicle.getRouteLength() < bestNewVehicle.getRouteLength()){
                bestNewVehicle = newVehicle;
            }
        }
        vehiclesUsed.remove(vehicle);
        vehiclesUsed.add(bestNewVehicle);
        solution.setVehiclesUsed(vehiclesUsed);
        return solution;
    }
}
