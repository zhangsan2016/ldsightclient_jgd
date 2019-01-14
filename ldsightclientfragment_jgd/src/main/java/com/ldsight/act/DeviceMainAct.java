package com.ldsight.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ldsightclient_jgd.R;
import com.ldsight.application.MyApplication;
import com.ldsight.component.DeviceTable;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.fragment.MainFragment;

import org.ddpush.im.v1.client.appserver.Pusher;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;

public class DeviceMainAct extends Activity {
	public static String DeviceMainFilter = "devicemainfilter";
	public static String UpdateCableParameterFilter = "updateCableParameter"; // 广播接收者
	public static String DeviceTemperatureFilter = "DeviceTemperatureFilter"; // 湿度温度照明度接受
	public static String DeviceStateFilter = "DeviceStateFilter"; // 设备状态（系统日期，照明度...）
	TimePickerDialog startTimePickerDialog;
	TimePickerDialog endTimePickerDialog;
	// 主灯辅灯定时按钮
	private RelativeLayout rly_primary_timing, rly_subsidiary_timing;

	private DeviceTable deviceTable;
	private int startHour;
	private int startMinute;

	private int endHour;
	private int endMinute;
	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;

	String strStartTime = "";
	String strEndTime = "";

	int energy100Hour;
	int energy100Minute;

	int energy75Hour;
	int energy75Minute;

	int energy50Hour;
	int energy50Minute;

	int energy25Hour;
	int energy25Minute;

	boolean tag;
	private ProgressDialog mProgress;
	// 当前操作，1代表开关，2代表亮度调节
	private int currentCon = 1;
	// 全局的StreetAndDevice变量
	StreetAndDevice streetAndDevice = new StreetAndDevice();
	private final String TAG_REQUEST = "MY_TAG";

	TextView txtStartTime, txtEndTime;// 定时开始结束时间
	// LinearLayout llStarttime, llEndTime; // 定时开始结束时间
	TextView txtStreetName;
	TextView txtVolt, txtVoltB, txtVoltC;
	TextView txtAmpere,txt_ampereb,txt_amperec;
	TextView txtPsum;
	TextView txtLifeCycle;
	// ToggleButton toggleButton;
	// 当前路段的id
	private String streetId;
	// 参数湿度温度光强
	private TextView shiDu, wenDu, guangQiangDu;
	/**
	 * 进度条参数
	 */
	// 进度百分比
	private TextView tv_progress1, tv_progress2, tv_progress3, tv_progress4,
			tv_progress5, tv_progress6;
	// 持续的开始时间
	private TextView tv_spacing_start_time1, tv_spacing_start_time2,
			tv_spacing_start_time3, tv_spacing_start_time4,
			tv_spacing_start_time5, tv_spacing_start_time6;
	/*
	 * private LinearLayout
	 * ll_spacing_start_time1,ll_spacing_start_time2,ll_spacing_start_time3
	 * ,ll_spacing_start_time4,ll_spacing_start_time5,ll_spacing_start_time6; //
	 * 进度条 private SeekBar sb_brightness1, sb_brightness2, sb_brightness3,
	 * sb_brightness4, sb_brightness5, sb_brightness6;
	 */

	// 光照使能、报警灯开关
	private ToggleButton lightMake, alarmLampSwitch;
	// 校时
	private Button calibrationTime;
	// 查询当前状态
	private Button currentState;
	// 当前状态（时间日期和亮度）
	private TextView stateDate, stateBrightness;
	// 主灯、辅灯定时时间
	private byte[] mainSixSectionDimmerIntensity;
	private byte[] assistSixSectionDimmerIntensity;

	// 更新湿度温度光强度
	Handler parameHandler = new Handler() {
		public void handleMessage(Message msg) {
			byte[] data = (byte[]) msg.obj;
			switch (msg.what) {
				case 4:
					System.out.println("更新湿度温度光强度和电箱参数信息 = " + Arrays.toString(data));
					double number1 = (data[14] << 8) + (double) (data[15] & 0xFF);
					//double number2 = (data[16] << 8) + (double) (data[17] & 0xFF);
					double number3 = (data[18] << 8) + (double) (data[19] & 0xFF);

				/*double number3 = (data[18] << 8) + (double) (data[19] & 0xFF);
				double number3 = (data[18] << 8) + (double) (data[19] & 0xFF);*/


					guangQiangDu.setText(number1 + " lux");
					//	wenDu.setText(number2 / 100 + "  %");
					shiDu.setText(number3 + "  ℃");
					break;

				case 5:
					//System.out.println("datalength = " + data.length);
					if(data.length == 61){
						// 获取系统当前时间 (年月日时分秒)
						String systemTime = "20" + data[14] + "-" + data[15] + "-"
								+ data[17] + " " + data[18] + ":" + data[19] + ":"
								+ data[20];
						System.out.println("xx 年月日时分秒" + systemTime);
						// 主灯亮度
						int mainLuminance = data[21];
						System.out.println("xx 主亮度" + mainLuminance);
						// 主灯六段调光
						mainSixSectionDimmerIntensity = new byte[] { data[22],
								data[23], data[24], data[25], data[26], data[27],
								data[28], data[29], data[30], data[31], data[32],
								data[33], data[34], data[35], data[36], data[37],
								data[38], data[39]};
						System.out.println("xxx 主灯六段调光 = " + Arrays.toString(mainSixSectionDimmerIntensity));
						// 辅灯亮度
						int assistLuminance = data[40];
						System.out.println("xx 辅亮度" + assistLuminance);
						// 辅灯六段调光
						assistSixSectionDimmerIntensity = new byte[] { data[41],
								data[42], data[43], data[44], data[45], data[46],
								data[47], data[48], data[49], data[50], data[51],
								data[52], data[53], data[54], data[55], data[56],
								data[57], data[58]};
						System.out.println("xxx 辅灯六段调光 = " + Arrays.toString(assistSixSectionDimmerIntensity));


						// 初始化界面
						stateDate.setText(systemTime);
						stateBrightness.setText(mainLuminance + "% / " + assistLuminance +"%");

					}

					break;

			}
			;
		};
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				showProgress();
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
							stopProgress();
							makeStreetAndDeviceHttpRequest(streetId);

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} else if (msg.what == 2) {
				tag = false;
				stopProgress();
				System.out.println("关闭progress");
				if (currentCon == 1) {
					System.out.println("信息入库");
					showProgress();
					makeLightSwitchHttpRequest();
					stopProgress();
				} else if (currentCon == 2) {
					try {
						showProgress();
						makeSaveStreetHttpRequest();
						stopProgress();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (msg.what == 3) {
				showProgress();
			} else {
				showToast(msg.obj.toString());
			}
		};
	};
	private boolean update = false;


	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_main);
		mVolleyQueue = Volley.newRequestQueue(this);
		Bundle bundle = getIntent().getExtras();
		streetId = bundle.getString("streetId");

