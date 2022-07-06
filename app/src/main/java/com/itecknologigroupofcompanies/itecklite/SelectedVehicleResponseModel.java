package com.itecknologigroupofcompanies.itecklite;

public class SelectedVehicleResponseModel {
    String BatteryHealth;
    Double BatteryVolt;
    int Ignition;
    String Location;
    int Speed;
    String VehicleNo;

    public SelectedVehicleResponseModel(String batteryHealth, Double batteryVolt, int ignition, String location, int speed, String vehicleNo) {
        BatteryHealth = batteryHealth;
        BatteryVolt = batteryVolt;
        Ignition = ignition;
        Location = location;
        Speed = speed;
        VehicleNo = vehicleNo;
    }

    public String getBatteryHealth() {
        return BatteryHealth;
    }

    public void setBatteryHealth(String batteryHealth) {
        BatteryHealth = batteryHealth;
    }

    public Double getBatteryVolt() {
        return BatteryVolt;
    }

    public void setBatteryVolt(Double batteryVolt) {
        BatteryVolt = batteryVolt;
    }

    public int getIgnition() {
        return Ignition;
    }

    public void setIgnition(int ignition) {
        Ignition = ignition;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getSpeed() {
        return Speed;
    }

    public void setSpeed(int speed) {
        Speed = speed;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }
}
