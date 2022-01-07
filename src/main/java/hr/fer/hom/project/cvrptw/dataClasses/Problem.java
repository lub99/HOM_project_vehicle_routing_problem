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
    private List<CustomerCalc> customersCalcs;
    private Customer depot; //-> takoder prvi korisnik u gornjoj listi
    private double[][] distances;
    private int vehicleLimit;
    private int vehicleCapacity;
    //private int vehiclesUsed;
    private List<Vehicle> vehicles;
    private List<Integer> unservedCustomerIndexes;
    private final int greedyParamForFarthestCustomer = 10;  //mijenjati ovisno o instanci (povecati proporc broju korisnika)
    private final int greedyParamForClosestCustomer = 5;
    //75 i 5 najbolje za i1, neka bude 10 i 5 zasad
    private final int initialTemperature = 100;
    private final int MAX_ITER = 10;

    public Problem() {
    }

    public static void main(String[] args) {

        Problem problem = new Problem();

        String instanceFile = args[0]; //"input/instances/i1.txt";
        String distancesFile = args[1]; //"input/distances/i1.txt";
        String outputFile = args[2];  //"output/solutions/i1.txt";
        String outputFileForPython = args[3];  //"output/plotting/i1.txt";
        /*
        String instanceFile = "input/instances/i1.txt";
        String distancesFile = "input/distances/i1.txt";
        String outputFile = "output/solutions/i1.txt";
        //String outputFileForPython = "output/plotting/i1.txt";
*/
        problem.importData(instanceFile);
        problem.importDistanceMatrix(distancesFile);
        Solution initialSolution = problem.greedyAlg();
        initialSolution.printToFile(outputFile);
        Util.printSolutionOnlyCustomerIndices(initialSolution, outputFileForPython);

        Solution optimizedSolution = problem.simulatedAnnealingOptimization(initialSolution);


    }

    /*
        current_solution = initial_solution;
        best_solution = current_solution;
        current_temperature = initial_temperature
        while(!termination_criterion){ //broj iteracija
             new_solution = select neighbor -> primijeniti niz operatora
             if (f(new_solution) < f(current_solution)) current_solution = new_solution
             else if (f(new_solution) >= f(current_solution)) and (rand(0,1) < Math.pow(e, -df/current_temperature)
                  current_solution = new_solution
             if (f(current_solution) < f(best_solution)) best_solution = current_solution
             update current_temperature
        }
        return best_solution;
         */
    private Solution simulatedAnnealingOptimization(Solution initialSolution) {
        List<Vehicle> vehicles = initialSolution.getVehiclesUsed();
        /*Vehicle v = vehicles.get(11);
        v.print();
        Customer probniKorisnik = this.customers.get(34);
        Vehicle newVehicle = v.insertCustomerAtIndex(probniKorisnik, 5);
        newVehicle.print();*/

        Vehicle v = vehicles.get(1);
        CustomerCalc c1 = v.getRoute().get(7);
        CustomerCalc c2 = v.getRoute().get(8);
        Vehicle newVehicle = v.twoCustomersIntraSwap(c1, c2);
        //Vehicle newVehicle = v.removeFromRoute(c);
        newVehicle.print();

        CustomerCalc newCustomer = vehicles.get(5).getRoute().get(6);
        System.out.println(newCustomer.printToString());
        CustomerCalc oldCustomer = v.getRoute().get(1);
        v.print();
        newVehicle = v.replaceCustomer(oldCustomer, newCustomer);
        boolean replaced = newVehicle.replaceSuccessful(v);
        System.out.println(replaced);
        newVehicle.print();

        return null;
    }

    private Solution greedyAlg() {
        Solution solution = new Solution();
        int unservedCustomersCount = this.unservedCustomerIndexes.size();
        this.vehicles = new ArrayList<>();
        int vehicleIndex = 0;
        int[] sortedCustomerIndexesFromDepot = sortCustomerIndexesByDistance(depot);
        while (unservedCustomersCount > 0 && this.vehicles.size() < this.vehicleLimit) {
            Vehicle vehicle = new Vehicle(vehicleIndex, this.vehicleCapacity, this.depot, this.distances);
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
                vehicle.addCustomerToEnd(nextCustomer);
                lastCustomer = nextCustomer;
            }
            vehicle.returnToGarage();
            this.vehicles.add(vehicle);
            vehicleIndex++;
        }
        if (unservedCustomersCount > 0) {
            System.out.println("Infeasible solution, unserved customers exist");
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
        Customer bestFound = null;
        int minTimeToDueDateOfFeasibleSolution = Integer.MAX_VALUE;
        for (Customer customer : candidateCustomers) {
            int[] returnStatement = vehicle.checkIfCustomerCanBeAddedToEnd(customer);
            if (returnStatement[0] == 0) continue;
            if (returnStatement[1] < minTimeToDueDateOfFeasibleSolution) {
                minTimeToDueDateOfFeasibleSolution = returnStatement[1];
                bestFound = customer;
            }
        }
        return bestFound;
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

    /*
    Add customer from route with min num of customers to some short route if possible
      -> za minimizaciju broja vozila
      -> prebaciti u Problem
     */

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

    private void importData(String instance) {
        this.customers = new ArrayList<>();
        this.customersCalcs = new ArrayList<>();
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
                CustomerCalc cCalc = new CustomerCalc(c);
                if (row_num == 0) {
                    c.setServed(true);
                    this.depot = c;
                } else {
                    this.unservedCustomerIndexes.add(c.getCustomerIndex());
                }
                this.customers.add(c);
                this.customersCalcs.add(cCalc);
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

    /*
    U vehicle1 stavljamo customer2 i suprotno
     */
    public Vehicle[] twoCustomerInterSwap(Vehicle vehicle1, Vehicle vehicle2, CustomerCalc customer1, CustomerCalc customer2){
        Vehicle[] newVehicles = new Vehicle[2];
        newVehicles[0] = vehicle1;
        newVehicles[1] = vehicle2;
        boolean replaced;
        Vehicle newVehicle1 = vehicle1.replaceCustomer(customer1, customer2);
        replaced = newVehicle1.replaceSuccessful(vehicle1);
        if (!replaced) return newVehicles;
        Vehicle newVehicle2 = vehicle2.replaceCustomer(customer2, customer1);
        replaced = newVehicle2.replaceSuccessful(vehicle2);
        if (!replaced) return newVehicles;
        newVehicles[0] = newVehicle1;
        newVehicles[1] = newVehicle2;
        return newVehicles;
    }

}
