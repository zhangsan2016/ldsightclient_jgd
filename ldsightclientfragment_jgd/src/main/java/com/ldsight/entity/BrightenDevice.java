package com.ldsight.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 亮化设备
 *
 * @author ldgd
 *
 */
public class BrightenDevice implements Serializable {

	/**
	 * 设备id
	 */
	private int deviceId;

	/**
	 * 序列号
	 */
	private String sequenceId;

	/**
	 * 六段调光定时时间
	 */
	private List<TimingStages> timingStagesList;

	/**
	 * 亮度
	 */
	private int luminance;

	/**
	 * 状态
	 */
	private int status;

	public BrightenDevice() {

	}

	public BrightenDevice(int deviceId, String sequenceId,
						  List<TimingStages> timingStagesList, int luminance, int status) {
		super();
		this.deviceId = deviceId;
		this.sequenceId = sequenceId;
		this.timingStagesList = timingStagesList;
		this.luminance = luminance;
		this.status = status;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public List<TimingStages> getTimingStagesList() {
		return timingStagesList;
	}

	public void setTimingStagesList(List<TimingStages> timingStagesList) {
		this.timingStagesList = timingStagesList;
	}

	public int getLuminance() {
		return luminance;
	}

	public void setLuminance(int luminance) {
		this.luminance = luminance;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
