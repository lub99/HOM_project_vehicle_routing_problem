package hr.fer.hom.project.cvrptw.dataClasses;

public class Customer {
    private Integer customerIndex;
    private Integer xCoordinate;
    private Integer yCoordinate;
    private Integer demand;
    private Integer readyTime;
    private Integer dueDate;
    private Integer serviceTime;
    private boolean served;
    private Integer servedTime;
    private Integer positionOnRoute;

    public Customer(int customerIndex, int x, int y, int demand,
                    int readyTime, int dueDate, int serviceTime){
        this.customerIndex = customerIndex;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.demand = demand;
        this.readyTime = readyTime;
        this.dueDate = dueDate;
        this.serviceTime = serviceTime;
        this.served = false;
    }

    public Customer(int[] data){
        this.customerIndex = data[0];
        this.xCoordinate = data[1];
        this.yCoordinate = data[2];
        this.demand = data[3];
        this.readyTime = data[4];
        this.dueDate = data[5];
        this.serviceTime = data[6];
    }

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

    public boolean getServed() {
        return served;
    }

    public Integer getServedTime() {
        return servedTime;
    }

    public Integer getPositionOnRoute() {
        return positionOnRoute;
    }

    public void setServed(boolean condition) {
        this.served = condition;
    }
    public void setServedTime(int time) {
        this.servedTime = time;
    }
    public void setPositionOnRoute(int position) {
        this.positionOnRoute = position;
    }

}
