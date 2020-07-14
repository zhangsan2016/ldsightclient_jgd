package com.ldsight.pro;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Util {

	public static byte[] getUuid(int max, int min) {
		byte[] uuid = new byte[8];
		for (int i = 0; i < uuid.length; i++) {
			uuid[i] = (byte) (min + Math.random() * ((max - min) + 1));
		}
		return uuid;
	}

	// 根据设备号生八位uuid
	public static byte[] getUuid(Context context) {
		byte[] uuid8 = new byte[8];
		// 获取设备独一无二的uuid
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		String tmDevice = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			tmDevice = "" + tm.getImei();
		}else{
			 tmDevice = "" + tm.getDeviceId();
		}
		String tmSerial = "" + tm.getSimSerialNumber();
		String androidId = ""
				+ android.provider.Settings.Secure.getString(
				context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		// 测试
		System.out.println("tmDevice = " + tmDevice);
		System.out.println("tmSerial  = " + tmSerial);
		System.out.println("androidId  = " + androidId);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		// 通过算法生成八位uuid
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(uniqueId.getBytes("UTF-8"));
			byte[] uuid16 = md.digest();
			// 转换成八位uuid
			System.arraycopy(uuid16, 0, uuid8, 0, 8);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return uuid8;
	}
}
