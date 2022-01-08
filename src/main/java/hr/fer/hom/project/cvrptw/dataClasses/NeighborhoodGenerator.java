package hr.fer.hom.project.cvrptw.dataClasses;

import hr.fer.hom.project.cvrptw.operators.RelocateCustomerInterOp;
import hr.fer.hom.project.cvrptw.operators.RelocateCustomerIntraOp;
import hr.fer.hom.project.cvrptw.operators.TwoCustomersSwapInterOp;
import hr.fer.hom.project.cvrptw.operators.TwoCustomersSwapIntraOp;

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
        RelocateCustomerIntraOp relocateIntraOp = new RelocateCustomerIntraOp(newSolution);
        newSolution = relocateIntraOp.run();
        TwoCustomersSwapIntraOp twoSwapIntraOp = new TwoCustomersSwapIntraOp(newSolution);
        newSolution = twoSwapIntraOp.run();
        TwoCustomersSwapInterOp twoSwapInterOp = new TwoCustomersSwapInterOp(newSolution);
        newSolution = twoSwapInterOp.run();
        RelocateCustomerInterOp relocateInterOp = new RelocateCustomerInterOp(newSolution);
        newSolution = relocateInterOp.run();
        return newSolution;
    }

    /*
    Implementirani intra operatori:
     - two customers intra swap
     - relocate customer -> insert customer within one vehicle
    Implementirani inter operatori:
     - two customers inter swap
     - relocate customer -> insert customer from one vehicle to other
     */
}
