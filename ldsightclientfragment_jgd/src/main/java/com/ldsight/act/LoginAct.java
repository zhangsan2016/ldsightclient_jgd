package com.ldsight.act;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.ldsight.application.MyApplication;
import com.ldsight.dao.MakeSampleHttpRequest;
import com.ldsight.entity.CheckUser;
import com.ldsight.service.OnlineService;
import com.ldsight.service.UpdateService;
import com.ldsight.util.CustomUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginAct extends Activity {
	private static final String LOGIN_RIGHT = "login_right"; // 登录成功
	private static final String ERORR_PASSWORD = "error_password"; // 密码错误
	private static final String CLIENT_NOT_EXIST = "client_not_exist"; // 该用户不存在
	private static final String CLIENT_STATE = "client_state"; // 登录成功保存用户名密码

	private ProgressDialog mProgress;
	private JsonObjectRequest jsonObjRequest;
	private final String TAG_REQUEST = "MY_TAG";
	private RequestQueue mVolleyQueue;
	/*
	 * 游客登录button
	 */
	private Button touristLogin;
	/*
	 * 版本信息
	 */
	private int newVersionCode;
	private String newVersionName;

	private SharedPreferences preferences;
	String username,password;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.login);

		preferences = getSharedPreferences(CLIENT_STATE, 0);
		username = preferences.getString("username", "");
		password = preferences.getString("password", "");

		if(!TextUtils.isEmpty(username) &&
				!TextUtils.isEmpty(password)){
			((EditText) findViewById(R.id.txt_user_name)).setText(username);
			((EditText) findViewById(R.id.txt_pass_word)).setText(password);
		}




		// 测试
		/*((EditText) findViewById(R.id.txt_user_name)).setText("ysdx");
		((EditText) findViewById(R.id.txt_pass_word)).setText("ysdx");*/

		mVolleyQueue = Volley.newRequestQueue(this);

		Button loginBtn = (Button) findViewById(R.id.btn_login);
		loginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				username = ((EditText) findViewById(R.id.txt_user_name))
						.getText().toString().trim();
				password = ((EditText) findViewById(R.id.txt_pass_word))
						.getText().toString().trim();
				if (username == null || password == null) {
					showToast("string null!");
					return;
				} else if (username.length() == 0 || password.length() == 0) {
					showToast("请输入用户名和密码");
					return;
				} else if (username.contains(" ") || password.contains(" ")) {
					showToast("用户名和密码不能含有空格");
					return;
				} else if (username.length() > 16 || password.length() > 16) {
					showToast("用户名和密码长度不能超过16");
					return;
				}
		/*		// 最高权限，直接进入系统
				else if (username.equals("hjw") && password.equals("hjw")) {
					Intent intent = new Intent(LoginAct.this, MainAct.class);
					startActivity(intent);
					LoginAct.this.finish();
					return;
				}*/
				showProgress();
				makeSampleHttpRequest();
			}
		});

		/*
		 * 游客登录
		 */
		touristLogin = (Button) this.findViewById(R.id.bt_tourist_login);
		touristLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginAct.this.finish();
				Intent touristMain = new Intent(LoginAct.this, TouristMainAct.class);
				startActivity(touristMain);

			/*	CheckUser cku = CheckUser.getInstance();
				cku.setUserName("youke");
				cku.setUserpwd("12132");
				cku.setJurisdiction(3);*/
			}
		});

		// 版本更新
		// 判断是否有网络
		if (CustomUtils.isNetworkConnected(LoginAct.this)) {
			// 根据登录的次数判断是否检测更新
			int clientCount = preferences.getInt("client_count", 0);
			System.out.println("client_count =" + clientCount);
			if (clientCount % 2 == 0) {
				// 检测版本更新
				new checkNewestVersionAsyncTask().execute();
				clientCount++;
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("client_count", clientCount);
				editor.commit();
			} else {
				clientCount++;
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("client_count", clientCount);
				editor.commit();
			}
		}


	}

	private void makeSampleHttpRequest() {
		String ip = getString(R.string.ip);
		// http://192.168.1.132:8080/hibernate.struts2/longinaction?u.username=888&u.userpassword=888
		// String url = "http://" + ip + ":8080/ldsight/clientAction";
		//String url = "http://" + ip + ":8080/ldsight/clientAction";

		String url = "http://" + "121.40.194.91" + ":8080/ldsight/clientAction";

		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("username", username);
		builder.appendQueryParameter("password", password);
		builder.appendQueryParameter("uuid",uuidToString(MyApplication.getInstance().getAppUuid()));

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.has("client") && response.has("state")) {
						try {
							String client = response
									.getString("client");
							String state = response.getString("state");
							if (state.equals(LOGIN_RIGHT)) {
								SharedPreferences.Editor editor = preferences
										.edit();
								editor.putString("username", username);
								editor.putString("password", password);
								editor.commit();

								// 设置保存用户信息
								CheckUser checkUser = CheckUser.getInstance();
								checkUser.setUserName(username);
								checkUser.setUserpwd(password);
								checkUser.setJurisdiction(1);



//										Intent intent = new Intent(
//												LoginAct.this, MainAct.class);
//										startActivity(intent);

									/*	Intent intent = new Intent(LoginAct.this, ParameterAct.class);
										intent.putExtra(ParameterAct.FRAGMENT_FLAG,
												ParameterAct.MAIN);
										startActivity(intent);*/


								Intent intent = new Intent(
										LoginAct.this, CatalogAct.class);
								startActivity(intent);

								// 启动心跳包服务
								Intent online = new Intent(MyApplication.getInstance(), OnlineService.class);
								startService(online);

								LoginAct.this.finish();


							} else if (state.equals(ERORR_PASSWORD)) {
								showToast("密码错误");
							} else if (state.equals(CLIENT_NOT_EXIST)) {
								showToast("用户不存在");
							}
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
				stopProgress();
				showToast(error.getMessage());
			}
		});

		// Set a retry policy in case of SocketTimeout & ConnectionTimeout
		// Exceptions. Volley does retry for you if you have specified the
		// policy.
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}


	class checkNewestVersionAsyncTask extends AsyncTask<Void, Void, Boolean> {
		// 后台执行，比较耗时的操作都放在这个位置
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (postCheckNewestVersionCommand()) {
				int vercode = CustomUtils.getVersionCode(LoginAct.this); // 用到前面第一节写的方法
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
				this);
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
		int verCode = CustomUtils.getVersionCode(this);
		String verName = CustomUtils.getVersionName(this);

		/*
		 * String str=
		 * "当前版本："+verName+" Code:"+verCode+" ,发现新版本："+newVersionName+
		 * " Code:"+newVersionCode+" ,是否更新？";
		 */
		String str = "发现新版本,是否更新？";
		Dialog dialog = new AlertDialog.Builder(this)
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
								Intent updateIntent = new Intent(
										LoginAct.this,
										UpdateService.class);
								updateIntent.putExtra("titleId",
										R.string.app_name);
								LoginAct.this.startService(
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


	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...");
	}

	private void stopProgress() {
		mProgress.cancel();
	}

	private void showToast(String msg) {
		Toast.makeText(LoginAct.this, msg, Toast.LENGTH_LONG).show();
	}

	private String uuidToString(byte[] a){
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if(i != a.length-1){
				b.append(",");
			}
		}
		return b.toString();
	}
}
