package com.ldsight.entity.zkyjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by ldgd on 2019/3/9.
 * 功能：
 * 说明：中科院协议，全亮度调节
 */

public class DimmingJson {


    /**
     * Confirm : 4
     * Dimming : 48
     */

    private int Confirm;
    private  int Dimming;

    public int getConfirm() {
        return Confirm;
    }

    public void setConfirm(int Confirm) {
        this.Confirm = Confirm;
    }

    public int getDimming() {
        return Dimming;
    }

    public void setDimming(int Dimming) {
        this.Dimming = Dimming;
    }


    public static void main(String args[]) {

        Gson gson = new GsonBuilder().serializeNulls().create();

        DimmingJson dimmingJson = new DimmingJson();
        dimmingJson.setConfirm(55);
        System.out.println(gson.toJson(dimmingJson));



    }

}
