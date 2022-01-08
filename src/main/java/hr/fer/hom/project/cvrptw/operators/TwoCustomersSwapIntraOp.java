package hr.fer.hom.project.cvrptw.operators;

import hr.fer.hom.project.cvrptw.dataClasses.Solution;

public class TwoCustomersSwapIntraOp {

    private Solution solution;

    public TwoCustomersSwapIntraOp(Solution solution){
        this.solution = solution;
    }

    public Solution run(){
        return this.solution;
    }
}
