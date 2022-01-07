package hr.fer.hom.project.cvrptw.operators.inter;

import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

public interface InterOperator {

    Vehicle[] interCross(Vehicle first, Vehicle second);
}
