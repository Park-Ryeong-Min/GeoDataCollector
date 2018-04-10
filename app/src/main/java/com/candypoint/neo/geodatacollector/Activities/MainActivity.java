package com.candypoint.neo.geodatacollector.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.candypoint.neo.geodatacollector.DBConnect.SQLiteConnection;
import com.candypoint.neo.geodatacollector.GlobalApplication;
import com.candypoint.neo.geodatacollector.R;
import com.candypoint.neo.geodatacollector.Utils.CollectGPSTask;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Button btnCollectGPSData;
    private Button btnCheckCollectedData;
    private EditText edttxtInputPeriod;
    private EditText edttxtInputSectionName;
    private EditText edttxtInputStatus;

    private boolean isButtonClicked;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backButtonPressedTime = 0;

    private SQLiteDatabase db;

    private CollectGPSTask task;

    private Socket socket;

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    public TextView latitude;
    public TextView longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        //initDatabase();
        initGoogleMap();

        GlobalApplication app = (GlobalApplication)getApplication();
        socket = app.getSocket();
        //socket.emit("ping");

        edttxtInputPeriod = (EditText)findViewById(R.id.main_edttxt_input_period);
        edttxtInputSectionName = (EditText)findViewById(R.id.main_edttxt_input_section_name);
        edttxtInputStatus = (EditText)findViewById(R.id.main_edttxt_input_status);

        latitude = (TextView)findViewById(R.id.main_text_view_lat);
        longitude = (TextView)findViewById(R.id.main_text_view_lng);

        isButtonClicked = false;
        btnCollectGPSData = (Button)findViewById(R.id.main_button_collect_gps);
        btnCollectGPSData.setBackgroundResource(R.color.buttonDefault);
        btnCollectGPSData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isButtonClicked) {
                    if(isEmpty(edttxtInputPeriod)){
                        //edttxtInputPeriod.setSelection(0);
                        Toast.makeText(getApplicationContext(), "정보 수집 주기를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(isEmpty(edttxtInputSectionName)){
                        //edttxtInputSectionName.setSelection(0);
                        Toast.makeText(getApplicationContext(), "구간의 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(isEmpty(edttxtInputStatus)){
                        Toast.makeText(getApplicationContext(), "구간의 속성값을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    isButtonClicked = true;
                    Toast.makeText(getApplicationContext(), "GPS 정보 수집을 시작합니다.", Toast.LENGTH_LONG).show();
                    btnCollectGPSData.setBackgroundResource(R.drawable.button_state);

                    long p = Long.parseLong(edttxtInputPeriod.getText().toString());
                    task = new CollectGPSTask(getApplicationContext(), MainActivity.this);
                    task.startTask(p);

                }else{
                    edttxtInputPeriod.setText("");
                    edttxtInputSectionName.setText("");
                    edttxtInputStatus.setText("");

                    isButtonClicked = false;
                    Toast.makeText(getApplicationContext(), "GPS 정보 수집을 중단합니다" , Toast.LENGTH_LONG).show();
                    btnCollectGPSData.setBackgroundResource(R.color.buttonDefault);
                    task.cancelTask();
                }
            }
        });

        btnCheckCollectedData = (Button)findViewById(R.id.main_button_show_collected_data);
        btnCheckCollectedData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, GeoDataListActivity.class);
                startActivity(it);
            }
        });

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - backButtonPressedTime;

        if(0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        }else{
            backButtonPressedTime = currentTime;
            Toast.makeText(getApplicationContext(), "종료하시려면 한번 더 눌러주세요,", Toast.LENGTH_SHORT).show();
        }
    }

    // 사용자로부터 권한을 획득
    private void requestPermission(){
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionCheckFineLocation == PackageManager.PERMISSION_DENIED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("권한이 필요합니다.")
                        .setMessage("이 기능을 사용하기 위해서는 \"대략적인 위치정보\" 권한이 필요합니다. 계속하시겠습니까?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "권한이 없으므로 어플리케이션을 종료합니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .create()
                        .show();
            }else{
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            }
        }

        if(permissionCheckCoarseLocation == PackageManager.PERMISSION_DENIED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("권한이 필요합니다.")
                        .setMessage("이 기능을 사용하기 위해서는 \"GPS 위치정보\" 권한이 필요합니다. 계속하시겠습니까?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "권한이 없으므로 어플리케이션을 종료합니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .create()
                        .show();
            }else{
                requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            }
        }
    }

    private void initDatabase(){
        SQLiteConnection conn = new SQLiteConnection(this);
        this.db = conn.getSQLiteDB();
    }

    private boolean isEmpty(EditText edttxt){
        return edttxt.getText().toString().trim().length() == 0;
    }

    private void initGoogleMap(){
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.main_current_map_view);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // 지도 타입 - 일반
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 19));
        }
    }

}
