package com.ldsight.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ldsightclient_jgd.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ldsight.entity.xinjiang.LoginJson;
import com.ldsight.util.HttpUtil;
import com.ldsight.util.LogUtil;
import com.ldsight.util.MapHttpConfiguration;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginXinjiangAct extends Activity {
    private static final String CLIENT_STATE = "client_state"; // 登录成功保存用户名密码

    private ProgressDialog mProgress;
    private final String TAG_REQUEST = "MY_TAG";
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
    String username, password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.login);


        preferences = getSharedPreferences(CLIENT_STATE, 0);
   /*     username = preferences.getString("username", "");
        password = preferences.getString("password", "");

        if (!TextUtils.isEmpty(username) &&
                !TextUtils.isEmpty(password)) {
            ((EditText) findViewById(R.id.txt_user_name)).setText(username);
            ((EditText) findViewById(R.id.txt_pass_word)).setText(password);
        }*/

        // ld:ld9102
        ((EditText) findViewById(R.id.txt_user_name)).setText("ld");
        ((EditText) findViewById(R.id.txt_pass_word)).setText("ld9102");


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

                showProgress();
                makeSampleHttpRequest();
            }
        });


    }


    private void makeSampleHttpRequest() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = MapHttpConfiguration.LOGIN_URl;

                RequestBody requestBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();


                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("xxx" + "失败" + e.toString());
                        LoginXinjiangAct.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("连接服务器失败！");
                            }
                        });
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        try {
                            String json = response.body().string();
                            Log.e("xxx", "成功 json = " + json);
                            stopProgress();

                            // 解析返回过来的json
                            Gson gson = new Gson();
                            LoginJson loginInfo = gson.fromJson(json, LoginJson.class);


                            if (loginInfo.getErrno() == 0) {

                                // 保存登录成功后的登录信息
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(CLIENT_STATE, json);
                                editor.commit();

                                // 跳转到主界面
                                Intent intent = new Intent(LoginXinjiangAct.this, ParameterAct.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt(ParameterAct.FRAGMENT_FLAG, ParameterAct.MAIN);
                                bundle.putSerializable("loginInfo", loginInfo);
                                intent.putExtras(bundle);
                                startActivity(intent);

                                // 关闭当前界面
                                LoginXinjiangAct.this.finish();

                            } else {
                                showToast("账号或者用户名错误！");
                                stopProgress();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast("获取返回参数异常");
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            showToast("解析异常");
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("获取异常错误 ：" + e.getMessage());
                        }

                    }
                }, requestBody);


            }
        }).start();


    }


    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginXinjiangAct.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void stopProgress() {
        mProgress.cancel();
    }

    private void showProgress() {
        mProgress = ProgressDialog.show(this, "", "Loading...");
    }


}
