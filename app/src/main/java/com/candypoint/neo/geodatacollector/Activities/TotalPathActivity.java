package com.candypoint.neo.geodatacollector.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.candypoint.neo.geodatacollector.GlobalApplication;
import com.candypoint.neo.geodatacollector.Models.EdgeList;
import com.candypoint.neo.geodatacollector.Models.GPSData;
import com.candypoint.neo.geodatacollector.Models.SimpleNodeData;
import com.candypoint.neo.geodatacollector.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 9..
 */

public class TotalPathActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    private GlobalApplication app;
    private Socket socket;
    private ArrayList<GPSData> dataList;
    private ArrayList<Marker> markerList;
    private EdgeList edgeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_path);

        initComponents();

        app = (GlobalApplication)getApplication();
        socket = app.getSocket();

        socket.on("responseGPSNodeData", responseGPSNodeData);


    }

    @Override
    protected void onResume() {
        if(!dataList.isEmpty()){
            dataList.clear();
        }
        JSONObject send = new JSONObject();
        try{
            send.put("sectionName", "전체");
        }catch (JSONException e){
            e.printStackTrace();
        }
        socket.emit("responseGPSNodeData", send);
        super.onResume();
    }

    @Override
    protected void onPause() {
        for(Marker m : markerList){
            m.remove();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        socket.off("responseGPSNodeData");
        super.onDestroy();
    }

    private void initComponents(){
        dataList = new ArrayList<GPSData>();
        markerList = new ArrayList<Marker>();
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.total_path_map_view);
        mapFragment.getMapAsync(this);
        edgeList = new EdgeList();
    }

    private Emitter.Listener responseGPSNodeData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
                    try{
                        JSONArray tmpArr = response.getJSONArray("list");
                        for(int i = 0; i < tmpArr.length(); i++){
                            JSONObject obj = tmpArr.getJSONObject(i);
                            long nodeID = obj.getLong("nodeID");
                            String secName = obj.getString("sectionName");
                            //long secNumber = obj.getLong("sectionNumber");
                            String tag;
                            if(obj.isNull("tag")){
                                tag = "";
                            }else{
                                tag = obj.getString("tag");
                            }
                            int status = obj.getInt("status");
                            double lat = obj.getDouble("latitude");
                            double lng = obj.getDouble("longitude");
                            ArrayList<Long> adj = new ArrayList<Long>();
                            JSONArray adjacent = obj.getJSONArray("adjacent");
                            for(int j = 0; j < adjacent.length(); j++){
                                JSONObject obj2 = adjacent.getJSONObject(j);
                                long adjNodeID = obj2.getLong("nodeID");
                                adj.add(adjNodeID);
                            }
                            GPSData tmp = new GPSData(nodeID, lng, lat, adj, secName, /*secNumber*/ 0, tag, status);
                            dataList.add(tmp);
                        }
                        Log.e("AFTER_REQUEST", Integer.toString(dataList.size()));

                        // 마커 표시하기
                        for(int i = 0; i < dataList.size(); i++){
                            int iconID;
                            switch (dataList.get(i).getStatus()){
                                case 0 :{
                                    iconID = R.drawable.node_icon_1;
                                    break;
                                }
                                case 1: {
                                    iconID = R.drawable.node_icon_2;
                                    break;
                                }
                                case 2: {
                                    iconID = R.drawable.node_icon_3;
                                    break;
                                }
                                case 3: {
                                    iconID = R.drawable.node_icon_4;
                                    break;
                                }
                                case 4: {
                                    iconID = R.drawable.node_icon_5;
                                    break;
                                }
                                default : {
                                    iconID = R.drawable.node_icon_1;
                                }
                            }
                            LatLng tmp = new LatLng(dataList.get(i).getLatitude(), dataList.get(i).getLongitude());
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(tmp)
                                    .title(dataList.get(i).getSectionName())
                                    .icon(BitmapDescriptorFactory.fromResource(iconID))
                                    .snippet(Long.toString(dataList.get(i).getNodeID()))
                            );
                            markerList.add(marker);
                        }

                        // edge 만들기
                        //Log.e("DATALIST", Integer.toString(dataList.size()));
                        for(int i = 0; i < dataList.size(); i++){
                            GPSData tmp = dataList.get(i);
                            SimpleNodeData t1 = new SimpleNodeData(tmp.getNodeID(), tmp.getLatitude(), tmp.getLongitude());
                            //Log.e("ADJLIST", Integer.toString(tmp.getAdjacentNodeID().size()));
                            for(int j = 0; j < tmp.getAdjacentNodeID().size(); j++){
                                long nodeID = tmp.getAdjacentNodeID().get(j);
                                for(int k = 0; k < dataList.size(); k++){
                                    GPSData tmp2 = dataList.get(k);
                                    if(tmp2.getNodeID() == nodeID){
                                        //Log.e("ADDEDGE", Long.toString(tmp.getNodeID()) + " , " + Long.toString(tmp2.getNodeID()));
                                        SimpleNodeData t2 = new SimpleNodeData(tmp2.getNodeID(), tmp2.getLatitude(), tmp2.getLongitude());
                                        edgeList.addEdge(t1, t2);
                                    }
                                }
                            }
                        }
                        //Log.e("NUM_OF_EDGE", Integer.toString(edgeList.size()));

                        // path 그리기
                        for(int i = 0; i < edgeList.size(); i++){
                            SimpleNodeData data[] = edgeList.getEdge(i).getNodes();
                            PolylineOptions lineOptions = new PolylineOptions();
                            lineOptions.add(new LatLng(data[0].getLatitude(), data[0].getLongitude()));
                            lineOptions.add(new LatLng(data[1].getLatitude(), data[1].getLongitude()));
                            googleMap.addPolyline(lineOptions);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.44989, 126.656291), 18));
        }
    }
}
