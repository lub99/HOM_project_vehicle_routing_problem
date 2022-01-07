package hr.fer.hom.project.cvrptw.constraints;

import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

import java.util.List;

public class ConstraintCalculator {
    private List<IConstraint> constraints;

    public ConstraintCalculator(List<IConstraint> constraints) {
        this.constraints = constraints;
    }

    public boolean passAllConstraints(Vehicle vehicle) {
        return constraints.stream().map(constraint -> constraint.pass(vehicle)).reduce(Boolean::logicalAnd).orElseThrow();
    }
}
