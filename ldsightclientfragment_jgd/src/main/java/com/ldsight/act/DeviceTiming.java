package com.ldsight.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ldsightclient_jgd.R;
import com.ldsight.application.MyApplication;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.util.Arrays;

public class DeviceTiming extends Activity {
	public static String receiver = "DEVICE_TIMING_RECELVER";
	// 定时开始结束时间
	TextView txtStartTime, txtEndTime;
	// 开始时间小时分钟
	private int startHour, startMinute, endHour, endMinute;
	// 开始时间结束时间字符串
	private String strStartTime = "";
	private String strEndTime = "";
	// 定时开始结束时间设置按钮
	private LinearLayout llStarttime, llEndTime;
	private TimePickerDialog startTimePickerDialog, endTimePickerDialog;
	private TextView tv_spacing_start_time1, tv_spacing_start_time2,
			tv_spacing_start_time3, tv_spacing_start_time4,
			tv_spacing_start_time5, tv_spacing_start_time6;
	// 确认按钮
	private Button okDevice;
	// 进度百分比
	private TextView tv_progress1, tv_progress2, tv_progress3, tv_progress4,
			tv_progress5, tv_progress6;
	private LinearLayout ll_spacing_start_time1, ll_spacing_start_time2,
			ll_spacing_start_time3, ll_spacing_start_time4,
			ll_spacing_start_time5, ll_spacing_start_time6;
	// 进度条
	private SeekBar sb_brightness1, sb_brightness2, sb_brightness3,
			sb_brightness4, sb_brightness5, sb_brightness6;

	// 标识
	private int advocateComplementaryCode;
	private ProgressDialog mProgress;
	// 定时数据返回标识
	private boolean timingToken;

