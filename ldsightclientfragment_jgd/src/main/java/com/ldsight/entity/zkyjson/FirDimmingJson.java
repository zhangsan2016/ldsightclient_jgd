package com.ldsight.entity.zkyjson;

/**
 * Created by ldgd on 2019/3/9.
 * 功能：
 * 说明：中科院协议，调光模版
 */

public class FirDimmingJson {


    /**
     * Confirm : 4
     * Dimming : 48
     */

    private int Confirm;
    private   int FirDimming;




    public int getConfirm() {
        return Confirm;
    }

    public void setConfirm(int confirm) {
        Confirm = confirm;
    }

    public int getFirDimming() {
        return FirDimming;
    }

    public void setFirDimming(int firDimming) {
        FirDimming = firDimming;
    }
}
