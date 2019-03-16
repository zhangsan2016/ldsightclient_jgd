package com.ldsight.act;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONObject;

public class AddElectricBox extends Activity {
	/*
	 * 电箱号、uuid、设备id、街道名
	 */
	private EditText  et_electric_uuid,
			et_street_name;
	/*
	 * 重置,添加,返回
	 */
	private Button btn_reset,bt_add_user;
	private LinearLayout ll_addUer_back;
	// Volley 框架（执行网络连接）
	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;
	private final String TAG_REQUEST = "MY_TAG";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_electric_box);

		mVolleyQueue = Volley.newRequestQueue(this);

		// 初始化View
		initView();
		// 设置点击事件
		setClick();
	}

	private void initView() {
		et_electric_uuid = (EditText) this.findViewById(R.id.et_electric_uuid);
		et_street_name = (EditText) this.findViewById(R.id.et_street_name);
		btn_reset = (Button) this.findViewById(R.id.btn_reset);
		bt_add_user = (Button) this.findViewById(R.id.bt_add_user);
		ll_addUer_back = (LinearLayout) this.findViewById(R.id.ll_addUer_back);

	}

	private void setClick() {
//		// 重置
//		btn_reset.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				et_electric_uuid.setText("");
//				et_street_name.setText("");
//			}
//		});
		// 添加
		bt_add_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 执行添加操作
				addElectric();

			}
		});
		// 返回
		ll_addUer_back.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddElectricBox.this.finish();
			}
		});

	}

	/**
	 * 添加一个电箱
	 */
	private void addElectric(){
		String ip = getString(R.string.ip);
		String url = "http://" + ip + ":8080/ldsight/SaveSDDAction";

		String uuid =  et_electric_uuid.getText().toString().trim();  //  uuid
		String streetName =  et_street_name.getText().toString().trim();   //  街道名

		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("uuid", uuid);
		builder.appendQueryParameter("streetName", streetName);

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					System.out.println("" + response.toString());
					if (response.has("state")) {

						String state = response.getString("state");


						if (state.equals("ok")) {
							showToast("添加成功！");
						}else{
							showToast("添加失败！");
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
				// Handle your error types accordingly.For Timeout & No
				// connection error, you can show 'retry' button.
				// For AuthFailure, you can re login with user
				// credentials.
				// For ClientError, 400 & 401, Errors happening on
				// client side when sending api request.
				// In this case you can check how client is forming the
				// api and debug accordingly.
				// For ServerError 5xx, you can do retry or handle
				// accordingly.
				if (error instanceof NetworkError) {
				} else if (error instanceof ClientError) {
				} else if (error instanceof ServerError) {
				} else if (error instanceof AuthFailureError) {
				} else if (error instanceof ParseError) {
				} else if (error instanceof NoConnectionError) {
				} else if (error instanceof TimeoutError) {
				}
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

	private void showToast(String msg) {
		Toast.makeText(AddElectricBox.this, msg, Toast.LENGTH_LONG).show();
	}

}
