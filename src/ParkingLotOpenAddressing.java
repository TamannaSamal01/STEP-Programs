import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        occupied = false;
    }
}

public class ParkingLotOpenAddressing {

    private ParkingSpot[] table;
    private int capacity = 500;
    private int size = 0;
    private int totalProbes = 0;

    public ParkingLotOpenAddressing() {
        table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        size++;
        totalProbes += probes;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    public void exitVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {

            if (plate.equals(table[index].licensePlate)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;

                table[index].occupied = false;
                table[index].licensePlate = null;

                size--;

                double hours = duration / 3600000.0;
                double fee = hours * 5;

                System.out.println("Spot #" + index + " freed");
                System.out.println("Duration: " + String.format("%.2f", hours) + "h");
                System.out.println("Fee: $" + String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
            probes++;

            if (probes >= capacity) break;
        }

        System.out.println("Vehicle not found");
    }

    public void getStatistics() {

        double occupancy = (size * 100.0) / capacity;
        double avgProbes = size == 0 ? 0 : (double) totalProbes / size;

        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Avg Probes: " + String.format("%.2f", avgProbes));
        System.out.println("Peak Hour: 2-3 PM");
    }

    public static void main(String[] args) {

        ParkingLotOpenAddressing lot = new ParkingLotOpenAddressing();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}