package com.candypoint.neo.geodatacollector.DBConnect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.File;

/**
 * Created by myown on 2018. 3. 28..
 */

public class SQLiteConnection {

    private Context context;
    private SQLiteDatabase sqliteDB;

    public SQLiteConnection(Context context){
        this.context = context;
        sqliteDB = initDatabase();

        initTables();
    }

    private SQLiteDatabase initDatabase(){
        SQLiteDatabase db = null;
        File file = new File(context.getFilesDir(), "geo.db");

        try{
            db = SQLiteDatabase.openOrCreateDatabase(file, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }

        return db;
    }

    public SQLiteDatabase getSQLiteDB(){
        return this.sqliteDB;
    }

    private void initTables(){
        if(this.sqliteDB != null){
            String sql = "CREATE TABLE IF NOT EXISTS geodata" +
                    "(" +
                    "ID "    + "INTEGER NOT NULL," +
                    "LATITUDE "  + "DOUBLE NOT NULL," +
                    "LONGITUDE " + "DOUBLE NOT NULL," +
                    "STATUS "    + "INTEGER NOT NULL," +
                    "PRIMARY KEY (ID)" +
                    ");";
            this.sqliteDB.execSQL(sql);
            Log.d("DBCONN", "table created.");
        }
    }
}
