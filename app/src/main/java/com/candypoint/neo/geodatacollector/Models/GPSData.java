package com.candypoint.neo.geodatacollector.Models;

/**
 * Created by myown on 2018. 3. 28..
 */

public class GPSData {
    private double longitude;
    private double latitude;

    public GPSData(){
        // default constructor
    }

    public GPSData(double lng, double lat){
        this.longitude = lng;
        this.latitude = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
