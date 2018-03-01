package com.ldsight.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.ldsight.act.DeviceMainAct;
import com.ldsight.adapter.MainListAdapter;
import com.ldsight.dao.MakeSampleHttpRequest;
import com.ldsight.entity.CheckUser;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.service.OnlineService;
import com.ldsight.service.UpdateService;
import com.ldsight.util.CustomUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
	private Context mContext;
	public static String DataRefresh = "DataRefresh"; // 广播接收者-
	private final String TAG_REQUEST = "MainFragment";
	private ListView listView;
	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;
	private ArrayList<StreetAndDevice> streetAndDevices;
	private List<String> cableIsAbnormal = new ArrayList<String>();
	MainListAdapter adapter;
	private ProgressDialog mProgress;
	/*
	 * 版本信息
	 */
	private int newVersionCode;
	private String newVersionName;
	/*
	 * 提示框
	 */
	private Dialog dialog;

	private boolean updata = false;


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// 接受更新广播
		IntentFilter filter = new IntentFilter(MainFragment.DataRefresh);
		MainFragment.this.getActivity().getApplicationContext()
				.registerReceiver(dataRefreshReceiver, filter);

		View rootView = inflater.inflate(R.layout.main_fragment, container,
				false);
		listView = (ListView) rootView.findViewById(R.id.main_list);
		mVolleyQueue = Volley.newRequestQueue(this.getActivity()
				.getApplicationContext());
		streetAndDevices = new ArrayList<StreetAndDevice>();
		if (!updata) {
			showProgress();
			makeSampleHttpRequest();
		}

		adapter = new MainListAdapter(MainFragment.this.getActivity(),
				streetAndDevices, cableIsAbnormal);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				// Intent intent = new Intent(MainFragment.this.getActivity()
				// .getApplicationContext(), DeviceMainAct.class);
				// Bundle bundle = new Bundle();
				// bundle.putString("streetId", streetAndDevices.get(position)
				// .getStreetId());
				// intent.putExtras(bundle);
				// startActivity(intent);

				// Intent intentck = new
				// Intent(MainFragment.this.getActivity().getApplicationContext(),
				// CheckoutActivity.class);
				Intent intent = new Intent(MainFragment.this.getActivity()
						.getApplicationContext(), DeviceMainAct.class);
				Bundle bundle = new Bundle();
				bundle.putString("streetId", streetAndDevices.get(position)
						.getStreetId());
				intent.putExtras(bundle);
				// startActivity(intent);
				startActivityForResult(intent, 1);

			}

		});
		// 测试
		/*
		 * MakeSampleHttpRequest ms = new MakeSampleHttpRequest(
		 * MainFragment.this.getActivity()); ms.makeSampleHttpRequest();
		 */

		// 启动心跳包服务
		Intent intent = new Intent(MainFragment.this.getActivity()
				.getApplicationContext(), OnlineService.class);
		MainFragment.this.getActivity().startService(intent);

		/*
		 * // 根据登录的次数判断是否检测更新 SharedPreferences preferences =
		 * mContext.getApplicationContext().getSharedPreferences("client_state",
		 * 0); int clientCount = preferences.getInt("client_count", 0);
		 * System.out.println("client_count =" + clientCount); if(clientCount %
		 * 2 != 0){ // 检测版本更新 new checkNewestVersionAsyncTask().execute();
		 * clientCount++; SharedPreferences.Editor editor = preferences.edit();
		 * editor.putInt("client_count", clientCount); editor.commit(); }
		 */

		return rootView;

	}

	private void makeSampleHttpRequest() {

		String ip = getString(R.string.ip);
		String url = "http://" + "121.40.194.91" + ":8080/ldsight/deviceAction";

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
													.equals("SZ1062")
													&& !streetId
													.equals("SZ1063")) {
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




										StreetAndDevice streetAndDevice = new StreetAndDevice();

										// 判断报警是否为空
										if (!temp.isNull("alarms")) {
											JSONObject alarms = temp
													.getJSONObject("alarms");
											streetAndDevice.setAlarmType(alarms
													.getInt("alarm_type"));
											streetAndDevice.setAlarmDeviceId(alarms
													.getString("device_id"));
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

										/*
										 * // 根据用户显示不同的数据 CheckUser cku =
										 * CheckUser.getInstance(); if
										 * (cku.getUserName().equals("zky")) {
										 * if (!streetAndDevice.getStreetId()
										 * .equals("SZ1018") && !streetAndDevice
										 * .getStreetId() .equals("SZ1019")) {
										 * continue; } } else if
										 * (cku.getUserName().equals( "mys")) {
										 * if (!streetAndDevice.getStreetId()
										 * .equals("SZ1023") && !streetAndDevice
										 * .getStreetId() .equals("SZ1024") &&
										 * !streetAndDevice .getStreetId()
										 * .equals("SZ1025")) { continue; } }
										 * else if (cku.getUserName().equals(
										 * "ysdx")) { if
										 * (!streetAndDevice.getStreetId()
										 * .equals("SZ1012") && !streetAndDevice
										 * .getStreetId() .equals("SZ1013") &&
										 * !streetAndDevice .getStreetId()
										 * .equals("SZ1014") && !streetAndDevice
										 * .getStreetId() .equals("SZ1015") &&
										 * !streetAndDevice .getStreetId()
										 * .equals("SZ1016") && !streetAndDevice
										 * .getStreetId() .equals("SZ1017")) {
										 * continue; } } else if
										 * (cku.getUserName().equals( "zky2")) {
										 * if (!streetAndDevice.getStreetId()
										 * .equals("SZ1018")) { continue; } }
										 * else if (cku.getUserName().equals(
										 * "zst")) { if
										 * (!streetAndDevice.getStreetId()
										 * .equals("SZ1019")) { continue; } }
										 * else if (cku.getUserName().equals(
										 * "admin")) { if
										 * (!streetAndDevice.getStreetId()
										 * .equals("SZ1001") && !streetAndDevice
										 * .getStreetId() .equals("SZ1002")) {
										 * continue; } } else if
										 * (cku.getUserName().equals( "ldgd")) {
										 * if
										 * (!streetAndDevice.getStreetId().equals
										 * ( "SZ1010")&&
										 * !streetAndDevice.getStreetId
										 * ().equals( "SZ1003")){ continue; } }
										 */

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
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}

	private BroadcastReceiver dataRefreshReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			// showProgress();
			makeSampleHttpRequest();
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (dialog != null) {
			dialog.dismiss();
		}
		MainFragment.this.getActivity().getApplicationContext()
				.unregisterReceiver(dataRefreshReceiver);
		super.onDestroy();

	}

	private void showProgress() {
		mProgress = ProgressDialog.show(mContext, "", "Loading...");

	}

	private void stopProgress() {
		if (mProgress != null) {
			mProgress.cancel();
		}
	}

	private void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

	class checkNewestVersionAsyncTask extends AsyncTask<Void, Void, Boolean> {
		// 后台执行，比较耗时的操作都放在这个位置
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (postCheckNewestVersionCommand()) {
				int vercode = CustomUtils.getVersionCode(mContext); // 用到前面第一节写的方法
				if (newVersionCode > vercode) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (result) {// 如果有最新版本
				System.out.println("下载最新版本");

				doNewVersionUpdate(); // 更新新版本
			} else {
				// notNewVersionDlgShow(); // 提示当前为最新版本
				System.out.println("当前最新版本");
			}
			super.onPostExecute(result);
		}

	}

	/**
	 * 从服务器获取当前最新版本号，如果成功返回TURE，如果失败，返回FALSE
	 *
	 * @return
	 */
	private Boolean postCheckNewestVersionCommand() {
		MakeSampleHttpRequest mshr = new MakeSampleHttpRequest(
				MainFragment.this.getActivity());
		// JSONObject response = mshr.getVersion();
		JSONObject response = mshr.post_to_server();
		try {
			newVersionCode = response.getInt("verCode");
			newVersionName = response.getString("verName");
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 提示更新新版本
	 */
	private void doNewVersionUpdate() {
		int verCode = CustomUtils.getVersionCode(mContext);
		String verName = CustomUtils.getVersionName(mContext);

		/*
		 * String str=
		 * "当前版本："+verName+" Code:"+verCode+" ,发现新版本："+newVersionName+
		 * " Code:"+newVersionCode+" ,是否更新？";
		 */
		String str = "发现新版本,是否更新？";
		Dialog dialog = new AlertDialog.Builder(mContext)
				.setTitle("软件更新")
				.setMessage(str)
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								/*
								 * m_progressDlg.setTitle("正在下载");
								 * m_progressDlg.setMessage("请稍候...");
								 * downFile(Common.UPDATESOFTADDRESS); //开始下载
								 */

								// 开启更新服务UpdateService
								// 这里为了把update更好模块化，可以传一些updateService依赖的值
								// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
								Intent updateIntent = new Intent(mContext,
										UpdateService.class);
								updateIntent.putExtra("titleId",
										R.string.app_name);
								MainFragment.this.getActivity().startService(
										updateIntent);
								dialog.dismiss();
							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int whichButton) {
								// 点击"取消"按钮之后退出程序
								// dialog.finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;

	}

}
