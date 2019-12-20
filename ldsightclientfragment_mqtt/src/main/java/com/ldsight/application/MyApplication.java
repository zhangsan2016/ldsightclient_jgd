package com.ldsight.application;

import android.app.Application;

import com.example.ldsightclient_jgd.R;
import com.ldsight.pro.Util;

import java.util.Arrays;

public class MyApplication extends Application {
	private static byte[] appUuid = null;
	private static MyApplication instance;
	private static String ip;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// appUuid = Util.getUuid(127, -127);
		appUuid = Util.getUuid(this);
		instance = this;
		// 测试
		System.out.println("appuuid = " + Arrays.toString(appUuid));
		//System.out.println("MyApplication_appuuid = " + Arrays.toString(Util.getUuid(this)));

		// 获取IP地址
		ip = getString(R.string.ip);




	}

	public static byte[] getAppUuid() {
		// 测试
		// System.out.println("appuuid = " + Arrays.toString(appUuid));
		if (appUuid == null) {
			appUuid = Util.getUuid(127, -127);
		}
		return appUuid;
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public static String getIp(){
		return ip;
	}

}
