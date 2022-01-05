package hr.fer.hom.project.cvrptw.dataClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Problem {

    private int customerCount;
    private List<Customer> customers;
    private Customer depot;
    private double[][] distances;
    //private int vehicleLimit;
    private int vehicleCapacity;
    //private int vehiclesUsed;
    private List<Vehicle> vehicles;

    public Problem(){}

    public static void main(String[] args) {

        Problem problem = new Problem();

        String instanceFile = "input/instances/i1.txt";
        String distancesFile = "input/distances/i1.txt";
        //problem.importData(instanceFile);
        //problem.importDistanceMatrix(distancesFile);
        Solution initialSolution = problem.greedyAlg();

    }

    private Solution greedyAlg() {
        Solution solution = new Solution();
        /*
        dok postoji neposluzeni korisnik{   -> impl neku jednostavnu provjeru
             uzmi novo vozilo  -> inicijalizirati novo vozilo, prvi korisnik skladiste
             dok vozilo nekog moze posluziti{  -> provjera broja paketa u vozilu s potraznjom korisnika
                                                  te provjera trenutnog vremena voznje sa zadanim service
                                                  vremenima korisnika
                 ako je vozilo u skladistu{
                      sljedeci korisnik je npr najdalji korisnik -> dogovoriti
                 inace{
                      odabrati sljedeceg korisnika pomocu neke heuristike
                 posluziti korisnika -> azurirati sve potrebno (korisnika i vozilo)
                                        izracunati trenutno predenu udaljenosti i trenutno vrijeme
             }
             vratiti vozilo u skladiste -> ponovno azuriranje podataka
         */

        return solution;
    }

    private void importData(String instance) {
        this.customers = new ArrayList<>();
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
                if(row_num == 0){
                    this.depot = c;
                }else{
                    this.customers.add(c);
                }
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
            double[][] data = new double[lineCount][lineCount];
            for (int i=0; i<lineCount; i++){
                String row = reader.readLine();
                data[i] = Arrays.stream(row.strip().split("\\s+")).mapToDouble(Double::parseDouble).toArray();
                //System.out.println(Arrays.toString(data[i]));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
