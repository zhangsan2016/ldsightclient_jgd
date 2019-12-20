package com.ldsight.act;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ldsightclient_jgd.R;
import com.ldsight.application.MyApplication;
import com.ldsight.entity.SingleLightDevice;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.util.Arrays;

public class SingleLightControlAct extends Activity {
	public static String DeviceParamFilter = "deviceParamFilter"; // 广播接收者
	private Button bt_check, bt_minus, bt_add;
	private TextView tv_number;
	private int number = 7;
	private TextView tv_dvid, tv_ampere, tv_volt, tv_power, tv_pf;
	private Button startLamp, stopLamp; // 单灯控制，开关灯
	// 测试
	// byte[] pushUUID = new byte[] { 3, 86, -128, 32, 48, 81, 5, 34 };
	// byte[] pushUUID = new byte[] { 3, 86, -128, 32, 48, 6, 17, 18 };
	/**
	 * 亮度调节 （进度条、百分比、调节亮度）
	 */
	private SeekBar brightness;
	private TextView percentage;
	/**
	 * 传递过来的灯杆号和uuid
	 */
	private String lampNumber;
	private byte[] uuid;
	// 单灯数据 (在线状态、 设备号、设备串号、设备电压、设备电流、设备功率、设备电能、功率因数)
	private TextView onlineStatus, Dvid, stringDvid, volt, ampere, power,
			electricity, powerFactor;
	// 报警灯开关
	private ToggleButton alarmLampSwitch;
	private ProgressDialog mProgress;

