package hr.fer.hom.project.cvrptw.dataClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Problem {

    private int customerCount;
    private List<Customer> customers;
    private Customer depot; //-> takoder prvi korisnik u gornjoj listi
    private double[][] distances;
    private int vehicleLimit;
    private int vehicleCapacity;
    //private int vehiclesUsed;
    private List<Vehicle> vehicles;
    private List<Integer> unservedCustomerIndexes;
    private final int greedyHeuristicParam = 5;

    public Problem(){}

    public static void main(String[] args) {

        Problem problem = new Problem();

        String instanceFile = "input/instances/i1.txt";
        String distancesFile = "input/distances/i1.txt";
        problem.importData(instanceFile);
        problem.importDistanceMatrix(distancesFile);
        Solution initialSolution = problem.greedyAlg();

    }

    private Solution greedyAlg() {
        Solution solution = new Solution();
        int unservedCustomersCount = this.unservedCustomerIndexes.size();
        int vehicleIndex = 0;
        while (unservedCustomersCount > 0 && this.vehicles.size() < this.vehicleLimit){
            Vehicle vehicle = new Vehicle(vehicleIndex, this.vehicleCapacity, customers.get(0));
            boolean vehicleInDepot = true;
            Customer nextCustomer, lastCustomer = null;
            while(true){
                if(vehicleInDepot){
                    nextCustomer = findFarthestUnservedCustomerFromDepot();
                    vehicleInDepot = false;
                }else{
                    int[] sortedIndexes = sortCustomerIndexesByDistance(lastCustomer);
                    nextCustomer = findBestNextCustomer(vehicle, sortedIndexes);
                }
                if (nextCustomer == null) break;
                this.unservedCustomerIndexes.remove(nextCustomer.getCustomerIndex());
                unservedCustomersCount--;
                vehicle.addCustomerToEnd(nextCustomer, this.distances);
                lastCustomer = nextCustomer;
            }
            vehicle.returnToGarage(this.distances);
            this.vehicles.add(vehicle);
            vehicleIndex++;
        }
        if (unservedCustomersCount > 0){
            System.out.println("Infeasible solution");
            solution.setFeasible(false);
            return solution;
        }
        solution.setVehiclesUsed(vehicles);
        return solution;
    }

    /*
    Metoda pronalazi najboljeg korisnika kojeg ce se sljedeće obići
    Uzima se 5 najblizih neposluzenih susjeda te odabiremo onog kojeg
    mozemo posluziti s obzirom na vremensko ogranicenje, a takoder ima
    minimalnu razliku trenutnog vremena s servicedTime-a
    Ako nijednog susjeda ne mozemo posluziti zbog vremenskog ogranicenja vratiti null
       -> tada vozilo vracamo u skladiste
     */
    private Customer findBestNextCustomer(Vehicle vehicle, int[] sortedIndexes) {
        int unservedCustomersFound = 0;
        List<Customer> candidateCustomers = new ArrayList<>();
        for (int i=0; i<sortedIndexes.length; i++){
            Customer customer = this.customers.get(sortedIndexes[i]);
            if (customer.isServed()){
                candidateCustomers.add(customer);
                unservedCustomersFound++;
            }
            if (unservedCustomersFound > this.greedyHeuristicParam) break;
        }
        Customer previousCustomer = vehicle.getLastCustomerInRoute();
        Customer bestFound = null;
        int minTimeToDueDateOfFeasibleSolution = Integer.MAX_VALUE;
        for (Customer customer : candidateCustomers){
             int diff = customer.getDueDate() - vehicle.getRouteTime();
             if (diff <= 0) continue;
             int potentialServedTimeOfNextCustomer = vehicle.calculateServedTimeOfNextCustomer(
                     previousCustomer, customer, this.distances);
             int timeOfReturnToDepot = potentialServedTimeOfNextCustomer + customer.getServiceTime()
                     + (int)(distances[customer.getCustomerIndex()][depot.getCustomerIndex()]+1);
             if (timeOfReturnToDepot > depot.getDueDate()) continue;
             if (diff < minTimeToDueDateOfFeasibleSolution){
                 minTimeToDueDateOfFeasibleSolution = diff;
                 bestFound = customer;
             }
        }
        return bestFound;
    }

    /*
    Metoda sortira korisnike prema udaljenosti od trazenog korisnika
    Vraca se polje indeksa korisnika
     */
    private int[] sortCustomerIndexesByDistance(Customer startingCustomer){
        int startIndex = startingCustomer.getCustomerIndex();
        List<Integer> sortedIndices = IntStream.range(0, this.distances[startIndex].length)
                .boxed().sorted((i, j) -> (Double.valueOf(this.distances[startIndex][i]))
                        .compareTo(this.distances[startIndex][j]) )
                .mapToInt(ele -> ele).boxed().collect(Collectors.toList());
        sortedIndices.remove(0);
        return sortedIndices.stream().mapToInt(i->i).toArray();
    }

    /*
    Metoda trazi najdaljeg korisnika od skladista ovisno o trenutno neposluzenim korisnicima
     */
    private Customer findFarthestUnservedCustomerFromDepot() {
        double maxDistance = 0;
        double distance;
        int farthestCustomerIndex = 0;
        int unservedCustomersCount = this.unservedCustomerIndexes.size();
        for (int i=0; i<unservedCustomersCount; i++){
            distance = this.distances[0][this.unservedCustomerIndexes.get(i)];
            if (distance > maxDistance) {
                maxDistance = distance;
                farthestCustomerIndex = this.unservedCustomerIndexes.get(i);
            }
        }
        return this.customers.get(farthestCustomerIndex);
    }

    private void importData(String instance) {
        this.customers = new ArrayList<>();
        this.unservedCustomerIndexes = new ArrayList<>();
        try {
            Path path = Paths.get(instance);
            BufferedReader reader = new BufferedReader(Files.newBufferedReader(path));
            reader.readLine();
            reader.readLine();
            String[] vehicleInfo = reader.readLine().strip().split("\\s+");
            this.vehicleLimit = Integer.parseInt(vehicleInfo[0]);
            this.vehicleCapacity = Integer.parseInt(vehicleInfo[1]);
            for(int i=0; i<4; i++){
                reader.readLine();
            }
            int[] customerData;
            String row = reader.readLine();
            int row_num = 0;
            while (row != null) {
                customerData = Arrays.stream(row.strip().split("\\s+")).mapToInt(Integer::parseInt).toArray();
                Customer c = new Customer(customerData);
                if (row_num == 0){
                    c.setServedTime(0);
                    c.setPositionOnRoute(0.0);
                    this.depot = c;
                }else{
                    this.unservedCustomerIndexes.add(c.getCustomerIndex());
                }
                this.customers.add(c);
                row = reader.readLine();
                row_num++;
            }
            this.customerCount = this.customers.size();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importDistanceMatrix(String distancesFile) {
        try {
            Path path = Paths.get(distancesFile);
            int lineCount = (int)(Files.lines(path).count());
            BufferedReader reader = new BufferedReader(Files.newBufferedReader(path));
            this.distances = new double[lineCount][lineCount];
            for (int i=0; i<lineCount; i++){
                String row = reader.readLine();
                this.distances[i] = Arrays.stream(row.strip().split("\\s+"))
                        .mapToDouble(Double::parseDouble).toArray();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
