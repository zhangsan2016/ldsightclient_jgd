package com.ldsight.act;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.SingleLightDevice;

import java.util.Arrays;

public class SingleLightDialogItemAct extends Activity {
	public static String QuerySingleLampReceiver = "querySingleLampReceiver"; // 单灯参数监听
	private SingleLightDevice singleLightDevice;
	private TextView deviceId, lineState, deviceIMEI, volt, ampere, power,
			energy, pfav;
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			// 获取单灯参数
			byte[] data = (byte[]) msg.obj;
			switch (msg.what) {
				case 1:
					// 获取单灯参数
					SingleLightDevice sld = new SingleLightDevice();
					sld.setLineState(data[14]);
					sld.setDeviceId((data[15] & 0xff) + "");
					StringBuffer imei = new StringBuffer();
					imei.append(Integer.toHexString((data[16] & 0xff)).toString());
					imei.append(Integer.toHexString((data[17] & 0xff)).toString());
					imei.append(Integer.toHexString((data[18] & 0xff)).toString());
					imei.append(Integer.toHexString((data[19] & 0xff)).toString());
					imei.append(Integer.toHexString((data[20] & 0xff)).toString());
					imei.append(Integer.toHexString((data[21] & 0xff)).toString());
					imei.append(Integer.toHexString((data[22] & 0xff)).toString());
					imei.append(Integer.toHexString((data[23] & 0xff)).toString());
					imei.append(Integer.toHexString((data[24] & 0xff)).toString());
					imei.append(Integer.toHexString((data[25] & 0xff)).toString());
					imei.append(Integer.toHexString((data[26] & 0xff)).toString());
					imei.append(Integer.toHexString((data[27] & 0xff)).toString());
					sld.setDeviceIMEI(imei.toString());
					sld.setVolt((data[28] << 8) + (double) (data[29] & 0xFF));
					sld.setAmpere((data[30] << 8) + (double) (data[31] & 0xFF));
					sld.setPower((data[32] << 8) + (double) (data[33] & 0xFF));
					sld.setEnergy((data[34] << 8) + (double) (data[35] & 0xFF));
					sld.setPfav((data[36] << 8) + (double) (data[37] & 0xFF));
					singleLightDevice = sld;
					// 显示单灯参数
					showParameter();
					break;

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.single_light_control_dialog);
		// 获取传递过来的对象
		// singleLightDevice =
		// (SingleLightDevice)getIntent().getSerializableExtra("sigleLightParameter");

		initView();

		// 动态注册单灯监听
		IntentFilter lightPoleParameterFilter = new IntentFilter(
				QuerySingleLampReceiver);
		lightPoleParameterFilter.setPriority(1000);
		registerReceiver(querySingleLampReceiver,
				lightPoleParameterFilter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(querySingleLampReceiver);
	}

	private void showParameter() {
		deviceId.setText(singleLightDevice.getDeviceId());
		if (singleLightDevice.getLineState() == 1) {
			lineState.setText("在线");
		} else {
			lineState.setText("不在线");
		}
		deviceIMEI.setText(singleLightDevice.getDeviceIMEI());
		volt.setText(singleLightDevice.getVolt() / 100 + " U");
		ampere.setText(singleLightDevice.getAmpere() / 100 + " A");
		power.setText(singleLightDevice.getPower() / 100 + " P");
		energy.setText(singleLightDevice.getEnergy() / 100 + " W");
		pfav.setText(singleLightDevice.getPfav() / 100 + " P");
	}

	private void initView() {
		deviceId = (TextView) this.findViewById(R.id.tv_deviceId);
		lineState = (TextView) this.findViewById(R.id.tv_line);
		deviceIMEI = (TextView) this.findViewById(R.id.tv_deviceIMEI);
		volt = (TextView) this.findViewById(R.id.tv_volt);
		ampere = (TextView) this.findViewById(R.id.tv_ampere);
		power = (TextView) this.findViewById(R.id.tv_power);
		energy = (TextView) this.findViewById(R.id.tv_energy);
		pfav = (TextView) this.findViewById(R.id.tv_pfav);
	}

	// 创建广播接收器
	private BroadcastReceiver querySingleLampReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			byte[] data = intent.getByteArrayExtra("data");
			System.out.println("xx dialogItemAct 获取" + Arrays.toString(data));
			Message msg = updateHandler.obtainMessage();
			msg.what = 1;
			msg.obj = data;
			updateHandler.sendMessage(msg);


		}
	};


}
