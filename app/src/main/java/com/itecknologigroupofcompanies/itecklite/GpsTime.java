package com.itecknologigroupofcompanies.itecklite;

public class GpsTime {
    String date,timezone,timezone_type;

    public GpsTime(String date, String timezone, String timezone_type) {
        this.date = date;
        this.timezone = timezone;
        this.timezone_type = timezone_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezone_type() {
        return timezone_type;
    }

    public void setTimezone_type(String timezone_type) {
        this.timezone_type = timezone_type;
    }
}
