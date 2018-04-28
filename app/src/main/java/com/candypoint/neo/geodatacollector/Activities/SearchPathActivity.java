package com.candypoint.neo.geodatacollector.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.candypoint.neo.geodatacollector.GlobalApplication;
import com.candypoint.neo.geodatacollector.Models.AdjacentEdge;
import com.candypoint.neo.geodatacollector.Models.EdgeList;
import com.candypoint.neo.geodatacollector.Models.GPSData;
import com.candypoint.neo.geodatacollector.Models.SimpleNodeData;
import com.candypoint.neo.geodatacollector.R;
import com.candypoint.neo.geodatacollector.Utils.FindPath;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by myown on 2018. 4. 9..
 */

public class SearchPathActivity extends AppCompatActivity implements OnMapReadyCallback{

    private TextView startingTag;
    private TextView destinationTag;
    private Button findPathButton;
    private long startNodeID;
    private long destNodeID;

    private boolean isSetStartingPoint;
    private boolean isSetDestinationPoint;
    private boolean findingPathFlag;

    private GoogleMap googleMap;
    private SupportMapFragment fragment;

    private GlobalApplication app;
    private Socket socket;

    private ArrayList<GPSData> dataList;
    private ArrayList<Marker> markerList;
    private ArrayList<SimpleAdjacent> tmpAdjList;
    private EdgeList edgeList;
    private ArrayList<Polyline> line;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_path);

        initComponents();
        requestData();
    }

    private void initComponents(){
        app = (GlobalApplication)getApplication();
        socket = app.getSocket();
        socket.on("responseGPSNodeData", responseGPSNodeData);

        dataList = new ArrayList<GPSData>();
        markerList = new ArrayList<Marker>();
        edgeList = new EdgeList();
        tmpAdjList = new ArrayList<SimpleAdjacent>();
        line = new ArrayList<Polyline>();

        isSetDestinationPoint = false;
        isSetStartingPoint = false;
        findingPathFlag = false;

        fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.search_path_map_view);
        fragment.getMapAsync(this);

        startingTag = (TextView)findViewById(R.id.search_path_text_view_starting_tag);
        destinationTag = (TextView)findViewById(R.id.search_path_text_view_destination_tag);
        findPathButton = (Button)findViewById(R.id.search_path_button);
        findPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findingPathFlag){
                    // 길을 찾아놨으면 이제 리셋을 해야한다.
                    findPathButton.setText("길 찾기");
                    findPathButton.setBackgroundColor(getResources().getColor(R.color.buttonDefault));
                    findingPathFlag = false;
                    startingTag.setText("");
                    destinationTag.setText("");
                    Toast.makeText(getApplicationContext(), "출발/도착지 정보를 초기화합니다.", Toast.LENGTH_SHORT).show();
                    for(int i = 0; i < line.size(); i++){
                        line.get(i).remove();
                    }
                    line.clear();
                }else{
                    // 길을 찾아야한다.
                    if(isSetStartingPoint && isSetDestinationPoint){
                        isSetStartingPoint = false;
                        isSetDestinationPoint = false;
                        findPathButton.setText("초기화");
                        findPathButton.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                        findingPathFlag = true;
                        Toast.makeText(getApplicationContext(), "경로를 탐색합니다.", Toast.LENGTH_SHORT).show();
                        // 경로 찾기 시작
                        Log.e("START/DEST", Long.toString(getStartNodeID()) + " , " + Long.toString(getDestNodeID()));

                        FindPath find = new FindPath(dataList, edgeList, getStartNodeID(), getDestNodeID());
                        EdgeList path = find.getPath();
                        // path 그리기
                        for(int i = 0; i < path.size(); i++){
                            SimpleNodeData data[] = path.getEdge(i).getNodes();
                            for(int j = 0; j < dataList.size(); j++){
                                if(dataList.get(j).getNodeID() == data[0].getNodeID()){
                                    data[0].setLongitude(dataList.get(j).getLongitude());
                                    data[0].setLatitude(dataList.get(j).getLatitude());
                                }
                                if(dataList.get(j).getNodeID() == data[1].getNodeID()){
                                    data[1].setLongitude(dataList.get(j).getLongitude());
                                    data[1].setLatitude(dataList.get(j).getLatitude());
                                }
                            }
                            Log.e("DATA", Long.toString(data[0].getNodeID()) + " -> " + Long.toString(data[1].getNodeID()));
                            Log.e("GPS", Double.toString(data[0].getLatitude()) + "," + Double.toString(data[0].getLongitude()) + " -> " + Double.toString(data[1].getLatitude()) + "," + Double.toString(data[1].getLongitude()));
                            PolylineOptions lineOptions = new PolylineOptions();
                            lineOptions.add(new LatLng(data[0].getLatitude(), data[0].getLongitude()));
                            lineOptions.add(new LatLng(data[1].getLatitude(), data[1].getLongitude()));
                            Polyline tmp = googleMap.addPolyline(lineOptions);
                            line.add(tmp);
                        }
                    }else{
                        if(!isSetStartingPoint && !isSetDestinationPoint){
                            Toast.makeText(getApplicationContext(), "출발지와 도착지를 선택해 주십시오.", Toast.LENGTH_SHORT).show();
                        }else if(!isSetStartingPoint){
                            Toast.makeText(getApplicationContext(), "출발지를 선택해 주십시오.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "도착지를 선택해 주십시오.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void requestData(){
        JSONObject json = new JSONObject();
        try{
            json.put("sectionName" ,"전체");
        }catch (JSONException e){
            e.printStackTrace();
        }
        socket.emit("responseGPSNodeData", json);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.44989, 126.656291), 18));
        }
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchPathActivity.this);
                builder.setTitle("출발/도착 설정");
                builder.setMessage("출발지 / 도착지를 선택하여주십시오.");
                builder.setPositiveButton("출발지", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSetStartingPoint = true;
                        startingTag.setText(marker.getTitle().toString());
                        //Log.e("출발", startingTag.getText().toString());
                        setStartNodeID(Long.parseLong(marker.getSnippet()));
                    }
                });
                builder.setNegativeButton("도착지", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSetDestinationPoint = true;
                        destinationTag.setText(marker.getTitle().toString());
                        //Log.e("도착", destinationTag.getText().toString());
                        setDestNodeID(Long.parseLong(marker.getSnippet()));
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        socket.off("responseGPSNodeData");
        super.onDestroy();
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
                                double dist = obj2.getDouble("distance");
                                tmpAdjList.add(new SimpleAdjacent(nodeID, adjNodeID, dist));
                                adj.add(adjNodeID);
                            }
                            GPSData tmp = new GPSData(nodeID, lng, lat, adj, secName, /*secNumber*/ 0, tag, status);
                            dataList.add(tmp);
                        }
                        //Log.e("AFTER_REQUEST", Integer.toString(dataList.size()));

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
                            if(dataList.get(i).getStatus() != 0){
                                continue;
                            }
                            LatLng tmp = new LatLng(dataList.get(i).getLatitude(), dataList.get(i).getLongitude());
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(tmp)
                                    .title(dataList.get(i).getTag())
                                    .icon(BitmapDescriptorFactory.fromResource(iconID))
                                    .snippet(Long.toString(dataList.get(i).getNodeID()))
                            );
                            markerList.add(marker);
                        }

                        // edge 만들기
                        //Log.e("DATALIST", Integer.toString(dataList.size()));
                        /*
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
                        */
                        for(int i = 0; i < tmpAdjList.size(); i++){
                            edgeList.addEdge(tmpAdjList.get(i).node[0], tmpAdjList.get(i).node[1], tmpAdjList.get(i).distance);
                        }

                        //Log.e("NUM_OF_EDGE", Integer.toString(edgeList.size()));

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public long getStartNodeID() {
        return startNodeID;
    }

    public void setStartNodeID(long startNodeID) {
        this.startNodeID = startNodeID;
    }

    public long getDestNodeID() {
        return destNodeID;
    }

    public void setDestNodeID(long destNodeID) {
        this.destNodeID = destNodeID;
    }

    private class SimpleAdjacent{
        public long[] node;
        public double distance;

        public SimpleAdjacent(long id1, long id2, double dist){
            this.node = new long[2];
            this.node[0] = id1;
            this.node[1] = id2;
            this.distance = dist;
        }
    }
}
