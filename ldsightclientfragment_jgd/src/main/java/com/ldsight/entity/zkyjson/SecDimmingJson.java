package com.ldsight.entity.zkyjson;

/**
 * Created by ldgd on 2019/3/9.
 * 功能：
 * 说明：中科院协议，辅灯调光模版
 */

public class SecDimmingJson {


    /**
     * Confirm : 4
     * Dimming : 48
     */

    private int Confirm;
    private int SecDimming;


    public int getConfirm() {
        return Confirm;
    }

    public void setConfirm(int confirm) {
        Confirm = confirm;
    }

    public int getSecDimming() {
        return SecDimming;
    }

    public void setSecDimming(int secDimming) {
        SecDimming = secDimming;
    }
}
