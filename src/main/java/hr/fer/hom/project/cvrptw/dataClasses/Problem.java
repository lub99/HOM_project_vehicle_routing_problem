package hr.fer.hom.project.cvrptw.dataClasses;

import hr.fer.hom.project.cvrptw.utils.Util;

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
    private final int greedyParamForFarthestCustomer = 10;
    private final int greedyParamForClosestCustomer = 3;

    public Problem() {
    }

    public static void main(String[] args) {

        Problem problem = new Problem();

        String instanceFile = args[0];  //args[0]
        String distancesFile = args[1]; // args[1];
        String outputFile = args[2]; // args[2];
        String outputFileForPython = args[3];  //"output/plotting/i1.txt"
        problem.importData(instanceFile);
        problem.importDistanceMatrix(distancesFile);
        Solution initialSolution = problem.greedyAlg();
        initialSolution.printToFile(outputFile);
        Util.printSolutionOnlyCustomerIndices(initialSolution, outputFileForPython);

    }

    private Solution greedyAlg() {
        Solution solution = new Solution();
        int unservedCustomersCount = this.unservedCustomerIndexes.size();
        this.vehicles = new ArrayList<>();
        int vehicleIndex = 0;
        int[] sortedCustomerIndexesFromDepot = sortCustomerIndexesByDistance(depot);
        while (unservedCustomersCount > 0 && this.vehicles.size() < this.vehicleLimit) {
            Vehicle vehicle = new Vehicle(vehicleIndex, this.vehicleCapacity, this.depot);
            boolean vehicleInDepot = true;
            Customer nextCustomer, lastCustomer = null;
            while (true) {
                if (vehicleInDepot) {
                    nextCustomer = findBestFirstCustomerFromDepot(sortedCustomerIndexesFromDepot);
                    vehicleInDepot = false;
                } else {
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
            this.depot.setPositionOnRoute(0.0);
            this.depot.setArrivalTime(0);
            vehicleIndex++;
        }
        if (unservedCustomersCount > 0) {
            System.out.println("Infeasible solution");
            solution.setFeasible(false);
            return solution;
        }
        solution.setVehiclesUsed(vehicles);
        System.out.println(solution);
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
        for (int i = 0; i < sortedIndexes.length; i++) {
            Customer customer = this.customers.get(sortedIndexes[i]);
            if (!customer.isServed()) {
                candidateCustomers.add(customer);
                unservedCustomersFound++;
            }
            if (unservedCustomersFound > this.greedyParamForClosestCustomer) break;
        }
        Customer previousCustomer = vehicle.getLastCustomerInRoute();
        Customer bestFound = null;
        int minTimeToDueDateOfFeasibleSolution = Integer.MAX_VALUE;
        for (Customer customer : candidateCustomers) {
            int potentialArrivalToNextCustomer = vehicle.calculateArrivalTimeToNextCustomer(
                    previousCustomer, customer, this.distances);
            int diff = customer.getDueDate() - potentialArrivalToNextCustomer;
            if (diff <= 0) continue;
            int timeOfReturnToDepot = potentialArrivalToNextCustomer + customer.getServiceTime()
                    + (int) (distances[customer.getCustomerIndex()][depot.getCustomerIndex()] + 1);
            if (timeOfReturnToDepot > depot.getDueDate()) continue;
            if (diff < minTimeToDueDateOfFeasibleSolution) {
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
    private int[] sortCustomerIndexesByDistance(Customer startingCustomer) {
        int startIndex = startingCustomer.getCustomerIndex();
        List<Integer> sortedIndices = IntStream.range(0, this.distances[startIndex].length)
                .boxed().sorted((i, j) -> (Double.valueOf(this.distances[startIndex][i]))
                        .compareTo(this.distances[startIndex][j]))
                .mapToInt(ele -> ele).boxed().collect(Collectors.toList());
        sortedIndices.remove(0);
        return sortedIndices.stream().mapToInt(i -> i).toArray();
    }

    /*
    Metoda trazi najdaljeg korisnika od skladista ovisno o trenutno neposluzenim korisnicima
    s minimalnim ready timeom
     */
    private Customer findBestFirstCustomerFromDepot(int[] sortedIndexes) {
        int unservedCustomersFound = 0;
        List<Customer> candidateCustomers = new ArrayList<>();
        for (int i = sortedIndexes.length-1; i >=0; i--) {
            Customer customer = this.customers.get(sortedIndexes[i]);
            if (!customer.isServed()) {
                candidateCustomers.add(customer);
                unservedCustomersFound++;
            }
            if (unservedCustomersFound > this.greedyParamForFarthestCustomer) break;
        }
        int minReadyTime = Integer.MAX_VALUE;
        Customer bestFound = null;
        for (Customer customer : candidateCustomers) {
            if (customer.getReadyTime() < minReadyTime){
                minReadyTime = customer.getReadyTime();
                bestFound = customer;
            }
        }
        return bestFound;
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
            for (int i = 0; i < 4; i++) {
                reader.readLine();
            }
            int[] customerData;
            String row = reader.readLine();
            int row_num = 0;
            while (row != null) {
                customerData = Arrays.stream(row.strip().split("\\s+")).mapToInt(Integer::parseInt).toArray();
                Customer c = new Customer(customerData);
                if (row_num == 0) {
                    c.setArrivalTime(0);
                    c.setServed(true);
                    c.setPositionOnRoute(0.0);
                    this.depot = c;
                } else {
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
            int lineCount = (int) (Files.lines(path).count());
            BufferedReader reader = new BufferedReader(Files.newBufferedReader(path));
            this.distances = new double[lineCount][lineCount];
            for (int i = 0; i < lineCount; i++) {
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
