package hr.fer.hom.project.cvrptw.lookupTables;

import hr.fer.hom.project.cvrptw.dataClasses.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistanceLookUp {
    private double[][] distances;

    public DistanceLookUp() {
    }

    private void importDistanceMatrix(String distancesFile) {
        try {
            Path path = Paths.get(distancesFile);
            int lineCount = (int)(Files.lines(path).count());
            BufferedReader reader = new BufferedReader(Files.newBufferedReader(path));
            this.distances = new double[lineCount][lineCount];
            for (int i=0; i<lineCount; i++){
                String row = reader.readLine();
                this.distances[i] = Arrays.stream(row.strip().split("\\s+")).mapToDouble(Double::parseDouble).toArray();
                //System.out.println(Arrays.toString(data[i]));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
Metoda trazi najdaljeg korisnika od skladista ovisno o trenutno neposluzenim korisnicima
 */
//    private Customer findFarthestCustomerFromDepot() {
//        double maxDistance = 0;
//        double distance;
//        int farthestCustomerIndex = 0;
//        int unservedCustomersCount = this.unservedCustomerIndexes.size();
//        for (int i=0; i<unservedCustomersCount; i++){
//            distance = this.distances[0][this.unservedCustomerIndexes.get(i)];
//            if (distance > maxDistance) {
//                maxDistance = distance;
//                farthestCustomerIndex = this.unservedCustomerIndexes.get(i);
//            }
//        }
//        return this.customers.get(farthestCustomerIndex);
//    }


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
}
