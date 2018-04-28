package com.candypoint.neo.geodatacollector.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.candypoint.neo.geodatacollector.R;

/**
 * Created by myown on 2018. 4. 6..
 */

public class MapSelectActivity extends AppCompatActivity {

    // Components
    private Button startGoogleMap;
    private Button startDaumMap;
    private Button totalPath;
    private Button searchPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);

        initComponents();
    }

    private void initComponents(){
        startGoogleMap = (Button)findViewById(R.id.map_select_start_with_google_map);
        startGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MapSelectActivity.this, MainActivity.class);
                startActivity(it);
                //finish();
            }
        });

        startDaumMap = (Button)findViewById(R.id.map_select_start_with_naver_map);
        startDaumMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MapSelectActivity.this, NaverMapActivity.class );
                startActivity(it);
                //finish();
            }
        });

        totalPath = (Button)findViewById(R.id.map_select_start_total_path);
        totalPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MapSelectActivity.this, TotalPathActivity.class);
                startActivity(it);
                //finish();
            }
        });

        searchPath = (Button)findViewById(R.id.map_select_start_search_path);
        searchPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MapSelectActivity.this, SearchPathActivity.class);
                startActivity(it);
                //finish();
            }
        });

    }
}
