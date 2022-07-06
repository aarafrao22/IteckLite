package com.itecknologigroupofcompanies.itecklite;

import java.util.ArrayList;
import java.util.List;

public class ResponseModel {
    private String name;
    List<VehicleModel> vehicle;
    private String success;

    public ResponseModel(String name, List<VehicleModel> vehicle, String success) {
        this.name = name;
        this.vehicle = vehicle;
        this.success = success;
    }

    public String getName() {
        return name;
    }

    public String getSuccess() {
        return success;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<VehicleModel> getVehicle() {
        return vehicle;
    }

    public void setVehicle(List<VehicleModel> vehicle) {
        this.vehicle = vehicle;
    }
}
