package service;

import vehicle.Vehicle;

/**
* The `VehicleService` class provides core functionalities related to vehicle operations within the application.
* It acts as a service layer component, abstracting vehicle-specific actions for other parts of the application,
* such as the `Main` class which utilizes this service to process different vehicle types.
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@link #printVehicleDetails(Vehicle)} - Displays information about a given vehicle and initiates its operation.</li>
* </ul>
*
* @see Vehicle
* @see Main
*/
public class VehicleService {
    /**
    * Prints the details of a given vehicle and initiates its starting sequence.
    * This method serves as an orchestrator, combining information display with operational activation.
    *
    * <p>
    * The method first retrieves a formatted string of the vehicle's information by calling
    * {@link Vehicle#getInfo()} and prints it to the console.
    * Subsequently, it invokes the {@link Vehicle#start()} method to simulate the vehicle
    * beginning its operation.
    * </p>
    *
    * @param vehicle The {@link Vehicle} object whose details are to be printed and started.
    *                This object must not be null.
    * @see Vehicle
    * @see Vehicle#getInfo()
    * @see Vehicle#start()
    */
    public void printVehicleDetails(Vehicle vehicle) {
        System.out.println("Vehicle Info: " + vehicle.getInfo());
        vehicle.start();
    }
}
