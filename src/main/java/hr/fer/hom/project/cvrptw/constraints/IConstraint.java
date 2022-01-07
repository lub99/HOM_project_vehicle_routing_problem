package hr.fer.hom.project.cvrptw.constraints;

import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

public interface IConstraint {
    boolean pass(Vehicle vehicle);
}
