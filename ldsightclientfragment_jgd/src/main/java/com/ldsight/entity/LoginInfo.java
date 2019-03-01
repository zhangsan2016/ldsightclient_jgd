package com.ldsight.entity;

import java.util.List;

/**
 * Created by ldgd on 2019/2/28.
 * 功能：
 * 说明：
 */

public class LoginInfo {


    /**
     * b : true
     * msg : null
     * data : [{"ID":"1","NAME":"admin","PHONE":"13633333333","PASSWORD":"d658c4853f123e4f377ab51e79c4b10c","MENUPOWER":"3","OPERATEPOWER":"15","SYSTEM":"15","REMARK":"","POSITION":"1","HEADPIC":"http://47.99.168.98:9001/Files/2018-12-27/o_1cvmrei6rr941fud1fh1jbg15c7h.jpg","LOGINSTATUS":"1","STATUS":"0","DEFSYSTEM":"2","url":"http://47.99.168.98:9001/API/CommonFn.asmx/Login","TOKEN":"u5I/5F0oAv2Vp+GfAzT73OoE7XHFJ+bvznVln4a1AjPo2Yk3Jzslv9kSk+tm2jCuMBqFEC2xWs0yxEJVuvtzdVdiiULZvGm312MvFqKVKMZa83b7rzbyYJdlI2sUcCiZQiYKfyI+qyFQXg/uUyV89JQYi4llTm5h1+NRv3MteVQ=","Response":"http://47.99.168.98:9002/api/Common.asmx/LoginToken?strToken=u5I/5F0oAv2Vp+GfAzT73OoE7XHFJ+bvznVln4a1AjPo2Yk3Jzslv9kSk+tm2jCuMBqFEC2xWs0yxEJVuvtzdVdiiULZvGm312MvFqKVKMZa83b7rzbyYJdlI2sUcCiZQiYKfyI+qyFQXg/uUyV89JQYi4llTm5h1+NRv3MteVQ="}]
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
         * ID : 1
         * NAME : admin
         * PHONE : 13633333333
         * PASSWORD : d658c4853f123e4f377ab51e79c4b10c
         * MENUPOWER : 3
         * OPERATEPOWER : 15
         * SYSTEM : 15
         * REMARK :
         * POSITION : 1
         * HEADPIC : http://47.99.168.98:9001/Files/2018-12-27/o_1cvmrei6rr941fud1fh1jbg15c7h.jpg
         * LOGINSTATUS : 1
         * STATUS : 0
         * DEFSYSTEM : 2
         * url : http://47.99.168.98:9001/API/CommonFn.asmx/Login
         * TOKEN : u5I/5F0oAv2Vp+GfAzT73OoE7XHFJ+bvznVln4a1AjPo2Yk3Jzslv9kSk+tm2jCuMBqFEC2xWs0yxEJVuvtzdVdiiULZvGm312MvFqKVKMZa83b7rzbyYJdlI2sUcCiZQiYKfyI+qyFQXg/uUyV89JQYi4llTm5h1+NRv3MteVQ=
         * Response : http://47.99.168.98:9002/api/Common.asmx/LoginToken?strToken=u5I/5F0oAv2Vp+GfAzT73OoE7XHFJ+bvznVln4a1AjPo2Yk3Jzslv9kSk+tm2jCuMBqFEC2xWs0yxEJVuvtzdVdiiULZvGm312MvFqKVKMZa83b7rzbyYJdlI2sUcCiZQiYKfyI+qyFQXg/uUyV89JQYi4llTm5h1+NRv3MteVQ=
         */

        private String ID;
        private String NAME;
        private String PHONE;
        private String PASSWORD;
        private String MENUPOWER;
        private String OPERATEPOWER;
        private String SYSTEM;
        private String REMARK;
        private String POSITION;
        private String HEADPIC;
        private String LOGINSTATUS;
        private String STATUS;
        private String DEFSYSTEM;
        private String url;
        private String TOKEN;
        private String Response;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getPHONE() {
            return PHONE;
        }

        public void setPHONE(String PHONE) {
            this.PHONE = PHONE;
        }

        public String getPASSWORD() {
            return PASSWORD;
        }

        public void setPASSWORD(String PASSWORD) {
            this.PASSWORD = PASSWORD;
        }

        public String getMENUPOWER() {
            return MENUPOWER;
        }

        public void setMENUPOWER(String MENUPOWER) {
            this.MENUPOWER = MENUPOWER;
        }

        public String getOPERATEPOWER() {
            return OPERATEPOWER;
        }

        public void setOPERATEPOWER(String OPERATEPOWER) {
            this.OPERATEPOWER = OPERATEPOWER;
        }

        public String getSYSTEM() {
            return SYSTEM;
        }

        public void setSYSTEM(String SYSTEM) {
            this.SYSTEM = SYSTEM;
        }

        public String getREMARK() {
            return REMARK;
        }

        public void setREMARK(String REMARK) {
            this.REMARK = REMARK;
        }

        public String getPOSITION() {
            return POSITION;
        }

        public void setPOSITION(String POSITION) {
            this.POSITION = POSITION;
        }

        public String getHEADPIC() {
            return HEADPIC;
        }

        public void setHEADPIC(String HEADPIC) {
            this.HEADPIC = HEADPIC;
        }

        public String getLOGINSTATUS() {
            return LOGINSTATUS;
        }

        public void setLOGINSTATUS(String LOGINSTATUS) {
            this.LOGINSTATUS = LOGINSTATUS;
        }

        public String getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(String STATUS) {
            this.STATUS = STATUS;
        }

        public String getDEFSYSTEM() {
            return DEFSYSTEM;
        }

        public void setDEFSYSTEM(String DEFSYSTEM) {
            this.DEFSYSTEM = DEFSYSTEM;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTOKEN() {
            return TOKEN;
        }

        public void setTOKEN(String TOKEN) {
            this.TOKEN = TOKEN;
        }

        public String getResponse() {
            return Response;
        }

        public void setResponse(String Response) {
            this.Response = Response;
        }
    }

}
