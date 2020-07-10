package com.ldsight.act;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.ldsight.application.MyApplication;
import com.ldsight.entity.VersionInfo;
import com.ldsight.entity.xinjiangJson.LoginJson;
import com.ldsight.service.UpdateService;
import com.ldsight.util.CustomUtils;
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
    private String updatedir;  // 版本更新地址

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
       username = preferences.getString("username", "");
        password = preferences.getString("password", "");

        if (!TextUtils.isEmpty(username) &&
                !TextUtils.isEmpty(password)) {
            ((EditText) findViewById(R.id.txt_user_name)).setText(username);
            ((EditText) findViewById(R.id.txt_pass_word)).setText(password);
        }

        // ld:ld9102
     //   ((EditText) findViewById(R.id.txt_user_name)).setText("ld");
      //  ((EditText) findViewById(R.id.txt_pass_word)).setText("ld9102");


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

        // 检测版本更新
        versionUpdates();

    }

    /**
     * 从服务器获取当前最新版本号，如果成功返回TURE，如果失败，返回FALSE
     *
     * @return
     */
   /* private Boolean postCheckNewestVersionCommand() {
        MakeSampleHttpRequest mshr = new MakeSampleHttpRequest(
                this);
        // JSONObject response = mshr.getVersion();
        JSONObject response = mshr.post_to_server();
        try {

            newVersionCode = response.getInt("versionCode");
            newVersionName = response.getString("versionName");
             updatedir = response.getString("updatedir");

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.e("xxx postCheckNewestVersionCommand = " + e.getMessage().toString());
            e.printStackTrace();

            return false;
        }
    }*/


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
                                // 保存用户名和密码
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.commit();

                                // 跳转到主界面
                                Intent intent = new Intent(LoginXinjiangAct.this, ParameterAct.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt(ParameterAct.FRAGMENT_FLAG, ParameterAct.MAIN);
                                bundle.putSerializable("loginInfo", loginInfo);
                                intent.putExtras(bundle);
                                startActivity(intent);

                                // 设置到全局
                                MyApplication.setLoginInfo(loginInfo);

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

   private void  versionUpdates(){
        // 版本更新
        // 判断是否有网络
        if (CustomUtils.isNetworkConnected(this)) {
            // 根据登录的次数判断是否检测更新
            int clientCount = preferences.getInt("client_count", 0);
            if (clientCount % 2 == 0) {
                // 检测版本更新

                HttpUtil.sendGetSookieHttpRequest("http://121.40.194.91:8089/APP/getUpdate", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                            String json = response.body().string();
                            VersionInfo ver = new Gson().fromJson(json, VersionInfo.class);
                            newVersionCode = Integer.parseInt(ver.getVersionCode());
                            newVersionName = ver.getVersionName();
                            updatedir = ver.getUpdatedir();
                            new checkNewestVersionAsyncTask().execute();


                    }
                });

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

    class checkNewestVersionAsyncTask extends AsyncTask<Void, Void, Boolean> {
        // 后台执行，比较耗时的操作都放在这个位置
        @Override
        protected Boolean doInBackground(Void... params) {

                int vercode = CustomUtils.getVersionCode(LoginXinjiangAct.this);
                if (newVersionCode > vercode) {
                    return true;
                } else {
                    return false;
                }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (result) {// 如果有最新版本
                System.out.println("下载最新版本");

                doNewVersionUpdate(); // 更新新版本
            } else {
                // notNewVersionDlgShow(); // 提示当前为最新版本
             Log.e("xxx","当前最新版本");
            }
            super.onPostExecute(result);
        }
    }
    /*
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


                                // 开启更新服务UpdateService
                                // 这里为了把update更好模块化，可以传一些updateService依赖的值
                                // 如布局ID，资源ID，动态获取的标题,这里以app_name为例
                                Intent updateIntent = new Intent(
                                        LoginXinjiangAct.this,
                                        UpdateService.class);
                                updateIntent.putExtra("titleId", R.string.app_name);
                                updateIntent.putExtra("updatedir", updatedir);
                                LoginXinjiangAct.this.startService(
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
