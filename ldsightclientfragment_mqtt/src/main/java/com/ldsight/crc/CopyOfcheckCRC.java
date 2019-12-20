package com.ldsight.crc;

import java.util.Arrays;

public class CopyOfcheckCRC {

	public static byte[] HexStringBytes(String src) {
		if (null == src || 0 == src.length()) {
			return null;
		}
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < (tmp.length / 2); i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}

		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		// System.out.println("_b0="+_b0);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * 获取crc
	 *
	 * @param data
	 * @return
	 */
	public static byte[] crc(byte[] data) {

		int crc = CRC16.calcCrc16(data);
		String str_crc = String.format("%04x", crc);
		byte[] CRC = HexStringBytes(str_crc);

		// 合并数组
		byte[] newCrc = new byte[data.length + 2];
		System.arraycopy(data, 0, newCrc, 0, data.length);
		System.arraycopy(CRC, 0, newCrc, data.length, CRC.length);

		return newCrc;
	}

	/**
	 * 校验CRC
	 * @param data  校验的数据
	 * @param data2  校验的crc
	 * @return
	 */
	public static boolean checkTheCrc(byte[] data, byte[] data2) {

		int crc = CRC16.calcCrc16(data);
		String str_crc = String.format("%04x", crc);
		byte[] CRC = HexStringBytes(str_crc);

		return Arrays.equals(CRC,data2);
	}

}
