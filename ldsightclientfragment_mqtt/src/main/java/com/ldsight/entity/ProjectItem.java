package com.ldsight.entity;

import java.util.List;

/**
 * Created by ldgd on 2019/3/1.
 * 功能： 工程项目信息
 * 说明：
 */

public class ProjectItem {


    /**
     * b : true
     * msg : null
     * data : [{"icon":"","text":"312国道工程","state":"","id":"3","uuid":"","LAT":"32.185492","LNG":"119.427094","type":0,"Illu":"0","children":null},{"icon":"","text":"长山安置小区工程","state":"","id":"4","uuid":"","LAT":"32.16108","LNG":"119.309629","type":0,"Illu":"0","children":null},{"icon":"","text":"镇荣公路工程","state":"","id":"5","uuid":"","LAT":"32.0555812794","LNG":"119.4036573172","type":0,"Illu":"0","children":null},{"icon":"","text":"茅以升大道工程","state":"","id":"6","uuid":"","LAT":"32.1414279664","LNG":"119.4093393999","type":0,"Illu":"0","children":null}]
     */

    private boolean b;
    private Object msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {


        private String icon;
        private String text;
        private String state;
        private String id;
        private String uuid;
        private String LAT;
        private String LNG;
        private int type;
        private String Illu;
        private Object children;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getLAT() {
            return LAT;
        }

        public void setLAT(String LAT) {
            this.LAT = LAT;
        }

        public String getLNG() {
            return LNG;
        }

        public void setLNG(String LNG) {
            this.LNG = LNG;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getIllu() {
            return Illu;
        }

        public void setIllu(String Illu) {
            this.Illu = Illu;
        }

        public Object getChildren() {
            return children;
        }

        public void setChildren(Object children) {
            this.children = children;
        }
    }
}
