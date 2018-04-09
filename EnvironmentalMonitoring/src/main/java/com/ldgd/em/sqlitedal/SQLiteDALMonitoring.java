package com.ldgd.em.sqlitedal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.ldgd.em.db.MySqliteHelper;
import com.ldgd.em.domain.Monitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldgd on 2017/4/6.
 * 介绍：
 */

public class SQLiteDALMonitoring {
    /**
     * 创建单例模式，避免反复创建
     */
    private static MySqliteHelper helper;

    public static MySqliteHelper getIntance(Context context) {
        if (helper == null) {
            helper = new MySqliteHelper(context);
        }
        return helper;
    }

    /**
     * 根据sql语句查询获得的
     *
     * @param db            数据库对象
     * @param sql           查询的sql语句
     * @param selectionArgs 查询条件的占位符
     * @return
     */
    public static Cursor selectDataBySql(SQLiteDatabase db, String sql, String[] selectionArgs) {
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(sql, selectionArgs);
        }
        return cursor;
    }

    public static boolean UpdateMonitoring(SQLiteDatabase db, String tableName, Monitoring pMonitoring) {
        int _Result = 0;
        if (db != null) {
            ContentValues _ContentValues = CreatParms(pMonitoring);
            String _Condition = " ID = " + pMonitoring.getId();
            _Result = db.update(tableName, _ContentValues, _Condition, null);

        }
        return _Result > 0;
    }

/*
    public static List<Person> cursorToList(Cursor cursor) {
        List<Person> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex(Constant._ID));
            String  name = cursor.getString(cursor.getColumnIndex(Constant.NAME));
            int  age = cursor.getInt(cursor.getColumnIndex(Constant.NAME));

            Person pserson = new Person(_id,name,age);
            list.add(pserson);
;        }

        return list;
    }*/


    public static boolean InsertMonitoring(SQLiteDatabase db, String tableName, Monitoring pMonitoring) {
        long _NewID = 0;
        if (db != null) {
            ContentValues _ContentValues = CreateParms(pMonitoring);
            _NewID = db.insert(tableName, null, _ContentValues);
        }
        // 释放数据库
        //  db.close();

        return _NewID > 0;
    }

    /**
     * 获取总条数
     *
     * @param db
     * @param tableName
     * @return
     */
    public static int getDataCount(SQLiteDatabase db, String tableName) {
        int count = 0;
        if (db != null) {
            Cursor cursor = db.rawQuery("select * from " + tableName, null);
            count = cursor.getCount();
        }
        return count;
    }


    public static ContentValues CreateParms(Monitoring p_Info) {
        ContentValues _ContentValues = new ContentValues();
        _ContentValues.put("name", p_Info.getName());
        _ContentValues.put("password", p_Info.getPassword());
        _ContentValues.put("serialumber", p_Info.getSerialumber());
        _ContentValues.put("latitude", p_Info.getLatitude());
        _ContentValues.put("longitude", p_Info.getLongitude());
        _ContentValues.put("ip", p_Info.getIp());
        _ContentValues.put("post", p_Info.getPost());
        _ContentValues.put("uuid", p_Info.getUuid());
        return _ContentValues;
    }

    public static Cursor selectAllMonitoring(SQLiteDatabase db) {
        Cursor cursor = null;
        if (db != null) {
            String sql = "select * from Monitoring";
            cursor = db.rawQuery(sql, null);
        }


        return cursor;
    }


    public static List<Monitoring> cursorToList(Cursor cursor) {

        List<Monitoring> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            String serialumber = cursor.getString(cursor.getColumnIndex("serialumber"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            String ip = cursor.getString(cursor.getColumnIndex("ip"));
            int post = cursor.getInt(cursor.getColumnIndex("post"));
            String uuid = cursor.getString(cursor.getColumnIndex("uuid"));

            Monitoring pserson = new Monitoring(_id, name, password, serialumber, latitude, longitude, ip, post, uuid);
            list.add(pserson);
        }

        return list;
    }


    public static ContentValues CreatParms(Monitoring p_Info) {
        ContentValues _ContentValues = new ContentValues();
        _ContentValues.put("ID", p_Info.getId());
        _ContentValues.put("name", p_Info.getName());
        _ContentValues.put("password", p_Info.getPassword());
        _ContentValues.put("serialumber", p_Info.getSerialumber());
        _ContentValues.put("latitude", p_Info.getLatitude());
        _ContentValues.put("longitude", p_Info.getLongitude());
        _ContentValues.put("ip", p_Info.getIp());
        _ContentValues.put("post", p_Info.getPost());
        _ContentValues.put("uuid", p_Info.getUuid());

        return _ContentValues;
    }


}
