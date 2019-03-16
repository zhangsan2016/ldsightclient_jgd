package com.ldsight.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.CheckUser;

/**
 * 权限登陆
 * @author Administrator
 *
 */
public class JurisdictionLoginAct extends Activity {
	private static final String LOGIN_RIGHT = "login_right";  // 登录成功
	private static final String ERORR_PASSWORD = "error_password";  // 密码错误
	private static final String CLIENT_NOT_EXIST = "client_not_exist";  // 该用户不存在
	private static final String CLIENT_STATE = "client_state";  // 登录成功保存用户名密码

	private ProgressDialog mProgress;
	private JsonObjectRequest jsonObjRequest;
	private final String TAG_REQUEST = "MY_TAG";
	private RequestQueue mVolleyQueue;

	private SharedPreferences preferences;

	String username;
	String password;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.jurisdiction_login);
		this.setContentView(R.layout.jurisdiction_login);

		// Initialise Volley Request Queue.

		((EditText) findViewById(R.id.txt_user_name)).setText(username);
		((EditText) findViewById(R.id.txt_pass_word)).setText(password);
		mVolleyQueue = Volley.newRequestQueue(this);

		Button loginBtn = (Button) findViewById(R.id.btn_login);
		loginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

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
				// 最高权限，直接进入系统
				else if (username.equals("hjw") && password.equals("hjw")) {
					Intent intent = new Intent(JurisdictionLoginAct.this, MainAct.class);
					startActivity(intent);
					JurisdictionLoginAct.this.finish();
					return;
				}
				showProgress();
				if(makeSampleHttpRequest()){
					finish();
				}

			}
		});
	}

	private boolean makeSampleHttpRequest() {


		CheckUser cku = CheckUser.getInstance();
		cku.setUserName("xxx");
		cku.setUserpwd("12132");
		cku.setJurisdiction(1);


		return true;

	}

	private void showProgress() {
		//mProgress = ProgressDialog.show(this, "", "Loading...");


	}

	private void stopProgress() {
		mProgress.cancel();
	}

	private void showToast(String msg) {
		Toast.makeText(JurisdictionLoginAct.this, msg, Toast.LENGTH_LONG).show();
	}
}
