package hr.fer.hom.project.cvrptw.neighbourhoodGenerators;

import hr.fer.hom.project.cvrptw.dataClasses.Solution;
import hr.fer.hom.project.cvrptw.operators.inter.InterOperator;
import hr.fer.hom.project.cvrptw.operators.intra.IntraOperator;

import java.util.List;

/**
 * There goes logic for neighborhood solution selection
 */
public class NeighbourhoodGenerator {
    private List<InterOperator> interOperators;
    private List<IntraOperator> intraOperators;

    public NeighbourhoodGenerator(List<InterOperator> interOperators, List<IntraOperator> intraOperators) {
        this.interOperators = interOperators;
        this.intraOperators = intraOperators;
    }

    public Solution generateNeighbourhoodSolution(Solution currentSolution) {
        // operators are applied one after another
        return null;
    }


}
