package com.ldsight.act;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.BrightenDevice;
import com.ldsight.util.StringUtil;

public class BrightenMainListDialogItemAct extends Activity {
	private TextView tvDeviceId;
	private TextView tvSequenceId;
	private TextView tvTimingStages1;
	private TextView tvLuminance1;
	private TextView tvTimingStages2;
	private TextView tvLuminance2;
	private TextView tvTimingStages3;
	private TextView tvLuminance3;
	private TextView tvTimingStages4;
	private TextView tvLuminance4;
	private TextView tvTimingStages5;
	private TextView tvLuminance5;
	private TextView tvTimingStages6;
	private TextView tvLuminance6;
	private TextView tvSingleLuminance;
	private TextView tvDeviceStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.brighten_mainlist_dialog);

		// 获取传递过来的对象
		BrightenDevice brightenDevice = (BrightenDevice) getIntent()
				.getSerializableExtra("equipment_information");

		findViews();

		// 设置传递过来的参数到Dialog中
		setData(brightenDevice);

	}

	private void setData(BrightenDevice brightenDevice) {

		if (brightenDevice != null) {

			tvDeviceId.setText(brightenDevice.getDeviceId() + "");
			tvSequenceId.setText(brightenDevice.getSequenceId() + "");

			tvTimingStages1.setText(brightenDevice.getTimingStagesList().get(0)
					.getHour()
					+ " : "
					+ brightenDevice.getTimingStagesList().get(0).getMinute());
			tvLuminance1.setText(new StringBuffer(StringUtil.getBit((byte)brightenDevice.getTimingStagesList().get(0).getLuminance())).reverse().toString()  + "");

			tvTimingStages2.setText(brightenDevice.getTimingStagesList().get(1)
					.getHour()
					+ " : "
					+ brightenDevice.getTimingStagesList().get(1).getMinute());
			tvLuminance2.setText(new StringBuffer(StringUtil.getBit((byte)brightenDevice.getTimingStagesList().get(1).getLuminance())).reverse().toString()  + "");


			tvTimingStages3.setText(brightenDevice.getTimingStagesList().get(2)
					.getHour()
					+ " : "
					+ brightenDevice.getTimingStagesList().get(2).getMinute());
			tvLuminance3.setText(new StringBuffer(StringUtil.getBit((byte)brightenDevice.getTimingStagesList().get(2).getLuminance())).reverse().toString()  + "");


			tvTimingStages4.setText(brightenDevice.getTimingStagesList().get(3)
					.getHour()
					+ " : "
					+ brightenDevice.getTimingStagesList().get(3).getMinute());
			tvLuminance4.setText(new StringBuffer(StringUtil.getBit((byte)brightenDevice.getTimingStagesList().get(3).getLuminance())).reverse().toString()  + "");

			tvTimingStages5.setText(brightenDevice.getTimingStagesList().get(4)
					.getHour()
					+ " : "
					+ brightenDevice.getTimingStagesList().get(4).getMinute());
			tvLuminance5.setText(new StringBuffer(StringUtil.getBit((byte)brightenDevice.getTimingStagesList().get(4).getLuminance())).reverse().toString()  + "");

			tvTimingStages6.setText(brightenDevice.getTimingStagesList().get(5)
					.getHour()
					+ " : "
					+ brightenDevice.getTimingStagesList().get(5).getMinute());
			tvLuminance6.setText(new StringBuffer(StringUtil.getBit((byte)brightenDevice.getTimingStagesList().get(5).getLuminance())).reverse().toString()  + "");

			tvSingleLuminance.setText(new StringBuffer(StringUtil.getBit((byte)brightenDevice.getLuminance())).reverse().toString() + "");
			tvDeviceStatus.setText(brightenDevice.getStatus() + "");

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void findViews() {
		tvDeviceId = (TextView) findViewById(R.id.tv_deviceId);
		tvSequenceId = (TextView) findViewById(R.id.tv_sequenceId);
		tvTimingStages1 = (TextView) findViewById(R.id.tv_timing_stages1);
		tvLuminance1 = (TextView) findViewById(R.id.tv_luminance1);
		tvTimingStages2 = (TextView) findViewById(R.id.tv_timing_stages2);
		tvLuminance2 = (TextView) findViewById(R.id.tv_luminance2);
		tvTimingStages3 = (TextView) findViewById(R.id.tv_timing_stages3);
		tvLuminance3 = (TextView) findViewById(R.id.tv_luminance3);
		tvTimingStages4 = (TextView) findViewById(R.id.tv_timing_stages4);
		tvLuminance4 = (TextView) findViewById(R.id.tv_luminance4);
		tvTimingStages5 = (TextView) findViewById(R.id.tv_timing_stages5);
		tvLuminance5 = (TextView) findViewById(R.id.tv_luminance5);
		tvTimingStages6 = (TextView) findViewById(R.id.tv_timing_stages6);
		tvLuminance6 = (TextView) findViewById(R.id.tv_luminance6);
		tvSingleLuminance = (TextView) findViewById(R.id.tv_single_luminance);
		tvDeviceStatus = (TextView) findViewById(R.id.tv_device_status);
	}

}
