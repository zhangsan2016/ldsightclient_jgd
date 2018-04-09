package com.ldgd.em.act;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ldgd.em.R;
import com.ldgd.em.db.MySqliteHelper;
import com.ldgd.em.domain.Monitoring;
import com.ldgd.em.sqlitedal.SQLiteDALMonitoring;

/**
 * Created by ldgd on 2017/4/5.
 * 介绍：
 */

public class ModifyActivity extends Activity {
    /**
     * 地址、UUID、id、gps、post
     */
    private EditText et_toponymy, et_uuid, et_ip, et_gps, et_post;
    private MySqliteHelper helper;
    private Monitoring currentMonitoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify);

        inintView();

        // 获取数据库帮助类对象
        helper = SQLiteDALMonitoring.getIntance(this);

        // 获取传递过来的参数
         currentMonitoring = (Monitoring) this.getIntent().getSerializableExtra("currentMonitoring");
        Log.e("currentMonitoring", "" + currentMonitoring.toString());
        if (currentMonitoring != null) {
            et_toponymy.setText(currentMonitoring.getName());
            et_uuid.setText(currentMonitoring.getUuid());
            et_ip.setText(currentMonitoring.getIp() + "");
            et_gps.setText(currentMonitoring.getLongitude() + "," + currentMonitoring.getLatitude());
            et_post.setText(currentMonitoring.getPost() + "");
        }


    }



    private void inintView() {
        et_toponymy = (EditText) this.findViewById(R.id.et_toponymy);
        et_uuid = (EditText) this.findViewById(R.id.et_uuid);
        et_ip = (EditText) this.findViewById(R.id.et_ip);
        et_gps = (EditText) this.findViewById(R.id.et_gps);
        et_post = (EditText) this.findViewById(R.id.et_post);

    }


    public void back(View v) {
        this.finish();
    }

    public void ConfirmChange(View v) {

        // 先判断数据是否完整


        // 获取数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        Monitoring  modifyMonitoring = (Monitoring) currentMonitoring.clone();

       try{
           modifyMonitoring.setName(et_toponymy.getText().toString().trim());
           modifyMonitoring.setUuid(et_uuid.getText().toString().trim());
           modifyMonitoring.setIp(et_ip.getText().toString().trim());
           modifyMonitoring.setLongitude(Double.parseDouble(et_gps.getText().toString().trim().split(",")[1]));
           modifyMonitoring.setLatitude(Double.parseDouble(et_gps.getText().toString().trim().split(",")[0]));
           modifyMonitoring.setPost(Integer.parseInt(et_post.getText().toString().trim()));

           boolean result = SQLiteDALMonitoring.UpdateMonitoring(db,"Monitoring",modifyMonitoring);
           if(result){
               Toast.makeText(this,"修改完成",Toast.LENGTH_SHORT).show();
               Intent intent = new Intent();
               intent.putExtra("modifyMonitoring",modifyMonitoring);
               this.setResult(0,intent);
               this.finish();


           }else{
               this.setResult(0);
               this.finish();
               Toast.makeText(this,"修改失败",Toast.LENGTH_SHORT).show();
           }
       }catch(Exception e){
          Toast.makeText(this,"输入的参数异常，请修改参数",Toast.LENGTH_SHORT).show();
       }




    }

}
