package com.ldsight.entity;

/**
 * Created by ldgd on 2019/3/5.
 * 功能：
 * 说明：
 */

public class HeartbeatStatis {


    /**
     * bKey : 185
     * iSessionKey : 9529924
     */

    private int bKey;
    private int iSessionKey;

    public int getBKey() {
        return bKey;
    }

    public void setBKey(int bKey) {
        this.bKey = bKey;
    }

    public int getISessionKey() {
        return iSessionKey;
    }

    public void setISessionKey(int iSessionKey) {
        this.iSessionKey = iSessionKey;
    }


    @Override
    public String toString() {
        return "HeartbeatStatis{" +
                "bKey=" + bKey +
                ", iSessionKey=" + iSessionKey +
                '}';
    }
}


