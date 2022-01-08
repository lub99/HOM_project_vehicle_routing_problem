package hr.fer.hom.project.cvrptw.dataClasses;

public class NeighborhoodGenerator {

    private Solution previousSolution;

    public NeighborhoodGenerator(Solution previousSolution){
        this.previousSolution = previousSolution;
    }

    /*
    returns new solution
     */
    public Solution selectNeighbor() {
        Solution newSolution = this.previousSolution.copy();
        //primjena operatora
        return newSolution;
    }
}
