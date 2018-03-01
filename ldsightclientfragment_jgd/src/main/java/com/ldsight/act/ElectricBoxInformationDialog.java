package com.ldsight.act;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.AlertDevice;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ElectricBoxInformationDialog extends Activity {
	private AlertDevice alertDevice;
	/*
	 * 显示的参数
	 */
	private TextView alarmId, alarmType, alarmDeviceId, alarmAmpere,
			alarmVoltage, alarmElectric, alarmDeviceAddress, alarmFactor,
			alarmPower, alarmDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.electric_box_dialog);
		alertDevice = (AlertDevice) getIntent()
				.getSerializableExtra("alertDevice");
		if (alertDevice == null) {
			Toast.makeText(this, "参数为空！", 0).show();
		} else {
			initView();
			setParameters();
		}

	}

	private void setParameters() {
		alarmId.setText("" + alertDevice.getAlarmId());
		if (alertDevice.getAlarmType() == 1) {
			alarmType.setText("电参异常");
		} else if (alertDevice.getAlarmType() == 2) {
			alarmType.setText("设备不在线");
		} else if (alertDevice.getAlarmType() == 3) {
			alarmType.setText("断缆报警");
		}
		alarmDeviceId.setText("" + alertDevice.getAlarmDeviceId());
		alarmAmpere.setText("" + alertDevice.getAlarmAmpere());
		alarmVoltage.setText("" + alertDevice.getAlarmVoltage());
		alarmElectric.setText("" + alertDevice.getAlarmElectric());
		alarmDeviceAddress.setText("" + alertDevice.getAlarmDeviceAddress());
		alarmFactor.setText("" + alertDevice.getAlarmFactor());
		alarmPower.setText("" + alertDevice.getAlarmPower());
		alarmDate.setText("" + alertDevice.getDate());
	}

	private void initView() {
		alarmId = (TextView) this.findViewById(R.id.tv_alarm_id);
		alarmType = (TextView) this.findViewById(R.id.tv_alarm_type);
		alarmDeviceId = (TextView) this.findViewById(R.id.tv_alarm_deviceId);
		alarmAmpere = (TextView) this.findViewById(R.id.tv_alarm_ampere);
		alarmVoltage = (TextView) this.findViewById(R.id.tv_alarm_voltage);
		alarmElectric = (TextView) this.findViewById(R.id.tv_alarm_electric);
		alarmDeviceAddress = (TextView) this
				.findViewById(R.id.tv_alarm_device_address);
		alarmFactor = (TextView) this.findViewById(R.id.tv_alarm_factor);
		alarmPower = (TextView) this.findViewById(R.id.tv_alarm_power);
		alarmDate = (TextView) this.findViewById(R.id.tv_alarm_date);
	}

}
