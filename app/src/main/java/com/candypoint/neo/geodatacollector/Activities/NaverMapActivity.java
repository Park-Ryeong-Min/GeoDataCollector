package com.candypoint.neo.geodatacollector.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.candypoint.neo.geodatacollector.NMaps.NMapPOIflagType;
import com.candypoint.neo.geodatacollector.NMaps.NMapViewerResourceProvider;
import com.candypoint.neo.geodatacollector.R;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;


/**
 * Created by myown on 2018. 4. 6..
 */

public class NaverMapActivity extends NMapActivity {

    private NMapView mapView;
    private final String CLIENT_ID = "gknYwJxHjXdZNDg2Uid0";
    private NMapController mapController;
    private NMapLocationManager locationManager;
    private NMapResourceProvider resourceProvider;
    private NMapOverlayManager overlayManager;
    private int markerID = NMapPOIflagType.PIN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = new NMapLocationManager(this);
        locationManager.setOnLocationChangeListener(locationChangeListener);
        locationManager.enableMyLocation(true);

        mapView = new NMapView(this);
        setContentView(mapView);
        mapView.setClientId(CLIENT_ID);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setFocusable(true);
        mapView.setFocusableInTouchMode(true);
        //mapView.requestFocus();
        mapView.setBuiltInZoomControls(true, null);

        mapController = mapView.getMapController();
        mapView.setOnMapStateChangeListener(stateChangeListener);

        resourceProvider = new NMapViewerResourceProvider(this);
        overlayManager = new NMapOverlayManager(this, mapView, resourceProvider);

        NMapPOIdata poiData = new NMapPOIdata(2, resourceProvider);
        poiData.beginPOIdata(2);
        poiData.addPOIitem(127.0630205, 37.5091300, "Pizza 777-111", markerID, 0);
        poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerID, 0);
        poiData.endPOIdata();

        NMapPOIdataOverlay poiDataOverlay = overlayManager.createPOIdataOverlay(poiData, null);
    }

    NMapView.OnMapStateChangeListener stateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            if(nMapError == null){
                // success
                //Log.e("NLOCATION", Double.toString(locationManager.getMyLocation().getLatitude()));
                //mapController.setMapCenter(new NGeoPoint(locationManager.getMyLocation().getLatitude(), locationManager.getMyLocation().getLongitude()), 11);

            }else{
                Log.e("NAVER_MAP","onMapInitHandler: error=" + nMapError.toString());
            }
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {

        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

        }
    };

    NMapLocationManager.OnLocationChangeListener locationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
            // 여기서 반환되는 것은 디바이스의 위치로 추정
            if(mapController != null){
                mapController.animateTo(nGeoPoint);
                mapController.setZoomLevel(18);
            }
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {

        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {

        }
    };

    private void overlayMarkerOnMap(){

    }

}
