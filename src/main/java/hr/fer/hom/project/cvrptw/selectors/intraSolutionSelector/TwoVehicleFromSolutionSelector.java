package hr.fer.hom.project.cvrptw.selectors.intraSolutionSelector;

import hr.fer.hom.project.cvrptw.dataClasses.Solution;
import hr.fer.hom.project.cvrptw.dataClasses.Vehicle;

public interface TwoVehicleFromSolutionSelector {
    /**
     * @return two vehicles ready for inter cross
     * */
    Vehicle[] selectTwoVehiclesToInterCross(Solution solution);
}
