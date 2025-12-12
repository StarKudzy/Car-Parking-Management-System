package com.example.demo;

import java.time.LocalDateTime;

public class ParkingSlot {

    private int slotId;
    private String slotNumber;
    private String slotType;
    private String status;
    private String plateNumber;
    private LocalDateTime timeIn;

    public ParkingSlot(int slotId, String slotNumber, String slotType,
                       String status, String plateNumber, LocalDateTime timeIn) {
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.status = status;
        this.plateNumber = plateNumber;
        this.timeIn = timeIn;
    }

    public int getSlotId() {
        return slotId;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public String getSlotType() {
        return slotType;
    }

    public String getStatus() {
        return status;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public LocalDateTime getTimeIn() {
        return timeIn;
    }
}
