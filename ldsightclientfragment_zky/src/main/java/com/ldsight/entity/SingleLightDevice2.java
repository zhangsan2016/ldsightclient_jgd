package com.ldsight.entity;

import java.io.Serializable;

public class SingleLightDevice2 implements Serializable {

	private String deviceId;    // 设备ID
	private int lineState;      // 在线状态
	//private byte[] uuid;


	/*public byte[] getUuid() {
		return uuid;
	}
	public void setUuid(byte[] uuid) {
		this.uuid = uuid;
	}*/
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getLineState() {
		return lineState;
	}
	public void setLineState(int lineState) {
		this.lineState = lineState;
	}


}
