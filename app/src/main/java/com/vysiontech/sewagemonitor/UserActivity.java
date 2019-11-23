package com.vysiontech.sewagemonitor;



public class UserActivity  {
    private double Wlevel;
    private double Latitude;
    private double Longitude;
    private double Battery;

    public UserActivity() {
    }

    public UserActivity(double wlevel, double latitude, double longitude, double battery) {
        this.Wlevel= wlevel;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Battery=battery;
    }

    public double getWlevel() {
        return Wlevel;
    }

    public void setWaterlevel(double wlevel) {
        this.Wlevel = wlevel;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public double getBattery() {
        return Battery;
    }

    public void setBattery(double battery) {
        this.Battery = battery;
    }
}

