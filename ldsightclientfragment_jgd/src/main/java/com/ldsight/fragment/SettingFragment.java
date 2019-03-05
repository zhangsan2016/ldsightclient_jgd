package com.ldsight.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ldsightclient_jgd.R;
import com.ldsight.act.AddDeviceAct;
import com.ldsight.act.AddUserAct;
import com.ldsight.act.AlertManageAct;
import com.ldsight.act.SingleLightControlAct;
import com.ldsight.adapter.TestPatternListAdapter;
import com.ldsight.application.MyApplication;
import com.ldsight.entity.CheckUser;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.util.DownloadFilesTask;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingFragment extends Fragment {
	public static final String SYSTEM_CONSTANT = "system_constant";
	private SharedPreferences preferences;
	public static final String ALERT_SOUND = "alert_sound";
	private JsonObjectRequest jsonObjRequest;
	private ArrayList<StreetAndDevice> streetAndDevices;
	TestPatternListAdapter adapter;
	private RequestQueue mVolleyQueue;
	private final String TAG = "SettingFragment";
	private ProgressDialog mProgress;

	/**
	 * 单灯控制
	 */
	private RelativeLayout singleLightControl;

	/**
	 * 判断开启报警功能是否成功
	 */
	private boolean startAlertBacktrack1 = false;
	private boolean startAlertBacktrack2 = false;
	private boolean startAlertBacktrack3 = false;
	/**
	 * 判断关闭报警功能是否成功
	 */
	private boolean stopAlertBacktrack1 = false;
	private boolean stopAlertBacktrack2 = false;
	private boolean stopAlertBacktrack3 = false;
	/**
	 * 下载
	 */
	private RelativeLayout downloadFile;
	/**
	 * 下载配置handler
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int info = msg.what;
			if (info == 1) {
				Toast.makeText(
						SettingFragment.this.getActivity()
								.getApplicationContext(), "下载更新异常", Toast.LENGTH_SHORT).show();
			} else if (info == 2) {
				Toast.makeText(
						SettingFragment.this.getActivity()
								.getApplicationContext(), "下载成功！", Toast.LENGTH_SHORT).show();
			}
		}
	};
	/**
	 * 下载进度条
	 */
	private ProgressDialog pd;
	/**
	 * 添加用户信息 、发送
	 */
	private RelativeLayout addUser;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		preferences = getActivity().getApplicationContext()
				.getSharedPreferences(SYSTEM_CONSTANT, 0);
		View rootView = inflater.inflate(R.layout.setting_fragment, container,
				false);
		streetAndDevices = new ArrayList<StreetAndDevice>();
		mVolleyQueue = Volley.newRequestQueue(this.getActivity()
				.getApplicationContext());
		showProgress();
	//	makeSampleHttpRequest();

		RelativeLayout addDeviceBtn = (RelativeLayout) rootView
				.findViewById(R.id.ll_add_device);
		addDeviceBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 添加需要最高权限
				CheckUser cku = CheckUser.getInstance();
				if (cku.getUserName() == null || cku.getUserpwd() == null
						|| cku.getJurisdiction() == 0) {
					Intent intent = new Intent(SettingFragment.this
							.getActivity().getApplicationContext(),
							AddDeviceAct.class);
					startActivity(intent);

				} else {
					if (cku.getJurisdiction() == 1) {

						Intent intent = new Intent(SettingFragment.this
								.getActivity().getApplicationContext(),
								AddDeviceAct.class);
						startActivity(intent);

					} else if (cku.getJurisdiction() == 3
							|| cku.getJurisdiction() == 2) {
						showToast("权限不足请联系管理员");
					}

				}

			}
		});

