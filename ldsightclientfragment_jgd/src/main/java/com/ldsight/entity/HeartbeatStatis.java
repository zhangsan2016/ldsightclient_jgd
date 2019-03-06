package com.ldsight.entity;

/**
 * Created by ldgd on 2019/3/5.
 * 功能：
 * 说明：
 */

public class HeartbeatStatis implements Cloneable {

    /**
     * b : true
     * msg : null
     * data : {"bKey":205,"iSessionKey":3475481}
     */

    private boolean b;
    private String msg;
    private DataBean data;

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        if (null == data) {
            return data = new DataBean();
        }
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public Object clone() throws CloneNotSupportedException {

        HeartbeatStatis heartbeatStatis = null;
        try {
            heartbeatStatis = (HeartbeatStatis) super.clone();
            heartbeatStatis.data = (DataBean) data.clone();//属性a的克隆，实现A类的深度克隆
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return heartbeatStatis;
    }



    public static class DataBean  implements Cloneable {

        /**
         * bKey : 205
         * iSessionKey : 3475481
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
            return "DataBean{" +
                    "bKey=" + bKey +
                    ", iSessionKey=" + iSessionKey +
                    '}';
        }

        public  Object clone() throws CloneNotSupportedException {
            DataBean dataBean = null;
            try {
                dataBean = (DataBean) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return dataBean;
        }
    }



    @Override
    public String toString() {
        return "HeartbeatStatis{" +
                "b=" + b +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }




}


