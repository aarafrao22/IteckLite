package com.itecknologigroupofcompanies.itecklite;

public class TripSocketResponseModel {

    int Angle;
    double Ign;
    int Spd;
    String Time;
    double X;
    double Y;

    public TripSocketResponseModel(int angle, double ign, int spd, String time, double x, double y) {
        Angle = angle;
        Ign = ign;
        Spd = spd;
        Time = time;
        X = x;
        Y = y;
    }

    public int getAngle() {
        return Angle;
    }

    public void setAngle(int angle) {
        Angle = angle;
    }

    public double getIgn() {
        return Ign;
    }

    public void setIgn(double ign) {
        Ign = ign;
    }

    public int getSpd() {
        return Spd;
    }

    public void setSpd(int spd) {
        Spd = spd;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }
}
