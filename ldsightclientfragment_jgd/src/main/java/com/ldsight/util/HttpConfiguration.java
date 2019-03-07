package com.ldsight.util;

/**
 * Created by ldgd on 2019/3/5.
 * 功能：
 * 说明：
 */

public class HttpConfiguration {

    //	String _Clientuuid = "1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1";
    public static final String  _Clientuuid = "13,11,99,99,99,99,99,99,99,99,99,99,99,99,99,12";
    public static final String urlSend = "http://47.99.177.66:9000/send ";
    public static final String urlPoll = "http://47.99.177.66:9000/poll ";
    public static final int NET = 6;

    public class PushType{
        public final static byte pushHeart = 0;  // 心跳
        public final static byte pushData = 1;  // 推送数据
        public final static byte pushResponse = 2;  // 应答
    }


}
