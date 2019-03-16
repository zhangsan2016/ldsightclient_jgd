package com.ldsight.entity;

import java.io.Serializable;

public class SingleLightDevice implements Serializable {

	private String deviceId;    // 设备ID
	private int lineState;      // 在线状态
	private String  deviceIMEI;  // 设备串号
	private double volt;// 电压
	private double ampere;// 电流
	private double power; // 功率
	private double energy; // 电能
	private double pfav;  // 功率因数
	private byte[] uuid;








	public byte[] getUuid() {
		return uuid;
	}
	public void setUuid(byte[] uuid) {
		this.uuid = uuid;
	}
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
	public String getDeviceIMEI() {
		return deviceIMEI;
	}
	public void setDeviceIMEI(String deviceIMEI) {
		this.deviceIMEI = deviceIMEI;
	}
	public double getVolt() {
		return volt;
	}
	public void setVolt(double volt) {
		this.volt = volt;
	}
	public double getAmpere() {
		return ampere;
	}
	public void setAmpere(double ampere) {
		this.ampere = ampere;
	}
	public double getPower() {
		return power;
	}
	public void setPower(double power) {
		this.power = power;
	}
	public double getEnergy() {
		return energy;
	}
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	public double getPfav() {
		return pfav;
	}
	public void setPfav(double pfav) {
		this.pfav = pfav;
	}








}
