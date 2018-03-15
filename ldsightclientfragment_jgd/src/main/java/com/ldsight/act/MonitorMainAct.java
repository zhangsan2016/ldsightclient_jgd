package com.ldsight.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.example.ldsightclient_jgd.R;
import com.ldsight.adapter.MonitorMainFragmentAdapter;
import com.ldsight.util.LogUtil;
import com.viewpagerindicator.TabPageIndicator;

import tvonvif.finder.CameraService;

/**
 * Created by ldgd on 2018/2/28.
 * 摄像头监控主界面
 */

public class MonitorMainAct extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.monitor_main_fragment);


        FragmentPagerAdapter adapter = new MonitorMainFragmentAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2); // 缓存界面数
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, CameraService.class);
        this.stopService(intent);

        this.finish();
        LogUtil.e("MonitorMainAct  onDestroy 执行");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtil.e("onBackPressed  onDestroy 执行");


    }
}
