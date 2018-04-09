package com.ldgd.em.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ldgd.em.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // 隐藏自定义标题蓝返回按钮
        ImageView imgBack = (ImageView) this.findViewById(R.id.backBtnInTopLayout);
        imgBack.setVisibility(View.GONE);
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.bt_list:
                Intent intent = new Intent(this,DeviceList.class);
                startActivity(intent);
                break;
            case R.id.bt_map:
                Intent mapIntent = new Intent(this,BaiduMapAct.class);
                startActivity(mapIntent);
                break;

        }

    }

}
