package com.candypoint.neo.geodatacollector.Models;

/**
 * Created by myown on 2018. 4. 10..
 */

public class AdjacentEdge {
    private SimpleNodeData[] nodes;
    private double distance;

    public AdjacentEdge(SimpleNodeData node1, SimpleNodeData node2){
        this.nodes = new SimpleNodeData[2];
        this.nodes[0] = node1;
        this.nodes[1] = node2;
        this.distance = distance(this.nodes[0].getLatitude(), this.nodes[0].getLongitude(), this.nodes[1].getLatitude(), this.nodes[1].getLongitude());
    }

    public AdjacentEdge(SimpleNodeData node1, SimpleNodeData node2, double dist){
        this.nodes = new SimpleNodeData[2];
        this.nodes[0] = node1;
        this.nodes[1] = node2;
        //this.distance = distance(this.nodes[0].getLatitude(), this.nodes[0].getLongitude(), this.nodes[1].getLatitude(), this.nodes[1].getLongitude());
        this.distance = dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double getDistance(double lat1, double lat2, double lng1, double lng2){
        double theta = lng1 - lng2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return dist;
    }

    public double distance(double lat1, double lat2,
                                  double lng1, double lng2){
        double a = (lat1-lat2)*distPerLat(lat1);
        double b = (lng1-lng2)*distPerLng(lat1);
        return Math.sqrt(a*a+b*b);
    }

    private double distPerLng(double lat){
        return 0.0003121092*Math.pow(lat, 4)
                +0.0101182384*Math.pow(lat, 3)
                -17.2385140059*lat*lat
                +5.5485277537*lat+111301.967182595;
    }

    private double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }
    /*
    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
    */

    public SimpleNodeData[] getNodes() {
        return nodes;
    }

    public void setNodes(SimpleNodeData[] nodes) {
        this.nodes = nodes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
