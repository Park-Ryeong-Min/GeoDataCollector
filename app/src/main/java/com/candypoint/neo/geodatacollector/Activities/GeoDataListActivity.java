package com.candypoint.neo.geodatacollector.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.candypoint.neo.geodatacollector.Adapters.GeoDataListAdapter;
import com.candypoint.neo.geodatacollector.GlobalApplication;
import com.candypoint.neo.geodatacollector.Models.GeoDataListViewItem;
import com.candypoint.neo.geodatacollector.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myown on 2018. 4. 1..
 */

public class GeoDataListActivity extends AppCompatActivity {

    private ListView listView;
    private Socket socket;
    private ArrayList<GeoDataListViewItem> geoDataList;
    private GeoDataListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_data_list);

        listView = (ListView)findViewById(R.id.geo_data_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(GeoDataListActivity.this, MapViewActivity.class);
                String name = geoDataList.get(position).getSectionName();
                it.putExtra("name", name);
                startActivity(it);
            }
        });

        geoDataList = new ArrayList<GeoDataListViewItem>();

        adapter = new GeoDataListAdapter(this, R.layout.geo_data_list_item, geoDataList);
        listView.setAdapter(adapter);

        GlobalApplication app = (GlobalApplication)getApplication();
        this.socket = app.getSocket();

        //socket.emit("responseGPSNodeNumber");
        socket.on("responseGPSNodeNumber", responseGPSNodeNumber);
    }

    private Emitter.Listener responseGPSNodeNumber = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
                    geoDataList.add(new GeoDataListViewItem("전체"));

                    // response 의 데이터를 꺼낸다.
                    try{
                        JSONArray arr = response.getJSONArray("list");
                        for(int i = 0; i < arr.length(); i++){
                            String tmp = arr.getString(i);
                            geoDataList.add(new GeoDataListViewItem(tmp));
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        this.geoDataList.clear();
        socket.emit("responseGPSNodeNumber");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("responseGPSNodeNumber");
    }
}
