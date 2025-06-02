package vehicle;


public class Car extends Vehicle {
    public Car(String make, String model, int year) {
        super(make, model, year);
    }

    public void playMusic() {
        System.out.println("Playing music...");
    }
}
