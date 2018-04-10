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


    }

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
