package com.candypoint.neo.geodatacollector.DBConnect;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.File;
import java.sql.ResultSet;

/**
 * Created by myown on 2018. 3. 28..
 */

public class SQLiteConnection {

    private Context context;
    private SQLiteDatabase sqliteDB;

    public SQLiteConnection(Context context){
        this.context = context;
        sqliteDB = initDatabase();

        //deleteTable();
        showTables();
        initTables();
        showTables();
    }

    private SQLiteDatabase initDatabase(){
        SQLiteDatabase db = null;
        File file = new File(context.getFilesDir(), "geo.db");
        Log.e("TSET", context.getFilesDir().toString());

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
                    "ID "    + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "LATITUDE "  + "DOUBLE NOT NULL," +
                    "LONGITUDE " + "DOUBLE NOT NULL," +
                    "STATUS "    + "INTEGER NOT NULL" +
                    "" +
                    ");";
            this.sqliteDB.execSQL(sql);
            Log.d("DBCONN", "table created.");
        }
    }

    private void showTables(){
        if(this.sqliteDB != null){
            String sql = "SELECT name FROM sqlite_master WHERE type='table'";
            Cursor cursor = this.sqliteDB.rawQuery(sql, null);
            if(cursor.moveToNext()){
                Log.d("DATABASE", cursor.getColumnName(0));
            }
        }
    }

    private void deleteTable(){
        if(this.sqliteDB != null){
            String sql = "DROP TABLE geodata;";
            this.sqliteDB.execSQL(sql);
        }
    }
}