	// 当前uuid
	private byte[] uuid;
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 判断定时是否成功
					if(!timingToken){
						Toast.makeText(DeviceTiming.this, "定时失败!", Toast.LENGTH_SHORT).show();
						stopProgress();
					}
					break;
				case 2:
					// 返回定时时间
					Intent intent1 = new Intent();
					intent1.putExtra("timeData",timeData);
					setResult(advocateComplementaryCode, intent1);
					finish();
					break;

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_timing);
		// 动态注册通知
		IntentFilter filter = new IntentFilter(DeviceTiming.receiver);
		registerReceiver(MyReceiver, filter);

		initView();
		initListeners();
		initVariable();

	}

	private void initVariable() {
		advocateComplementaryCode = (Integer) getIntent().getSerializableExtra(
				"primary_timing");
		byte[] timingTime = (byte[]) getIntent().getByteArrayExtra(
				"timing_time");
		uuid = (byte[]) getIntent().getByteArrayExtra("uuid");

		if (timingTime == null) {
			return;
		}
		// 初始化时间参数
		setTime(timingTime);
		startTimePickerDialog = new TimePickerDialog(DeviceTiming.this,
				new OnTimeSetListener() {
					public void onTimeSet(TimePicker view, int hourOfDay,
										  int minute) {
						String strStartHour = "" + hourOfDay;
						String strStartMinute = "" + minute;

						if (hourOfDay / 10 <= 0) {
							strStartHour = "0" + strStartHour;
						}
						if (minute / 10 <= 0) {
							strStartMinute = "0" + strStartMinute;
						}
						startHour = hourOfDay;
						startMinute = minute;
						strStartTime = strStartHour + " : " + strStartMinute;
						txtStartTime.setText(strStartTime);
						tv_spacing_start_time1.setText(strStartTime);
					}
				}, timingTime[0], timingTime[1], true);

		endTimePickerDialog = new TimePickerDialog(DeviceTiming.this,
				new OnTimeSetListener() {
					public void onTimeSet(TimePicker view, int hourOfDay,
										  int minute) {
						String strEndHour = "" + hourOfDay;
						String strEndMinute = "" + minute;

						if (hourOfDay / 10 <= 0) {
							strEndHour = "0" + strEndHour;
						}
						if (minute / 10 <= 0) {
							strEndMinute = "0" + strEndMinute;
						}
						endHour = hourOfDay;
						endMinute = minute;
						strEndTime = strEndHour + " : " + strEndMinute;
						txtEndTime.setText(strEndTime);
						tv_spacing_start_time6.setText(strEndTime);

					}
				}, timingTime[14], timingTime[15], true);


	}

	private void setTime(byte[] time) {
		txtStartTime.setText(time[0] + ":" + isLessTen(time[1]));
		txtEndTime.setText(time[15] + ":" + isLessTen(time[16]) );

		tv_spacing_start_time1.setText(time[0] + ":" + time[1]);
		sb_brightness1.setProgress(time[2]);
		sb_brightness2.setProgress(time[5]);
		sb_brightness3.setProgress(time[8]);
		sb_brightness4.setProgress(time[11]);
		sb_brightness5.setProgress(time[14]);

		tv_spacing_start_time2.setText(time[3] + ":" + isLessTen(time[4]));
		tv_spacing_start_time3.setText(time[6] + ":" + isLessTen(time[7]) );
		tv_spacing_start_time4.setText(time[9] + ":" + isLessTen(time[10]));
		tv_spacing_start_time5.setText(time[12] + ":" + isLessTen(time[13]));
		tv_spacing_start_time6.setText(time[15] + ":" + isLessTen(time[16]));
		// 六段不可点击
		sb_brightness6.setEnabled(false);
	}

	/**
	 * 小于10的数字后面加0
	 * @return
	 */
	private String isLessTen(byte time){
		String timeStr = Integer.toString(time);
		if(time < 10){
			timeStr = "0" + time;
		}
		return timeStr;
	}

	private void initListeners() {
		// 进度更新
		sb_brightness1
				.setOnSeekBarChangeListener(new MySeekBarChangeListener());
		sb_brightness2
				.setOnSeekBarChangeListener(new MySeekBarChangeListener());
		sb_brightness3
				.setOnSeekBarChangeListener(new MySeekBarChangeListener());
		sb_brightness4
				.setOnSeekBarChangeListener(new MySeekBarChangeListener());
		sb_brightness5
				.setOnSeekBarChangeListener(new MySeekBarChangeListener());
		sb_brightness6
				.setOnSeekBarChangeListener(new MySeekBarChangeListener());
		// 六段时间设置
		ll_spacing_start_time1
				.setOnClickListener(new MyOnclickCancelListener());
		ll_spacing_start_time2
				.setOnClickListener(new MyOnclickCancelListener());
		ll_spacing_start_time3
				.setOnClickListener(new MyOnclickCancelListener());
		ll_spacing_start_time4
				.setOnClickListener(new MyOnclickCancelListener());
		ll_spacing_start_time5
				.setOnClickListener(new MyOnclickCancelListener());
		ll_spacing_start_time6
				.setOnClickListener(new MyOnclickCancelListener());
		// 开始结束时间设置
		llStarttime.setOnClickListener(new MyOnclickCancelListener());
		llEndTime.setOnClickListener(new MyOnclickCancelListener());
		// 设置
		okDevice.setOnClickListener(new MyOnclickCancelListener());

	}

	private void initView() {
		// 六个阶段的时间LinearLayout
		ll_spacing_start_time1 = (LinearLayout) this
				.findViewById(R.id.ll_spacing_start_time1);
		ll_spacing_start_time2 = (LinearLayout) this
				.findViewById(R.id.ll_spacing_start_time2);
		ll_spacing_start_time3 = (LinearLayout) this
				.findViewById(R.id.ll_spacing_start_time3);
		ll_spacing_start_time4 = (LinearLayout) this
				.findViewById(R.id.ll_spacing_start_time4);
		ll_spacing_start_time5 = (LinearLayout) this
				.findViewById(R.id.ll_spacing_start_time5);
		ll_spacing_start_time6 = (LinearLayout) this
				.findViewById(R.id.ll_spacing_start_time6);

		sb_brightness1 = (SeekBar) this.findViewById(R.id.sb_brightness1);
		sb_brightness2 = (SeekBar) this.findViewById(R.id.sb_brightness2);
		sb_brightness3 = (SeekBar) this.findViewById(R.id.sb_brightness3);
		sb_brightness4 = (SeekBar) this.findViewById(R.id.sb_brightness4);
		sb_brightness5 = (SeekBar) this.findViewById(R.id.sb_brightness5);
		sb_brightness6 = (SeekBar) this.findViewById(R.id.sb_brightness6);

		// 进度条界面参数初始化
		tv_progress1 = (TextView) this.findViewById(R.id.tv_progress1);
		tv_progress2 = (TextView) this.findViewById(R.id.tv_progress2);
		tv_progress3 = (TextView) this.findViewById(R.id.tv_progress3);
		tv_progress4 = (TextView) this.findViewById(R.id.tv_progress4);
		tv_progress5 = (TextView) this.findViewById(R.id.tv_progress5);
		tv_progress6 = (TextView) this.findViewById(R.id.tv_progress6);

		// 六个阶段的textView
		tv_spacing_start_time1 = (TextView) this
				.findViewById(R.id.tv_spacing_start_time1);
		tv_spacing_start_time2 = (TextView) this
				.findViewById(R.id.tv_spacing_start_time2);
		tv_spacing_start_time3 = (TextView) this
				.findViewById(R.id.tv_spacing_start_time3);
		tv_spacing_start_time4 = (TextView) this
				.findViewById(R.id.tv_spacing_start_time4);
		tv_spacing_start_time5 = (TextView) this
				.findViewById(R.id.tv_spacing_start_time5);
		tv_spacing_start_time6 = (TextView) this
				.findViewById(R.id.tv_spacing_start_time6);

		txtStartTime = (TextView) findViewById(R.id.txt_device_main_start_time);
		txtEndTime = (TextView) findViewById(R.id.txt_device_main_end_time);

		okDevice = (Button) this.findViewById(R.id.btn_ok_device_main);

		// 开始结束时间设置
		llStarttime = (LinearLayout) this
				.findViewById(R.id.ll_device_main_start_time);
		llEndTime = (LinearLayout) this
				.findViewById(R.id.ll_device_main_end_time);

	}

	private class MySeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {
			switch (seekBar.getId()) {
				case R.id.sb_brightness1:
					tv_progress1.setText(progress + "%");
					break;
				case R.id.sb_brightness2:
					tv_progress2.setText(progress + "%");
					break;
				case R.id.sb_brightness3:
					tv_progress3.setText(progress + "%");
					break;
				case R.id.sb_brightness4:
					tv_progress4.setText(progress + "%");
					break;
				case R.id.sb_brightness5:
					tv_progress5.setText(progress + "%");
					break;
				case R.id.sb_brightness6:
					System.out.println("sb_brightness6");
					tv_progress6.setText(progress + "%");
					break;

			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}

	}

	private class MyOnclickCancelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
				case R.id.btn_ok_device_main:
					showProgress();
					new Thread() {
						public void run() {
							getTimingParameter();
							try {
								sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// finish();

						}

					}.start();

					break;
				case R.id.ll_spacing_start_time1:
					new TimePickerDialog(DeviceTiming.this,
							new OnTimeSetListener() {
								public void onTimeSet(TimePicker view,
													  int hourOfDay, int minute) {
									String strEndHour = "" + hourOfDay;
									String strEndMinute = "" + minute;

									if (hourOfDay / 10 <= 0) {
										strEndHour = "0" + strEndHour;
									}
									if (minute / 10 <= 0) {
										strEndMinute = "0" + strEndMinute;
									}
									endHour = hourOfDay;
									endMinute = minute;
									strEndTime = strEndHour + " : " + strEndMinute;
									tv_spacing_start_time1.setText(strEndTime);
									txtStartTime.setText(strEndTime);
								}
							}, Integer.parseInt(tv_spacing_start_time1.getText()
							.toString().trim().split(":")[0].trim()),
							Integer.parseInt(tv_spacing_start_time1.getText()
									.toString().trim().split(":")[1].trim()), true)
							.show();
					break;
				case R.id.ll_spacing_start_time2:
					new TimePickerDialog(DeviceTiming.this,
							new OnTimeSetListener() {
								public void onTimeSet(TimePicker view,
													  int hourOfDay, int minute) {
									String strEndHour = "" + hourOfDay;
									String strEndMinute = "" + minute;

									if (hourOfDay / 10 <= 0) {
										strEndHour = "0" + strEndHour;
									}
									if (minute / 10 <= 0) {
										strEndMinute = "0" + strEndMinute;
									}
									endHour = hourOfDay;
									endMinute = minute;
									strEndTime = strEndHour + " : " + strEndMinute;
									tv_spacing_start_time2.setText(strEndTime);
								}
							}, Integer.parseInt(tv_spacing_start_time2.getText()
							.toString().trim().split(":")[0].trim()),
							Integer.parseInt(tv_spacing_start_time2.getText()
									.toString().trim().split(":")[1].trim()), true)
							.show();
					break;
				case R.id.ll_spacing_start_time3:
					new TimePickerDialog(DeviceTiming.this,
							new OnTimeSetListener() {
								public void onTimeSet(TimePicker view,
													  int hourOfDay, int minute) {
									String strEndHour = "" + hourOfDay;
									String strEndMinute = "" + minute;

									if (hourOfDay / 10 <= 0) {
										strEndHour = "0" + strEndHour;
									}
									if (minute / 10 <= 0) {
										strEndMinute = "0" + strEndMinute;
									}
									endHour = hourOfDay;
									endMinute = minute;
									strEndTime = strEndHour + " : " + strEndMinute;
									tv_spacing_start_time3.setText(strEndTime);
								}
							}, Integer.parseInt(tv_spacing_start_time3.getText()
							.toString().trim().split(":")[0].trim()),
							Integer.parseInt(tv_spacing_start_time3.getText()
									.toString().trim().split(":")[1].trim()), true)
							.show();
					break;
				case R.id.ll_spacing_start_time4:
					new TimePickerDialog(DeviceTiming.this,
							new OnTimeSetListener() {
								public void onTimeSet(TimePicker view,
													  int hourOfDay, int minute) {
									String strEndHour = "" + hourOfDay;
									String strEndMinute = "" + minute;

									if (hourOfDay / 10 <= 0) {
										strEndHour = "0" + strEndHour;
									}
									if (minute / 10 <= 0) {
										strEndMinute = "0" + strEndMinute;
									}
									endHour = hourOfDay;
									endMinute = minute;
									strEndTime = strEndHour + " : " + strEndMinute;
									tv_spacing_start_time4.setText(strEndTime);
								}
							}, Integer.parseInt(tv_spacing_start_time4.getText()
							.toString().trim().split(":")[0].trim()),
							Integer.parseInt(tv_spacing_start_time4.getText()
									.toString().trim().split(":")[1].trim()), true)
							.show();
					break;
				case R.id.ll_spacing_start_time5:
					new TimePickerDialog(DeviceTiming.this,
							new OnTimeSetListener() {
								public void onTimeSet(TimePicker view,
													  int hourOfDay, int minute) {
									String strEndHour = "" + hourOfDay;
									String strEndMinute = "" + minute;

									if (hourOfDay / 10 <= 0) {
										strEndHour = "0" + strEndHour;
									}
									if (minute / 10 <= 0) {
										strEndMinute = "0" + strEndMinute;
									}
									endHour = hourOfDay;
									endMinute = minute;
									strEndTime = strEndHour + " : " + strEndMinute;
									tv_spacing_start_time5.setText(strEndTime);
								}
							}, Integer.parseInt(tv_spacing_start_time5.getText()
							.toString().trim().split(":")[0].trim()),
							Integer.parseInt(tv_spacing_start_time5.getText()
									.toString().trim().split(":")[1].trim()), true)
							.show();
					break;
				case R.id.ll_spacing_start_time6:
					new TimePickerDialog(DeviceTiming.this,
							new OnTimeSetListener() {
								public void onTimeSet(TimePicker view,
													  int hourOfDay, int minute) {
									String strEndHour = "" + hourOfDay;
									String strEndMinute = "" + minute;

									if (hourOfDay / 10 <= 0) {
										strEndHour = "0" + strEndHour;
									}
									if (minute / 10 <= 0) {
										strEndMinute = "0" + strEndMinute;
									}
									endHour = hourOfDay;
									endMinute = minute;
									strEndTime = strEndHour + " : " + strEndMinute;
									tv_spacing_start_time6.setText(strEndTime);
									txtEndTime.setText(strEndTime);
								}
							}, Integer.parseInt(tv_spacing_start_time6.getText()
							.toString().trim().split(":")[0].trim()),
							Integer.parseInt(tv_spacing_start_time6.getText()
									.toString().trim().split(":")[1].trim()), true)
							.show();
					break;
				case R.id.ll_device_main_start_time:
					startTimePickerDialog.show();
					break;
				case R.id.ll_device_main_end_time:
					endTimePickerDialog.show();
					break;
			}
		}

	}
	private byte[] timeData;
	private void getTimingParameter() {
		timeData = new byte[18];
		// 定时开始结束时间
		String[] txtStartTimeAry = txtStartTime.getText().toString().trim()
				.split(":");
		String[] txtEndTimeAry = txtEndTime.getText().toString().trim()
				.split(":");
		// 六段调光时间
		String[] timingTime1 = tv_spacing_start_time1.getText().toString()
				.trim().split(":");
		String[] timingTime2 = tv_spacing_start_time2.getText().toString()
				.trim().split(":");
		String[] timingTime3 = tv_spacing_start_time3.getText().toString()
				.trim().split(":");
		String[] timingTime4 = tv_spacing_start_time4.getText().toString()
				.trim().split(":");
		String[] timingTime5 = tv_spacing_start_time5.getText().toString()
				.trim().split(":");
		String[] timingTime6 = tv_spacing_start_time6.getText().toString()
				.trim().split(":");
		// 六段调光亮度
		int progress1 = sb_brightness1.getProgress();
		int progress2 = sb_brightness2.getProgress();
		int progress3 = sb_brightness3.getProgress();
		int progress4 = sb_brightness4.getProgress();
		int progress5 = sb_brightness5.getProgress();
		int progress6 = sb_brightness6.getProgress();

		// 封装到data数组

		timeData[0] = (byte) Integer.parseInt(timingTime1[0].trim());
		timeData[1] = (byte) Integer.parseInt(timingTime1[1].trim());
		timeData[2] = (byte) progress1;

		timeData[3] = (byte) Integer.parseInt(timingTime2[0].trim());
		timeData[4] = (byte) Integer.parseInt(timingTime2[1].trim());
		timeData[5] = (byte) progress2;

		timeData[6] = (byte) Integer.parseInt(timingTime3[0].trim());
		timeData[7] = (byte) Integer.parseInt(timingTime3[1].trim());
		timeData[8] = (byte) progress3;

		timeData[9] = (byte) Integer.parseInt(timingTime4[0].trim());
		timeData[10] = (byte) Integer.parseInt(timingTime4[1].trim());
		timeData[11] = (byte) progress4;

		timeData[12] = (byte) Integer.parseInt(timingTime5[0].trim());
		timeData[13] = (byte) Integer.parseInt(timingTime5[1].trim());
		timeData[14] = (byte) progress5;

		timeData[15] = (byte) Integer.parseInt(timingTime6[0].trim());
		timeData[16] = (byte) Integer.parseInt(timingTime6[1].trim());
		timeData[17] = (byte) progress6;

		/*
		 * Intent intent = new Intent(); intent.putExtra("parameter", data);
		 * setResult(advocateComplementaryCode, intent);
		 */

		// 推送定时到下位机
		pusher(timeData);

	};

	private void pusher(final byte[] timing) {
		new Thread() {
			public void run() {
				Pusher pusher = null;
				// 获取当前应用的uuid
				byte[] uuidApp = MyApplication.getInstance().getAppUuid();
				try {
					byte[] data = new byte[29];
					data[0] = -62; // 指令
					data[1] = 0; // 设备地址
					System.arraycopy(uuidApp, 0, data, 2, uuidApp.length); // uuid
					System.arraycopy(timing, 0, data, 10, timing.length); // 定时时间
					data[28] = (byte) advocateComplementaryCode; // 设备号  // 1主灯2辅灯0全部

					pusher = new Pusher(MyApplication.getIp(), 9966, 5000);

					System.out.println("data = " + Arrays.toString(data));
					pusher.push0x20Message(uuid, data);
					timingToken = false;
					myHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							myHandler.sendEmptyMessage(1);
						}
					}, 5000);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pusher != null) {
						try {
							pusher.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}.start();

	}
	/**
	 * 广播接收
	 */
	private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			byte[] data = intent.getByteArrayExtra("data");
			if(data[13] == -62){
				if(data[15] == 0){
					String msg = "";
					if(data[16] == 1){
						msg = "主灯定时设置成功！";
					}else{
						msg = "辅灯定时设置成功！";
					}
					Toast.makeText(DeviceTiming.this, msg, Toast.LENGTH_SHORT).show();
					timingToken = true;
					stopProgress();


					myHandler.sendEmptyMessage(2);
				}
			}


		}
	};

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...", true, false);
	}

	private void stopProgress() {
		if (mProgress != null) {
			mProgress.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 关闭接收者
		unregisterReceiver(MyReceiver);

	}

}