	private Handler myHandle = new Handler() {
		public void handleMessage(Message msg) {
			byte[] data = (byte[]) msg.obj;
			int number = Integer.parseInt(tv_number.getText().toString());
			System.out.println("xx number = " + number);
			System.out.println("xx number = " + (number != data[15]));
			if (number != data[15]) {
				return;
			}
			switch (msg.what) {
				case 1:
					// 获取单灯参数

				/*
				 * tv_dvid.setText("设备号: " + data[14]); double volt = ((data[15]
				 * << 8) + (data[16] & 0xff)) / (double) 100;
				 * tv_volt.setText("电压: " + volt + " V"); double ampere =
				 * ((data[17] << 8) + data[18]) / (double) 100;
				 * tv_ampere.setText("电流: " + ampere + " A"); double power =
				 * ((data[19] << 8) + data[20]) / (double) 100;
				 * tv_power.setText("功率: " + power + " W"); double pf =
				 * ((data[23] << 8) + data[24]) / (double) 100;
				 * tv_pf.setText("Pf值: " + pf);
				 */

					break;

				case 2:
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

					// 显示到页面
					if (sld.getLineState() == 1) {
						onlineStatus.setText("在线");
					} else {
						onlineStatus.setText("不在线");
					}
					Dvid.setText(sld.getDeviceId());
					stringDvid.setText(sld.getDeviceIMEI());
					volt.setText(sld.getVolt() / 100 + "");
					ampere.setText(sld.getAmpere() / 100 + "");
					power.setText(sld.getPower() / 100 + "");
					electricity.setText(sld.getEnergy() / 100 + "");
					powerFactor.setText(sld.getPfav() / 100 + "");
					break;
			}
		};
	};

	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			int progress = msg.what;
			percentage.setText(progress + " %");
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_light_control_activity);

		initView();

		initParameter();

		initOnClick();

		// 动态注册通知
		IntentFilter filter = new IntentFilter(
				SingleLightControlAct.DeviceParamFilter);
		registerReceiver(receiver, filter);

	}

	private void initParameter() {
		lampNumber = getIntent().getStringExtra("lampNumber");
		uuid = getIntent().getByteArrayExtra("uuid");
		if (lampNumber != null && uuid != null) {
			tv_number.setText(lampNumber);
			number = Integer.parseInt(lampNumber.trim());
		}

	}

	private void initView() {
		bt_check = (Button) this.findViewById(R.id.bt_check);
		bt_minus = (Button) this.findViewById(R.id.bt_minus);
		bt_add = (Button) this.findViewById(R.id.bt_add);
		tv_number = (TextView) this.findViewById(R.id.tv_number);

		/*
		 * // 设备信息 tv_dvid = (TextView) this.findViewById(R.id.tv_dvid);
		 * tv_ampere = (TextView) this.findViewById(R.id.tv_ampere); tv_volt =
		 * (TextView) this.findViewById(R.id.tv_volt); tv_power = (TextView)
		 * this.findViewById(R.id.tv_power); tv_pf = (TextView)
		 * this.findViewById(R.id.tv_pf);
		 */

		startLamp = (Button) this
				.findViewById(R.id.single_light_control_start_lamp);
		stopLamp = (Button) this
				.findViewById(R.id.single_light_control_stop_lamp);

		// 进度条和百分比
		brightness = (SeekBar) this
				.findViewById(R.id.sb_single_light_brightness);
		percentage = (TextView) this
				.findViewById(R.id.tv_single_light_percentage);
		// brightnessControl = (Button) this
		// .findViewById(R.id.bt_brightness_control);

		// 设备参数
		onlineStatus = (TextView) this.findViewById(R.id.tv_online_status);
		Dvid = (TextView) this.findViewById(R.id.tv_dvid);
		stringDvid = (TextView) this.findViewById(R.id.tv_string_dvid);
		volt = (TextView) this.findViewById(R.id.tv_volt);
		ampere = (TextView) this.findViewById(R.id.tv_ampere);
		power = (TextView) this.findViewById(R.id.tv_power);
		electricity = (TextView) this.findViewById(R.id.tv_electricity);
		powerFactor = (TextView) this.findViewById(R.id.tv_power_factor);

		// 报警灯开关
		alarmLampSwitch = (ToggleButton) this
				.findViewById(R.id.tb_alarm_lamp_switch);
	}

	private void initOnClick() {

		// uuidString.setLength(0);

		bt_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						Pusher pusher = null;
						try {

							// 获取当前应用的uuid
							MyApplication myApplication = MyApplication
									.getInstance();
							byte[] appUuid = myApplication.getAppUuid();
							// 灯杆号
							String lampNumber = tv_number.getText().toString()
									.trim();
							byte[] data = new byte[10];
							data[0] = 9;
							data[1] = Byte.parseByte(lampNumber);

							System.arraycopy(appUuid, 0, data, 2,
									appUuid.length);
							// 测试
							System.out.println(Arrays.toString(data));
							pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
							pusher.push0x20Message(uuid, data);
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
						// showToast("发送成功");
					}
				}.start();
				/*
				 * new Thread() { public void run() { Pusher pusher = null; try
				 * {
				 *
				 * // 获取当前应用的uuid MyApplication myApplication = MyApplication
				 * .getInstance(); byte[] uuid = myApplication.getAppUuid(); //
				 * 灯杆号 String lampNumber = tv_number.getText().toString()
				 * .trim();
				 *
				 * byte[] data = new byte[10]; data[0] = 82; data[1] =
				 * Byte.parseByte(lampNumber);
				 *
				 * System.arraycopy(uuid, 0, data, 2, uuid.length);
				 *
				 * // byte[] data2 = new byte[] {0x81,55,55};
				 * System.out.println(Arrays.toString(data)); pusher = new
				 * Pusher("121.40.194.91", 9966, 5000);
				 *
				 * pusher.push0x20Message(uuid, data); } catch (Exception e) {
				 * e.printStackTrace(); } finally { if (pusher != null) { try {
				 * pusher.close(); } catch (Exception e) { } } } //
				 * showToast("发送成功"); } }.start();
				 */

			}
		});

		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number < 100) {
					number++;
					tv_number.setText(number + "");
				}
			}
		});

		bt_minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number > 0) {
					number--;
					tv_number.setText(number + "");
				}

			}
		});

		// 单灯控制开关灯
		startLamp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						Pusher pusher = null;
						// 设备地址
						String lampNumber = tv_number.getText().toString();
						// 指令 设备地址 光强
						byte[] data = new byte[] { 65,
								Byte.parseByte(lampNumber), 100 };
						try {
							pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
							pusher.push0x20Message(uuid, data);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (pusher != null) {
								try {
									pusher.close();
								} catch (Exception e) {
								}
							}
						}
					};
				}.start();

			}
		});
		stopLamp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						Pusher pusher = null;
						// 设备地址
						String lampNumber = tv_number.getText().toString();
						// 指令 设备地址 光强
						byte[] data = new byte[] { 65,
								Byte.parseByte(lampNumber), 0 };
						try {
							pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
							pusher.push0x20Message(uuid, data);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (pusher != null) {
								try {
									pusher.close();
								} catch (Exception e) {
								}
							}
						}
					};
				}.start();
			}
		});

		// 亮度调节进度条
		brightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				final int progress = seekBar.getProgress();

				new Thread() {
					public void run() {
						int myprogress = 0;
						if (progress <= 5) {
							myprogress = 0;

						} else if (progress <= 30) {
							myprogress = 25;
						} else if (progress <= 70) {
							myprogress = 50;
						} else if (progress <= 95) {
							myprogress = 75;
						} else if (progress > 95) {
							myprogress = 100;
						}
						progressHandler.sendEmptyMessage(myprogress);
						Pusher pusher = null;
						// 设备地址
						String lampNumber = tv_number.getText().toString();

						// 指令 设备地址 光强
						byte[] data = new byte[] { 65,
								Byte.parseByte(lampNumber), (byte) myprogress };
						try {
							pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
							pusher.push0x20Message(uuid, data);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (pusher != null) {
								try {
									pusher.close();
								} catch (Exception e) {
								}
							}
						}
					};
				}.start();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
			}
		});
		// 亮度调节button
		// brightnessControl.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// new Thread() {
		// public void run() {
		// Pusher pusher = null;
		// // 设备地址
		// String lampNumber = tv_number.getText().toString();
		//
		// // 指令 设备地址 光强
		// byte[] data = new byte[] { 65,
		// Byte.parseByte(lampNumber),(byte) brightness.getProgress()};
		// try {
		// pusher = new Pusher("121.40.194.91", 9966, 5000);
		// pusher.push0x20Message(pushUUID, data);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// if (pusher != null) {
		// try {
		// pusher.close();
		// } catch (Exception e) {
		// }
		// }
		// }
		// };
		// }.start();
		// }
		// });

		LinearLayout back = (LinearLayout) this.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SingleLightControlAct.this.finish();
			}
		});

		// 报警灯开关
		alarmLampSwitch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
												 boolean isChecked) {
						int lightNumber = Integer.parseInt(tv_number.getText()
								.toString());
						startAlarm(isChecked, lightNumber);

					}
				});
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			byte[] data = intent.getByteArrayExtra("data");
			Message msg = myHandle.obtainMessage();
			msg.what = 2;
			msg.obj = data;
			myHandle.sendMessage(msg);

		}
	};

	private void showToast(String msg) {
		Toast.makeText(SingleLightControlAct.this, msg, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void startAlarm(final boolean isChecked, final int lightNumber) {
		showProgress();
		new Thread() {
			public void run() {
				Pusher pusher = null;
				// 获取当前应用的uuid
				byte[] AppUuid = MyApplication.getInstance().getAppUuid();
				try {
					byte[] data = new byte[11];
					data[0] = 41;
					data[1] = (byte) lightNumber;
					System.out.println("xx uuid = " + Arrays.toString(AppUuid));
					System.arraycopy(AppUuid, 0, data, 2, AppUuid.length);
					if (isChecked) {
						data[10] = 1;
					} else {
						data[10] = 0;
					}
					System.out.println("xx data = " + Arrays.toString(data));
					pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
					pusher.push0x20Message(uuid, data);
					// pusher.push0x20Message(new
					// byte[]{3,86,-128,32,48,81,5,34}, data);
				} catch (Exception e) {
					e.printStackTrace();
					showToast(e.getMessage());
					stopProgress();
				} finally {
					if (pusher != null) {
						try {
							pusher.close();
							sleep(2000);
							stopProgress();
						} catch (Exception e) {
						}
					}
				}
				// showToast("发送成功");
			}
		}.start();
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...", true, false);
	}

	private void stopProgress() {
		mProgress.dismiss();
	}

}
