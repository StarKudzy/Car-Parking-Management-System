package com.example.demo;

public class ReportRow {

    private final String period;
    private final int totalVehicles;
    private final int vipCount;
    private final int normalCount;

    public ReportRow(String period, int totalVehicles, int vipCount, int normalCount) {
        this.period = period;
        this.totalVehicles = totalVehicles;
        this.vipCount = vipCount;
        this.normalCount = normalCount;
    }

    public String getPeriod() {
        return period;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public int getVipCount() {
        return vipCount;
    }

    public int getNormalCount() {
        return normalCount;
    }
}
