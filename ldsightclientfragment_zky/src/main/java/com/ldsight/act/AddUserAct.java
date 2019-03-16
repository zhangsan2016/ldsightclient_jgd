package com.ldsight.act;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUserAct extends Activity implements OnClickListener {
	/*
	 * 返回按钮
	 */
	private LinearLayout addUerBack;
	/*
	 * 重置按钮
	 */
	private Button reset;
	/*
	 * 用户名（用手机号标识）
	 */
	private EditText number_id;
	/*
	 * 添加用户按钮
	 */
	private Button addUser;
	/*
	 * 权限下拉框
	 */
	private Spinner spinner;
	/**
	 * 下拉框适配器
	 */
	private ArrayAdapter arrayAdapter;
	/*
	 * 下拉框选中的值
	 */
	private String mobiles;
	private String spinnerText;
	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;
	private final String TAG_REQUEST = "MY_TAG";
	/**
	 * 用户权限
	 */
	RadioButton average_user, administrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_user);
		// 初始化视图
		initView();
		// 设置点击事件
		setOnClick();

	}

	private void setOnClick() {
		addUerBack.setOnClickListener(this);
		// reset.setOnClickListener(this);
		addUser.setOnClickListener(this);
		// 初始化下拉框
		// initSpinner();
		// 权限选择
		RelativeLayout rl_administrator = (RelativeLayout) this
				.findViewById(R.id.rl_administrator);
		rl_administrator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				administrator.setChecked(true);
				average_user.setChecked(false);
			}
		});
		RelativeLayout rl_average_user = (RelativeLayout) this
				.findViewById(R.id.rl_average_user);
		rl_average_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				average_user.setChecked(true);
				administrator.setChecked(false);
			}
		});
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		addUerBack = (LinearLayout) this.findViewById(R.id.ll_addUer_back);
		// reset = (Button) this.findViewById(R.id.btn_reset);
		number_id = (EditText) this.findViewById(R.id.et_number_id);
		addUser = (Button) this.findViewById(R.id.bt_add_user);
		// spinner = (Spinner) this.findViewById(R.id.sp_jurisdiction);
		average_user = (RadioButton) this
				.findViewById(R.id.rb_add_user_average_user);
		administrator = (RadioButton) this
				.findViewById(R.id.rb_add_user_administrator);

	}

	/**
	 * button点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_addUer_back: // 返回
				AddUserAct.this.finish();
				break;
			case R.id.bt_add_user: // 添加用户

				// 使用正则表达式判断号码
				mobiles = number_id.getText().toString().trim();
				Pattern p = Pattern
						.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
				Matcher m = p.matcher(mobiles);
				if (!m.matches()) {
					showToast("请输入正确的手机号码");
					number_id.requestFocus();
					return;
				}
				// 获取下拉框参数
				spinnerText = spinner.getSelectedItem().toString();
				// 执行下拉操作
				addUsereSampleHttpRequest();

				break;
		}
	}

	/**
	 * 显示Toast
	 *
	 * @param msg
	 */
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 初始化权限下拉框
	 */
	private void initSpinner() {
		// arrayAdapter = ArrayAdapter.createFromResource(this, R.array.plantes,
		// android.R.layout.simple_spinner_item);
		// arrayAdapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		arrayAdapter = ArrayAdapter.createFromResource(this, R.array.plantes,
				android.R.layout.simple_spinner_item);

		// 设置下拉列表的风格
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 将adapter2 添加到spinner中
		spinner.setAdapter(arrayAdapter);

	}

	/*
	 * 下拉框监听
	 *
	 * @author Administrator
	 */
	class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								   long arg3) {

			System.out.println(spinnerText);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 访问网络
	 */
	private void addUsereSampleHttpRequest() {

		String ip = getString(R.string.ip);
		// http://192.168.1.132:8080/hibernate.struts2/longinaction?u.username=888&u.userpassword=888
		// String url = "http://" + ip + ":8080/ldsight/clientAction";
		String url = "http://192.168.1.132:8080/ldsight/RegisteredAction";
		String permissions = null;
		// 判断选中的权限
		// if(spinnerText.equals("管理员")){
		// permissions = "1";
		// }else if(spinnerText.equals("普通用户")){
		// permissions = "2";
		// }else {
		// showToast("请选择权限");
		// return;
		// }
		// 1为管理员，二为普通用户
		if (average_user.isChecked()) {
			permissions = "2";
		} else if (administrator.isChecked()) {
			permissions = "1";
		} else {
			showToast("请选择权限");
			return;
		}

		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("username", mobiles);
		builder.appendQueryParameter("usertype", permissions);
		mVolleyQueue = Volley.newRequestQueue(this);

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				showToast("添加新用户成功！");
						/*
						 * try { if (response.has("client") &&
						 * response.has("state")) { try { String client =
						 * response .getString("client"); String state =
						 * response.getString("state"); if
						 * (state.equals(LOGIN_RIGHT)) {
						 * SharedPreferences.Editor editor = preferences
						 * .edit(); editor.putString("username", username);
						 * editor.putString("password", password);
						 * editor.commit(); Intent intent = new Intent(
						 * LoginAct.this, MainAct.class); startActivity(intent);
						 * LoginAct.this.finish(); } else if
						 * (state.equals(ERORR_PASSWORD)) { showToast("密码错误"); }
						 * else if (state.equals(CLIENT_NOT_EXIST)) {
						 * showToast("用户不存在"); } } catch (Exception e) {
						 * e.printStackTrace(); } } } catch (Exception e) {
						 * e.printStackTrace(); showToast("JSON parse error"); }
						 */
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
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(1000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}
}
