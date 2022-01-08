package hr.fer.hom.project.cvrptw.operators;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import hr.fer.hom.project.cvrptw.dataClasses.CustomerCalc;
import hr.fer.hom.project.cvrptw.dataClasses.Solution;
import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

public class RelocateCustomerInterOp {

    private Solution solution;

    public RelocateCustomerInterOp(Solution solution){
        this.solution = solution;
    }

    /*
    1.izaberi neku rutu s malo korisnika
    2.izaberi korisnike iz te rute
    3.pokusaj ubaciti tog korisnika u neku drugu rutu s vecim brojem korisnika (for petlja)
    4.ako je ubacivanje uspjelo provjeri da li je ruta ostala prazna (samo 2 korisnika)
    5.ako je ruta ostala prazna, izbrisati je, tj ne ubacivati novu
     */
    public Solution run(){
        List<Vehicle> vehicles = solution.getVehiclesUsed();
        int vehiclesCount = vehicles.size();
        Random random = new Random();
        Vehicle fewCustomersVehicle = getVehicleWithSmallNumOfCustomers(vehicles);

        int customersInVehicleCount = fewCustomersVehicle.getNumberOfCustomersInRoute();
        int customerIndex = random.nextInt(customersInVehicleCount-1)+1;
        CustomerCalc customer = fewCustomersVehicle.getRoute().get(customerIndex);

        Vehicle newVehicle, increasedVehicleNew = null, increasedVehicleOld = null, reducedVehicle = null;
        boolean addingSuccessful = false;

        for(int i=0; i<5; i++){
            int vehicleIndex = random.nextInt(vehiclesCount);
            Vehicle manyCustomersVehicle = solution.getVehiclesUsed().get(vehicleIndex);
            if (fewCustomersVehicle.getNumberOfCustomersInRoute() >= manyCustomersVehicle.getNumberOfCustomersInRoute()) continue;
            for (int j=0; j<3; j++){
                if(addingSuccessful) break;
                int index =  random.nextInt(manyCustomersVehicle.getNumberOfCustomersInRoute()-1)+1;
                newVehicle = manyCustomersVehicle.insertCustomerAtIndex(customer.getCustomer(), index);
                addingSuccessful = manyCustomersVehicle.replaceSuccessful(newVehicle);
                if (addingSuccessful){
                   //System.out.println("korisnik ubacen");
                   addingSuccessful = true;
                   increasedVehicleNew = newVehicle;
                   increasedVehicleOld = manyCustomersVehicle;
                   reducedVehicle = fewCustomersVehicle.removeFromRoute(customer);
                }
            }
        }
        if (addingSuccessful){
            if (reducedVehicle.getRoute().size() != 2){
                vehicles.remove(fewCustomersVehicle);
                vehicles.add(reducedVehicle);
            }
            vehicles.remove(increasedVehicleOld);
            vehicles.add(increasedVehicleNew);
            solution.setVehiclesUsed(vehicles);
            System.out.println("Inserted customer: " + customer.getCustomer().getCustomerIndex());
        }
        return this.solution;
    }

    public Vehicle getVehicleWithSmallNumOfCustomers(List<Vehicle> vehicles){
        Collections.sort(vehicles, Comparator.comparing(Vehicle::getNumberOfCustomersInRoute));
        Random random = new Random();
        int shortRouteIndex;
        int randomNumber = random.nextInt(10);
        if (randomNumber < 6) shortRouteIndex = 0;
        else if (randomNumber > 8) shortRouteIndex = 2;
        else shortRouteIndex = 1;
        return vehicles.get(shortRouteIndex);
    }
}
