package hr.fer.hom.project.cvrptw.lookupTables;

import hr.fer.hom.project.cvrptw.dataClasses.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class InputDataLookUp {
    // init to empty
    private Customer[] customers;
    private int vehicleLimit;
    private int vehicleCapacity;

    public InputDataLookUp(String instanceFileRelativePath) {
        importData(instanceFileRelativePath);
    }

    private void importData(String instanceFileRelativePath) {
        ArrayList<Customer> customersTemp = new ArrayList<>();
        try {
            Path path = Paths.get(instanceFileRelativePath);
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
            while (row != null) {
                customerData = Arrays.stream(row.strip().split("\\s+")).mapToInt(Integer::parseInt).toArray();
                customersTemp.add(new Customer(customerData));
                row = reader.readLine();
            }
            reader.close();

            this.customers = new Customer[customersTemp.size()];
            this.customers = customersTemp.toArray(this.customers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // with depot include in customers count
    public int getCustomersCount() {
        return customers.length;
    }

    public Customer getCustomerByIndex(int index) {
        assert index >= 0 && index < customers.length;
        return customers[index];
    }

    public int getVehicleLimit() {
        return vehicleLimit;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }
}
