package hr.fer.hom.project.cvrptw.dataClasses;

public class Customer {
    private Integer customerIndex;
    private Integer xCoordinate;
    private Integer yCoordinate;
    private Integer demand;
    private Integer readyTime;
    private Integer dueDate;
    private Integer serviceTime;

    public Integer getCustomerIndex() {
        return customerIndex;
    }

    public Integer getxCoordinate() {
        return xCoordinate;
    }

    public Integer getyCoordinate() {
        return yCoordinate;
    }

    public Integer getDemand() {
        return demand;
    }

    public Integer getReadyTime() {
        return readyTime;
    }

    public Integer getDueDate() {
        return dueDate;
    }

    public Integer getServiceTime() {
        return serviceTime;
    }
}
