package com.ldsight.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
import com.ldsight.adapter.MainListAdapter;
import com.ldsight.application.MyApplication;
import com.ldsight.entity.CheckUser;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.service.OnlineService;

import org.ddpush.im.v1.client.appserver.Pusher;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainAct extends Activity {
	private ListView listView;
	private ProgressDialog mProgress;
	private ArrayList<StreetAndDevice> streetAndDevices;
	public final static String UDER = "UpdatEcableService";

	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;
	private final String TAG_REQUEST = "MY_TAG";
	MainListAdapter adapter;
	private CheckUser cku;
	private List<String> cableIsAbnormal = new ArrayList<String>();
	private boolean isStop = false;
	private boolean startAnimation = false;
	private static int sign = 1;
	private String errorId;

	private ImageView iv_map;

	/**
	 * 电缆异常，通过handler报警
	 */
	private Handler upAnimationHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case 1:
//				View v = (View) msg.obj;
//
//				final RelativeLayout rl_cable_state = (RelativeLayout) v
//						.findViewById(R.id.rl_cable_state);
//
//				final Animation cableAnim = AnimationUtils.loadAnimation(
//						MainAct.this, R.drawable.cable_is_abnormal);
//				rl_cable_state.startAnimation(cableAnim);
//
//				rl_cable_state.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Animation cableAnim = AnimationUtils
//								.loadAnimation(MainAct.this,
//										R.drawable.cable_is_abnormal_null);
//						rl_cable_state.startAnimation(cableAnim);
//					}
//				});
//				cableAnim.setAnimationListener(new AnimationListener() {
//
//					@Override
//					public void onAnimationStart(Animation animation) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onAnimationRepeat(Animation animation) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onAnimationEnd(Animation animation) {
//						// TODO Auto-generated method stub
//						rl_cable_state.startAnimation(cableAnim);
//					}
//				});
//
//				break;
//
//			}

		};
	};


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		listView = (ListView) findViewById(R.id.main_list);
		mVolleyQueue = Volley.newRequestQueue(this);
		streetAndDevices = new ArrayList<StreetAndDevice>();
		showProgress();
		makeSampleHttpRequest();
		// 刷新各个路灯参数信息(下位机会上传新数据到数据库)
		refresh();

		//adapter = new MainListAdapter(this, streetAndDevices, cableIsAbnormal);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				Intent intent = new Intent(MainAct.this, DeviceMainAct.class);
				Bundle bundle = new Bundle();
				bundle.putString("streetId", streetAndDevices.get(position)
						.getStreetId());
				intent.putExtras(bundle);
				startActivity(intent);



			}

		});
		// 这个广播接受电缆异常异常
		/*UDER upE = new UDER();
		IntentFilter filter = new IntentFilter(MainAct.UDER);
		registerReceiver(upE, filter);*/

		LinearLayout systemLogLayout = (LinearLayout) findViewById(R.id.system_log_layout);
		systemLogLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				CheckUser cku = CheckUser.getInstance();
				// 测试
				System.out.println("cku.getJurisdiction() = "
						+ cku.getJurisdiction());
				if (cku.getUserName() == null || cku.getUserpwd() == null
						|| cku.getJurisdiction() == 0) {
					Intent jurisdictionlogin = new Intent(MainAct.this,
							JurisdictionLoginAct.class);
					startActivity(jurisdictionlogin);
				} else {
					if (cku.getJurisdiction() == 1
							|| cku.getJurisdiction() == 2) {
						Intent intent = new Intent(MainAct.this,
								ParameterAct.class);
						intent.putExtra(ParameterAct.FRAGMENT_FLAG,
								ParameterAct.SYSTEM_LOG);
						startActivity(intent);
					} else if (cku.getJurisdiction() == 3) {
						showToast("权限不足请联系管理员");
					}
				}

				/*
				 * Intent intent = new Intent(MainAct.this, ParameterAct.class);
				 * intent.putExtra(ParameterAct.FRAGMENT_FLAG,
				 * ParameterAct.SYSTEM_LOG); startActivity(intent);
				 */

			}
		});

		LinearLayout testPatternLayout = (LinearLayout) findViewById(R.id.test_pattern_layout);
		testPatternLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				/*
				 * CheckUser cku = CheckUser.getInstance(); if
				 * (cku.getUserName() == null || cku.getUserpwd() == null ||
				 * cku.getJurisdiction() == 0) { Intent jurisdictionlogin = new
				 * Intent(MainAct.this, JurisdictionLoginAct.class);
				 * startActivity(jurisdictionlogin); } else { if
				 * (cku.getJurisdiction() == 1 || cku.getJurisdiction() == 2) {
				 * // TODO Auto-generated method stub Intent intent = new
				 * Intent(MainAct.this, ParameterAct.class);
				 * intent.putExtra(ParameterAct.FRAGMENT_FLAG,
				 * ParameterAct.TEST_PATTERN); startActivity(intent); } else if
				 * (cku.getJurisdiction() == 3) { showToast("权限不足请联系管理员"); }
				 *
				 * }
				 */

				Intent intent = new Intent(MainAct.this, ParameterAct.class);
				intent.putExtra(ParameterAct.FRAGMENT_FLAG,
						ParameterAct.TEST_PATTERN);
				startActivity(intent);

			}
		});
		LinearLayout settingLayout = (LinearLayout) findViewById(R.id.setting_layout);
		settingLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				/*
				 * CheckUser cku = CheckUser.getInstance(); if
				 * (cku.getUserName() == null || cku.getUserpwd() == null ||
				 * cku.getJurisdiction() == 0) { Intent jurisdictionlogin = new
				 * Intent(MainAct.this, JurisdictionLoginAct.class);
				 * startActivity(jurisdictionlogin); } else { if
				 * (cku.getJurisdiction() == 1 || cku.getJurisdiction() == 2) {
				 * Intent intent = new Intent(MainAct.this, ParameterAct.class);
				 * intent.putExtra(ParameterAct.FRAGMENT_FLAG,
				 * ParameterAct.SETTING); startActivity(intent); } else if
				 * (cku.getJurisdiction() == 3) { showToast("权限不足请联系管理员"); }
				 *
				 * }
				 */

				Intent intent = new Intent(MainAct.this, ParameterAct.class);
				intent.putExtra(ParameterAct.FRAGMENT_FLAG,
						ParameterAct.SETTING);
				startActivity(intent);

			}
		});

		Intent intent = new Intent(MainAct.this, OnlineService.class);
		startService(intent);
		/*
		 * // 填写用户信息 Intent userInfoIntent = new Intent(MainAct.this,
		 * UserInformationAct.class); startActivity(userInfoIntent);
		 */


		// 初始化视图
		initView();

	}

	private void initView() {
		// iv_map = (ImageView) this.findViewById(R.id.iv_map);
		LinearLayout iv_map = (LinearLayout) this.findViewById(R.id.system_map_layout);
		iv_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开百度地图界面
				Intent baiDuMapIt = new Intent(MainAct.this, BaiDuMapAct.class);
				startActivity(baiDuMapIt);
			}
		});
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		showProgress();
		makeSampleHttpRequest();
	}

	private void makeSampleHttpRequest() {

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

										if (temp.isNull("deviceParam")) {
											continue;
										}

										JSONObject deviceParam = temp
												.getJSONObject("deviceParam");
										JSONObject street = temp
												.getJSONObject("street");
										StreetAndDevice streetAndDevice = new StreetAndDevice();

										// 根据用户显示不同的数据
										CheckUser cku = CheckUser.getInstance();
										String streetId = street
												.getString("streetId");
										if (cku.getUserName().equals("zky")) {
											if (!streetId.equals("SZ1018")
													&& !streetId
													.equals("SZ1019")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"mys")) {
											if (!streetId.equals("SZ1023")
													&& !streetId
													.equals("SZ1024")
													&& !streetId
													.equals("SZ1025")) {
												continue;
											}
										}else if (cku.getUserName().equals(
												"csazf")) {
											if (!streetId.equals("SZ1061")
													&& !streetId
													.equals("SZ1062")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"ysdx")) {
											if (!streetId.equals("SZ1012")
													&& !streetId
													.equals("SZ1013")
													&& !streetId
													.equals("SZ1014")
													&& !streetId
													.equals("SZ1015")
													&& !streetId
													.equals("SZ1016")
													&& !streetId
													.equals("SZ1017")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"zky2")) {
											if (!streetId.equals("SZ1018")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"zst")) {
											if (!streetId.equals("SZ1019")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"admin")) {
											if (!streetId.equals("SZ1001")
													&& !streetId
													.equals("SZ1002")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"ldgd")) {
											if (!streetId.equals("SZ1010")
													&& !streetId
													.equals("SZ1003")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"ynyl")) {
											if (!streetId.equals("SZ1032")
													&& !streetId
													.equals("SZ1033")
													&& !streetId
													.equals("SZ1034")
													&& !streetId
													.equals("SZ1035")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"sxtc")) {
											if (!streetId.equals("SZ1036")
													&& !streetId
													.equals("SZ1037")
													&& !streetId
													.equals("SZ1038")) {
												continue;
											}
										} else if (cku.getUserName().equals(
												"zj312")) {
											if (!streetId.equals("SZ1043")
													&& !streetId
													.equals("SZ1044")
													&& !streetId
													.equals("SZ1045")
													&& !streetId
													.equals("SZ1046")
													&& !streetId
													.equals("SZ1047")
													&& !streetId
													.equals("SZ1048")
													&& !streetId
													.equals("SZ1049")
													&& !streetId
													.equals("SZ1050")
													&& !streetId
													.equals("SZ1051")
													&& !streetId
													.equals("SZ1052")
													&& !streetId.equals("SZ1053")&& !streetId.equals("SZ1054")
													&& !streetId.equals("SZ1056")
													&& !streetId.equals("SZ1057")
													&& !streetId.equals("SZ1058")) {
												continue;
											}
										}else{
											return;
										}



										streetAndDevice.setCityId(street
												.getString("cityId"));
										streetAndDevice.setEndTime(street
												.getString("endTime"));
										streetAndDevice.setStartTime(street
												.getString("startTime"));
										streetAndDevice.setEnergy100(street
												.getString("energy100"));
										streetAndDevice.setEnergy75(street
												.getString("energy75"));
										streetAndDevice.setEnergy50(street
												.getString("energy50"));
										streetAndDevice.setEnergy25(street
												.getString("energy25"));
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

									adapter.notifyDataSetChanged();
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
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		//mVolleyQueue.start();
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...");

	}

	private void stopProgress() {
		mProgress.cancel();
	}

	private void showToast(String msg) {
		Toast.makeText(MainAct.this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		/*
		 * cku = CheckUser.getInstance(); cku.setUserName(null);
		 * cku.setUserpwd(null); cku.setJurisdiction(0);
		 */

	}

	/**
	 * 接收异常广播更新Adapert
	 *
	 * @author Administrator
	 *
	 */
	/*private class UDER extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			if (intent.getAction() == MainAct.UDER) {

				String streetName = intent.getStringExtra("streetName");
				errorId = intent.getStringExtra("errorId");

				for (int i = 0; i < listView.getCount(); i++) {
					View v = listView.getChildAt(i);
					if (v != null) {
						// // 测试
						// System.out.println("View = " + v);

						TextView txt_street_name = (TextView) v
								.findViewById(R.id.txt_street_name);
						String streetNameCheck = txt_street_name.getText()
								.toString().trim();
						if (streetNameCheck.equals(streetName)) {
							TextView cableState = (TextView) v
									.findViewById(R.id.textView9);
							cableState.setText("电缆异常");
							cableState.setTextColor(Color.RED);

							// 判断是否为no，no代表此条目已满
							TextView txt_cable1State1 = (TextView) v
									.findViewById(R.id.txt_cable1);
							TextView txt_cable1State2 = (TextView) v
									.findViewById(R.id.txt_cable2);
							TextView txt_cable1State3 = (TextView) v
									.findViewById(R.id.txt_cable3);
							TextView txt_cable1State4 = (TextView) v
									.findViewById(R.id.txt_cable4);

							if ("OK".equals(txt_cable1State1.getText()
									.toString().trim())) {
								// 提示错误
								TextView textView11 = (TextView) v
										.findViewById(R.id.textView10);
								txt_cable1State1.setText("NO");
								textView11.setText("电缆" + errorId);
								txt_cable1State1.setTextColor(Color.RED);
								textView11.setTextColor(Color.RED);
							} else if ("OK".equals(txt_cable1State2.getText()
									.toString().trim())) {
								TextView textView11 = (TextView) v
										.findViewById(R.id.textView11);
								txt_cable1State2.setText("NO");
								textView11.setText("电缆" + errorId);
								txt_cable1State2.setTextColor(Color.RED);
								textView11.setTextColor(Color.RED);
							} else if ("OK".equals(txt_cable1State3.getText()
									.toString().trim())) {
								TextView textView12 = (TextView) v
										.findViewById(R.id.textView12);
								txt_cable1State3.setText("NO");
								textView12.setText("电缆" + errorId);
								txt_cable1State3.setTextColor(Color.RED);
								textView12.setTextColor(Color.RED);
							} else if ("OK".equals(txt_cable1State4.getText()
									.toString().trim())) {
								TextView textView13 = (TextView) v
										.findViewById(R.id.textView13);
								txt_cable1State4.setText("NO");
								textView13.setText("电缆" + errorId);
								txt_cable1State4.setTextColor(Color.RED);
								textView13.setTextColor(Color.RED);
							}

							Message msg = upAnimationHandler.obtainMessage();
							msg.what = 1;
							msg.obj = v;

							upAnimationHandler.sendMessage(msg);

						}
					}

				}

			}
		}

	}*/

	/**
	 * 刷新路灯参数
	 */
	private void refresh() {
		new Thread() {
			public void run() {
				Pusher pusher = null;
				for (StreetAndDevice streetAndDevice : streetAndDevices) {
					try {
						// byte[] data = new byte[] { 3 };
						byte[] data = new byte[] {14,55,55};

						System.out.println(Arrays.toString(data));
						pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
						pusher.push0x20Message(streetAndDevice.getByteUuid(),
								data);
						pusher.push0x20Message(streetAndDevice.getByteUuid(),
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
				}

				// showToast("发送成功");
			}
		}.start();
	}



}
