package hr.fer.hom.project.cvrptw.utils;

import hr.fer.hom.project.cvrptw.dataClasses.Customer;
import hr.fer.hom.project.cvrptw.dataClasses.Solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class Util {

    /**
     * This method is used to print to file whose is used for python script to visualise solution on graph
     * */
    public static void printSolutionOnlyCustomerIndices(Solution solution, String outputFilePython){
        StringBuilder stringBuilder = new StringBuilder();

        var vehiclesUsed = solution.getVehiclesUsed();
        for (var vehicle : vehiclesUsed) {
            var oneRoute = vehicle.getRoute()
                    .stream()
                    .map(customer -> String.valueOf(customer.getCustomer().getCustomerIndex()))
                    .collect(Collectors.joining(","));
            stringBuilder.append(oneRoute).append("\n");
        }

        printToFile(outputFilePython, stringBuilder.toString());
    }

    public static void printToFile(String outputFile, String toPrint) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(toPrint);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
