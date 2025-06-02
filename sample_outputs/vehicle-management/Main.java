import vehicle.Car;
import vehicle.Truck;
import service.VehicleService;

public class Main {
    public static void main(String[] args) {
        Car car = new Car("Toyota", "Camry", 2022);
        Truck truck = new Truck("Ford", "F-150", 2021, 10000);

        VehicleService service = new VehicleService();
        service.printVehicleDetails(car);
        service.printVehicleDetails(truck);
    }
}