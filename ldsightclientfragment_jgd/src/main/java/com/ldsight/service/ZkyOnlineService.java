package com.ldsight.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.ldsight.entity.HeartbeatStatis;
import com.ldsight.util.HttpConfiguration;
import com.ldsight.util.HttpUtil;
import com.ldsight.util.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ldsight.util.HttpConfiguration.url;

public class ZkyOnlineService extends Service {
    /**
     * 心跳间隔
     */
    public int heartbeatInterval = 50;
    public long lastSent = 0;
    public boolean stoped = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 发送心跳
        startHeartbeat();

        // 拉取数据
        pullData();

        return super.onStartCommand(intent, flags, startId);
    }

    private void pullData() {
        RequestBody requestBody = new FormBody.Builder()
                .add("type", HttpConfiguration.NET)
                .build();

        HttpUtil.sendHttpRequest(url, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("pullData", "pullData" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                // 保存心跳返回数据
                Gson gson = new Gson();
                Log.e("pullData", "pullData 成 功" + json);

            }


        }, requestBody);
    }

    private void startHeartbeat() {


        new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    try {

                        // 发送心跳包
                        heartbeat();


                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            }
        }).start();


    }

    private void heartbeat() {

        if (System.currentTimeMillis() - lastSent < heartbeatInterval * 1000) {
            return;
        }

        // 发送http协议
        sendHttp();

        lastSent = System.currentTimeMillis();
    }

    private void sendHttp() {

        RequestBody requestBody = new FormBody.Builder()
                .add("version", "225")
                .add("type", HttpConfiguration.NET)
                .add("key", "0")
                .add("uuidFrom", HttpConfiguration._Clientuuid)
                .add("uuidTo", "")
                .add("crc", "")
                .add("data", "")
                .build();

        HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("startHeartbeat", "startHeartbeat失败" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                // 保存心跳返回数据
                Gson gson = new Gson();
                HeartbeatStatis heartbeatStatis = gson.fromJson(json, HeartbeatStatis.class);
                Log.e("startHeartbeat", "startHeartbeat成功" + json);

                Log.e("startHeartbeat", "heartbeatStatis = " + heartbeatStatis.toString());

            }


        }, requestBody);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("service onDestroy 被执行");
        stoped = true;
    }
}
