package com.ldsight.entity;

/**
 * Created by ldgd on 2019/3/4.
 * 功能：
 * 说明：
 */

public class HeartbeatParameter {


    /**
     * b : true
     * msg : null
     * data : {"bKey":60,"iSessionKey":1548595}
     */

    private boolean b;
    private Object msg;
    private Data data;

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        /**
         * bKey : 60
         * iSessionKey : 1548595
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
    }
}
