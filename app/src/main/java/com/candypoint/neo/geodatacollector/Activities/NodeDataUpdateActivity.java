package com.candypoint.neo.geodatacollector.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.candypoint.neo.geodatacollector.GlobalApplication;
import com.candypoint.neo.geodatacollector.Models.GPSData;
import com.candypoint.neo.geodatacollector.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by myown on 2018. 4. 6..
 */

public class NodeDataUpdateActivity extends AppCompatActivity implements OnMapReadyCallback{


    // Google Map
    public GoogleMap googleMap;
    private SupportMapFragment fragment;

    // Components
    private TextView nodeIdTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView sectionNameTextView;
    private TextView statusTextView;
    private TextView tagTextView;
    private Button tagUpdateButton;
    private Button removeNodeButton;
    private Button addAdjacentButton;

    // Member variables
    private GPSData nodeData;
    private GlobalApplication app;
    private Socket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_data_update);

        Intent it = getIntent();
        this.nodeData = (GPSData)it.getSerializableExtra("GPSData");

        app = (GlobalApplication)getApplication();
        socket = app.getSocket();
        socket.on("tagUpdateResult", tagUpdateResult);
        socket.on("removeNodeResult", removeNodeResult);

        initComponents();
        setGPSDataToTextView(this.nodeData);
        //Log.e("ON_CREATE_END", "on create method end.");
    }

    // 여기서는 자신의 위치를 확인할 필요가 없다.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //Log.e("MAP_READY", "google map ready.");
        resetCameraOnGoogleMap();
    }

    @Override
    protected void onDestroy() {
        socket.off("tagUpdateResult");
        socket.off("removeNodeResult");
        super.onDestroy();
    }

    private void initComponents(){
        // initialize Map Fragment
        fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.node_data_update_map);
        fragment.getMapAsync(this);

        // initialize TextView
        nodeIdTextView = (TextView)findViewById(R.id.node_data_update_txtview_node_id);
        latitudeTextView = (TextView)findViewById(R.id.node_data_update_txtview_latitude);
        longitudeTextView = (TextView)findViewById(R.id.node_data_update_txtview_longitude);
        sectionNameTextView = (TextView)findViewById(R.id.node_data_update_txtview_section_name);
        tagTextView = (TextView)findViewById(R.id.node_data_update_txtview_tag);
        statusTextView = (TextView)findViewById(R.id.node_data_update_txtview_status);

        // initialize Button and set button click event listener
        tagUpdateButton = (Button)findViewById(R.id.node_data_update_button_tag_update);
        tagUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NodeDataUpdateActivity.this);
                builder.setTitle("Tag Update");
                builder.setMessage("수정할 Tag를 입력해주세요.");

                // EditText 삽입하기
                final EditText edt = new EditText(NodeDataUpdateActivity.this);
                builder.setView(edt);

                // 확인 버튼 설정
                builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = edt.getText().toString();
                        JSONObject send = new JSONObject();
                        try{
                            send.put("tag", tag);
                            send.put("nodeID", nodeData.getNodeID());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        socket.emit("changeNodeTag", send);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        removeNodeButton = (Button)findViewById(R.id.node_data_update_button_remove_node);
        removeNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NodeDataUpdateActivity.this);
                builder.setTitle("Remove Node");
                builder.setMessage("현재 Node를 삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject send = new JSONObject();
                        try{
                            send.put("nodeID", nodeData.getNodeID());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        socket.emit("removeNodeByID", send);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        addAdjacentButton = (Button)findViewById(R.id.node_data_update_button_add_adjacent);
        addAdjacentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NodeDataUpdateActivity.this);
                builder.setTitle("Add adjacent node");
                builder.setMessage("현재 Node에 인접한 Node의 ID를 입력하십시오.");

                final EditText edt = new EditText(NodeDataUpdateActivity.this);
                builder.setView(edt);

                builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long nodeID = Long.parseLong(edt.getText().toString());
                        JSONObject json = new JSONObject();
                        try{
                            json.put("addNodeID", nodeID);
                            json.put("curNodeID", nodeData.getNodeID());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        socket.emit("addAdjacentNode", json);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    // Camera 위치 보정
    private void resetCameraOnGoogleMap(){
        LatLng latlng = new LatLng(this.nodeData.getLatitude(), this.nodeData.getLongitude());
        Log.e("MARKER_LOCATION", Double.toString(this.nodeData.getLatitude()) + " , " + Double.toString(this.nodeData.getLongitude()));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 19));
        this.googleMap.addMarker(new MarkerOptions()
                .position(latlng)
        );
    }

    private void setGPSDataToTextView(GPSData data){
        nodeIdTextView.setText(Long.toString(data.getNodeID()));
        latitudeTextView.setText(Double.toString(data.getLatitude()));
        longitudeTextView.setText(Double.toString(data.getLongitude()));
        sectionNameTextView.setText(data.getSectionName());
        statusTextView.setText(Integer.toString(data.getStatus()));
        tagTextView.setText(data.getTag());
    }

    private Emitter.Listener tagUpdateResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
                    int result = -1;
                    String tag = "";
                    try{
                        result = response.getInt("result");
                        tag = response.getString("tag");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    if(result < 0){
                        Toast.makeText(getApplicationContext(), "서버와의 통신 과정에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }else if(result == 1){
                        Toast.makeText(getApplicationContext(), "성공적으로 데이터를 수정하였습니다.", Toast.LENGTH_SHORT).show();
                        nodeData.setTag(tag);
                        setGPSDataToTextView(nodeData);
                    }else if(result == 2){
                        Toast.makeText(getApplicationContext(), "값이 변경되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "데이터 업데이트에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    private Emitter.Listener removeNodeResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
                    int result = -1;
                    try{
                        result = response.getInt("result");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    if(result < 0){
                        Toast.makeText(getApplicationContext(), "서버와의 통신 과정에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }else if(result == 1){
                        Toast.makeText(getApplicationContext(), "Node 삭제에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Node 삭제에 실패하였습니다." , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };
}
