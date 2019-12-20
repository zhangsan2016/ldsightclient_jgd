package com.ldsight.act;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;

import java.util.Arrays;

public class WarningInformationAct extends Activity {
	// tv_device_type  tv_device_address tv_device_volt tv_device_electricity tv_device_power  tv_device_energy tv_device_power_diviseur
	// 设备类型 、设备地址、电压 、电流 、功率 、 电能 功率因数
	private TextView type, address,volt,electricity,power,energy,powerDiviseur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_warning);
		initView();
		showWarningParame(); // 显示报警参数


		LinearLayout prevButton = (LinearLayout) findViewById(R.id.ll_warning_back);
		prevButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				WarningInformationAct.this.finish();

			}
		});
	}

	private void initView() {
		type = (TextView) this.findViewById(R.id.tv_device_type);
		address = (TextView) this.findViewById(R.id.tv_device_address);
		volt= (TextView) this.findViewById(R.id.tv_device_volt);
		electricity= (TextView) this.findViewById(R.id.tv_device_electricity);
		power= (TextView) this.findViewById(R.id.tv_device_power);
		energy= (TextView) this.findViewById(R.id.tv_device_energy);
		powerDiviseur= (TextView) this.findViewById(R.id.tv_device_power_diviseur);
	}

	private void showWarningParame() {
		byte[] data = getIntent().getByteArrayExtra("data");
		System.out.println("showWarningParame = " + Arrays.toString(data));
		if(data != null){
			if(data[7] == 1){
				type.setText("电参异常");
			}else if(data[7] == 2){
				type.setText("设备不在线");
			}else if(data[7] == 3){
				type.setText("断缆报警");
			}
		}
		address.setText(data[8] + "");
		volt.setText(data[9] + "");
		electricity.setText(data[10] + "");
		power.setText(data[11] + "");
		energy.setText(data[12] + "");
		powerDiviseur.setText(data[13] + "");



	}

}
