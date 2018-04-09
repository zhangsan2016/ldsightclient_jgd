package com.ldgd.em.domain;

import java.io.Serializable;

/**
 * Created by ldgd on 2017/3/6.
 * 介绍：
 */

public class Monitoring implements Serializable , Cloneable {

    // 设备编号
    private int id;
    // 设备名称
    private String name;
    // 设备密码
    private String password;
    // 序列号
    private String serialumber;
    // 经度
    private double longitude;
    //纬度
    private double latitude;
    // ip
    private String ip;
    // 端口号
    private int post;
    // uuid
    private String uuid;

    public Monitoring(){};

    public Monitoring(int id, String name, String password, String serialumber, double longitude, double latitude, String ip, int post, String uuid) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.serialumber = serialumber;
        this.longitude = longitude;
        this.latitude = latitude;
        this.ip = ip;
        this.post = post;
        this.uuid = uuid;
    }

    public void setIp(String ip) {
        if (ip != null && !ip.equals("")) {
            this.ip = ip;
        }

    }

    public void setPost(int post) {
        this.post = post;
    }

    public void setUuid(String uuid) {
        if (uuid != null && !uuid.equals("")) {
            this.uuid = uuid;
        }
    }

    public String getIp() {
        return ip;
    }

    public int getPost() {
        return post;
    }

    public String getUuid() {
        return uuid;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSerialumber(String serialumber) {
        this.serialumber = serialumber;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getSerialumber() {
        return serialumber;
    }


    @Override
    public String toString() {
        return "Monitoring{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", serialumber='" + serialumber + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", ip='" + ip + '\'' +
                ", post=" + post +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    /**
     * Return a copy of this object.
     */
    public Object clone() {
        Monitoring stu = null;
        try{
            stu = (Monitoring)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;

    }
}