//		Button backButton = (Button) rootView
//				.findViewById(R.id.btn_setting_fragment_back);
//		backButton.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				SettingFragment.this.getActivity().finish();
//			}
//		});

		RelativeLayout alertManageButton = (RelativeLayout) rootView
				.findViewById(R.id.rl_setting_fragment_alert_manage);
		alertManageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SettingFragment.this.getActivity()
						.getApplicationContext(), AlertManageAct.class);
				startActivity(intent);
			}
		});
		// 开启报警功能
		ToggleButton alertSoundButton = (ToggleButton) rootView
				.findViewById(R.id.btn_setting_fragment_alert_sound);
		boolean alertSound = preferences.getBoolean(ALERT_SOUND, false);
		alertSoundButton.setChecked(alertSound);
		alertSoundButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
												 boolean isChecked) {
						// statAlert(isChecked);

						if (isChecked) {
							System.out.println("开");

							for (int i = 0; i < streetAndDevices.size(); i++) {

								System.out.println(Arrays
										.toString(streetAndDevices.get(i)
												.getByteUuid()));
								final byte[] uuid = streetAndDevices.get(i)
										.getByteUuid();
								new Thread() {
									public void run() {
										Pusher pusher = null;
										try {
											byte[] data = new byte[] { 5, 1 };
											System.out.println(Arrays
													.toString(data));
											pusher = new Pusher(
													MyApplication.getIp(), 9966, 5000);
											startAlertBacktrack1 = pusher
													.push0x20Message(uuid, data);

											for (int i = 0; i < 2; i++) {
												// Thread.sleep(1000);
												startAlertBacktrack2 = pusher
														.push0x20Message(uuid,
																data);
											}
											startAlertBacktrack3 = pusher
													.push0x20Message(uuid,
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
									};
								}.start();

							}
							// 测试
							// showToast("成功开启报警！"
							// +startAlertBacktrack1+startAlertBacktrack2
							// +startAlertBacktrack3);
							// if (startAlertBacktrack1 || startAlertBacktrack2
							// || startAlertBacktrack3) {
							showToast("成功开启报警！");
							SharedPreferences.Editor editor = preferences
									.edit();
							editor.putBoolean(ALERT_SOUND, true);
							editor.commit();
							// }

						} else {

							System.out.println("关");

							for (int i = 0; i < streetAndDevices.size(); i++) {

								System.out.println(Arrays
										.toString(streetAndDevices.get(i)
												.getByteUuid()));
								final byte[] uuid = streetAndDevices.get(i)
										.getByteUuid();
								new Thread() {
									public void run() {
										Pusher pusher = null;
										try {
											byte[] data = new byte[] { 5, 0 };
											System.out.println(Arrays
													.toString(data));
											pusher = new Pusher(
													MyApplication.getIp(), 9966, 5000);
											pusher.push0x20Message(uuid, data);

											for (int i = 0; i < 2; i++) {
												Thread.sleep(1000);
												pusher.push0x20Message(uuid,
														data);
											}
											pusher.push0x20Message(uuid,
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
									};
								}.start();

							}

							showToast("报警已关闭！");
							SharedPreferences.Editor editor = preferences
									.edit();
							editor.putBoolean(ALERT_SOUND, false);
							editor.commit();
						}
					}

				});

		// 添加用户信息
		addUser = (RelativeLayout) rootView.findViewById(R.id.rl_add_user);
		addUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent addUser = new Intent(getActivity(),AddUserAct.class);
				// startActivity(addUser);

				Intent addUser = new Intent(getActivity(), AddUserAct.class);
				startActivity(addUser);

			}
		});

//		downloadFile = (RelativeLayout) rootView
//				.findViewById(R.id.tv_download_configuration);
//		downloadFile.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// 创建进度对话框
//				// pd = new
//				// ProgressDialog(SettingFragment.this.getActivity().getApplicationContext());
//				pd = new ProgressDialog(SettingFragment.this.getActivity());
//				// 设置进度条样式
//				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//				// 设置进度框提示文字
//				pd.setMessage("正在下载，请稍后......");
//				pd.show();
//
//				// 判断手机内存卡是否可用
//				if (Environment.getExternalStorageState().equals(
//						Environment.MEDIA_MOUNTED)) {
//					String dd = SettingFragment.this.getActivity()
//							.getApplicationContext().getFilesDir().getPath();
//					/*
//					 * DownloadFilesThread download = new
//					 * DownloadFilesThread("http://192.168.1.132:8080/excel.xls"
//					 * ,this.getFilesDir().getPath() + "/sdcard/excel.xls");
//					 */
//					DownloadFilesThread download = new DownloadFilesThread(
//							getString(R.string.configuration_download),
//							"/sdcard/excel.xls");
//					new Thread(download).start(); // 开启线程
//
//				} else {
//					Toast.makeText(
//							SettingFragment.this.getActivity()
//									.getApplicationContext(), "sd卡不能用", 0)
//							.show();
//					pd.dismiss();
//				}
//
//			}
//		});



		// 单灯控制
		singleLightControl = (RelativeLayout) rootView.findViewById(R.id.rl_setting_dandengkongzhi);
		singleLightControl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent slca = new Intent(getActivity(), SingleLightControlAct.class);
				startActivity(slca);

			}
		});


		return rootView;
	}

	private void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

