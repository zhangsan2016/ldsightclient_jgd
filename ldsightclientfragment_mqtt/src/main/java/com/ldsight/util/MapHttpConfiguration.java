package com.ldsight.util;

/**
 * Created by ldgd on 2019/9/22.
 * 功能：
 * 说明：http交互配置
 */

public class MapHttpConfiguration {
    // content-type 用户登录
    public  static String CONTENT_TYPE_USER_LOGIN = "user/login";
    // content-type 项目列表
    public static String CONTENT_TYPE_PROJECT_LIST = "project/list";
    // content-type 电箱路灯列表
    public static String CONTENT_TYPE_DEVICE_LAMP_LIST = "v_device_lamp/list";
    // content-type 电箱列表
    public static String DEVICE_EBOX = "v_device_ebox/list";
    // content-type 汇报设备配置
    public static String REPORT_CONFIG = "device/reportConfig";
    // content-type 单个设备信息
    public static String VIEW_BY_UUID = "device/viewByUUID";
    // content-type 设备控制
    public static String DEVICE_CONTROL = "device/control";



    private static String URL_BASE = "https://iot.sz-luoding.com:888/api/";
    // 登录地址
    public static String LOGIN_URl = URL_BASE + CONTENT_TYPE_USER_LOGIN;
    // 获取项目列表地址
    public static String PROJECT_LIST_URL = URL_BASE + CONTENT_TYPE_PROJECT_LIST;
    // 获取项目下路灯地址
    public static String DEVICE_LAMP_LIST_URL = URL_BASE + CONTENT_TYPE_DEVICE_LAMP_LIST;
    // 获取电箱列表地址
    public static String DEVICE_EBOX_URL = URL_BASE + DEVICE_EBOX;
    // 获取汇报设备配置地址
    public static String REPORT_CONFIG_URL = URL_BASE + REPORT_CONFIG;
    // 获取获取单个设备信息
    public static String VIEW_BY_UUID_URL = URL_BASE + VIEW_BY_UUID;
    // 设备控制请求地址
    public static String DEVICE_CONTROL_URL = URL_BASE + DEVICE_CONTROL;



}
