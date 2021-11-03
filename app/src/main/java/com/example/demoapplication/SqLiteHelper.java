package com.example.demoapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SqLiteHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME="EpisodeDataBase";
    public static final String TABLE_NAME="MyShowTable";
    private static final String Table_Column_ID="id";
    private static final String Table_Column_ShowId="eId";
    private static final String Table_Column_1_Name="eName";
    private static final String Table_Column_2_url="eUrl";
    private static final String Table_Column_3_type="eType";
    private static final String Table_Column_4_season="eSeason";

    public SqLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
                // ""+Table_Column_2_url+" BLOB," +
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"" +
                " ("+Table_Column_ID+" INTEGER PRIMARY KEY  AUTOINCREMENT ," + " "+Table_Column_ShowId+" INTEGER," + " "+Table_Column_1_Name+" VARCHAR," +
                " "+Table_Column_3_type+" VARCHAR," + " "+Table_Column_4_season+" INTEGER, " +
                ""+Table_Column_2_url + "BLOB)";

        db.execSQL(CREATE_TABLE);
    }
    //byte[]  url
    public void addNewData(int eid,String name, String type, Integer season) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(Table_Column_ID, id);
        values.put(Table_Column_ShowId, eid);
        values.put(Table_Column_1_Name, name);
        // values.put(Table_Column_2_url, url);
        values.put(Table_Column_3_type, type);
        values.put(Table_Column_4_season, season);
        // content values to our table.
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // we have created a new method for reading all the courses.
    public ArrayList<Episode> readData() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        // on below line we are creating a new array list.
        ArrayList<Episode> courseModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new Episode(cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        // and returning our array list.
        cursor.close();
        return courseModalArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
    public int getTableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public long DeleteRowAfterSwipe(int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME, Table_Column_ShowId + " = ?", new String[]{ String.valueOf(id) });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
