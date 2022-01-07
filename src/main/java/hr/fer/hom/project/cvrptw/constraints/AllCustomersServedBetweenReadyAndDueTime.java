package hr.fer.hom.project.cvrptw.constraints;

import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

public class AllCustomersServedBetweenReadyAndDueTime implements IConstraint {
    @Override
    public boolean pass(Vehicle vehicle) {
        return false;
    }
}
