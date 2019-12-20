package com.ldsight.entity;

import java.io.Serializable;

public class StreetAndDevice implements Serializable {
	private String streetId;// 街道ID
	private String streetName;// 街道名字
	private String cityId;// 城市ID
	private String startTime;// 电灯开启时间
	private String endTime;// 电灯关闭时间

	private int deviceParamId;
	private String deviceId;// 传感设备ID
	private int mb_addr;// 从站地址
	private int mb_func;// 功能码
	private int mb_size;// 字节数
	private int mb_a_volt;// A相电压
	private int mb_b_volt;// B相电压
	private int mb_c_volt;// C相电压
	private int mb_a_Ampere;// A相电流
	private int mb_b_Ampere;// B相电流
	private int mb_c_Ampere;// C相电流
	private int mb_hz;// 频率
	private int mb_pfav;// 总功率因数
	private int mb_ssum;// 总视在功率
	private int mb_yed;// 总有功电度
	private int mb_ned;// 总无功电度
	private int mb_pa;// A相有功功率
	private int mb_pb;// B相有功功率
	private int mb_pc;// C相有功功率
	private int mb_psum;// 总有功功率
	private int mb_qa;// A相无功功率
	private int mb_qb;// B相无功功率
	private int mb_qc;// C相无功功率
	private int mb_qsum;// 总无功功率
	private String mb_time;// 上传时间

	private String uuid= "";
	private String energy100;
	private String energy75;
	private String energy50;
	private String energy25;

	private int lightSwitch;

	/*
	 * 报警参数
	 */
	private int alarmId; // 报警Id
	private int alarmType; // 报警类型
	private int alarmAmpere; // 电流
	private int alarmDeviceAddress; // 设备地址
	private String alarmDeviceId; // 电箱id
	private int alarmElectric; // 电能
	private int alarmFactor; // 功率因素
	private int alarmPower; // 功率
	private int alarmVoltage; // 电压

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

	public int getLightSwitch() {
		return lightSwitch;
	}

	public void setLightSwitch(int lightSwitch) {
		this.lightSwitch = lightSwitch;
	}

	public String getUuid() {
		return uuid;
	}

