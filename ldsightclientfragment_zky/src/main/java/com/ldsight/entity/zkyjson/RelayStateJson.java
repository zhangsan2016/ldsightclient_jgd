package com.ldsight.entity.zkyjson;

/**
 * Created by ldgd on 2019/3/11.
 * 功能：
 * 说明：  中科院协议，设置继电器状态模版
 */

public class RelayStateJson {

    private int Confirm;
    private int Rel_State;

    public int getConfirm() {
        return Confirm;
    }

    public void setConfirm(int confirm) {
        Confirm = confirm;
    }

    public int getRel_State() {
        return Rel_State;
    }

    public void setRel_State(int rel_State) {
        Rel_State = rel_State;
    }
}
