package com.candypoint.neo.geodatacollector.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.candypoint.neo.geodatacollector.DBConnect.SQLiteConnection;
import com.candypoint.neo.geodatacollector.R;

public class MainActivity extends AppCompatActivity {

    private Button btnCollectGPSData;
    private Button btnCheckCollectedData;

    private boolean isButtonClicked;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backButtonPressedTime = 0;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        initDatabase();

        isButtonClicked = false;
        btnCollectGPSData = (Button)findViewById(R.id.main_button_collect_gps);
        btnCollectGPSData.setBackgroundResource(R.color.buttonDefault);
        btnCollectGPSData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isButtonClicked) {
                    isButtonClicked = true;
                    Toast.makeText(getApplicationContext(), "GPS 정보 수집을 시작합니다.", Toast.LENGTH_LONG).show();
                    btnCollectGPSData.setBackgroundResource(R.drawable.button_state);
                }else{
                    isButtonClicked = false;
                    Toast.makeText(getApplicationContext(), "GPS 정보 수집을 중단합니다" , Toast.LENGTH_LONG).show();
                    btnCollectGPSData.setBackgroundResource(R.color.buttonDefault);
                }
            }
        });

        btnCheckCollectedData = (Button)findViewById(R.id.main_button_show_collected_data);
        btnCheckCollectedData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, MapViewActivity.class);
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
}