		// 第一次更新数据
		if (!update) {
			update = true;
			showProgress();
			// 获取当前定时时间
			getTimingTime();
		}
		// 初始化视图
		initView();
		// 设置点击界面
		initSetOnClick();

		// 获取街道电表信息
		makeStreetAndDeviceHttpRequest(streetId);

		// 动态注册通知
		IntentFilter filter = new IntentFilter(DeviceMainAct.DeviceMainFilter);
		registerReceiver(deviceMainReceiver, filter);
		IntentFilter cableParameterFilter = new IntentFilter(
				DeviceMainAct.UpdateCableParameterFilter);
		registerReceiver(cableParameterReceiver, cableParameterFilter);
		// 湿度温度接收器
		IntentFilter filter3 = new IntentFilter(
				DeviceMainAct.DeviceTemperatureFilter);
		registerReceiver(DeviceTemperatureReceiver, filter3);
		// 设备状态接收器
		IntentFilter filter4 = new IntentFilter(DeviceMainAct.DeviceStateFilter);
		registerReceiver(DeviceTemperatureReceiver, filter4);

		LinearLayout prevButton = (LinearLayout) findViewById(R.id.ll_prev_device_main);
		prevButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DeviceMainAct.this.finish();
			}
		});
		/*
		 * Button okButton = (Button) findViewById(R.id.btn_ok_device_main);
		 * okButton.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { showProgress(); // 开始时间 strStartTime =
		 * txtStartTime.getText().toString().trim(); strEndTime =
		 * txtEndTime.getText().toString().trim(); new Thread() { public void
		 * run() { Pusher pusher = null; byte[] data = new byte[20]; data[0] =
		 * -62; data[1] = 0;
		 *
		 * data[2] = (byte) Integer .parseInt(tv_spacing_start_time1.getText()
		 * .toString().split(":")[0].trim()); data[3] = (byte) Integer
		 * .parseInt(tv_spacing_start_time1.getText()
		 * .toString().split(":")[1].trim()); data[4] = (byte)
		 * sb_brightness1.getProgress();
		 *
		 * data[5] = (byte) Integer .parseInt(tv_spacing_start_time2.getText()
		 * .toString().split(":")[0].trim()); data[6] = (byte) Integer
		 * .parseInt(tv_spacing_start_time2.getText()
		 * .toString().split(":")[1].trim()); data[7] = (byte)
		 * sb_brightness2.getProgress();
		 *
		 * data[8] = (byte) Integer .parseInt(tv_spacing_start_time3.getText()
		 * .toString().split(":")[0].trim()); data[9] = (byte) Integer
		 * .parseInt(tv_spacing_start_time3.getText()
		 * .toString().split(":")[1].trim()); data[10] = (byte)
		 * sb_brightness3.getProgress();
		 *
		 * data[11] = (byte) Integer .parseInt(tv_spacing_start_time4.getText()
		 * .toString().split(":")[0].trim()); data[12] = (byte) Integer
		 * .parseInt(tv_spacing_start_time4.getText()
		 * .toString().split(":")[1].trim()); data[13] = (byte)
		 * sb_brightness4.getProgress();
		 *
		 * data[14] = (byte) Integer .parseInt(tv_spacing_start_time5.getText()
		 * .toString().split(":")[0].trim()); data[15] = (byte) Integer
		 * .parseInt(tv_spacing_start_time5.getText()
		 * .toString().split(":")[1].trim()); data[16] = (byte)
		 * sb_brightness5.getProgress();
		 *
		 * data[17] = (byte) Integer .parseInt(tv_spacing_start_time6.getText()
		 * .toString().split(":")[0].trim()); data[18] = (byte) Integer
		 * .parseInt(tv_spacing_start_time6.getText()
		 * .toString().split(":")[1].trim()); data[19] = (byte)
		 * sb_brightness6.getProgress();
		 *
		 * // 测试 System.out.println("data = " + Arrays.toString(data)); try {
		 * pusher = new Pusher("121.40.194.91", 9966, 5000);
		 * pusher.push0x20Message( streetAndDevice.getByteUuid(), data);
		 *
		 *
		 * Message message = handler.obtainMessage(); message.what = 3;
		 * handler.sendMessage(message);
		 *
		 *
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } finally { if (pusher != null) { try {
		 * pusher.close(); sleep(2000); stopProgress(); } catch (Exception e) {
		 * } } } }; }.start(); }
		 *
		 * private void sendTiming() { // 开始时间 strStartTime =
		 * txtStartTime.getText().toString().trim(); strEndTime =
		 * txtEndTime.getText().toString().trim(); new Thread() { public void
		 * run() { Pusher pusher = null; try { boolean result;
		 *
		 * byte[] dataCheck = new byte[19]; dataCheck[0] = 66; dataCheck[1] =
		 * (byte) Integer.parseInt(strStartTime .split(":")[0]); dataCheck[2] =
		 * (byte) Integer.parseInt(strStartTime .split(":")[1]); dataCheck[3] =
		 * (byte) 100; dataCheck[4] = (byte) checkMax24(Integer
		 * .parseInt(strStartTime.split(":")[0] + energy100Hour)); dataCheck[5]
		 * = (byte) (Integer .parseInt(strStartTime.split(":")[1] +
		 * energy100Minute)); dataCheck[6] = (byte) 75; dataCheck[7] = (byte)
		 * checkMax24(Integer .parseInt(strStartTime.split(":")[0] +
		 * energy100Hour + energy75Hour)); dataCheck[8] = (byte) (Integer
		 * .parseInt(strStartTime.split(":")[1] + energy100Minute +
		 * energy75Minute)); dataCheck[9] = (byte) 50; dataCheck[10] = (byte)
		 * checkMax24(Integer .parseInt(strStartTime.split(":")[0] +
		 * energy100Hour + energy75Hour + energy50Hour)); dataCheck[11] = (byte)
		 * (Integer .parseInt(strStartTime.split(":")[0] + energy100Minute +
		 * energy75Minute + energy50Minute)); dataCheck[12] = (byte) 25;
		 * dataCheck[13] = (byte) checkMax24(Integer
		 * .parseInt(strStartTime.split(":")[0] + energy100Hour + energy75Hour +
		 * energy50Hour)); dataCheck[14] = (byte) (Integer
		 * .parseInt(strStartTime.split(":")[0] + energy100Minute +
		 * energy75Minute + energy50Minute)); dataCheck[15] = (byte) 25;
		 * dataCheck[16] = (byte) (byte) checkMax24(Integer
		 * .parseInt(strStartTime.split(":")[0] + energy100Hour + energy75Hour +
		 * energy50Hour)); dataCheck[17] = (byte) (Integer
		 * .parseInt(strStartTime.split(":")[0] + energy100Minute +
		 * energy75Minute + energy50Minute)); dataCheck[18] = (byte) 25;
		 *
		 * byte[] data = new byte[19]; data[0] = 66; data[1] = (byte) 8; data[2]
		 * = (byte) 30; data[3] = (byte) 100; data[4] = (byte) 18; data[5] =
		 * (byte) 00; data[6] = (byte) 100; data[7] = (byte) 18; data[8] =
		 * (byte) 00; data[9] = (byte) 100; data[10] = (byte) 18; data[11] =
		 * (byte) 00; data[12] = (byte) 100; data[13] = (byte) 18; data[14] =
		 * (byte) 00; data[15] = (byte) 100; data[16] = (byte) 18; data[17] =
		 * (byte) 00; data[18] = (byte) 100;
		 *
		 * // 获取开始的小时 + 分钟 = 分钟数 int allStartTime = startHour * 60 +
		 * startMinute; int allEndTime = endHour * 60 + endMinute;
		 *
		 * int allChangeTime = (energy100Hour + energy25Hour + energy50Hour +
		 * energy75Hour) 60 + (energy100Minute + energy25Minute + energy50Minute
		 * + energy75Minute); // 把所有的转化成分钟做比较，如果开始的分钟大于结束的分钟就把昨天的时间 + 今天的时间 = //
		 * 开始结束时间间距分钟数，需要匹配分钟数才能往下执否则报错 // 第二种情况是开始时间大于结束时间 ， //
		 * 把结束时间减去开始时间就可以得到间距的时间，然后判断时间是否匹配 // 第三中状况是出现参数异常 if (allStartTime >
		 * allEndTime) { if (1440 - allStartTime + allEndTime != allChangeTime)
		 * { Message message = handler.obtainMessage(); message.obj = "总时间不匹配";
		 * handler.sendMessage(message); return; } } else if (allStartTime <
		 * allEndTime) { if (allEndTime - allStartTime != allChangeTime) {
		 * Message message = handler.obtainMessage(); message.obj = "总时间不匹配";
		 * handler.sendMessage(message); return; } } else { Message message =
		 * handler.obtainMessage(); message.obj = "总时间不匹配";
		 * handler.sendMessage(message); return; } pusher = new
		 * Pusher("121.40.194.91", 9966, 5000); tag = true; currentCon = 2;
		 * Message message = handler.obtainMessage(); message.what = 3;
		 * handler.sendMessage(message);
		 * streetAndDevice.setEnergy100(energy100Hour + ":" + energy100Minute);
		 * streetAndDevice.setEnergy75(energy75Hour + ":" + energy75Minute);
		 * streetAndDevice.setEnergy50(energy50Hour + ":" + energy50Minute);
		 * streetAndDevice.setEnergy25(energy25Hour + ":" + energy25Minute);
		 * while (tag == true) { result = pusher.push0x20Message(
		 * streetAndDevice.getByteUuid(), data); Thread.sleep(1000); } result =
		 * pusher.push0x20Message( streetAndDevice.getByteUuid(), new byte[] { 0
		 * }); } catch (Exception e) { e.printStackTrace(); } finally { if
		 * (pusher != null) { try { pusher.close(); } catch (Exception e) { } }
		 * } // showToast("发送成功"); } }.start(); } });
		 */

		LinearLayout refreshButton = (LinearLayout) findViewById(R.id.ll_device_main_refresh);
		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new Thread() {
					public void run() {
						Pusher pusher = null;
						try {
							// 获取当前应用的uuid
							MyApplication myApplication = MyApplication
									.getInstance();
							byte[] uuid = myApplication.getAppUuid();

							// 刷新指令
							byte[] data = new byte[] { 14, 55, 55 };

							// 更新湿度温度信息
							byte[] data2 = new byte[10];
							data2[0] = 30;
							data2[1] = 0;
							System.arraycopy(uuid, 0, data2, 2, uuid.length);
							pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
							pusher.push0x20Message(
									streetAndDevice.getByteUuid(), data2);

							// 间隔n秒发送刷新指令
							sleep(500);
							pusher.push0x20Message(
									streetAndDevice.getByteUuid(), data);

							// 获取当前定时时间
							sleep(500);
							getTimingTime();

							pusher.push0x20Message(
									streetAndDevice.getByteUuid(),
									new byte[] { 0 });
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
				Message message = handler.obtainMessage();
				message.what = 1;
				handler.sendMessage(message);
			}
		});
		// toggleButton = (ToggleButton)
		// findViewById(R.id.btn_device_main_up_or_down);


	}

	private void getTimingTime() {
		new Thread() {
			public void run() {
				Pusher pusher = null;

				try {
					while (true) {
						if (streetAndDevice.getByteUuid() != null) {
							byte[] data = new byte[10];
							data[0] = 48;
							data[1] = 0;
							// 获取当前应用的uuid
							byte[] uuid = MyApplication.getInstance()
									.getAppUuid();
							System.arraycopy(uuid, 0, data, 2, uuid.length);
							pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
							pusher.push0x20Message(
									streetAndDevice.getByteUuid(), data);
							// System.out.println("uuid = " +
							// Arrays.toString(uuid));
//							 System.out.println("data = " +
//							 Arrays.toString(data));
							break;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
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

	private void setSeekBar() {
		/*
		 * // 初始化 sb_brightness1.setProgress(50);
		 * sb_brightness2.setProgress(25); sb_brightness3.setProgress(50);
		 * sb_brightness4.setProgress(75); sb_brightness5.setProgress(100);
		 * sb_brightness6.setProgress(0);
		 *
		 * sb_brightness6.setEnabled(false);
		 */
		// 设置开始时间对应开始时间
		// tv_spacing_start_time1.setText(txtStartTime.getText().toString());
		// tv_spacing_start_time6.setText(txtEndTime.getText().toString());

	}

	private void initSetOnClick() {
		rly_primary_timing.setOnClickListener(new MyOnclickCancelListener());
		rly_subsidiary_timing.setOnClickListener(new MyOnclickCancelListener());
		/*
		 * // 进度条滑动事件监听
		 *  sb_brightness1 .setOnSeekBarChangeListener(new
		 * OnSeekBarChangeListener() {
		 *
		 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onProgressChanged(SeekBar seekBar, int
		 * progress, boolean fromUser) { tv_progress1.setText(progress + "%"); }
		 * });
		 *
		 * sb_brightness2 .setOnSeekBarChangeListener(new
		 * OnSeekBarChangeListener() {
		 *
		 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onProgressChanged(SeekBar seekBar, int
		 * progress, boolean fromUser) { tv_progress2.setText(progress + "%"); }
		 * });
		 *
		 * sb_brightness3 .setOnSeekBarChangeListener(new
		 * OnSeekBarChangeListener() {
		 *
		 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onProgressChanged(SeekBar seekBar, int
		 * progress, boolean fromUser) { tv_progress3.setText(progress + "%"); }
		 * }); sb_brightness4 .setOnSeekBarChangeListener(new
		 * OnSeekBarChangeListener() {
		 *
		 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onProgressChanged(SeekBar seekBar, int
		 * progress, boolean fromUser) { tv_progress4.setText(progress + "%"); }
		 * }); sb_brightness5 .setOnSeekBarChangeListener(new
		 * OnSeekBarChangeListener() {
		 *
		 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onProgressChanged(SeekBar seekBar, int
		 * progress, boolean fromUser) { tv_progress5.setText(progress + "%"); }
		 * }); sb_brightness6 .setOnSeekBarChangeListener(new
		 * OnSeekBarChangeListener() {
		 *
		 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
		 *
		 * }
		 *
		 * @Override public void onProgressChanged(SeekBar seekBar, int
		 * progress, boolean fromUser) { tv_progress6.setText(progress + "%"); }
		 * }); // 六个阶段的时间设置 // tv_spacing_start_time1.setOnClickListener(new
		 * timeListener()); // tv_spacing_start_time2.setOnClickListener(new
		 * timeListener()); // tv_spacing_start_time3.setOnClickListener(new
		 * timeListener()); // tv_spacing_start_time4.setOnClickListener(new
		 * timeListener()); // tv_spacing_start_time5.setOnClickListener(new
		 * timeListener()); // tv_spacing_start_time6.setOnClickListener(new
		 * timeListener());
		 *
		 * // 六个阶段的时间设置LinearLayout
		 * ll_spacing_start_time1.setOnClickListener(new timeListener());
		 * ll_spacing_start_time2.setOnClickListener(new timeListener());
		 * ll_spacing_start_time3.setOnClickListener(new timeListener());
		 * ll_spacing_start_time4.setOnClickListener(new timeListener());
		 * ll_spacing_start_time5.setOnClickListener(new timeListener());
		 * ll_spacing_start_time6.setOnClickListener(new timeListener());
		 */

		// 校时,光照使能
		calibrationTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 校时
				showProgress();
				new Thread() {
					public void run() {

						Pusher pusher = null;
						try {

							Time t = new Time(); // or Time t=new Time("GMT+8");
							t.setToNow(); // 取得系统时间。
							int year = t.year;
							int month = t.month;
							int date = t.monthDay;
							int hour = t.hour; // 0-23
							int minute = t.minute;
							int second = t.second;
							int week = t.weekDay;
							System.out.println(year + "年" + (month + 1) + "月"
									+ date + "日" + "星期" + (week + 1)+ hour + "时" + minute + "分"
									+ second + "秒" );
							// // 年转换成byte
							// byte[] yearBt = intToBytes(year);
							String yearString = year + "";
							yearString = yearString.substring(2, 4);

							byte[] data = new byte[] { -47, 0,
									Byte.parseByte(yearString),
									(byte) (month + 1), (byte) date,
									(byte) (week + 1), (byte) hour, (byte) minute,
									(byte)second };

							// byte[] data2 = new byte[] {0x81,55,55};
							// 测试
							System.out.println(Arrays.toString(data));
							pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
							/*
							 * pusher.push0x20Message(
							 * streetAndDevice.getByteUuid(), data);
							 */
							pusher.push0x20Message(
									streetAndDevice.getByteUuid(), data);
						} catch (Exception e) {
							e.printStackTrace();
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

						Message message = handler.obtainMessage();
						message.obj = "校时成功！";
						handler.sendMessage(message);
					}
				}.start();

			}
		});
		lightMake.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				// 光照使能
				if (isChecked) {

					new Thread() {
						public void run() {
							Pusher pusher = null;
							try {
								byte[] data = new byte[] { -57, 0, 1 };

								// byte[] data2 = new byte[] {0x81,55,55};
								System.out.println(Arrays.toString(data));
								pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
								/*
								 * pusher.push0x20Message(
								 * streetAndDevice.getByteUuid(), data);
								 */
								pusher.push0x20Message(
										streetAndDevice.getByteUuid(), data);

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
				} else {
					new Thread() {
						public void run() {
							Pusher pusher = null;
							try {
								byte[] data = new byte[] { -57, 0, 0 };

								// byte[] data2 = new byte[] {0x81,55,55};
								System.out.println(Arrays.toString(data));
								pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
								/*
								 * pusher.push0x20Message(
								 * streetAndDevice.getByteUuid(), data);
								 */
								pusher.push0x20Message(
										streetAndDevice.getByteUuid(), data);

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
				}
			}
		});

		LinearLayout ll = (LinearLayout) this
				.findViewById(R.id.ll_single_light_control);
		ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent singleLightIntent = new Intent(DeviceMainAct.this,
						SingleLightAct.class);
				Bundle mBundle = new Bundle();
				mBundle.putByteArray("uuid", streetAndDevice.getByteUuid());
				/*
				 * // 测试 System.out.println("streetAndDevice.getByteUuid() = " +
				 * Arrays.toString(streetAndDevice.getByteUuid()));
				 */
				singleLightIntent.putExtras(mBundle);
				startActivity(singleLightIntent);
			}
		});

		// 查询当前状态
		currentState.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress();
				new Thread() {
					public void run() {

						Pusher pusher = null;

						// 获取当前应用的uuid
						byte[] uuid = MyApplication.getInstance().getAppUuid();
						try {
							byte[] data = new byte[10];
							System.arraycopy(uuid, 0, data, 2, uuid.length);
							data[0] = 48;
							data[1] = 0;

							pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
							pusher.push0x20Message(
									streetAndDevice.getByteUuid(), data);
							// pusher.push0x20Message(new byte[] { 3, 86,
							// -128,32, 48, 6, 17, 18 }, data);
						} catch (Exception e) {
							e.printStackTrace();
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
		});
		// 报警灯开关
		alarmLampSwitch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
												 final boolean isChecked) {
						showProgress();
						new Thread() {
							public void run() {
								Pusher pusher = null;
								// 获取当前应用的uuid
								byte[] uuid = MyApplication.getInstance()
										.getAppUuid();
								try {
									byte[] data = new byte[11];
									data[0] = 41;
									data[1] = 0;
									System.arraycopy(uuid, 0, data, 2,
											uuid.length);
									if (isChecked) {
										data[10] = 1;
									} else {
										data[10] = 0;
									}
									pusher = new Pusher(MyApplication.getIp(), 9966,
											10000);
									pusher.push0x20Message(
											streetAndDevice.getByteUuid(), data);
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
				});

	}

	private void initView() {

		txtStartTime = (TextView) findViewById(R.id.txt_device_main_start_time);
		// llStarttime =(LinearLayout)
		// findViewById(R.id.ll_device_main_start_time);
		txtEndTime = (TextView) findViewById(R.id.txt_device_main_end_time);
		// llEndTime = (LinearLayout)
		findViewById(R.id.ll_device_main_end_time);

		txtStreetName = (TextView) findViewById(R.id.txt_street_name);
		txtVolt = (TextView) findViewById(R.id.txt_volt);
		txtAmpere = (TextView) findViewById(R.id.txt_ampere);
		txt_ampereb = (TextView) findViewById(R.id.txt_ampereb);
		txt_amperec = (TextView) findViewById(R.id.txt_amperec);

		txtPsum = (TextView) findViewById(R.id.txt_psum);
		txtLifeCycle = (TextView) findViewById(R.id.txt_life_cycle);
		// 电压B、C
		txtVoltB = (TextView) findViewById(R.id.txt_Bvolt);
		txtVoltC = (TextView) findViewById(R.id.txt_Cvolt);

		// deviceTable = (DeviceTable)
		// findViewById(R.id.layout_device_main_table);
		// // 湿度温度光照度
		shiDu = (TextView) this.findViewById(R.id.tv_shidu);
		//wenDu = (TextView) this.findViewById(R.id.tv_wendu);
		guangQiangDu = (TextView) this.findViewById(R.id.tv_guangqiangdu);

		// 查询当前状态(日期与亮度)
		stateDate = (TextView) this.findViewById(R.id.tv_date);
		stateBrightness = (TextView) this.findViewById(R.id.tv_brightness);
		// 进度条界面参数初始化
		tv_progress1 = (TextView) this.findViewById(R.id.tv_progress1);
		tv_progress2 = (TextView) this.findViewById(R.id.tv_progress2);
		tv_progress3 = (TextView) this.findViewById(R.id.tv_progress3);
		tv_progress4 = (TextView) this.findViewById(R.id.tv_progress4);
		tv_progress5 = (TextView) this.findViewById(R.id.tv_progress5);
		tv_progress6 = (TextView) this.findViewById(R.id.tv_progress6);

		// 六个阶段的时间
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
		/*
		 * // 六个阶段的时间LinearLayout ll_spacing_start_time1 = (LinearLayout)
		 * this.findViewById(R.id.ll_spacing_start_time1);
		 * ll_spacing_start_time2 = (LinearLayout)
		 * this.findViewById(R.id.ll_spacing_start_time2);
		 * ll_spacing_start_time3 = (LinearLayout)
		 * this.findViewById(R.id.ll_spacing_start_time3);
		 * ll_spacing_start_time4 = (LinearLayout)
		 * this.findViewById(R.id.ll_spacing_start_time4);
		 * ll_spacing_start_time5 = (LinearLayout)
		 * this.findViewById(R.id.ll_spacing_start_time5);
		 * ll_spacing_start_time6 = (LinearLayout)
		 * this.findViewById(R.id.ll_spacing_start_time6);
		 *
		 * sb_brightness1 = (SeekBar) this.findViewById(R.id.sb_brightness1);
		 * sb_brightness2 = (SeekBar) this.findViewById(R.id.sb_brightness2);
		 * sb_brightness3 = (SeekBar) this.findViewById(R.id.sb_brightness3);
		 * sb_brightness4 = (SeekBar) this.findViewById(R.id.sb_brightness4);
		 * sb_brightness5 = (SeekBar) this.findViewById(R.id.sb_brightness5);
		 * sb_brightness6 = (SeekBar) this.findViewById(R.id.sb_brightness6);
		 */

		// 光照使能和校时
		lightMake = (ToggleButton) this.findViewById(R.id.tb_Light_make);
		calibrationTime = (Button) this.findViewById(R.id.bt_calibration_time);
		// 当前状态
		currentState = (Button) this.findViewById(R.id.bt_current_state);
		// 报警灯
		alarmLampSwitch = (ToggleButton) this
				.findViewById(R.id.tv_alarm_lamp_switch);
		rly_primary_timing = (RelativeLayout) this
				.findViewById(R.id.rly_primary_timing);
		rly_subsidiary_timing = (RelativeLayout) this
				.findViewById(R.id.rly_subsidiary_timing);
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...", true, false);
	}

	private void stopProgress() {
		if (mProgress != null) {
			mProgress.dismiss();
		}

		// mProgress.cancel();
	}

	public void makeStreetAndDeviceHttpRequest(String streetId) {
		if (streetId.equals("") || streetId == null) {
			return;
		}
		String ip = getString(R.string.ip);
		String url = "http://" + ip + ":8080/ldsight/streetAndDeviceAction";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("streetId", streetId);

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.has("streetAndDevice")) {
						try {
							JSONObject jsonObject = response
									.getJSONObject("streetAndDevice");
							JSONObject deviceParam = jsonObject
									.getJSONObject("deviceParam");
							JSONObject street = jsonObject
									.getJSONObject("street");
							streetAndDevice.setCityId(street
									.getString("cityId"));
							streetAndDevice.setEndTime(street
									.getString("endTime"));
							streetAndDevice.setStartTime(street
									.getString("startTime"));
							streetAndDevice.setDeviceId(street
									.getString("deviceId"));
							streetAndDevice
									.setDeviceParamId(deviceParam
											.getInt("deviceParamId"));
							streetAndDevice.setMb_a_Ampere(deviceParam
									.getInt("mb_a_Ampere"));
							streetAndDevice.setMb_a_volt(deviceParam
									.getInt("mb_a_volt"));
							streetAndDevice.setMb_addr(deviceParam
									.getInt("mb_addr"));
							streetAndDevice.setMb_b_Ampere(deviceParam
									.getInt("mb_b_Ampere"));
							streetAndDevice.setMb_b_volt(deviceParam
									.getInt("mb_b_volt"));
							streetAndDevice.setMb_c_Ampere(deviceParam
									.getInt("mb_c_Ampere"));
							streetAndDevice.setMb_c_volt(deviceParam
									.getInt("mb_c_volt"));
							streetAndDevice.setMb_func(deviceParam
									.getInt("mb_func"));
							streetAndDevice.setMb_hz(deviceParam
									.getInt("mb_hz"));
							streetAndDevice.setMb_ned(deviceParam
									.getInt("mb_ned"));
							streetAndDevice.setMb_pa(deviceParam
									.getInt("mb_pa"));
							streetAndDevice.setMb_pb(deviceParam
									.getInt("mb_pb"));
							streetAndDevice.setMb_pc(deviceParam
									.getInt("mb_pc"));
							streetAndDevice.setMb_pfav(deviceParam
									.getInt("mb_pfav"));
							streetAndDevice.setMb_psum(deviceParam
									.getInt("mb_psum"));
							streetAndDevice.setMb_qa(deviceParam
									.getInt("mb_qa"));
							streetAndDevice.setMb_qb(deviceParam
									.getInt("mb_qb"));
							streetAndDevice.setMb_qc(deviceParam
									.getInt("mb_qc"));
							streetAndDevice.setMb_qsum(deviceParam
									.getInt("mb_qsum"));
							streetAndDevice.setMb_size(deviceParam
									.getInt("mb_size"));
							streetAndDevice.setMb_ssum(deviceParam
									.getInt("mb_ssum"));
							streetAndDevice.setMb_time(deviceParam
									.getString("mb_time"));
							streetAndDevice.setMb_yed(deviceParam
									.getInt("mb_yed"));
							streetAndDevice.setStreetId(street
									.getString("streetId"));
							streetAndDevice.setStreetName(street
									.getString("streetName"));
							streetAndDevice.setUuid(street
									.getString("uuid"));
							streetAndDevice.setEnergy100(street
									.getString("energy100"));
							streetAndDevice.setEnergy75(street
									.getString("energy75"));
							streetAndDevice.setEnergy50(street
									.getString("energy50"));
							streetAndDevice.setEnergy25(street
									.getString("energy25"));
							streetAndDevice.setLightSwitch(street
									.getInt("lightSwitch"));

							txtStreetName.setText(streetAndDevice
									.getStreetName());
							// txtStartTime.setText(streetAndDevice
							// .getStartTime());
							// txtEndTime.setText(streetAndDevice
							// .getEndTime());

									/*
									 * DecimalFormat voltDF = new DecimalFormat(
									 * "######0.0"); txtVolt.setText("" +
									 * voltDF.format((double) streetAndDevice
									 * .getMb_a_volt() / 100) + "V");
									 *
									 * // txtVolt.setText("" // + ((double)
									 * streetAndDevice // .getMb_a_volt()) /
									 * 100);
									 *
									 * txtAmpere.setText("" +
									 * (streetAndDevice.getMb_a_Ampere()) +
									 * "A"); txtPsum.setText("" +
									 * streetAndDevice.getMb_psum() + "KW");
									 */

							DecimalFormat df = new DecimalFormat(
									"######0.0 ");
							txtAmpere.setText(""
									+ df.format(((long) streetAndDevice
									.getMb_a_Ampere()) / 1000)
									+ "A");

							txt_ampereb.setText(""
									+ df.format(((long) streetAndDevice
									.getMb_b_Ampere()) / 1000)
									+ "A");
							txt_amperec.setText(""
									+ df.format(((long) streetAndDevice
									.getMb_c_Ampere()) / 1000)
									+ "A");

							DecimalFormat voltDF = new DecimalFormat(
									"######0.0");
							txtVolt.setText(""
									+ voltDF.format(((double) streetAndDevice
									.getMb_a_volt()) / 100)
									+ "V");
							txtVoltB.setText(""
									+ voltDF.format(((double) streetAndDevice
									.getMb_b_volt()) / 100)
									+ "V");
							txtVoltC.setText(""
									+ voltDF.format(((double) streetAndDevice
									.getMb_c_volt()) / 100)
									+ "V");

							txtPsum.setText(""
									+ voltDF.format(((double) streetAndDevice
									.getMb_psum()) / 100000)
									+ "KW");

							txtLifeCycle.setText(""
									+ streetAndDevice.getStartTime()
									+ ":"
									+ streetAndDevice.getEndTime());

							energy100Hour = Integer
									.parseInt(streetAndDevice
											.getEnergy100().split(":")[0]);
							energy100Minute = Integer
									.parseInt(streetAndDevice
											.getEnergy100().split(":")[1]);

							energy75Hour = Integer
									.parseInt(streetAndDevice
											.getEnergy75().split(":")[0]);
							energy75Minute = Integer
									.parseInt(streetAndDevice
											.getEnergy75().split(":")[1]);

							energy50Hour = Integer
									.parseInt(streetAndDevice
											.getEnergy50().split(":")[0]);
							energy50Minute = Integer
									.parseInt(streetAndDevice
											.getEnergy50().split(":")[1]);

							energy25Hour = Integer
									.parseInt(streetAndDevice
											.getEnergy25().split(":")[0]);
							energy25Minute = Integer
									.parseInt(streetAndDevice
											.getEnergy25().split(":")[1]);

							// deviceTable.init100PickerDialog(
							// energy100Hour, energy100Minute);
							// deviceTable.init75PickerDialog(
							// energy100Hour, energy75Minute);
							// deviceTable.init50PickerDialog(
							// energy100Hour, energy50Minute);
							// deviceTable.init25PickerDialog(
							// energy100Hour, energy25Minute);

							startHour = Integer
									.parseInt(streetAndDevice
											.getStartTime().split(":")[0]);
							startMinute = Integer
									.parseInt(streetAndDevice
											.getStartTime().split(":")[1]);

							endHour = Integer.parseInt(streetAndDevice
									.getEndTime().split(":")[0]);
							endMinute = Integer
									.parseInt(streetAndDevice
											.getEndTime().split(":")[1]);

							// txtStartTime.setText(streetAndDevice
							// .getStartTime());
							// txtEndTime.setText(streetAndDevice
							// .getEndTime());

							// if (streetAndDevice.getLightSwitch() ==
							// 1) {
							// toggleButton.setChecked(true);
							// } else {
							// toggleButton.setChecked(false);
							// }
							// toggleButton
							// .setOnCheckedChangeListener(new
							// OnCheckedChangeListener() {
							// public void onCheckedChanged(
							// CompoundButton buttonView,
							// boolean isChecked) {
							// Conf.CURRENT_CONF = Conf.SINGLE_SWITCH;
							// currentCon = 1;
							// if (isChecked) {
							// showProgress();
							// streetAndDevice
							// .setLightSwitch(1);
							// // showToast("开灯");
							// new Thread() {
							// public void run() {
							// Pusher pusher = null;
							// try {
							// byte[] data = new byte[] {
							// 1,
							// 1 };
							// System.out
							// .println(Arrays
							// .toString(data));
							// pusher = new Pusher(
							// "121.40.194.91",
							// 9966,
							// 5000);
							// tag = true;
							// while (tag == true) {
							// pusher.push0x20Message(
							// streetAndDevice
							// .getByteUuid(),
							// data);
							// Thread.sleep(1000);
							// }
							// pusher.push0x20Message(
							// streetAndDevice
							// .getByteUuid(),
							// new byte[] { 0 });
							// } catch (Exception e) {
							// e.printStackTrace();
							// } finally {
							// if (pusher != null) {
							// try {
							// pusher.close();
							// } catch (Exception e) {
							// }
							// }
							// }
							// // showToast("发送成功");
							// }
							// }.start();
							// } else {
							// showProgress();
							// streetAndDevice
							// .setLightSwitch(2);
							// // showToast("关灯");
							// new Thread() {
							// public void run() {
							// Pusher pusher = null;
							// try {
							// byte[] data = new byte[] {
							// 1,
							// 2 };
							// System.out
							// .println(Arrays
							// .toString(data));
							// pusher = new Pusher(
							// "121.40.194.91",
							// 9966,
							// 5000);
							// tag = true;
							// while (tag == true) {
							// pusher.push0x20Message(
							// streetAndDevice
							// .getByteUuid(),
							// data);
							// Thread.sleep(1000);
							// }
							// pusher.push0x20Message(
							// streetAndDevice
							// .getByteUuid(),
							// new byte[] { 0 });
							// } catch (Exception e) {
							// e.printStackTrace();
							// } finally {
							// if (pusher != null) {
							// try {
							// pusher.close();
							// } catch (Exception e) {
							// }
							// }
							// }
							// // showToast("发送成功");
							// }
							// }.start();
							// }
							// }
							// });

							startTimePickerDialog = new TimePickerDialog(
									DeviceMainAct.this,
									new OnTimeSetListener() {
										public void onTimeSet(
												TimePicker view,
												int hourOfDay,
												int minute) {
											String strStartHour = ""
													+ hourOfDay;
											String strStartMinute = ""
													+ minute;

											if (hourOfDay / 10 <= 0) {
												strStartHour = "0"
														+ strStartHour;
											}
											if (minute / 10 <= 0) {
												strStartMinute = "0"
														+ strStartMinute;
											}
											startHour = hourOfDay;
											startMinute = minute;
											strStartTime = strStartHour
													+ " : "
													+ strStartMinute;
											txtStartTime
													.setText(strStartTime);
											streetAndDevice
													.setStartTime(strStartTime);
											tv_spacing_start_time1
													.setText(strStartTime);
										}
									},
									Integer.parseInt(streetAndDevice
											.getStartTime().split(":")[0]),
									Integer.parseInt(streetAndDevice
											.getStartTime().split(":")[1]),
									true);

							endTimePickerDialog = new TimePickerDialog(
									DeviceMainAct.this,
									new OnTimeSetListener() {
										public void onTimeSet(
												TimePicker view,
												int hourOfDay,
												int minute) {
											String strEndHour = ""
													+ hourOfDay;
											String strEndMinute = ""
													+ minute;

											if (hourOfDay / 10 <= 0) {
												strEndHour = "0"
														+ strEndHour;
											}
											if (minute / 10 <= 0) {
												strEndMinute = "0"
														+ strEndMinute;
											}
											endHour = hourOfDay;
											endMinute = minute;
											strEndTime = strEndHour
													+ " : "
													+ strEndMinute;
											streetAndDevice
													.setEndTime(strEndTime);
											txtEndTime
													.setText(strEndTime);
											tv_spacing_start_time6
													.setText(strEndTime);

										}
									},
									Integer.parseInt(streetAndDevice
											.getEndTime().split(":")[0]),
									Integer.parseInt(streetAndDevice
											.getEndTime().split(":")[1]),
									true);

							// 设置节能状态
							setEnergySavingState();
							// 初始化进度条
							setSeekBar();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("JSON parse error");
				}
				stopProgress();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (error instanceof NetworkError) {
				} else if (error instanceof ClientError) {
				} else if (error instanceof ServerError) {
				} else if (error instanceof AuthFailureError) {
				} else if (error instanceof ParseError) {
				} else if (error instanceof NoConnectionError) {
				} else if (error instanceof TimeoutError) {
				}

				stopProgress();
				showToast(error.getMessage());
			}
		});

		// Set a retry policy in case of SocketTimeout & ConnectionTimeout
		// Exceptions. Volley does retry for you if you have specified the
		// policy.
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}

	private void makeSaveStreetHttpRequest()
			throws UnsupportedEncodingException {
		String ip = getString(R.string.ip);
		String url = "http://" + ip + ":8080/ldsight/updateStreetAction";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("cityId", streetAndDevice.getCityId());
		builder.appendQueryParameter("deviceId", streetAndDevice.getDeviceId());
		builder.appendQueryParameter("endTime", streetAndDevice.getEndTime());
		builder.appendQueryParameter("startTime",
				streetAndDevice.getStartTime());
		builder.appendQueryParameter("streetId", streetAndDevice.getStreetId());
		builder.appendQueryParameter("streetName",
				streetAndDevice.getStreetName());
		builder.appendQueryParameter("energy100",
				streetAndDevice.getEnergy100());
		builder.appendQueryParameter("energy25", streetAndDevice.getEnergy25());
		builder.appendQueryParameter("energy75", streetAndDevice.getEnergy75());
		builder.appendQueryParameter("energy50", streetAndDevice.getEnergy50());
		builder.appendQueryParameter("lightSwitch",
				"" + streetAndDevice.getLightSwitch());
		builder.appendQueryParameter("uuid", "" + streetAndDevice.getUuid());

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
				} catch (Exception e) {
					e.printStackTrace();
					showToast("JSON parse error");
				}
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
							stopProgress();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			}
		}, new Response.ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				if (error instanceof NetworkError) {
				} else if (error instanceof ClientError) {
				} else if (error instanceof ServerError) {
				} else if (error instanceof AuthFailureError) {
				} else if (error instanceof ParseError) {
				} else if (error instanceof NoConnectionError) {
				} else if (error instanceof TimeoutError) {
				}
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
							stopProgress();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
				showToast(error.getMessage());
			}
		});
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}

	private void makeLightSwitchHttpRequest() {
		String ip = getString(R.string.ip);
		String url = "http://" + ip + ":8080/ldsight/lightSwitchAction";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("streetId", streetAndDevice.getStreetId());
		builder.appendQueryParameter("light_switch",
				"" + streetAndDevice.getLightSwitch());

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
				} catch (Exception e) {
					e.printStackTrace();
					showToast("JSON parse error");
				}
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
							stopProgress();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			}
		}, new Response.ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				if (error instanceof NetworkError) {
				} else if (error instanceof ClientError) {
				} else if (error instanceof ServerError) {
				} else if (error instanceof AuthFailureError) {
				} else if (error instanceof ParseError) {
				} else if (error instanceof NoConnectionError) {
				} else if (error instanceof TimeoutError) {
				}
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
							stopProgress();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
				showToast(error.getMessage());
			}
		});
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}

	private void showToast(String msg) {
		Toast.makeText(DeviceMainAct.this, msg, Toast.LENGTH_SHORT).show();
	}

	private BroadcastReceiver deviceMainReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Message message = handler.obtainMessage();
			message.what = 2;
			handler.sendMessage(message);
		}
	};
	// 创建广播接收器
	private BroadcastReceiver cableParameterReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			// TODO
		}
	};

	/**
	 * 设置节能状态
	 */

	private void setEnergySavingState() {
		TextView energySavingState = (TextView) DeviceMainAct.this
				.findViewById(R.id.txt_energy);

		// 设置节能状态
		// 根据当前时间判断节能状态
		Calendar ca = Calendar.getInstance();
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		int minute = ca.get(Calendar.MINUTE);

		// ca.setTime(new java.util.Date());
		// int hour = ca.HOUR_OF_DAY;
		// int minute = ca.MINUTE;
		int energy100StartHour = Integer.parseInt(streetAndDevice
				.getStartTime().split(":")[0]);
		int energy100EndHour = Integer.parseInt(streetAndDevice

				.getStartTime().split(":")[0])
				+ Integer
				.parseInt(streetAndDevice.getEnergy100().split(":")[0]);
		int energy100StartMinute = Integer.parseInt(streetAndDevice
				.getStartTime().split(":")[1]);
		int energy100EndMinute = Integer.parseInt(streetAndDevice
				.getStartTime().split(":")[1])
				+ Integer
				.parseInt(streetAndDevice.getEnergy100().split(":")[1]);

		int energy75StartHour = energy100EndHour;
		int energy75EndHour = energy75StartHour
				+ Integer.parseInt(streetAndDevice.getEnergy75().split(":")[0]);
		int energy75StartMinute = energy100EndMinute;
		int energy75EndMinute = energy75StartMinute
				+ Integer.parseInt(streetAndDevice.getEnergy75().split(":")[1]);

		int energy50StartHour = energy75EndHour;
		int energy50EndHour = energy50StartHour
				+ Integer.parseInt(streetAndDevice.getEnergy50().split(":")[0]);
		int energy50StartMinute = energy75EndMinute;
		int energy50EndMinute = energy50StartMinute
				+ Integer.parseInt(streetAndDevice.getEnergy50().split(":")[1]);

		int energy25StartHour = energy50EndHour;
		int energy25EndHour = energy25StartHour
				+ Integer.parseInt(streetAndDevice.getEnergy25().split(":")[0]);
		int energy25StartMinute = energy25EndHour;
		int energy25EndMinute = energy25EndHour
				+ Integer.parseInt(streetAndDevice.getEnergy25().split(":")[1]);

		if (hour >= energy100StartHour && hour < energy100EndHour) {
			System.out.println("100%");
			energySavingState.setText("100%");
		} else if (hour >= energy75StartHour && hour < energy75EndHour) {
			System.out.println("75%");
			energySavingState.setText("75%");
		} else if (hour >= checkBig24(energy50StartHour)
				&& hour < checkBig24(energy50EndHour)) {
			System.out.println("50%");
			energySavingState.setText("50%");
		} else if (hour >= checkBig24(energy25StartHour)
				&& hour <= checkBig24(energy25EndHour)) {
			System.out.println("25%");
			energySavingState.setText("25%");
		} else {
			System.out.println("0%");
			energySavingState.setText("0%");
		}

	}

	/**
	 * 转换24小时的时间格式
	 *
	 * @param checkTime
	 * @return
	 */
	private int checkBig24(int checkTime) {
		if (checkTime >= 24) {
			// checkTime = (checkTime % 12) == 0 ? 12 : (checkTime % 12);
			checkTime = (checkTime % 12);
		}
		return checkTime;
	}

	/**
	 * 判断时间值，不能大于24
	 *
	 * @param time
	 */
	private int checkMax24(int time) {
		time = time % 24;
		return time;
	}

	/**
	 * 时间监听
	 *
	 * @author Administrator
	 *
	 */
	private class timeListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.ll_spacing_start_time1:
					new TimePickerDialog(DeviceMainAct.this,
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
					new TimePickerDialog(DeviceMainAct.this,
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
					new TimePickerDialog(DeviceMainAct.this,
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
					new TimePickerDialog(DeviceMainAct.this,
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
					new TimePickerDialog(DeviceMainAct.this,
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
					new TimePickerDialog(DeviceMainAct.this,
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

			}
		}
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */
	public static byte[] intToBytes(int value) {
		byte[] src = new byte[2];
		src[0] = (byte) ((value >> 8) & 0xFF);
		src[1] = (byte) (value & 0xFF);
		return src;
	}

	private BroadcastReceiver DeviceTemperatureReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {



			/*
			 * byte[] data = intent.getByteArrayExtra("data");
			 * System.out.println("Arrays =" + Arrays.toString(data)); Message
			 * msg = parameHandler.obtainMessage(); msg.what = 4; msg.obj =
			 * data; parameHandler.sendMessage(msg);
			 *
			 */
			if (intent.getAction().equals(DeviceTemperatureFilter)) {
				byte[] data = intent.getByteArrayExtra("data");
				Message msg = parameHandler.obtainMessage();
				msg.what = 4;
				msg.obj = data;
				parameHandler.sendMessage(msg);
			} else if (intent.getAction().equals(DeviceStateFilter)) {
				byte[] data = intent.getByteArrayExtra("data");
				Message msg = parameHandler.obtainMessage();
				msg.what = 5;
				msg.obj = data;
				parameHandler.sendMessage(msg);
			}

		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent();
		intent.setAction(MainFragment.DataRefresh);
		sendBroadcast(intent);
		unregisterReceiver(deviceMainReceiver);
		unregisterReceiver(cableParameterReceiver);
		unregisterReceiver(DeviceTemperatureReceiver);
	}

	private class MyOnclickCancelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent;
			int startTimeHour;
			int startTimeMinute;
			int enTimeHour;
			int enTimeMinute;
			switch (v.getId()) {
				case R.id.rly_primary_timing:
					intent = new Intent(DeviceMainAct.this, DeviceTiming.class);
					intent.putExtra("primary_timing", 1);
					intent.putExtra("timing_time", mainSixSectionDimmerIntensity);
					intent.putExtra("uuid", streetAndDevice.getByteUuid());
					DeviceMainAct.this.startActivityForResult(intent, 0);
					break;
				case R.id.rly_subsidiary_timing:
					intent = new Intent(DeviceMainAct.this, DeviceTiming.class);
					intent.putExtra("primary_timing", 2);
					intent.putExtra("timing_time", assistSixSectionDimmerIntensity);
					intent.putExtra("uuid", streetAndDevice.getByteUuid());
					DeviceMainAct.this.startActivityForResult(intent, 0);
					break;
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			// 判断主辅灯,更新当前主辅灯定时状态
			if(resultCode == 1){
				mainSixSectionDimmerIntensity = data.getByteArrayExtra("timeData");

			}else if(resultCode == 2){
				assistSixSectionDimmerIntensity = data.getByteArrayExtra("timeData");
			}



		}
	}



}