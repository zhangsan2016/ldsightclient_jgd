package com.ldsight.util;

import java.security.MessageDigest;
import java.util.UUID;

/**
 *  字符串转换类
 * @author ldgd
 *
 */
public class StringUtil {

	public static String checkBlankString(String param) {
		if (param == null) {
			return "";
		}
		return param;
	}

	public static String md5(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(encryptStr.getBytes("UTF-8"));
		byte[] digest = md.digest();
		StringBuffer md5 = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
			md5.append(Character.forDigit((digest[i] & 0xF), 16));
		}

		encryptStr = md5.toString();
		return encryptStr;
	}

	public static String sha1(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(encryptStr.getBytes("UTF-8"));
		byte[] digest = md.digest();
		StringBuffer sha1 = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			sha1.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
			sha1.append(Character.forDigit((digest[i] & 0xF), 16));
		}

		encryptStr = sha1.toString();
		return encryptStr;
	}

	public static byte[] md5Byte(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(encryptStr.getBytes("UTF-8"));
		return md.digest();
	}

	public static byte[] sha1Byte(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(encryptStr.getBytes("UTF-8"));
		return md.digest();
	}

	public static String genUUIDHexString(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static UUID parseUUIDFromHexString(String hexUUID) throws Exception{
		byte[] data = hexStringToByteArray(hexUUID);
		long msb = 0;
		long lsb = 0;

		for (int i=0; i<8; i++)
			msb = (msb << 8) | (data[i] & 0xff);
		for (int i=8; i<16; i++)
			lsb = (lsb << 8) | (data[i] & 0xff);

		return new java.util.UUID(msb,lsb);
	}

	private static char convertDigit(int value) {

		value &= 0x0f;
		if (value >= 10)
			return ((char) (value - 10 + 'a'));
		else
			return ((char) (value + '0'));

	}
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	public static String convert(final byte bytes[]) {

		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(convertDigit((int) (bytes[i] >> 4)));
			sb.append(convertDigit((int) (bytes[i] & 0x0f)));
		}
		return (sb.toString());

	}

	public static String convert(final byte bytes[],int pos, int len) {

		StringBuffer sb = new StringBuffer(len * 2);
		for (int i = pos; i < pos+len; i++) {
			sb.append(convertDigit((int) (bytes[i] >> 4)));
			sb.append(convertDigit((int) (bytes[i] & 0x0f)));
		}
		return (sb.toString());

	}


	/**
	 * byte数组转16进制字符串
	 * @param data
	 * @return
	 */
	public static String bytesToString(byte[] data) {
		String getString = "";
		for (int i = 0; i < data.length; i++) {
			getString += String.format("%02X", data[i]);
		}
		return getString;
	}

	/**
	 * byte转16进制字符串
	 * @param data
	 * @return
	 */
	public static String byteToString(byte data) {
		String getString = "";
		getString = String.format("%02X", data);
		return getString;
	}


	/**
	 * bite 转byte
	 * @param bit
	 * @return
	 */
	public static byte bitToByte(String bit) {
		int re, len;
		if (null == bit) {
			return 0;
		}
		len = bit.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit处理
			if (bit.charAt(0) == '0') {// 正数
				re = Integer.parseInt(bit, 2);
			} else {// 负数
				re = Integer.parseInt(bit, 2) - 256;
			}
		} else {//4 bit处理
			re = Integer.parseInt(bit, 2);
		}
		return (byte) re;
	}


	/**
	 * byte转bit
	 * @param by
	 * @return
	 */
	public static String getBit(byte by){
		StringBuffer sb = new StringBuffer();
		sb.append((by>>7)&0x1)
				.append((by>>6)&0x1)
				.append((by>>5)&0x1)
				.append((by>>4)&0x1)
				.append((by>>3)&0x1)
				.append((by>>2)&0x1)
				.append((by>>1)&0x1)
				.append((by>>0)&0x1);
		return sb.toString();
	}


	//高位在前，低位在后
	public static byte[] int2bytes(int num){
		byte[] result = new byte[4];
		result[0] = (byte)((num >>> 24) & 0xff);//说明一
		result[1] = (byte)((num >>> 16)& 0xff );
		result[2] = (byte)((num >>> 8) & 0xff );
		result[3] = (byte)((num >>> 0) & 0xff );
		return result;
	}

	//高位在前，低位在后
	public static int bytes2int(byte[] bytes){
		int result = 0;
		if(bytes.length == 4){
			int a = (bytes[0] & 0xff) << 24;//说明二
			int b = (bytes[1] & 0xff) << 16;
			int c = (bytes[2] & 0xff) << 8;
			int d = (bytes[3] & 0xff);
			result = a | b | c | d;
		}
		return result;
	}

	/**
	 * byte数组中取int数值，高位在前,低位在后的顺序(长度2)
	 */
	public static int bytesToInt2(byte[] src) {
		int value;
		value = (int) (
				((src[0] & 0xFF)<<8)
						|(src[1] & 0xFF));
		return value;
	}



	public static String stringToHexString(String str,String mybKey){
		int c = 0;
		int loginKey = Integer.valueOf(mybKey);
		String data = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			c=str.charAt(i);
			c ^= loginKey;
			data=Integer.toHexString(c);
			String tmp = "";
			if(data.length()==1){
				data="0"+data;
			}
			tmp +=data;
			sb.append(tmp);
		}
		String datas = sb.toString();
		return datas;
	}


}
