package com.ldsight.act;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ddpush.im.v1.client.appserver.Pusher;

import com.example.ldsightclient_jgd.R;
import com.ldsight.adapter.SingleLightControlAdapter;
import com.ldsight.application.MyApplication;
import com.ldsight.entity.SingleLightDevice;
import com.ldsight.entity.SingleLightDevice2;
import com.ldsight.service.OnlineService;

import android.app.Activity;
import android.content.ClipData.Item;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleLightAct extends Activity {
	public static String lightPoleTagParameterFilter = "lightPoleTagParameterFilter"; // 单灯参数监听
	private ListView singleLight;
	private SingleLightControlAdapter singleLightAdapter;
	protected boolean stoped = true;
	private List<SingleLightDevice> singleLightDeviceList;
	private List<SingleLightDevice2> singleLightDeviceList2;
	protected long lastReceived = 0;
	private int lightPoleTag = 0; // 当前灯杆
	private byte[] uuid = new byte[8];
	private Button btCheckSingleLight; // 查询按钮
	private EditText lightId; // 灯杆号
	private int checkLinghtId = -2; // 当前要查询的灯杆号
	// 单灯数据 (在线状态、 设备号、设备串号、设备电压、设备电流、设备功率、设备电能、功率因数)
	private TextView onlineStatus, Dvid, stringDvid, volt, ampere, power,
			electricity, powerFactor;
	/*
	 * 显示当前查询的单灯详细信息
	 */
	private Handler showDetailedHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 获取单灯参数
			byte[] data = (byte[]) msg.obj;
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

			// 最后把查询的值复位
			checkLinghtId = -2;
		};
	};
	/*
	 * 更新listview
	 */
	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 获取单灯参数
			byte[] data = (byte[]) msg.obj;
			switch (msg.what) {
				// 版本一返回的data
				case 1:

					// -1代表结束
					if (data[14] == -1) {
						// 结束线程发送
						stoped = false;
						Toast.makeText(SingleLightAct.this, "路灯加载结束", 1).show();
						return;

					} else {
						// 判断是否有重复返回的数据（....重复的数据、不是连贯的数据）
						if (singleLightDeviceList.size() > 0
								&& data[15] == Integer
								.parseInt(singleLightDeviceList.get(
										singleLightDeviceList.size() - 1)
										.getDeviceId())
								|| data[15] != singleLightDeviceList.size() + 1) {
							return;
						}

						SingleLightDevice sld = new SingleLightDevice();
						// 判断是否在线
						if (data[14] == 0) {
							sld.setLineState(data[14]);
							sld.setDeviceId((data[15] & 0xff) + "");
							StringBuffer imei = new StringBuffer();
							imei.append(Integer.toHexString((data[16] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[17] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[18] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[19] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[20] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[21] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[22] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[23] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[24] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[25] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[26] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[27] & 0xff))
									.toString());
							sld.setDeviceIMEI(imei.toString());

						} else {
							sld.setLineState(data[14]);
							sld.setDeviceId((data[15] & 0xff) + "");
							StringBuffer imei = new StringBuffer();
							imei.append(Integer.toHexString((data[16] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[17] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[18] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[19] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[20] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[21] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[22] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[23] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[24] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[25] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[26] & 0xff))
									.toString());
							imei.append(Integer.toHexString((data[27] & 0xff))
									.toString());
							sld.setDeviceIMEI(imei.toString());
							;
							sld.setVolt((data[28] << 8)
									+ (double) (data[29] & 0xFF));
							sld.setAmpere((data[30] << 8)
									+ (double) (data[31] & 0xFF));
							sld.setPower((data[32] << 8)
									+ (double) (data[33] & 0xFF));
							sld.setEnergy((data[34] << 8)
									+ (double) (data[35] & 0xFF));
							sld.setPfav((data[36] << 8)
									+ (double) (data[37] & 0xFF));

						}
						//sld.setUuid(uuid);
						singleLightDeviceList.add(sld);
						singleLightAdapter.notifyDataSetChanged();
						lightPoleTag++;
						getSingleLightparameter();
					}

					break;
				case 2:
					// -1代表结束
					if (data[14] == 0) {
						// 结束线程发送
						stoped = false;
						Toast.makeText(SingleLightAct.this, "路灯加载结束", 1).show();
						return;

					} else {
						// 条件1、小于当前需要获取的灯杆号不显示 2、不等于当前需要获取的灯杆不显示
						if (data[14] < (lightPoleTag + 1)
								|| data[14] != lightPoleTag + 1) {
							return;
						}
						SingleLightDevice2 sld2 = new SingleLightDevice2();
						sld2.setDeviceId(data[14] + "");
						sld2.setLineState(data[32]);

						singleLightDeviceList2.add(sld2);
						singleLightAdapter.notifyDataSetChanged();
						lightPoleTag++;
						getSingleLightparameter();

					}
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_light_control);
		// 获取传过来的uuid
		uuid = getIntent().getByteArrayExtra("uuid");

		initView();
		setOnClickListener();
		//
		singleLightAdapter = new SingleLightControlAdapter(SingleLightAct.this,
				singleLightDeviceList2);
		singleLightAdapter.setUUID(uuid);
		singleLight.setAdapter(singleLightAdapter);
		// 动态注册单灯监听
		IntentFilter lightPoleParameterFilter = new IntentFilter(
				SingleLightAct.lightPoleTagParameterFilter);
		registerReceiver(lightPoleTagParameterReceiver,
				lightPoleParameterFilter);
		//查询的接收者
		IntentFilter querySingleLampfileterr = new IntentFilter(
				SingleLightDialogItemAct.QuerySingleLampReceiver);
		querySingleLampfileterr.setPriority(500);
		registerReceiver(querySingleLampReceiver,
				querySingleLampfileterr);

		// getSingleLightparameter();
		// 获取所有单灯数据
		getSingleLight();

		//
		// SingleLightDevice sd = new SingleLightDevice();
		// sd.setDeviceId("SD55");
		// sd.setLineState(0);
		// SingleLightDevice sd2 = new SingleLightDevice();
		// sd2.setDeviceId("SD66");
		// sd2.setLineState(1);
		// singleLightDeviceList.add(sd);
		// singleLightDeviceList.add(sd2);
		//
		// SingleLightControlAdapter singleLightAdapter = new
		// SingleLightControlAdapter(
		// this, singleLightDeviceList);
		// singleLight.setAdapter(singleLightAdapter);

	}

	private void getSingleLight() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				while (stoped == true) {
					try {
						if (System.currentTimeMillis() - lastReceived < 20000) { // 判断时间间隔是否大于十分钟，小于十分钟不执行操作
							sleep(2000);
							continue;
						} else {
							lastReceived = System.currentTimeMillis();
							// 获取单灯参数
							getSingleLightparameter();
							// sleep(2000);
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(lightPoleTagParameterReceiver);
		unregisterReceiver(querySingleLampReceiver);
		// 结束线程发送
		stoped = false;
		this.finish();

	}

	private void getSingleLightparameter() {
		new Thread() {
			public void run() {

				Pusher pusher = null;
				try {
					// 获取当前应用的uuid
					MyApplication myApplication = MyApplication.getInstance();
					byte[] appUUID = myApplication.getAppUuid();

					/*
					 *  版本一指令
					 */
					// byte[] data = new byte[10];
					// data[0] = 9;
					// data[1] = (byte) lightPoleTag;
					// System.arraycopy(appUUID, 0, data, 2, appUUID.length);
					// pusher = new Pusher("121.40.194.91", 9966, 5000);
					// pusher.push0x20Message(uuid, data);
					// pusher.push0x20Message(uuid, new byte[] { 0 });


					byte[] data = new byte[11];
					data[0] = 05;  // 指令id
					data[1] = 00;
					System.arraycopy(appUUID, 0, data, 2, appUUID.length);
					data[10] = (byte) lightPoleTag;
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
	}

	private void setOnClickListener() {
		singleLight.setOnItemLongClickListener(new mItemLongClickListener());
		singleLight.setOnItemClickListener(new mOnItemClickListener());
		// 查询单灯信息
		btCheckSingleLight.setOnClickListener(new OnClickListener() {

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
							String lampNumber = lightId.getText().toString()
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
							checkLinghtId = Byte.parseByte(lampNumber);
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
		});

	}

	private void initView() {
		singleLightDeviceList = new ArrayList<SingleLightDevice>();
		singleLightDeviceList2 = new ArrayList<SingleLightDevice2>();
		singleLight = (ListView) this.findViewById(R.id.lv_single_light);
		// 单灯查询
		btCheckSingleLight = (Button) this
				.findViewById(R.id.bt_single_linght_check);
		// 灯杆号
		lightId = (EditText) this.findViewById(R.id.et_lightid);
		// 设备参数
		onlineStatus = (TextView) this.findViewById(R.id.tv_online_status);
		Dvid = (TextView) this.findViewById(R.id.tv_dvid);
		stringDvid = (TextView) this.findViewById(R.id.tv_string_dvid);
		volt = (TextView) this.findViewById(R.id.tv_volt);
		ampere = (TextView) this.findViewById(R.id.tv_ampere);
		power = (TextView) this.findViewById(R.id.tv_power);
		electricity = (TextView) this.findViewById(R.id.tv_electricity);
		powerFactor = (TextView) this.findViewById(R.id.tv_power_factor);
	}

	// 获取当前路灯广播接收器
	private BroadcastReceiver lightPoleTagParameterReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			byte[] data = intent.getByteArrayExtra("data");

			Message msg = myHandler.obtainMessage();
			msg.what = 2;
			msg.obj = data;
			myHandler.sendMessage(msg);

		}
	};

	// 查询广播接收器
	private BroadcastReceiver querySingleLampReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			byte[] data = intent.getByteArrayExtra("data");

			Message msg = showDetailedHandler.obtainMessage();
			msg.what = 1;
			msg.obj = data;
			showDetailedHandler.sendMessage(msg);


		}
	};


	private class mItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
									   final int position, long id) {
			new Thread() {
				public void run() {
					Pusher pusher = null;
					try {

						// 获取当前应用的uuid
						MyApplication myApplication = MyApplication
								.getInstance();
						byte[] appUuid = myApplication.getAppUuid();
						// 灯杆号
						String lampNumber = lightId.getText().toString()
								.trim();
						byte[] data = new byte[10];
						data[0] = 9;
						data[1] = (byte) (position + 1);

						System.arraycopy(appUuid, 0, data, 2,
								appUuid.length);

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
			Intent it = new Intent(SingleLightAct.this,
					SingleLightDialogItemAct.class);
			/*	Bundle mBundle = new Bundle();
				mBundle.putSerializable("sigleLightParameter",
						singleLightDeviceList.get(position));
				it.putExtras(mBundle);*/
			startActivity(it);

			return false;
		}

	}

	private class mOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {

			Intent slca = new Intent(SingleLightAct.this, SingleLightControlAct.class);
			slca.putExtra("lampNumber", (position +1) + "");
			slca.putExtra("uuid",uuid);
			startActivity(slca);

		}

	}




}
