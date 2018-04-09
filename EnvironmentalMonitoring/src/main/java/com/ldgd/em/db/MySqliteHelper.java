package com.ldgd.em.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ldgd.em.domain.Monitoring;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ldgd on 2017/4/7.
 * 介绍：
 */

public class MySqliteHelper  extends SQLiteOpenHelper{

    public MySqliteHelper(Context context) {
        super(context, "monitoring", null, 1);
    }

    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder s_CreateTableScript = new StringBuilder();


        s_CreateTableScript.append("		Create  TABLE Monitoring(");
        s_CreateTableScript
                .append("				[ID] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        s_CreateTableScript.append("				,[name] varchar(10) NOT NULL");
        s_CreateTableScript.append("				,[password] varchar(20)");
        s_CreateTableScript.append("				,[serialumber] varchar(30) NOT NULL");
        s_CreateTableScript.append("				,[latitude] varchar(30) NOT NULL");
        s_CreateTableScript.append("				,[longitude] varchar(30) NOT NULL");
        s_CreateTableScript.append("				,[ip] varchar(30) ");
        s_CreateTableScript.append("				,[post] integer ");
        s_CreateTableScript.append("				,[uuid] varchar(30) NOT NULL");
        s_CreateTableScript.append("				)");

        db.execSQL(s_CreateTableScript.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }




}
