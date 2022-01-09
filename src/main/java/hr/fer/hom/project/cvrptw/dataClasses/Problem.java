package hr.fer.hom.project.cvrptw.dataClasses;

import hr.fer.hom.project.cvrptw.Timer;
import hr.fer.hom.project.cvrptw.utils.Util;

import java.io.*;
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
    private List<Vehicle> vehicles;
    private List<Integer> unservedCustomerIndexes;
    private int greedyParamForFarthestCustomer; // = 150;  //mijenjati ovisno o instanci (povecati proporc broju korisnika)
    private final int greedyParamForClosestCustomer = 5;
    //75 i 3 najbolje za i1
    private final int initialTemperature = 50;  //25
    private final double finalTemperature = 0.01;
    private final double beta = 0.001;
    private final int MAX_ITER = 10000;
    //private final int MAX_ITER_NO_IMPROVEMENT = (int) 0.01*MAX_ITER; //1%
    private final int MAX_ITER_NO_IMPROVEMENT = 250;
    private Timer timer;

    public Problem() {
    }

    public static void main(String[] args) {

        Problem problem = new Problem();

        //int instanceIndex = Integer.parseInt(args[0]);
        //int timeLimit = Integer.parseInt(args[1]);
        int instanceIndex = 6;
        int timeLimit = 1;
        problem.setTimer(timeLimit);
        String inputFileName = "i" + instanceIndex + ".txt";
        String outputFileName = "res-" + timeLimit + "m-i" + instanceIndex + ".txt";

        String instanceFile = "input/instances/" + inputFileName;
        String distancesFile = "input/distances/" + inputFileName;
        String outputFile = "output/solutions/" + outputFileName;
        String outputFileBestSeenSolutionParams = "output/bestSeenSolutionsParams/" + outputFileName;
        String outputFileForPython = "output/plotting/" + outputFileName;

        problem.importData(instanceFile);
        problem.importDistanceMatrix(distancesFile);

        boolean previousSolutionExists = problem.checkIfSomeSolutionExists(outputFile);
        double[] lastBestSavedSolutionParameters = null;
        if (previousSolutionExists) {
            lastBestSavedSolutionParameters = problem.importLastSavedSolution(outputFile);
        }

        Solution initialSolution = problem.greedyAlg();
        System.out.println(initialSolution.toString());

        Solution optimizedSolution = problem.simulatedAnnealingOptimization(initialSolution);
        System.out.println(optimizedSolution.toString());

        double[] currentSolutionParameters = optimizedSolution.getParameters();
        if (!previousSolutionExists) {
            System.out.println("New solution found, saving solution");
            optimizedSolution.printToFile(outputFile);
            Util.printSolutionOnlyCustomerIndices(optimizedSolution, outputFileForPython);
        } else {
            if (problem.checkIfNewSolutionIsBetter(lastBestSavedSolutionParameters, currentSolutionParameters)) {
                System.out.println("Better solution found, saving solution");
                optimizedSolution.printToFile(outputFile);
                problem.printToFile(outputFileBestSeenSolutionParams, problem.toString()
                        + "\n number_of_iterations=" + optimizedSolution.getNumberOfIter() + "\n" +
                        "final_SA_temp=" + optimizedSolution.getFinalTempSA()
                );
                Util.printSolutionOnlyCustomerIndices(optimizedSolution, outputFileForPython);
            }
        }
    }

    private Solution simulatedAnnealingOptimization(Solution initialSolution) {
        Solution currentSolution = initialSolution.copy();
        Solution bestSolution = initialSolution.copy();
        double currentTemperature = initialTemperature;
        int iter = 0;
        int no_improvement_iters = 0;
        NeighborhoodGenerator neighborhoodGenerator = new NeighborhoodGenerator();
        while (iter < MAX_ITER //){ //currentTemperature > finalTemperature
                || System.currentTimeMillis() < this.timer.getEnd()) {
            if (no_improvement_iters >= MAX_ITER_NO_IMPROVEMENT) {
                neighborhoodGenerator.setPreviousSolution(bestSolution);
            } else {
                neighborhoodGenerator.setPreviousSolution(currentSolution);
            }
            Solution newSolution = neighborhoodGenerator.selectNeighbor();
            if (currentSolution.checkIfNewSolutionIsBetterPlus(newSolution)) {
                currentSolution = newSolution.copy();
            } else if (checkTemperatureCondition(currentSolution, newSolution, currentTemperature)) {
                currentSolution = newSolution.copy();
            }
            if (bestSolution.checkIfNewSolutionIsBetterPlus(currentSolution)) {
                bestSolution = currentSolution.copy();
                //currentTemperature *= 0.99;
                currentTemperature /= (1 + this.beta * currentTemperature);
                no_improvement_iters = 0;
            }
            iter++;
            no_improvement_iters++;
        }
        System.out.println("Number of iterations: " + iter);
        System.out.println("Final temperature: " + currentTemperature);
        bestSolution.setNumberOfIter(iter);
        bestSolution.setFinalTempSA(currentTemperature);
        return bestSolution;
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
        for (int i = sortedIndexes.length - 1; i >= 0; i--) {
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
            if (customer.getReadyTime() < minReadyTime) {
                minReadyTime = customer.getReadyTime();
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

    private boolean checkTemperatureCondition(Solution currentSolution, Solution newSolution, double temp) {
        double df = currentSolution.getTotalDistance() - newSolution.getTotalDistance();
        double num = Math.exp(df / temp);
        return Math.random() < num;
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
            this.greedyParamForFarthestCustomer = (int) ((double) this.customerCount * 0.75);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] importLastSavedSolution(String filePath) {
        double[] previousSolution = new double[2];
        try {
            Path path = Paths.get(filePath);
            BufferedReader reader = new BufferedReader(Files.newBufferedReader(path));
            String firstLine = reader.readLine();
            int vehicleCount = Integer.parseInt(firstLine);
            for (int i = 0; i < vehicleCount; i++) {
                reader.readLine();
            }
            double routeLength = Double.parseDouble(reader.readLine());
            previousSolution[0] = Double.valueOf(vehicleCount);
            previousSolution[1] = routeLength;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return previousSolution;
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

    private void setTimer(int factor) {
        this.timer = new Timer(factor);
    }

    /*
    U vehicle1 stavljamo customer2 i suprotno
     */
    public static Vehicle[] twoCustomerInterSwap(Vehicle vehicle1, Vehicle vehicle2, CustomerCalc customer1, CustomerCalc customer2) {
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

    private boolean checkIfNewSolutionIsBetter(double[] previousSolutionParams, double[] newSolutionParams) {
        if (newSolutionParams[0] < previousSolutionParams[0]) return true;
        if (newSolutionParams[0] == previousSolutionParams[0] &&
                newSolutionParams[1] < previousSolutionParams[1]) return true;
        return false;
    }

    private boolean checkIfSomeSolutionExists(String outputFile) {
        Path path = Paths.get(outputFile);
        return Files.exists(path);
    }

    @Override
    public String toString() {
        return "greedyParamForFarthestCustomer=" + greedyParamForFarthestCustomer + "\n" +
                "greedyParamForClosestCustomer=" + greedyParamForClosestCustomer + "\n" +
                "initialTemperature=" + initialTemperature + "\n" +
                "finalTemperature=" + finalTemperature + "\n" +
                "beta=" + beta + "\n" +
                "MAX_ITER=" + MAX_ITER + "\n" +
                "MAX_ITER_NO_IMPROVEMENT=" + MAX_ITER_NO_IMPROVEMENT;
    }

    public void printToFile(String outputFile, String toPrint) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(toPrint);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
