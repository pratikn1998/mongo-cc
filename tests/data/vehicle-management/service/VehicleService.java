package service;

import vehicle.Vehicle;

public class VehicleService {
    public void printVehicleDetails(Vehicle vehicle) {
        System.out.println("Vehicle Info: " + vehicle.getInfo());
        vehicle.start();
    }
}
