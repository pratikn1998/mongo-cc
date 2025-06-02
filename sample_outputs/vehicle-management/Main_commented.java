import vehicle.Car;
import vehicle.Truck;
import service.VehicleService;

/**
* The `Main` class serves as the entry point for the vehicle management application.
* It demonstrates the core functionality by creating instances of `Car` and `Truck`,
* which are concrete implementations of the `Vehicle` abstraction.
* <p>
* This class showcases the use of polymorphism by treating both `Car` and `Truck` objects
* as `Vehicle` types when interacting with the `VehicleService`.
* It instantiates a `VehicleService` and uses it to process different vehicle types,
* illustrating a client-service interaction pattern.
* </p>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li><code>main(String[] args)</code> - The application's starting point, responsible for
*       initializing vehicle objects and demonstrating service interactions.</li>
* </ul>
*
* @see Car
* @see Truck
* @see VehicleService
* @see Vehicle
*/
public class Main {
    /**
    * The entry point of the Vehicle Management application.
    * This method demonstrates the creation and usage of different vehicle types (Car and Truck)
    * and showcases how a {@link VehicleService} can interact with these vehicle objects polymorphically.
    *
    * <p>
    * The method performs the following steps:
    * <ol>
    *     <li>Instantiates a {@link Car} object with specific make, model, and year.</li>
    *     <li>Instantiates a {@link Truck} object with specific make, model, year, and towing capacity.</li>
    *     <li>Creates an instance of {@link VehicleService}.</li>
    *     <li>Calls {@link VehicleService#printVehicleDetails(Vehicle)} for the created {@link Car} object.
    *         This displays the car's information and simulates its start sequence.</li>
    *     <li>Calls {@link VehicleService#printVehicleDetails(Vehicle)} for the created {@link Truck} object.
    *         This displays the truck's information and simulates its start sequence.</li>
    * </ol>
    * This setup illustrates a simple client-service interaction pattern within the application's architecture,
    * where {@code Main} acts as the client orchestrating operations on {@link Vehicle} instances through the {@link VehicleService}.
    * </p>
    *
    * @param args Command line arguments (not used in this application).
    * @see Car
    * @see Truck
    * @see VehicleService
    * @see VehicleService#printVehicleDetails(Vehicle)
    */
    public static void main(String[] args) {
        Car car = new Car("Toyota", "Camry", 2022);
        Truck truck = new Truck("Ford", "F-150", 2021, 10000);

        VehicleService service = new VehicleService();
        service.printVehicleDetails(car);
        service.printVehicleDetails(truck);
    }
}