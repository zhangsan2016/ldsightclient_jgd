package com.ldsight.act;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.ldsight.entity.StreetAndDevice;

import org.json.JSONObject;

public class DeviceParamAct extends Activity {
	private JsonObjectRequest jsonObjRequest;
	StreetAndDevice streetAndDevice = new StreetAndDevice();
	private ProgressDialog mProgress;
	private RequestQueue mVolleyQueue;
	private final String TAG_REQUEST = "MY_TAG";


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_param);
		mVolleyQueue = Volley.newRequestQueue(this);
		String streetId = getIntent().getStringExtra("streetid");
		System.out.println("streetId = " + streetId);
		// 获取街道电表信息
		showProgress();
		makeStreetAndDeviceHttpRequest(streetId);

		LinearLayout backButton = (LinearLayout) findViewById(R.id.ll_device_param_back);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DeviceParamAct.this.finish();
			}
		});

		Button okButton = (Button) findViewById(R.id.btn_device_param_ok);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DeviceParamAct.this.finish();
			}
		});

		TextView deleteDevice =  (TextView) this.findViewById(R.id.tv_delete);
		deleteDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(DeviceParamAct.this).setTitle("提示").setMessage("是否删除该设备？")
						.setPositiveButton("是", null)
						.setNegativeButton("否", null).show();
			}
		});
	}

	public void makeStreetAndDeviceHttpRequest(String streetId) {
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

							EditText idEditText = (EditText) findViewById(R.id.edit_device_param_street_id);
							idEditText.setText(streetAndDevice
									.getStreetId());

							// EditText passwordEditText = (EditText) findViewById(R.id.edit_device_param_password);

							EditText streetNameEditText = (EditText) findViewById(R.id.edit_device_param_street_name);
							streetNameEditText.setText(streetAndDevice
									.getStreetName());

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
				// showToast(error.getMessage());
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
		//	mVolleyQueue.start();
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...", true, false);
	}

	private void stopProgress() {
		mProgress.dismiss();
	}

	private void showToast(String msg) {
		Toast.makeText(DeviceParamAct.this, msg, Toast.LENGTH_LONG).show();
	}
}
