package com.candypoint.neo.geodatacollector.Models;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 10..
 */

public class EdgeList {
    private ArrayList<AdjacentEdge> list;

    public EdgeList(){
        this.list = new ArrayList<AdjacentEdge>();
    }

    public boolean addEdge(long id1, long id2, double dist){
        boolean ret = true;
        for(int i = 0; i < this.list.size(); i++){
            SimpleNodeData nodes[] = this.list.get(i).getNodes();
            if(nodes[0].getNodeID() == id1 && nodes[1].getNodeID() == id2){
                ret = false;
            }
            if(nodes[0].getNodeID() == id2 && nodes[1].getNodeID() == id1){
                ret = false;
            }
        }
        if(ret){
            // 같은게 없음
            AdjacentEdge e = new AdjacentEdge(new SimpleNodeData(id1, 0, 0), new SimpleNodeData(id2, 0,0), dist);
            this.list.add(e);
        }
        return ret;
    }

    public boolean addEdge(SimpleNodeData data1, SimpleNodeData data2){
        boolean ret = true;
        for(int i = 0; i < this.list.size(); i++){
            SimpleNodeData nodes[] = this.list.get(i).getNodes();
            if((isEqual(nodes[0], data1) && isEqual(nodes[1], data2) || (isEqual(nodes[1], data1) && isEqual(nodes[0], data2)))){
                // 같은게 있으면 거른다
                ret = false;
            }
        }
        if(ret){
            // 같은게 없음
            AdjacentEdge e = new AdjacentEdge(data1, data2);
            this.list.add(e);
        }
        return ret;
    }

    private boolean isEqual(SimpleNodeData a, SimpleNodeData b){
        if(a.getLongitude() == b.getLongitude() && a.getLatitude() == b.getLatitude() && a.getNodeID() == b.getNodeID()){
            return true;
        }
        return false;
    }

    public AdjacentEdge getEdge(int i){
        return this.list.get(i);
    }

    public AdjacentEdge getEdge(long id1, long id2){
        for(int i = 0; i < this.list.size(); i++){
            SimpleNodeData tmp[] = this.list.get(i).getNodes();
            if((tmp[0].getNodeID() == id1) && (tmp[1].getNodeID() == id2)){
                return this.list.get(i);
            }
            if((tmp[1].getNodeID() == id1) && (tmp[0].getNodeID() == id2)){
                return this.list.get(i);
            }
        }
        return null;
    }

    public AdjacentEdge getEdge(double lat1, double lng1, double lat2, double lng2){
        for(int i = 0; i < this.list.size(); i++){
            SimpleNodeData tmp[] = this.list.get(i).getNodes();
            if(((tmp[0].getLongitude() == lng1) && (tmp[0].getLatitude() == lat1)) && ((tmp[1].getLongitude() == lng2) && (tmp[1].getLatitude() == lat2))){
                return this.list.get(i);
            }
            if(((tmp[1].getLongitude() == lng1) && (tmp[1].getLatitude() == lat1)) || ((tmp[0].getLongitude() == lng2) && (tmp[0].getLatitude() == lat2))){
                return this.list.get(i);
            }
        }
        return null;
    }

    public boolean isEmpty(){
        return this.list.isEmpty();
    }

    public void removeEdge(int i){
        this.list.remove(i);
    }

    public void removeEdge(AdjacentEdge e){
        this.list.remove(e);
    }

    public void removeEdge(long id1, long id2){
        for(int i = 0; i < this.list.size(); i++){
            SimpleNodeData tmp[] = this.list.get(i).getNodes();
            if((tmp[0].getNodeID() == id1) && (tmp[1].getNodeID() == id2)){
                this.removeEdge(i);
                return;
            }
            if((tmp[1].getNodeID() == id1) && (tmp[0].getNodeID() == id2)){
                this.removeEdge(i);
                return;
            }
        }
    }

    public void removeEdge(double lat1, double lng1, double lat2, double lng2){
        for(int i = 0; i < this.list.size(); i++){
            SimpleNodeData tmp[] = this.list.get(i).getNodes();
            if(((tmp[0].getLongitude() == lng1) && (tmp[0].getLatitude() == lat1)) && ((tmp[1].getLongitude() == lng2) && (tmp[1].getLatitude() == lat2))){
                this.list.remove(i);
                return;
            }
            if(((tmp[1].getLongitude() == lng1) && (tmp[1].getLatitude() == lat1)) || ((tmp[0].getLongitude() == lng2) && (tmp[0].getLatitude() == lat2))){
                this.list.remove(i);
                return;
            }
        }
    }

    public int size(){
        return this.list.size();
    }
}
