package com.ldsight.service;

import com.google.gson.Gson;
import com.ldsight.entity.HeartbeatStatis;
import com.ldsight.util.HttpConfiguration;
import com.ldsight.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ldgd on 2019/3/5.
 * 功能：
 * 说明：
 */

public class check {

    /**
     * 心跳间隔
     */
    public static int heartbeatInterval = 2;
    public static long lastSent = 0;
    public static boolean stoped = false;
    public final int HEARTBEAT = 10;
    public static HeartbeatStatis heartbeatStatis;


    private static void pullData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("uuidFrom", HttpConfiguration._Clientuuid)
                        .build();

                HttpUtil.sendHttpRequest(HttpConfiguration.urlPoll, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("pullData....Exception = " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();


                        // 保存心跳返回数据
                        Gson gson = new Gson();
                        System.out.println("pullData 成 功");

                    }


                }, requestBody);
            }
        }).start();
    }

    private static void startHeartbeat() {


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

    private static void heartbeat() {

        if (System.currentTimeMillis() - lastSent < heartbeatInterval * 1000) {
            //   LogUtil.e(" heartbeatInterval * 1000 = " +  heartbeatInterval * 1000);
            return;
        }

        // 发送http协议
        sendHttpHeartbeat();

        lastSent = System.currentTimeMillis();
    }

    private static synchronized void sendHttpHeartbeat() {

        RequestBody requestBody = new FormBody.Builder()
                .add("version", "225")
                .add("type", HttpConfiguration.NET + "")
                .add("key", "0")
                .add("uuidFrom", HttpConfiguration._Clientuuid)
                .add("uuidTo", "")
                .add("crc", "")
                .add("data", "")
                .build();

        HttpUtil.sendSookiePostHttpRequest(HttpConfiguration.urlSend, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("\"startHeartbeat失败\" + e.toString()");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                System.out.println("startHeartbeat成功" + json);


            }


        }, requestBody);
    }

    public static void main(String[] args) {

        String str = "\t{\"Confirm\":3,\"Fif_tp_Fir\":80,\"Fif_tt_Fir\":\"04:00\",\"Fir_tp_Fir\":100,\"Fir_tt_Fir\":\"18:30\",\"Four_tp_Fir\":30,\"Four_tt_Fir\":\"00 : 00\",\"Sec_tp_Fir\":0,\"Sec_tt_Fir\":\"20:00\",\"Six_tp_Fir\":0,\"Six_tt_Fir\":\"07 : 00\",\"Thir_tp_Fir\":60,\"Thir_tt_Fir\":\"22 : 00\"}#";

        System.out.println("xx = " + str.replaceAll(" ", ""));


    }


}
