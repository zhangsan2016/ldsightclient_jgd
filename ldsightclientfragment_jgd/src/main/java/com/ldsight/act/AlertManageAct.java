package com.ldsight.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.ldsight.adapter.AlertManageListAdapter;
import com.ldsight.entity.CheckUser;
import com.ldsight.entity.StreetAndDevice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlertManageAct extends Activity {
	private ProgressDialog mProgress;
	private ArrayList<StreetAndDevice> streetAndDevices;
	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;
	AlertManageListAdapter adapter;
	private ListView listview;
	private final String TAG_REQUEST = "MY_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_manage_main);
		streetAndDevices = new ArrayList<StreetAndDevice>();
		mVolleyQueue = Volley.newRequestQueue(this);
		listview = (ListView) findViewById(R.id.alert_manage_listview);
		showProgress();
		makeSampleHttpRequest();
		clickListener();




	}

	private void clickListener() {
		LinearLayout back = (LinearLayout) this.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertManageAct.this.finish();
			}
		});
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
									adapter = new AlertManageListAdapter(
											AlertManageAct.this,
											streetAndDevices);
									adapter.notifyDataSetChanged();
									listview.setAdapter(adapter);
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
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(AlertManageAct.this, "", "Loading...");
	}

	private void stopProgress() {
		mProgress.cancel();
	}

	private void showToast(String msg) {
		Toast.makeText(AlertManageAct.this, msg, Toast.LENGTH_LONG).show();
	}
}
