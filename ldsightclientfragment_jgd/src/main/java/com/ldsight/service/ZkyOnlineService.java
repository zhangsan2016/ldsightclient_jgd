package com.ldsight.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

public class ZkyOnlineService extends Service {
    /**
     * 心跳间隔
     */
    public int heartbeatInterval = 50;
    public long lastSent = 0;
    public boolean stoped = false;
    public final int HEARTBEAT = 10;
    public static HeartbeatStatis heartbeatStatis;

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HEARTBEAT:  // 心跳数据

                    String json = (String) msg.obj;

                    // 保存心跳返回数据
                    Gson gson = new Gson();
                    HeartbeatStatis  statis = gson.fromJson(json, HeartbeatStatis.class);

                    if(statis != null && statis.getData() != null){
                        // 判断数据状态
                        if (statis.isB()) {
                            // 等于0表示返回成功
                            if(statis.getData().getISessionKey() !=  0){
                                try {
                                    heartbeatStatis = (HeartbeatStatis) statis.clone();
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.e("startHeartbeat", "heartbeatStatis = " + heartbeatStatis.toString());
                        } else {
                            LogUtil.e("startHeartbeat" + "心跳异常 ,请稍等60秒..." + "\n");
                            // 重新发送心跳包
                            //  heartbeatStatis = new HeartbeatStatis();
                            // sendHttpHeartbeat();
                        }
                    }

                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 发送心跳
        startHeartbeat();

        new Thread() {
            @Override
            public void run() {
                super.run();

                // 拉取数据
                while (stoped == false) {
                    try {
                        pullData();
                        Thread.sleep(6000);
                      //  pullData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();


        return super.onStartCommand(intent, flags, startId);
    }

    private synchronized void pullData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("uuidFrom", HttpConfiguration._Clientuuid)
                        .build();

                HttpUtil.sendHttpRequest(HttpConfiguration.urlPoll, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("pullData", "pullData....Exception = " + e.toString());
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
        }).start();
    }

    private void startHeartbeat() {


        new Thread(new Runnable() {

            @Override
            public void run() {

                while (stoped == false) {
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
            //   LogUtil.e(" heartbeatInterval * 1000 = " +  heartbeatInterval * 1000);
            return;
        }


        lastSent = System.currentTimeMillis();

        // 发送http协议
        sendHttpHeartbeat();


    }

    private synchronized void sendHttpHeartbeat() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int key = 0;

                if (heartbeatStatis != null && heartbeatStatis.getData() != null) {
                    key = heartbeatStatis.getData().getISessionKey();
                }

                RequestBody requestBody = new FormBody.Builder()
                        .add("version", "225")
                        .add("type", HttpConfiguration.NET)
                        .add("key", String.valueOf(key))
                        .add("uuidFrom", HttpConfiguration._Clientuuid)
                        .add("uuidTo", "")
                        .add("crc", "")
                        .add("data", "")
                        .build();

                LogUtil.e(" String.valueOf(key) = " + String.valueOf(key));

                HttpUtil.sendSookiePostHttpRequest(HttpConfiguration.urlSend, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("startHeartbeat", "startHeartbeat失败" + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();

                        Log.e("startHeartbeat", "startHeartbeat成功" + json);

                        Message msg = handler.obtainMessage();
                        msg.what = HEARTBEAT;
                        msg.obj = json;
                        handler.sendMessage(msg);
                    }
                }, requestBody);
            }
        }).start();

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
