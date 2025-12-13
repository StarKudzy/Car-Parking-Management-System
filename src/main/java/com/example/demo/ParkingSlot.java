package com.example.demo;

public class ParkingSlot {

    private int slotId;
    private String slotNumber;
    private String slotType;
    private String status;

    public ParkingSlot(int slotId, String slotNumber, String slotType, String status) {
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.status = status;
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
}
