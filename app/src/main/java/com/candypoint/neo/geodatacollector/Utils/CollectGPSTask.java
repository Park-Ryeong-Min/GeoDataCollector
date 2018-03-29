package com.candypoint.neo.geodatacollector.Utils;

import android.content.Context;
import android.location.LocationManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by myown on 2018. 3. 28..
 */

public class CollectGPSTask {

    private Context context;

    private TimerTask task;
    private Timer timer;

    public CollectGPSTask(final Context context){
        this.context = context;
        this.task = new TimerTask() {
            LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            
            @Override
            public void run() {

            }
        };
    }
}
