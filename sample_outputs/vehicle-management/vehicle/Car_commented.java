package vehicle;


/**
* Represents a Car, which is a specialized type of {@link Vehicle} designed for personal transportation.
* <p>
* This class extends the {@link Vehicle} base class, inheriting common attributes such as make, model, and year.
* It provides a constructor to initialize these inherited properties and includes specific behaviors
* relevant to a car, such as playing music.
* </p>
*
* <p><b>Inheritance:</b></p>
* <ul>
*   <li>Extends {@link Vehicle}: Inherits core vehicle properties and behaviors.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li><code>Car(String make, String model, int year)</code>: Constructor to initialize a new Car instance.</li>
*   <li><code>playMusic()</code>: Simulates playing music, demonstrating a car-specific action.</li>
* </ul>
*
* @see Vehicle
*/
public class Car extends Vehicle {
    public Car(String make, String model, int year) {
        super(make, model, year);
    }

    /**
    * Simulates the action of playing music within the car.
    * This method demonstrates a specific capability of the {@code Car} class.
    * <p>
    * Although it performs a calculation using {@link MathUtils#add(int, int)},
    * the result of this calculation is not directly utilized for the music playback simulation itself.
    * This suggests a potential dependency on a utility class for general mathematical operations,
    * even if the specific calculation is not directly relevant to the method's primary function.
    * </p>
    * The primary function of this method is to print a confirmation message to the console,
    * indicating that music is being played.
    */
    public void playMusic() {
        int result = MathUtils.add(5, 7);
        System.out.println("Playing music...");
    }

    
}
