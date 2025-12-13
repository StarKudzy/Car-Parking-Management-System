package com.example.demo;

public class VehicleSearchRow {

    private final String plateNumber;
    private final String vehicleType;
    private final String brand;
    private final String colour;
    private final int wheels;
    private final String slotNumber;
    private final String slotType;
    private final String timeIn;

    public VehicleSearchRow(
            String plateNumber,
            String vehicleType,
            String brand,
            String colour,
            int wheels,
            String slotNumber,
            String slotType,
            String timeIn
    ) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.colour = colour;
        this.wheels = wheels;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.timeIn = timeIn;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public String getColour() {
        return colour;
    }

    public int getWheels() {
        return wheels;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public String getSlotType() {
        return slotType;
    }

    public String getTimeIn() {
        return timeIn;
    }
}
