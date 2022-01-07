package hr.fer.hom.project.cvrptw;

import hr.fer.hom.project.cvrptw.dataClasses.Solution;
import hr.fer.hom.project.cvrptw.neighbourhoodGenerators.NeighbourhoodGenerator;

public class SimulatedAnnealing {
    private NeighbourhoodGenerator neighbourhoodGenerator;
    // maybe instead of this class that handle temperature decrease
    private Double initialTemperature;
    private Solution bestSeenSolution;

    public SimulatedAnnealing(NeighbourhoodGenerator neighbourhoodGenerator, Double initialTemperature, Solution initialSolution) {
        this.neighbourhoodGenerator = neighbourhoodGenerator;
        this.initialTemperature = initialTemperature;
        this.bestSeenSolution = initialSolution;
    }

    public Solution run() {
        // TODO implement SA
        return null;
    }
}
