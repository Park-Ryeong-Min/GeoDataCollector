package com.candypoint.neo.geodatacollector.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.candypoint.neo.geodatacollector.GlobalApplication;
import com.candypoint.neo.geodatacollector.Models.GPSData;
import com.candypoint.neo.geodatacollector.R;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by myown on 2018. 3. 28..
 */

public class CollectGPSTask {

    private Context context;
    private AppCompatActivity appCompatActivity;

    private TimerTask task;
    private Timer timer;

    private Socket socket;

    private LatLng position;
    private LocationManager locationManager;

    public CollectGPSTask(final Context context, final AppCompatActivity activity) {
        this.context = context;
        this.appCompatActivity = activity;
        GlobalApplication app = (GlobalApplication)this.context.getApplicationContext();
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(context, "Location changed", Toast.LENGTH_SHORT).show();
                Log.d("LOCATION_CHANGE ", Double.toString(location.getLatitude()) + " , " + Double.toString(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        this.socket = app.getSocket();
        this.task = new TimerTask() {

            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.e("PERMISSION", "DENIED");

                    return;
                }

                // 이걸로는 안되는 듯함
                //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.1f, mLocationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null){
                    Log.d("LOCATION", Double.toString(location.getLongitude()) + " , " + Double.toString(location.getLatitude()));
                    //GPSData gps = new GPSData(location.getLongitude(), location.getLatitude());
                    JSONObject send = new JSONObject();

                    EditText edt = activity.findViewById(R.id.main_edttxt_input_section_name);
                    EditText status = activity.findViewById(R.id.main_edttxt_input_status);

                    try{
                        send.put("longitude", location.getLongitude());
                        send.put("latitude", location.getLatitude());
                        send.put("sectionName", edt.getText().toString());
                        send.put("status", Long.parseLong(status.getText().toString()));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    socket.emit("getGPSNodeData", send);
                    position = new LatLng(location.getLatitude(), location.getLongitude());

                    final String latStr = Double.toString(location.getLatitude());
                    final String lngStr = Double.toString(location.getLongitude());

                    Context tmp = context;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView lat = (TextView)(activity).findViewById(R.id.main_text_view_lat);
                            TextView lng = (TextView)(activity).findViewById(R.id.main_text_view_lng);

                            lat.setText("Latitude  : " + latStr);
                            lng.setText("Longitude : " + lngStr);
                        }
                    });
                }
                //Looper.loop();
            }
        };
    }

    public void startTask(long p){
        this.timer = new Timer();
        this.timer.schedule(this.task, 0, p);
    }

    public void cancelTask(){
        this.timer.cancel();
        this.timer = null;
    }

    public LatLng getPosition(){
        return this.position;
    }

}