	public byte[] getByteUuid() {
		if(uuid.isEmpty() || uuid.equals("")){
			return null;
		}
		byte[] byteUuid = new byte[8];
		String[] struuid = uuid.split(",");
		for (int i = 0; i < byteUuid.length; i++) {
			byteUuid[i] = Byte.parseByte(struuid[i]);
		}

		return byteUuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEnergy100() {
		return energy100;
	}

	public void setEnergy100(String energy100) {
		this.energy100 = energy100;
	}

	public String getEnergy75() {
		return energy75;
	}

	public void setEnergy75(String energy75) {
		this.energy75 = energy75;
	}

	public String getEnergy50() {
		return energy50;
	}

	public void setEnergy50(String energy50) {
		this.energy50 = energy50;
	}

	public String getEnergy25() {
		return energy25;
	}

	public void setEnergy25(String energy25) {
		this.energy25 = energy25;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStreetId() {
		return streetId;
	}

	public void setStreetId(String streetId) {
		this.streetId = streetId;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public int getDeviceParamId() {
		return deviceParamId;
	}

	public void setDeviceParamId(int deviceParamId) {
		this.deviceParamId = deviceParamId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getMb_addr() {
		return mb_addr;
	}

	public void setMb_addr(int mb_addr) {
		this.mb_addr = mb_addr;
	}

	public int getMb_func() {
		return mb_func;
	}

	public void setMb_func(int mb_func) {
		this.mb_func = mb_func;
	}

	public int getMb_size() {
		return mb_size;
	}

	public void setMb_size(int mb_size) {
		this.mb_size = mb_size;
	}

	public int getMb_a_volt() {
		return mb_a_volt;
	}

	public void setMb_a_volt(int mb_a_volt) {
		this.mb_a_volt = mb_a_volt;
	}

	public int getMb_b_volt() {
		return mb_b_volt;
	}

	public void setMb_b_volt(int mb_b_volt) {
		this.mb_b_volt = mb_b_volt;
	}

	public int getMb_c_volt() {
		return mb_c_volt;
	}

	public void setMb_c_volt(int mb_c_volt) {
		this.mb_c_volt = mb_c_volt;
	}

	public int getMb_a_Ampere() {
		return mb_a_Ampere;
	}

	public void setMb_a_Ampere(int mb_a_Ampere) {
		this.mb_a_Ampere = mb_a_Ampere;
	}

	public int getMb_b_Ampere() {
		return mb_b_Ampere;
	}

	public void setMb_b_Ampere(int mb_b_Ampere) {
		this.mb_b_Ampere = mb_b_Ampere;
	}

	public int getMb_c_Ampere() {
		return mb_c_Ampere;
	}

	public void setMb_c_Ampere(int mb_c_Ampere) {
		this.mb_c_Ampere = mb_c_Ampere;
	}

	public int getMb_hz() {
		return mb_hz;
	}

	public void setMb_hz(int mb_hz) {
		this.mb_hz = mb_hz;
	}

	public int getMb_pfav() {
		return mb_pfav;
	}

	public void setMb_pfav(int mb_pfav) {
		this.mb_pfav = mb_pfav;
	}

	public int getMb_ssum() {
		return mb_ssum;
	}

	public void setMb_ssum(int mb_ssum) {
		this.mb_ssum = mb_ssum;
	}

	public int getMb_yed() {
		return mb_yed;
	}

	public void setMb_yed(int mb_yed) {
		this.mb_yed = mb_yed;
	}

	public int getMb_ned() {
		return mb_ned;
	}

	public void setMb_ned(int mb_ned) {
		this.mb_ned = mb_ned;
	}

	public int getMb_pa() {
		return mb_pa;
	}

	public void setMb_pa(int mb_pa) {
		this.mb_pa = mb_pa;
	}

	public int getMb_pb() {
		return mb_pb;
	}

	public void setMb_pb(int mb_pb) {
		this.mb_pb = mb_pb;
	}

	public int getMb_pc() {
		return mb_pc;
	}

	public void setMb_pc(int mb_pc) {
		this.mb_pc = mb_pc;
	}

	public int getMb_psum() {
		return mb_psum;
	}

	public void setMb_psum(int mb_psum) {
		this.mb_psum = mb_psum;
	}

	public int getMb_qa() {
		return mb_qa;
	}

	public void setMb_qa(int mb_qa) {
		this.mb_qa = mb_qa;
	}

	public int getMb_qb() {
		return mb_qb;
	}

	public void setMb_qb(int mb_qb) {
		this.mb_qb = mb_qb;
	}

	public int getMb_qc() {
		return mb_qc;
	}

	public void setMb_qc(int mb_qc) {
		this.mb_qc = mb_qc;
	}

	public int getMb_qsum() {
		return mb_qsum;
	}

	public void setMb_qsum(int mb_qsum) {
		this.mb_qsum = mb_qsum;
	}

	public String getMb_time() {
		return mb_time;
	}

	public void setMb_time(String mb_time) {
		this.mb_time = mb_time;
	}

	/*public String toString() {
		return "streetId:" + streetId + ";streetName:" + streetName;
	}*/

	@Override
	public String toString() {
		return "StreetAndDevice [streetId=" + streetId + ", streetName="
				+ streetName + ", cityId=" + cityId + ", startTime="
				+ startTime + ", endTime=" + endTime + ", deviceParamId="
				+ deviceParamId + ", deviceId=" + deviceId + ", mb_addr="
				+ mb_addr + ", mb_func=" + mb_func + ", mb_size=" + mb_size
				+ ", mb_a_volt=" + mb_a_volt + ", mb_b_volt=" + mb_b_volt
				+ ", mb_c_volt=" + mb_c_volt + ", mb_a_Ampere=" + mb_a_Ampere
				+ ", mb_b_Ampere=" + mb_b_Ampere + ", mb_c_Ampere="
				+ mb_c_Ampere + ", mb_hz=" + mb_hz + ", mb_pfav=" + mb_pfav
				+ ", mb_ssum=" + mb_ssum + ", mb_yed=" + mb_yed + ", mb_ned="
				+ mb_ned + ", mb_pa=" + mb_pa + ", mb_pb=" + mb_pb + ", mb_pc="
				+ mb_pc + ", mb_psum=" + mb_psum + ", mb_qa=" + mb_qa
				+ ", mb_qb=" + mb_qb + ", mb_qc=" + mb_qc + ", mb_qsum="
				+ mb_qsum + ", mb_time=" + mb_time + ", uuid=" + uuid
				+ ", energy100=" + energy100 + ", energy75=" + energy75
				+ ", energy50=" + energy50 + ", energy25=" + energy25
				+ ", lightSwitch=" + lightSwitch + ", alarmId=" + alarmId
				+ ", alarmType=" + alarmType + ", alarmAmpere=" + alarmAmpere
				+ ", alarmDeviceAddress=" + alarmDeviceAddress
				+ ", alarmDeviceId=" + alarmDeviceId + ", alarmElectric="
				+ alarmElectric + ", alarmFactor=" + alarmFactor
				+ ", alarmPower=" + alarmPower + ", alarmVoltage="
				+ alarmVoltage + "]";
	}


}
