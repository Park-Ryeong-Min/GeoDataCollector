package com.candypoint.neo.geodatacollector.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.candypoint.neo.geodatacollector.GlobalApplication;
import com.candypoint.neo.geodatacollector.Models.GPSData;
import com.candypoint.neo.geodatacollector.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 3. 28..
 */

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;

    private SupportMapFragment mapFragment;
    private Toolbar toolbar;

    private Socket socket;

    private ArrayList<GPSData> nodeList;
    private ArrayList<Marker> markerList;

    private String sectionName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);


        Intent it = getIntent();
        //Log.e("PARAM", it.getStringExtra("name"));
        sectionName = it.getStringExtra("name");

        nodeList = new ArrayList<GPSData>();
        markerList = new ArrayList<Marker>();

        GlobalApplication app = (GlobalApplication)getApplication();
        this.socket = app.getSocket();

        JSONObject request = new JSONObject();
        try{
            request.put("sectionName", sectionName);
        }catch (JSONException e){
            e.printStackTrace();
        }


        //socket.emit("responseGPSNodeData", request);
        socket.on("responseGPSNodeData",responseGPSNodeData);


        //toolbar = (Toolbar)findViewById(R.id.map_view_toolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setTitle(sectionName);


        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_view_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 19));
            this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
                    googleMap.animateCamera(center);

                    return false;
                }
            });
        }
        this.googleMap.setOnMarkerClickListener(this);
    }

    // 액션바 메뉴 초기화
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_view_menu, menu);
        return true;
    }

    // 액션바 메뉴 클릭 이벤트 리스너 등록
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map_view_menu_remove_section:{
                // section 삭제 버튼 클릭 시
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapViewActivity.this);
                alertDialogBuilder.setMessage("구간 " + sectionName + "을(를) 삭제할까요?")
                        .setCancelable(false)
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject send = new JSONObject();
                                try{
                                    send.put("sectionName", sectionName);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                socket.emit("removeSectionByName", send);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("responseGPSNodeData");
    }

    private Emitter.Listener responseGPSNodeData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
                    try{
                        JSONArray arr = response.getJSONArray("list");
                        for(int i = 0; i < arr.length(); i++){
                            //Log.e("OBJECT", arr.getJSONObject(i).toString());
                            JSONObject tmp = arr.getJSONObject(i);
                            double lat = tmp.getDouble("latitude");
                            double lng = tmp.getDouble("longitude");
                            long nodeID = tmp.getLong("nodeID");
                            String sectionName = tmp.getString("sectionName");
                            String tag;
                            if(tmp.has("tag")) {
                                tag = tmp.getString("tag");
                            }else{
                                tag = "";
                            }
                            JSONArray innerArr = tmp.getJSONArray("adjacent");
                            int status = tmp.getInt("status");
                            ArrayList<Long> adj = new ArrayList<Long>();
                            for(int j = 0; j < innerArr.length(); j++){
                                //adj.add(innerArr.get(j));
                                JSONObject tmp2 = innerArr.getJSONObject(j);
                                adj.add(tmp2.getLong("nodeID"));
                            }
                            nodeList.add(new GPSData(nodeID, lng, lat,adj, sectionName, 0, tag, status));
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    // 불러온 내용을 지도에 마커로 표시함
                    for(int i = 0; i < nodeList.size(); i++){
                        int iconID;
                        switch (nodeList.get(i).getStatus()){
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
                        LatLng tmp = new LatLng(nodeList.get(i).getLatitude(), nodeList.get(i).getLongitude());
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(tmp)
                                .title(nodeList.get(i).getSectionName())
                                .icon(BitmapDescriptorFactory.fromResource(iconID))
                                .snippet(Long.toString(nodeList.get(i).getNodeID()))
                        );
                        markerList.add(marker);
                    }
                }
            });
        }
    };

    @Override
    public boolean onMarkerClick(Marker marker) {
        long nodeID = Long.parseLong(marker.getSnippet());
        int idx = -1;
        for(int i = 0; i < nodeList.size(); i++){
            if(nodeList.get(i).getNodeID() == nodeID){
                idx = i;
                break;
            }
        }
        if(idx == -1){
            return false;
        }
        //Log.e("MARKER", Integer.toString(nodeList.get(idx).getStatus()));
        Intent it = new Intent(MapViewActivity.this, NodeDataUpdateActivity.class);
        it.putExtra("GPSData", nodeList.get(idx));
        startActivity(it);
        return true;
    }

    @Override
    protected void onResume() {
        JSONObject request = new JSONObject();
        try{
            request.put("sectionName", sectionName);
        }catch (JSONException e){
            e.printStackTrace();
        }
        socket.emit("responseGPSNodeData", request);
        super.onResume();
    }

    @Override
    protected void onPause() {
        for(Marker m : markerList){
            m.remove();
        }
        markerList.clear();
        nodeList.clear();
        googleMap.clear();
        super.onPause();
    }
}
