package com.itecknologigroupofcompanies.itecklite;


public class SelectedVehicleResponseModel {
    String BatteryHealth;
    String BatteryVolt;
    GpsTime GpsTime;
    String Ignition;
    String Location;
    String Speed;
    String VehicleNo;
    String X;
    String Y;
    String Ang;
    String CustName;


    public SelectedVehicleResponseModel(String batteryHealth, String batteryVolt, com.itecknologigroupofcompanies.itecklite.GpsTime gpsTime, String ignition, String location, String speed, String vehicleNo, String x, String y, String ang, String custName) {
        BatteryHealth = batteryHealth;
        BatteryVolt = batteryVolt;
        GpsTime = gpsTime;
        Ignition = ignition;
        Location = location;
        Speed = speed;
        VehicleNo = vehicleNo;
        X = x;
        Y = y;
        Ang = ang;
        CustName = custName;
    }


    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getAng() {
        return Ang;
    }

    public void setAng(String ang) {
        Ang = ang;
    }

    public String getBatteryHealth() {
        return BatteryHealth;
    }

    public void setBatteryHealth(String batteryHealth) {
        BatteryHealth = batteryHealth;
    }

    public String getBatteryVolt() {
        return BatteryVolt;
    }

    public void setBatteryVolt(String batteryVolt) {
        BatteryVolt = batteryVolt;
    }

    public com.itecknologigroupofcompanies.itecklite.GpsTime getGpsTime() {
        return GpsTime;
    }

    public void setGpsTime(com.itecknologigroupofcompanies.itecklite.GpsTime gpsTime) {
        GpsTime = gpsTime;
    }

    public String getIgnition() {
        return Ignition;
    }

    public void setIgnition(String ignition) {
        Ignition = ignition;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

}
