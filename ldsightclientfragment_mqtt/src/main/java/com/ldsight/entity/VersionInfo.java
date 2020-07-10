package com.ldsight.entity;

/**
 * Created by ldgd on 2020/7/10.
 * 功能：
 * 说明：
 */

public class VersionInfo {


    /**
     * updatedir : http://121.40.194.91:8089/ldsightclient_mqtt.apk
     * versionName : 1.1.1
     * versionCode : 5
     */

    private String updatedir;
    private String versionName;
    private String versionCode;

    public String getUpdatedir() {
        return updatedir;
    }

    public void setUpdatedir(String updatedir) {
        this.updatedir = updatedir;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
