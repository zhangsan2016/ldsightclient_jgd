package com.ldsight.entity;

import java.io.Serializable;

public class AlertDevice implements Serializable {

	/*
	 * 报警参数
	 */
	private int alarmId;   // 报警Id
	private int alarmType;  // 报警类型
	private int alarmAmpere;  // 电流(A)
	private int alarmDeviceAddress;  // 设备地址
	private String alarmDeviceId;  // 电箱id
	private int alarmElectric;   // 电能 (W)
	private int alarmFactor;    // 功率因素
	private int alarmPower;     // 功率 (P)
	private int alarmVoltage;   // 电压(U)
	private String date;        // 日期



	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public int getAlarmAmpere() {
		return alarmAmpere;
	}
	public void setAlarmAmpere(int alarmAmpere) {
		this.alarmAmpere = alarmAmpere;
	}
	public int getAlarmDeviceAddress() {
		return alarmDeviceAddress;
	}
	public void setAlarmDeviceAddress(int alarmDeviceAddress) {
		this.alarmDeviceAddress = alarmDeviceAddress;
	}
	public String getAlarmDeviceId() {
		return alarmDeviceId;
	}
	public void setAlarmDeviceId(String alarmDeviceId) {
		this.alarmDeviceId = alarmDeviceId;
	}
	public int getAlarmElectric() {
		return alarmElectric;
	}
	public void setAlarmElectric(int alarmElectric) {
		this.alarmElectric = alarmElectric;
	}
	public int getAlarmFactor() {
		return alarmFactor;
	}
	public void setAlarmFactor(int alarmFactor) {
		this.alarmFactor = alarmFactor;
	}
	public int getAlarmPower() {
		return alarmPower;
	}
	public void setAlarmPower(int alarmPower) {
		this.alarmPower = alarmPower;
	}
	public int getAlarmVoltage() {
		return alarmVoltage;
	}
	public void setAlarmVoltage(int alarmVoltage) {
		this.alarmVoltage = alarmVoltage;
	}



}
