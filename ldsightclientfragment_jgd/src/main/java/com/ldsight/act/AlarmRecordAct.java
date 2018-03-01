package com.ldsight.act;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
import com.ldsight.adapter.AlertAdapter;
import com.ldsight.dao.MakeSampleHttpRequest;
import com.ldsight.entity.AlertDevice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AlarmRecordAct extends Activity {
	private final String TAG_REQUEST = "MY_TAG";
	private ListView alarmRecordList;
	private AlertAdapter alertAdapter;
	private List<AlertDevice> alertDevices;
	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;
	private String streetName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_alarm_record);

		// 获取传递的数据
		String deviceId = getIntent().getStringExtra("DeviceId");
		streetName = getIntent().getStringExtra("streetName");
		initView();
		argumentInitialization();

		// makeAlarmRecordHttpRequest(deviceId);
		alertAdapter = new AlertAdapter(this, alertDevices, streetName);
		alarmRecordList.setAdapter(alertAdapter);
		// 测试
		MakeSampleHttpRequest aa = new MakeSampleHttpRequest(this);
		alertDevices = aa.getalarmRecor("10", alertAdapter, alertDevices);
		//设置listview点击事件
		alarmRecordList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent(AlarmRecordAct.this,ElectricBoxInformationDialog.class);
				Bundle mBundle = new Bundle();
				intent.putExtras(mBundle);
				mBundle.putSerializable("alertDevice",alertDevices.get(position));
				intent.putExtras(mBundle);
				startActivity(intent);
			}
		});

	}

	private void argumentInitialization() {
		alertDevices = new ArrayList<AlertDevice>();
		mVolleyQueue = Volley.newRequestQueue(this);
	}

	private void initView() {
		alarmRecordList = (ListView) this.findViewById(R.id.lv_alarm_record);
	}

	private void makeAlarmRecordHttpRequest(String deviceId) {
		String ip = getString(R.string.ip);
		String url = "http://" + ip + ":8080/alarm/AlarmJsons";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("Device", "10");

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (response.has("td")) {
					try {
						JSONArray jsonArr = response.getJSONArray("td");
						alertDevices.clear();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject temp = jsonArr.getJSONObject(i);
							AlertDevice ad = new AlertDevice();

							ad.setAlarmId(temp.getInt("alarm_id"));
							ad.setAlarmType(temp.getInt("alarm_type"));
							ad.setAlarmAmpere(temp.getInt("ampere"));
							ad.setAlarmDeviceAddress(temp
									.getInt("device_address"));
							ad.setAlarmDeviceId(temp
									.getString("device_id"));
							ad.setAlarmElectric(temp.getInt("electric"));
							ad.setAlarmFactor(temp.getInt("factor"));
							ad.setAlarmPower(temp.getInt("power"));
							ad.setAlarmVoltage(temp.getInt("voltage"));
							SimpleDateFormat df = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							ad.setDate(temp.getString("date"));
							// Date date =
							// df.parse(temp.getString("date"));
							alertDevices.add(ad);
						}
						alertAdapter.notifyDataSetChanged();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			}
		});
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		//	mVolleyQueue.start();
	}

}
