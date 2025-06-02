package vehicle;

public class Truck extends Vehicle {
    private int towingCapacity;

    public Truck(String make, String model, int year, int towingCapacity) {
        super(make, model, year);
        this.towingCapacity = towingCapacity;
    }

    public int getTowingCapacity() {
        return towingCapacity;
    }

    public int inferThisLLM(int extraCapacity) {
        return MathUtils.add(this.towingCapacity, 0);
    }

    public int inferThisLLM2(int extraCapacity) {
        return MathUtils.something(this.towingCapacity, 0);
    }
}