/*	private void makeSampleHttpRequest() {
		String ip = getString(R.string.ip);
		String url = "http://" + ip + ":8080/ldsight/deviceAction";

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response.has("streetAndDevices")) {
								try {
									JSONArray jsonArr = response
											.getJSONArray("streetAndDevices");
									streetAndDevices.clear();
									for (int i = 0; i < jsonArr.length(); i++) {
										JSONObject temp = jsonArr
												.getJSONObject(i);
										JSONObject deviceParam = temp
												.getJSONObject("deviceParam");
										JSONObject street = temp
												.getJSONObject("street");
										StreetAndDevice streetAndDevice = new StreetAndDevice();
										streetAndDevice.setCityId(street
												.getString("cityId"));
										streetAndDevice.setEndTime(street
												.getString("endTime"));
										streetAndDevice.setStartTime(street
												.getString("startTime"));
										streetAndDevice.setDeviceId(street
												.getString("deviceId"));
										streetAndDevice.setDeviceParamId(deviceParam
												.getInt("deviceParamId"));
										streetAndDevice
												.setMb_a_Ampere(deviceParam
														.getInt("mb_a_Ampere"));
										streetAndDevice
												.setMb_a_volt(deviceParam
														.getInt("mb_a_volt"));
										streetAndDevice.setMb_addr(deviceParam
												.getInt("mb_addr"));
										streetAndDevice
												.setMb_b_Ampere(deviceParam
														.getInt("mb_b_Ampere"));
										streetAndDevice
												.setMb_b_volt(deviceParam
														.getInt("mb_b_volt"));
										streetAndDevice
												.setMb_c_Ampere(deviceParam
														.getInt("mb_c_Ampere"));
										streetAndDevice
												.setMb_c_volt(deviceParam
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
										streetAndDevices.add(streetAndDevice);

									}
									adapter = new TestPatternListAdapter(
											SettingFragment.this.getActivity()
													.getApplicationContext(),
											streetAndDevices);
									adapter.notifyDataSetChanged();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							showToast("JSON parse error");
						}
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

				//showToast(error.getMessage());
			}
		});

		// Set a retry policy in case of SocketTimeout & ConnectionTimeout
		// Exceptions. Volley does retry for you if you have specified the
		// policy.
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();

		stopProgress();

	}*/

	private void showProgress() {
		mProgress = ProgressDialog.show(SettingFragment.this.getActivity(), "",
				"Loading...");
	}

	private void stopProgress() {
		mProgress.cancel();
	}

	/**
	 * 开启报警
	 *
	 * @param isChecked
	 */
	private void statAlert(boolean isChecked) {

		if (isChecked) {
			System.out.println("开");

			for (int i = 0; i < streetAndDevices.size(); i++) {

				System.out.println(Arrays.toString(streetAndDevices.get(i)
						.getByteUuid()));
				final byte[] uuid = streetAndDevices.get(i).getByteUuid();
				new Thread() {
					public void run() {
						Pusher pusher = null;
						try {
							byte[] data = new byte[] { 5, 1 };
							System.out.println(Arrays.toString(data));
							pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
							startAlertBacktrack1 = pusher.push0x20Message(uuid,
									data);

							for (int i = 0; i < 2; i++) {
								// Thread.sleep(1000);
								startAlertBacktrack2 = pusher.push0x20Message(
										uuid, data);
							}
							startAlertBacktrack3 = pusher.push0x20Message(uuid,
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
					};
				}.start();

				// 测试
				// showToast("成功开启报警！"
				// +startAlertBacktrack1+startAlertBacktrack2
				// +startAlertBacktrack3);
				if (startAlertBacktrack1 || startAlertBacktrack2
						|| startAlertBacktrack3) {
					showToast("成功开启报警！");
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean(ALERT_SOUND, true);
					editor.commit();
				}

			}

		} else {

			System.out.println("关");

			for (int i = 0; i < streetAndDevices.size(); i++) {

				System.out.println(Arrays.toString(streetAndDevices.get(i)
						.getByteUuid()));
				final byte[] uuid = streetAndDevices.get(i).getByteUuid();
				new Thread() {
					public void run() {
						Pusher pusher = null;
						try {
							byte[] data = new byte[] { 5, 0 };
							System.out.println(Arrays.toString(data));
							pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
							pusher.push0x20Message(uuid, data);

							for (int i = 0; i < 2; i++) {
								Thread.sleep(1000);
								pusher.push0x20Message(uuid, data);
							}
							pusher.push0x20Message(uuid, new byte[] { 0 });
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
					};
				}.start();

			}

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(ALERT_SOUND, false);
			editor.commit();
		}
	}

	/**
	 * 文件下载
	 */
	public class DownloadFilesThread implements Runnable {
		private static final String TAG = "MainActivity";
		public String path;
		public String filePath;

		public DownloadFilesThread(String path, String filePath) {
			this.path = path;
			this.filePath = filePath;
		}

		@Override
		public void run() {
			try {
				// Thread.sleep(3 * 1000);
				File file = DownloadFilesTask.DownloadFiles(path, filePath, pd);
				pd.dismiss();
				Message msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
			} catch (Exception e) {
				// Toast.makeText(MainActivity.this, "下载更新异常，进入主界面", 0).show();
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
				e.printStackTrace();
				pd.dismiss();
			}
		}
	}

}
