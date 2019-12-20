package com.ldsight.entity;

import java.io.Serializable;

/**
 * 六段调光时间参数类
 */
public class TimingStages implements Serializable{



	public TimingStages(int hour, int minute, int luminance) {
		super();
		this.hour = hour;
		this.minute = minute;
		this.luminance = luminance;
	}

	// 小时
	public int hour;
	// 分
	public int minute;
	// 亮度
	public int luminance;

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getLuminance() {
		return luminance;
	}

	public void setLuminance(int luminance) {
		this.luminance = luminance;
	}

}
