package com.candypoint.neo.geodatacollector.Models;

/**
 * Created by myown on 2018. 4. 10..
 */

public class SimpleNodeData {
    private double latitude;
    private double longitude;
    private long nodeID;

    public SimpleNodeData(){
        // default constructor
    }

    public SimpleNodeData(long id, double lat, double lng){
        this.latitude = lat;
        this.longitude = lng;
        this.nodeID = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getNodeID() {
        return nodeID;
    }

    public void setNodeID(long nodeID) {
        this.nodeID = nodeID;
    }
}
