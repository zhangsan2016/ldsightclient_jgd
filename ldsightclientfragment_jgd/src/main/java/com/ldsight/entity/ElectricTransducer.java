package com.ldsight.entity;

import java.util.List;

/**
 * Created by ldgd on 2019/3/1.
 * 功能：变电器信息
 * 说明：
 */

public class ElectricTransducer {

    /**
     * b : true
     * msg : null
     * data : [{"icon":"","text":"安置小区变电器","state":"","id":"10076","uuid":"LD2016BA312000098","LAT":"32.161645","LNG":"119.309751","type":5,"Illu":"0","children":null}]
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
        /**
         * icon :
         * text : 安置小区变电器
         * state :
         * id : 10076
         * uuid : LD2016BA312000098
         * LAT : 32.161645
         * LNG : 119.309751
         * type : 5
         * Illu : 0
         * children : null
         */

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
