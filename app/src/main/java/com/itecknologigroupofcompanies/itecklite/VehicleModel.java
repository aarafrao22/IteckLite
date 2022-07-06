package com.itecknologigroupofcompanies.itecklite;

public class VehicleModel {

    private String object_id,veh_reg,vehicle_id;

    public VehicleModel(String object_id, String veh_reg, String vehicle_id) {
        this.object_id = object_id;
        this.veh_reg = veh_reg;
        this.vehicle_id = vehicle_id;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getVeh_reg() {
        return veh_reg;
    }

    public void setVeh_reg(String veh_reg) {
        this.veh_reg = veh_reg;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }
}
