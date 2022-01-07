package hr.fer.hom.project.cvrptw.dataClasses;

import java.util.Objects;

public class CustomerCalc {

    private Customer customer;
    private Integer arrivalTime;
    private Double positionOnRoute;

    public CustomerCalc(Customer c){
        this.customer = c;
    }

    public CustomerCalc(Customer c, int arrivalTime, double positionOnRoute){
        this.customer = c;
        this.arrivalTime = arrivalTime;
        this.positionOnRoute = positionOnRoute;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(Integer arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public Double getPositionOnRoute() {
        return positionOnRoute;
    }
    public void setPositionOnRoute(Double positionOnRoute) {
        this.positionOnRoute = positionOnRoute;
    }
    public String printToString() {
        return customer.getCustomerIndex() + "(" + arrivalTime + ")";
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerCalc customer = (CustomerCalc) o;
        return Objects.equals(((CustomerCalc) o).getCustomer().getCustomerIndex(),
                customer.getCustomer().getCustomerIndex())
                && Objects.equals(arrivalTime, customer.arrivalTime);
    }
}
