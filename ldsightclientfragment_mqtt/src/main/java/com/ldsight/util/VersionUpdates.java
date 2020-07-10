package com.ldsight.util;

import android.app.Activity;

import com.ldsight.dao.MakeSampleHttpRequest;

import org.json.JSONObject;

/**
 * Created by ldgd on 2020/7/9.
 * 功能：
 * 说明：版本跟新
 */

public class VersionUpdates {

    /*
   * 版本信息
   */
    private int newVersionCode;
    private String newVersionName;

    private Activity activity;

    /**
     * 从服务器获取当前最新版本号，如果成功返回TURE，如果失败，返回FALSE
     *
     * @return
     */
    private Boolean postCheckNewestVersionCommand() {
        MakeSampleHttpRequest mshr = new MakeSampleHttpRequest(activity);
        JSONObject response = mshr.post_to_server();
        try {
            newVersionCode = response.getInt("verCode");
            newVersionName = response.getString("verName");
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }




}
