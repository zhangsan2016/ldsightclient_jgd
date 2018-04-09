package com.ldgd.em.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.ldgd.em.R;
import com.ldgd.em.funsdkdemo.ActivityGuideDeviceCamera;

import org.json.JSONObject;

/**
 * Created by ldgd on 2017/3/4.
 * 介绍：
 */

public class LoginAct extends Activity{
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        mVolleyQueue = Volley.newRequestQueue(this);

        // 获取保存好的用户名密码
        preferences = getSharedPreferences(CLIENT_STATE, 0);
        username = preferences.getString("username", "ldgd");
        password = preferences.getString("password", "ldgd");
        ((EditText) findViewById(R.id.txt_user_name)).setText(username);
        ((EditText) findViewById(R.id.txt_pass_word)).setText(password);


        // 启动报警推送消息服务
    //    startPushAlarmNotification();


    }

    /**
     * 登录
     * @param view
     */
    public void onLogin(View view) {

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

 /*       Intent intent = new Intent();
        intent.setClass(LoginAct.this, DeviceList.class);
        startActivity(intent);
        LoginAct.this.finish();*/

  /*      Intent intent = new Intent();
        intent.setClass(LoginAct.this, BaiduMapAct.class);
        startActivity(intent);
        LoginAct.this.finish();*/

        showProgress();
        makeSampleHttpRequest();


    }


    private void startPushAlarmNotification() {
//        Intent intent = new Intent(this, ServiceGuidePushAlarmNotification.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startService(intent);
    }
    private void makeSampleHttpRequest() {
        String ip = getString(R.string.ip);
        // http://192.168.1.132:8080/hibernate.struts2/longinaction?u.username=888&u.userpassword=888
        // String url = "http://" + ip + ":8080/ldsight/clientAction";
        String url = "http://" + ip + ":8080/ldsight/clientAction";

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("username", username);
        builder.appendQueryParameter("password", password);
     //   builder.appendQueryParameter("uuid",uuidToString(MyApplication.getInstance().getAppUuid()));

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


                                Intent intent = new Intent();
                                intent.setClass(LoginAct.this, MainActivity.class);
                                startActivity(intent);
                                LoginAct.this.finish();


                           /*     Intent intent = new Intent();
                                intent.setClass(LoginAct.this, ActivityGuideDeviceCamera.class);
                                int id = ("76bd70ae3e34abd3" + "北大医疗").hashCode();
                                intent.putExtra("FUN_DEVICE_ID", id);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                LoginAct.this.finish();*/


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
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
       // mVolleyQueue.start();
    }

    private void showToast(String msg) {
        Toast.makeText(LoginAct.this, msg, Toast.LENGTH_LONG).show();
    }
    private void showProgress() {
        mProgress = ProgressDialog.show(this, "", "Loading...");
    }

    private void stopProgress() {
        if(mProgress != null){
            mProgress.cancel();
        }

    }

}
