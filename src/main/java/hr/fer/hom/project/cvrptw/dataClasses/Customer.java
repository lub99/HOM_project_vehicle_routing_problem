package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.Objects;

public class Customer {
    private Integer customerIndex;
    private Integer xCoordinate;
    private Integer yCoordinate;
    private Integer demand;
    private Integer readyTime;
    private Integer dueDate;
    private Integer serviceTime;
    private boolean served;

    public Customer(int customerIndex, int x, int y, int demand,
                    int readyTime, int dueDate, int serviceTime) {
        this.customerIndex = customerIndex;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.demand = demand;
        this.readyTime = readyTime;
        this.dueDate = dueDate;
        this.serviceTime = serviceTime;
    }

    public Customer(int[] data) {
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

    public boolean isServed() { return served; }

    public void setServed(boolean condition) { this.served = condition;}

   /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerIndex, customer.customerIndex)
                && Objects.equals(arrivalTime, customer.arrivalTime);
    }*/

   /* @Override
    public int hashCode() {
        return Objects.hash(customerIndex, arrivalTime);
    }*/

    /*public Customer copy() {
        var copyCustomer = new Customer(
                customerIndex,
                xCoordinate,
                yCoordinate,
                demand,
                readyTime,
                dueDate,
                serviceTime);
        copyCustomer.setServed(served);
        copyCustomer.setArrivalTime(arrivalTime);
        copyCustomer.setPositionOnRoute(positionOnRoute);
        return copyCustomer;
    }*/
}
