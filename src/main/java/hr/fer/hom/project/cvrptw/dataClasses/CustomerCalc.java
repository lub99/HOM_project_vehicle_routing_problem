package hr.fer.hom.project.cvrptw.dataClasses;

public class CustomerCalc {

    private Customer customer;
    //private boolean served;
    private Integer arrivalTime;
    private Double positionOnRoute;

    public CustomerCalc(Customer c){
        this.customer = c;
    }

    public CustomerCalc(Customer c, int arrivalTime, double positionOnRoute){
        this.customer = c;
        //this.served = served;
        this.arrivalTime = arrivalTime;
        this.positionOnRoute = positionOnRoute;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    /*public boolean isServed() {
        return served;
    }
    public void setServed(boolean served) {
        this.served = served;
    }*/
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
}
