package com.ldsight.entity;

/**
 * Created by ldgd on 2019/3/1.
 * 功能：
 * 说明：
 */

public class ElectricityBox {


    /**
     * icon :
     * text : 茅以升1号电箱
     * state :
     * id : 10085
     * uuid : 1,1,99,99,99,99,99,99,99,99,99,99,99,99,96,1
     * LAT : 32.141428
     * LNG : 119.409339
     * type : 1
     * Illu : 631
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
