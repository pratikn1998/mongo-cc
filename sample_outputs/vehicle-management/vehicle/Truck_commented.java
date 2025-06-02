package vehicle;

/**
* Represents a Truck, which is a specialized type of Vehicle with an additional towing capacity attribute.
* <p>
* This class extends the {@link Vehicle} base class, inheriting common vehicle properties such as make, model, and year.
* It introduces a specific attribute, `towingCapacity`, to define the maximum weight the truck can tow.
* The class provides methods to access this towing capacity and includes example methods that demonstrate
* interaction with a utility class, {@link MathUtils}, for numerical operations related to the truck's towing capacity.
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li><code>int towingCapacity</code> - The towing capacity of the truck, typically measured in pounds or kilograms.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li><code>Truck(String make, String model, int year, int towingCapacity)</code> - Constructor to initialize a new Truck instance,
*       setting its make, model, year (inherited from Vehicle), and its specific towing capacity.</li>
*   <li><code>getTowingCapacity()</code> - Returns the towing capacity of the truck.</li>
*   <li><code>inferThisLLM(int extraCapacity)</code> - An example method that performs a calculation involving the truck's
*       towing capacity by delegating to {@link MathUtils#add(int, int)}. This method currently returns the
*       towing capacity itself, as the `extraCapacity` parameter is unused and `0` is added.</li>
*   <li><code>inferThisLLM2(int extraCapacity)</code> - Another example method that performs a calculation involving the truck's
*       towing capacity by delegating to {@link MathUtils#something(int, int)}. This method illustrates
*       a more general mathematical operation defined in the utility class.</li>
* </ul>
*
* @see Vehicle
* @see MathUtils
*/
public class Truck extends Vehicle {
    private int towingCapacity;

    public Truck(String make, String model, int year, int towingCapacity) {
        super(make, model, year);
        this.towingCapacity = towingCapacity;
    }

    /**
    * Retrieves the towing capacity of this truck.
    * This method provides read-only access to the `towingCapacity` attribute,
    * encapsulating the internal state of the `Truck` object.
    *
    * @return The towing capacity of the truck as an integer.
    */
    public int getTowingCapacity() {
        return towingCapacity;
    }

    /**
    * Calculates a value related to the truck's towing capacity.
    * This method currently returns the truck's base towing capacity without incorporating the `extraCapacity` parameter.
    * It utilizes a utility method from `MathUtils` for the calculation.
    *
    * @param extraCapacity An integer representing additional capacity, though currently unused in the calculation.
    * @return The truck's current `towingCapacity`.
    * @see MathUtils#add(int, int)
    */
    public int inferThisLLM(int extraCapacity) {
        return MathUtils.add(this.towingCapacity, 0);
    }

    /**
    * Calculates a value based on the truck's towing capacity using a utility method.
    * This method acts as a wrapper for a mathematical operation defined in {@link MathUtils}.
    *
    * @param extraCapacity An integer parameter that is currently unused in the calculation.
    * @return The result of the `MathUtils.something` operation, which is the product of the truck's
    *         towing capacity and 0. This effectively always returns 0.
    * @see MathUtils#something(int, int)
    */
    public int inferThisLLM2(int extraCapacity) {
        return MathUtils.something(this.towingCapacity, 0);
    }
}
