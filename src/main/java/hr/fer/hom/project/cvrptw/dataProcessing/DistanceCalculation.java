package hr.fer.hom.project.cvrptw.dataProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class DistanceCalculation {

    public static void main(String[] args) {

        String distances = "";
        String inputFile = "input/instances/i4.txt";
        String outputFile = "input/distances/i4.txt";

        try {
            Path path = Paths.get(inputFile);
            int lineCount = (int)(Files.lines(path).count());
            BufferedReader reader = new BufferedReader(Files.newBufferedReader(path));
            int[][] data = new int[lineCount-7][7];
            for (int i=0; i<7; i++){
                reader.readLine();
            }
            String row = reader.readLine();
            int row_num = 0;
            while (row != null) {
                data[row_num] = Arrays.stream(row.strip().split("\\s+")).mapToInt(Integer::parseInt).toArray();
                row = reader.readLine();
                row_num++;
            }
            reader.close();

            double distance;
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.UK);
            DecimalFormat df = (DecimalFormat)nf;
            df.setMaximumFractionDigits(4);
            for (int i=0; i< data.length; i++){
                for (int j=0; j<data.length; j++){
                    distance = Math.sqrt(Math.pow((data[i][1] - data[j][1]), 2)
                                       + Math.pow((data[i][2] - data[j][2]), 2));
                    distances += df.format(distance) + " ";
                }
                distances += "\n";
            }
            //System.out.println(distances);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(distances);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
