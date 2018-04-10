package com.candypoint.neo.geodatacollector.DBConnect;

import android.database.sqlite.SQLiteDatabase;

import com.candypoint.neo.geodatacollector.Models.GPSData;

/**
 * Created by myown on 2018. 3. 29..
 */

public class GPSInsert {

    private SQLiteConnection conn;
    private SQLiteDatabase db;

    public GPSInsert(SQLiteConnection conn){
        this.conn = conn;
        this.db = this.conn.getSQLiteDB();
    }

    public void insertGPSData(GPSData gps){
        String sql = "INSERT INTO geo";
    }
}
