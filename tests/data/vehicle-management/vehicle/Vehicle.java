package vehicle;

public class Vehicle {
    protected String make;
    protected String model;
    protected int year;

    public Vehicle(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public void start() {
        System.out.println("Vehicle is starting...");
    }

    public String getInfo() {
        return year + " " + make + " " + model;
    }
}
