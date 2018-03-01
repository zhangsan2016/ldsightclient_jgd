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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ldsightclient_jgd.R;
import com.ldsight.adapter.BrightenMainListAdapter;
import com.ldsight.application.MyApplication;
import com.ldsight.crc.CopyOfcheckCRC;
import com.ldsight.entity.BrightenDevice;
import com.ldsight.entity.TimingStages;
import com.ldsight.util.StringUtil;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BrightenMain extends Activity {

	public static String DATA_REFRESH_FILTER = "BrightenMainDataRefreshFilter"; // 广播接收者
	private ListView brightenMainList;
	private BrightenMainListAdapter adapter;
	private Button btTiming;
	private long flagTime;
	private Button btSetting;
	private ProgressDialog mProgress;

	private CheckBox cb1;
	private CheckBox cb2;
	private CheckBox cb3;
	private CheckBox cb4;
	private CheckBox cb5;
	private CheckBox cb6;
	private CheckBox cb7;
	private CheckBox cb8;

	/**
	 * 亮化设备集合
	 */
	private List<BrightenDevice> brightenDevices;
	/**
	 * 安置房景观灯uuid
	 */
	private byte[] azfUuid = new byte[] { 3, 86, -128, 32, 48, 6, 18, 1 };
	/**
	 * 轮询标识（轮询状态开、关）
	 */
	private boolean pollFlag = false;
	/**
	 * 轮询当前位置（从1开始）
	 */
	private int pollCurrentLocation = 1;
	/**
	 * 轮询发送的data数据
	 */
	protected byte[] pollData;

	private Handler updataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// 获取data
			byte[] data = (byte[]) msg.obj;

			switch (msg.what) {

				case -1:
					if (mProgress != null) {
						mProgress.dismiss();
					}
					break;
				case 0:
					String text = (String) msg.obj;
					mProgress = ProgressDialog.show(BrightenMain.this, text,
							"Loading...", true, false);
					break;

				case 5:

					// 校验CRC
					boolean checkCrc = CopyOfcheckCRC.checkTheCrc(
							Arrays.copyOfRange(data, 5, 47),
							Arrays.copyOfRange(data, 47, 49));

					if (checkCrc) {

						// 首先判断是否是最后一个（0等于最后一个）
						if (data[14] == 0) {
							// 设置轮询状态
							pollFlag = false;
							return;
						}

						// 判断是否当前要获取的设备号
						if (data[14] != pollCurrentLocation) {
							return;
						}

						// 获取设备号
						int deviceId = data[14];

						// 获取序列号
						StringBuffer sequenceIdBuf = new StringBuffer();
						for (int i = 15; i < 27; i++) {
							sequenceIdBuf.append(StringUtil.byteToString(data[i])
									+ " ");
						}

						// 获取六段调光定时时间
						List<TimingStages> timingStagesList = new ArrayList<TimingStages>();

					/*
					 * TimingStages ts1 = new TimingStages(data[28], data[29],
					 * data[30]); TimingStages ts2 = new TimingStages(data[31],
					 * data[32], data[33]); TimingStages ts3 = new
					 * TimingStages(data[34], data[35], data[36]); TimingStages
					 * ts4 = new TimingStages(data[37], data[38], data[39]);
					 * TimingStages ts5 = new TimingStages(data[40], data[41],
					 * data[42]); TimingStages ts6 = new TimingStages(data[43],
					 * data[44], data[45]);
					 */

						TimingStages ts1 = new TimingStages(data[27], data[28],
								data[29]);
						TimingStages ts2 = new TimingStages(data[30], data[31],
								data[32]);
						TimingStages ts3 = new TimingStages(data[33], data[34],
								data[35]);
						TimingStages ts4 = new TimingStages(data[36], data[37],
								data[38]);
						TimingStages ts5 = new TimingStages(data[39], data[40],
								data[41]);
						TimingStages ts6 = new TimingStages(data[42], data[43],
								data[44]);
						timingStagesList.add(ts1);
						timingStagesList.add(ts2);
						timingStagesList.add(ts3);
						timingStagesList.add(ts4);
						timingStagesList.add(ts5);
						timingStagesList.add(ts6);

						// 获取亮度
						int luminance = data[45];

						// 获取状态
						int status = data[46];

						// 把参数保存到集合
						BrightenDevice brightenDevice = new BrightenDevice(
								deviceId, sequenceIdBuf.toString(),
								timingStagesList, luminance, status);
						brightenDevices.add(brightenDevice);

						// 发送指令获取下一个设备信息(设备号加1)
						// data = [5, 0, 78, 59, 98, -4, -110, -86, 92, 74, 0]
						pollData[10] = (byte) pollCurrentLocation++;
						pusherCommand(pollData);
						// 修改当前时间
						flagTime = new Date().getTime();
						// 更新listView
						adapter.notifyDataSetChanged();

					} else {
						try {
							throw new Exception("CRC 数据校验出错");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					break;
				case -63:

					if (data[15] == 0) {
						Toast.makeText(BrightenMain.this, "全设置成功！", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(BrightenMain.this, "全设置失败！", Toast.LENGTH_SHORT).show();
					}

					break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.brighten_main_activity);

		findViews();

		initData();

		setListener();

		initBroadCast();

		/*
		 * runOnUiThread(new Runnable() {
		 *
		 * @Override public void run() {
		 *
		 * Intent intent = new Intent(BrightenMain.this,
		 * SingleLightDialogItemAct.class); startActivity(intent);
		 *
		 * } });
		 */

	}

	/**
	 * 动态注册广播
	 */
	private void initBroadCast() {

		// 动态注册通知
		IntentFilter filter = new IntentFilter(BrightenMain.DATA_REFRESH_FILTER);
		registerReceiver(brightenMainReceiver, filter);

	}

	private void setListener() {

		// listView点击事件
		brightenMainList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				// 跳转到单灯设置界面
				Intent intent = new Intent(BrightenMain.this,
						SingleLightSettingAct.class);
				intent.putExtra("uuid", azfUuid);
				intent.putExtra("deviceId", brightenDevices.get(position)
						.getDeviceId());
				startActivity(intent);

			}
		});

		// 定时点击事件
		btTiming.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(BrightenMain.this,
						BrightenTiming.class);

				intent.putExtra("primary_timing", 1);
				// intent.putExtra("timing_time",
				// mainSixSectionDimmerIntensity);
				intent.putExtra("uuid", azfUuid);
				startActivityForResult(intent, 0);

			}
		});

		// 控制
		btSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				settingPush();

			}
		});

	}

	protected void settingPush() {

		new Thread() {
			public void run() {
				// 加载提示
				showProgress();
				// 光强度bit类型转换十进制代表八个设置状态
				StringBuffer sb = new StringBuffer();
				sb.append(cb8.isChecked() ? 1 : 0);
				sb.append(cb7.isChecked() ? 1 : 0);
				sb.append(cb6.isChecked() ? 1 : 0);
				sb.append(cb5.isChecked() ? 1 : 0);
				sb.append(cb4.isChecked() ? 1 : 0);
				sb.append(cb3.isChecked() ? 1 : 0);
				sb.append(cb2.isChecked() ? 1 : 0);
				sb.append(cb1.isChecked() ? 1 : 0);
				byte luminance = StringUtil.bitToByte(sb.toString());

				Pusher pusher = null;
				// 获取当前应用的uuid
				byte[] uuidApp = MyApplication.getInstance().getAppUuid();
				try {
					byte[] data = new byte[11];
					data[0] = -63; // 指令(0xC1)
					data[1] = 0; // 设备地址
					System.arraycopy(uuidApp, 0, data, 2, uuidApp.length); // 自身uuid
					data[10] = luminance; // 光强

					pusher = new Pusher(MyApplication.getIp(), 9966, 5000);

					// crc校验
					byte[] crc = CopyOfcheckCRC.crc(data);
					System.out.println("控制 data = " + Arrays.toString(crc));
					pusher.push0x20Message(azfUuid, crc);

					updataHandler.sendEmptyMessageDelayed(-1, 4000);

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

	private void initData() {
		brightenDevices = new ArrayList<BrightenDevice>();

		BrightenDevice brightenDevice = new BrightenDevice();
		brightenDevices.add(brightenDevice);

		adapter = new BrightenMainListAdapter(this, brightenDevices);
		brightenMainList.setAdapter(adapter);

		// 轮询获取数据
		getData();

	}

	private void getData() {

		new Thread() {
			public void run() {

				try {
					// 获取当前应用的uuid
					byte[] uuid = MyApplication.getAppUuid();

					pollData = new byte[11];
					pollData[0] = 5; // 指令
					pollData[1] = 0;

					System.arraycopy(uuid, 0, pollData, 2, uuid.length);

					pollFlag = true;
					do {
						// System.out.println("data = "+
						// Arrays.toString(pollData));
						Date currentTime = new Date();
						if ((currentTime.getTime() - flagTime) > 4000) {
							pusherCommand(pollData);
							flagTime = currentTime.getTime();
						}

					} while (pollFlag);

					System.out.println("轮询结束！");

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();

		/*
		 * Message message = updataHandler.obtainMessage(); message.what = 1;
		 * updataHandler.sendMessage(message);
		 */
	}

	/**
	 * 推送指令
	 *
	 * @param data
	 *            推送的数据
	 */
	protected void pusherCommand(byte[] data) {

		new Thread() {
			public void run() {
				Pusher pusher = null;
				try {
					System.out.println("pusherCommand data = "
							+ Arrays.toString(pollData));

					pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
					// crc校验
					byte[] crc = CopyOfcheckCRC.crc(pollData);
					pusher.push0x20Message(azfUuid, crc);

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

	private void findViews() {
		brightenMainList = (ListView) findViewById(R.id.brighten_main_list);

		btTiming = (Button) this.findViewById(R.id.bt_timing);

		btSetting = (Button) this.findViewById(R.id.bt_setting);

		cb1 = (CheckBox) findViewById(R.id.cb_1);
		cb2 = (CheckBox) findViewById(R.id.cb_2);
		cb3 = (CheckBox) findViewById(R.id.cb_3);
		cb4 = (CheckBox) findViewById(R.id.cb_4);
		cb5 = (CheckBox) findViewById(R.id.cb_5);
		cb6 = (CheckBox) findViewById(R.id.cb_6);
		cb7 = (CheckBox) findViewById(R.id.cb_7);
		cb8 = (CheckBox) findViewById(R.id.cb_8);

	}

	private BroadcastReceiver brightenMainReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 根据action过滤广播内容
			if (DATA_REFRESH_FILTER.equals(intent.getAction())) {

				byte[] data = intent.getByteArrayExtra("data");
				Message msg = updataHandler.obtainMessage();
				msg.what = data[13];
				msg.obj = data;
				updataHandler.sendMessage(msg);

			}

		}

	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		this.unregisterReceiver(brightenMainReceiver);

		if (pollFlag) {
			pollFlag = false;
		}

		super.onDestroy();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		if (pollFlag) {
			pollFlag = false;
		}

		super.onDestroy();

		super.onStop();
	}

	private void showProgress() {
		updataHandler.sendEmptyMessage(0);
	}
}
