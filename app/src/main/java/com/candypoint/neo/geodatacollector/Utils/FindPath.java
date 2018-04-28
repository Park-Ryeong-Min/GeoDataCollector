package com.candypoint.neo.geodatacollector.Utils;

import android.util.Log;
import android.widget.Toast;

import com.candypoint.neo.geodatacollector.Models.AdjacentEdge;
import com.candypoint.neo.geodatacollector.Models.EdgeList;
import com.candypoint.neo.geodatacollector.Models.GPSData;
import com.candypoint.neo.geodatacollector.Models.SimpleNodeData;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 10..
 */

public class FindPath {

    private final double INF = 98765;
    private ArrayList<GPSData> nodeData;
    private EdgeList edgeData;
    private long startNodeID;
    private long destNodeID;
    private EdgeList path;
    private int size;
    private double[][] adj;
    private Node[] memo;
    private double[] dist;
    private int[] check;
    private int[] track;
    private ArrayList<ArrayList<Integer>> pathNode;

    public FindPath(ArrayList<GPSData> node, EdgeList edge, long start, long dest){
        this.nodeData = node;
        this.size = this.nodeData.size();
        this.adj = new double[this.size][this.size];
        this.memo = new Node[this.size];
        this.dist = new double[this.size];
        this.check = new int[this.size];
        this.track = new int[this.size];
        this.edgeData = edge;
        this.startNodeID = start;
        this.destNodeID = dest;
        this.path = new EdgeList();

        this.pathNode = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < size; i++){
            this.pathNode.add(new ArrayList<Integer>());
        }

        for(int i = 0; i < size; i++){
            memo[i] = new Node(i, this.nodeData.get(i).getNodeID());
        }

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                adj[i][j] = INF;
            }
        }

        for(int i = 0; i < size; i++){
            dist[i] = INF;
            check[i] = 0;
            track[i] = -1;
        }

        for(int i = 0; i < this.edgeData.size(); i++){
            AdjacentEdge tmp = this.edgeData.getEdge(i);
            //Log.e("CHECK_EDGE_TMP", Double.toString(tmp.getDistance()));
            SimpleNodeData v1 = tmp.getNodes()[0];
            SimpleNodeData v2 = tmp.getNodes()[1];
            int v1ID = nodeID2Index(v1.getNodeID());
            int v2ID = nodeID2Index(v2.getNodeID());
            //Log.e("CHECK_TRANS", Integer.toString(v1ID) + " , " + Integer.toString(v2ID));
            adj[v1ID][v2ID] = tmp.getDistance();
            adj[v2ID][v1ID] = tmp.getDistance();
        }

        dijkstra();
    }

    private void dijkstra(){
        dist[nodeID2Index(startNodeID)] = 0;
        //Log.e("START", Integer.toString(nodeID2Index(startNodeID)));
        for(int i = 0;  i < this.size; i++){
            //Log.e("DISTANCE_BEFORE", Double.toString(dist[i]));
        }

        for(int k = 0; k < size; k++){
            double m = INF + 1.0f;
            //Log.e("CHECK_M", Double.toString(m));
            int x = -1;
            for(int i = 0; i < size; i++){
                if(check[i] == 0 && m > dist[i]){
                    m = dist[i];
                    x = i;
                }
            }
            //Log.e("CHECK_X", Integer.toString(x));
            check[x] = 1;
            for(int i = 0; i < size; i++){
                if(dist[i] > dist[x] + adj[x][i]){
                    dist[i] = dist[x] + adj[x][i];
                    //this.pathNode.get(x).add(i);
                    Log.e("PATH", Long.toString(memo[x].nodeID) + " -> " + Long.toString(memo[i].nodeID));
                    track[i] = x;
                }
            }

        }
        for(int i = 0; i < size; i++){
            if(memo[i].nodeID <= 1400){
                continue;
            }
            Log.e("TRACK_INFO", Integer.toString(i) + "[" + Long.toString(memo[i].nodeID) + "]" + " : " + Double.toString(dist[i]));
        }
        findRoute(nodeID2Index(destNodeID));
    }

    private void findRoute(int x){
        Log.e("TRACK", Long.toString(memo[x].nodeID));
        if(memo[x].nodeID == destNodeID){
            //Log.e("END", Long.toString(memo[x].nodeID));
            //path.addEdge(memo[x].nodeID, memo[track[x]].nodeID, 0);
        }
        if(track[x] == -1){
            return;
        }else{
            path.addEdge(memo[x].nodeID, memo[track[x]].nodeID, 0);

            findRoute(track[x]);
        }
    }


    private int nodeID2Index(long id){
        for(int i = 0; i < size; i++){
            if(memo[i].nodeID == id){
                return i;
            }
        }
        return -1;
    }

    private boolean allVisit(boolean arr[], int size){
        for(int i = 0; i < size; i++){
            if(!arr[i]){
                return false;
            }
        }
        return true;
    }

    private double getDistance(long id1, long id2){
        AdjacentEdge tmp = this.edgeData.getEdge(id1, id2);
        if(tmp != null){
            return tmp.getDistance();
        }else{
            return -1;
        }
    }

    public EdgeList getPath(){
        return this.path;
    }

    private class Node{
        public int index;
        public long nodeID;

        public Node(int idx, long id){
            this.index = idx;
            this.nodeID = id;
        }
    }
}
