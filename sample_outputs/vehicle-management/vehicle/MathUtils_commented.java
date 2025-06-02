package vehicle;

/**
* The {@code MathUtils} class provides fundamental mathematical operations as static utility methods.
* It encapsulates basic arithmetic functions like addition and multiplication, making them readily available
* without requiring an instance of the class. This design pattern suggests {@code MathUtils} serves as a
* stateless helper class, likely used across various parts of the application requiring simple numerical computations.
* Its static nature implies it's a foundational component, offering reusable mathematical logic to other classes
* within the system, such as those in the vehicle management domain.
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@link #add(int, int) add(int, int)}: Performs addition of two integers.</li>
*   <li>{@link #something(int, int) something(int, int)}: Performs multiplication of two integers.</li>
* </ul>
*
* <p>This class is designed to be a utility class and therefore should not be instantiated.</p>
*/
public class MathUtils {
    /**
    * Adds two integer values and returns their sum.
    * This method is a static utility function within the `MathUtils` class,
    * designed for performing basic arithmetic addition.
    *
    * @param a The first integer operand.
    * @param b The second integer operand.
    * @return The sum of `a` and `b`.
    */
    public static int add(int a, int b) {
        return a + b;
    }
    /**
    * Performs a basic arithmetic multiplication operation on two integer inputs.
    * This method is a static utility function within the {@link MathUtils} class,
    * designed for direct invocation without requiring an instance of {@code MathUtils}.
    * It serves as a foundational, reusable component for numerical calculations
    * across various parts of the application, such as calculating costs or dimensions.
    *
    * @param a The first integer operand.
    * @param b The second integer operand.
    * @return The product of {@code a} and {@code b}.
    * @see MathUtils#add(int, int)
    * @see Truck#inferThisLLM2(int)
    */
    public static int something(int a, int b) {
        return a * b;
    }
}
