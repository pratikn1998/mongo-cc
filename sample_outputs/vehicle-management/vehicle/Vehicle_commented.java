package vehicle;

/**
* Represents a generic vehicle, serving as a foundational blueprint for various vehicle types.
* This class encapsulates common attributes such as make, model, and year, providing a basic
* structure for all vehicles within the system. It defines fundamental behaviors that any
* vehicle might exhibit, such as starting and providing basic information.
* <p>
* As a base class, `Vehicle` is designed to be extended by more specialized vehicle types
* (e.g., {@link Car}, {@link Truck}), allowing them to inherit and potentially override
* its core characteristics and functionalities.
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li><code>protected String make</code>: The brand or manufacturer of the vehicle.</li>
*   <li><code>protected String model</code>: The specific model name of the vehicle.</li>
*   <li><code>protected int year</code>: The manufacturing year of the vehicle.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li><code>Vehicle(String make, String model, int year)</code>: Constructor to initialize a new Vehicle instance.</li>
*   <li><code>start()</code>: Simulates the action of starting the vehicle.</li>
*   <li><code>getInfo()</code>: Retrieves a formatted string containing the vehicle's year, make, and model.</li>
* </ul>
*
* @see Car
* @see Truck
* @see VehicleService#printVehicleDetails(Vehicle)
*/
public class Vehicle {
    protected String make;
    protected String model;
    protected int year;

    public Vehicle(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    /**
    * Initiates the starting sequence for the vehicle.
    * <p>
    * This method simulates the action of a vehicle starting up. It prints a generic message to the console
    * indicating that the vehicle is in the process of starting.
    * </p>
    * <p>
    * In a more complex application, this method could be overridden by subclasses (e.g., {@code Car}, {@code Motorcycle})
    * to implement specific starting behaviors relevant to that vehicle type, such as checking fuel levels,
    * engaging ignition, or performing system diagnostics.
    * </p>
    * <p>
    * This method is called by {@link VehicleService#printVehicleDetails(Vehicle)} to demonstrate
    * a vehicle's operational capability after its details have been displayed.
    * </p>
    */
    public void start() {
        System.out.println("Vehicle is starting...");
    }

    /**
    * Retrieves a formatted string containing the basic identifying information of the vehicle.
    * This method concatenates the `year`, `make`, and `model` attributes into a single human-readable string.
    *
    * <p>This method is a simple accessor that provides a summary of the vehicle's core details.
    * It is utilized by other components, such as the {@code VehicleService.printVehicleDetails} method,
    * to display vehicle information to the user or for logging purposes.</p>
    *
    * @return A {@code String} representing the vehicle's year, make, and model (e.g., "2023 Toyota Camry").
    */
    public String getInfo() {
        return year + " " + make + " " + model;
    }
}
