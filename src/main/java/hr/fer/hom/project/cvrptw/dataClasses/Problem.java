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

    public int customerCount;
    public List<Customer> customers;
    public Customer depot;
    public double[][] distances;
    public int vehicleLimit;
    public int vehicleCapacity;
    public int vehicleCount;

    public Problem(){}

    public static void main(String[] args) {

        Problem problem = new Problem();

        String instanceFile = "input/instances/i1.txt";
        String distancesFile = "input/distances/i1.txt";
        //problem.importData(instanceFile);
        //problem.importDistanceMatrix(distancesFile);

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
