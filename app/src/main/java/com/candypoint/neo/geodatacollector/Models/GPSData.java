package com.candypoint.neo.geodatacollector.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by myown on 2018. 3. 28..
 */

public class GPSData implements Serializable{

    private long nodeID;
    private ArrayList<Long> adjacentNodeID;
    private String tag;
    private int status;
    private String sectionName;
    private long sectionNumber;
    private double longitude;
    private double latitude;

    public GPSData(){
        // default constructor
    }

    public GPSData(long id, double lng, double lat, ArrayList<Long> adj, String secName, long secNumber, String tag, int status){
        this.nodeID = id;
        this.adjacentNodeID = adj;
        this.sectionName = secName;
        this.sectionNumber = secNumber;
        this.tag = tag;
        this.status = status;
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

    public long getNodeID() {
        return nodeID;
    }

    public void setNodeID(long nodeID) {
        this.nodeID = nodeID;
    }

    public ArrayList<Long> getAdjacentNodeID() {
        return adjacentNodeID;
    }

    public void setAdjacentNodeID(ArrayList<Long> adjacentNodeID) {
        this.adjacentNodeID = adjacentNodeID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public long getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(long sectionNumber) {
        this.sectionNumber = sectionNumber;
    }
}
