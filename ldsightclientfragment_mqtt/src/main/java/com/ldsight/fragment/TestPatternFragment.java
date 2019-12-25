package com.ldsight.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ldsightclient_jgd.R;
import com.google.gson.Gson;
import com.ldsight.adapter.TestPatternListAdapter;
import com.ldsight.application.MyApplication;
import com.ldsight.base.BaseFragment;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.entity.xinjiangJson.DeviceLampJson;
import com.ldsight.entity.xinjiangJson.LoginJson;
import com.ldsight.entity.xinjiangJson.ProjectJson;
import com.ldsight.util.HttpUtil;
import com.ldsight.util.LogUtil;
import com.ldsight.util.MapHttpConfiguration;

import org.ddpush.im.v1.client.appserver.Pusher;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestPatternFragment extends BaseFragment {
    public static String TestPatternFragmentBroadcast = "TestPatternFragmentBroadcast"; // 广播接收者
    private JsonObjectRequest jsonObjRequest;
    private RequestQueue mVolleyQueue;
    private final String TAG_REQUEST = "MY_TAG";
    private ArrayList<StreetAndDevice> streetAndDevices;
    private TestPatternListAdapter adapter;
    private ProgressDialog mProgress;
    private ListView listView;
    private Button stop_lights; // 关灯
    // 选择框
    private CheckBox cbM;
    private CheckBox cbA;

    // 设置继电器
    private Button btRelaySetting;
    private Button btRelayFullOpen, btRelayAllOff;
    private CheckBox cb_relay1, cb_relay2, cb_relay3, cb_relay4, cb_relay5;
    private LoginJson loginInfo;
    private Context context;


    public TestPatternFragment() {
    }


    public static Fragment newInstance(Context context, LoginJson loginInfo) {
        TestPatternFragment fragment = new TestPatternFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("loginInfo", loginInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    /**
     * 电箱列表
     */
    private List<DeviceLampJson.DataBeanX.DeviceLamp> electricityBoxList = new ArrayList<DeviceLampJson.DataBeanX.DeviceLamp>();

    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    if (TestPatternFragment.this != null && TestPatternFragment.this.isAdded()) {
                        byte[] data = (byte[]) msg.obj;
                        if (data[14] == 0) {
                            Toast.makeText(TestPatternFragment.this.getActivity()
                                    .getApplicationContext(), "开启继电器成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TestPatternFragment.this.getActivity().getApplicationContext(), "继电器已关闭！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case 2:
                    // 输出Toast
                    String str = (String) msg.obj;
                    Toast.makeText(TestPatternFragment.this.getActivity().getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.test_pattern_fragment,
                container, false);

        listView = (ListView) rootView.findViewById(R.id.test_pattern_list);
        adapter = new TestPatternListAdapter(TestPatternFragment.this.getActivity(), electricityBoxList);
        listView.setAdapter(adapter);

        streetAndDevices = new ArrayList<StreetAndDevice>();
        mVolleyQueue = Volley.newRequestQueue(this.getActivity()
                .getApplicationContext());

        // 获取传递过来的数据
        //  loginInfo = (LoginInfo) getActivity().getIntent().getSerializableExtra("loginInfo");
        //  loginInfo = (LoginInfo)  getArguments().getString("loginInfo");
        loginInfo = (LoginJson) getArguments().getSerializable("loginInfo");

        //  showProgress();
        // 初始化View
        initView(rootView);


        // 联网获取信息
        // makeSampleHttpRequest(loginInfo.getData().get(0).getID());


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.selectIn(position);
            }

        });
        cbM = (CheckBox) rootView.findViewById(R.id.cb_m);
        cbA = (CheckBox) rootView.findViewById(R.id.cb_a);

        // 全选
        LinearLayout btnSelectAll = (LinearLayout) rootView
                .findViewById(R.id.ll_test_pattern_select_all);
        final CheckBox cbSelectAll = (CheckBox) rootView
                .findViewById(R.id.cb_test_pattern_select_all);
        cbSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                System.out.println("onCheckedChanged = " + isChecked);
                adapter.smartSelectAll(isChecked);
            }
        });
        btnSelectAll.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (cbSelectAll.isChecked()) {
                    cbSelectAll.setChecked(false);
                } else {
                    cbSelectAll.setChecked(true);
                }
            }
        });


        // 反选
        LinearLayout btnInvert = (LinearLayout) rootView
                .findViewById(R.id.ll_test_pattern_deselect);
        btnInvert.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                adapter.invertSelect();
            }
        });


        // 调光
        SeekBar sbLight = (SeekBar) rootView
                .findViewById(R.id.se_test_pattem_fragment_light);
        sbLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!cbM.isChecked() && !cbA.isChecked()) {
                    Message msg = myHandler.obtainMessage();
                    msg.obj = "请选择主灯或者辅灯";
                    msg.what = 2;
                    myHandler.sendMessage(msg);
                    return;
                }
                int progress = seekBar.getProgress();


                pushBrightness(progress, progress, null);

			/*
                if (progress >= 0 && progress <= 10) {
					 pushBrightness(0,progress,"关灯");
				} else if (progress >= 20 && progress <= 30) {
					pushBrightness(25,progress,"设置亮度为25%");
				} else if (progress >= 45 && progress <= 55) {
					pushBrightness(50,progress,"设置亮度为50%");
				} else if (progress >= 70 && progress <= 80) {
					pushBrightness(75,progress,"设置亮度为75%");
				} else if (progress >= 95 && progress <= 100) {
					pushBrightness(100,progress,"设置亮度为100%");
				}*/

                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            stopProgress();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

            }
        });
        stop_lights = (Button) rootView
                .findViewById(R.id.test_pattern_stop_lights);
        stop_lights.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                boolean[] tags = adapter.getTags();
                for (int i = 0; i < tags.length; i++) {
                    if (tags[i]) {
                        final byte[] uuid = streetAndDevices.get(i)
                                .getByteUuid();
                        new Thread() {
                            public void run() {
                                Pusher pusher = null;
                                try {
                                    // 			byte[] data = new byte[] { -63, 0, 0, 55,55 };

                                    // 获取当前应用的uuid
                                    MyApplication myApplication = MyApplication.getInstance();
                                    byte[] appUuid = myApplication.getAppUuid();

                                    byte[] data = new byte[23];
                                    data[0] = -63;  // 指令(C1)
                                    data[1] = 0;     // 设备地址
                                    System.arraycopy(appUuid, 0, data, 2, appUuid.length); // Appuuid
                                    data[10] = (byte) 0; // 光照
                                    data[11] = 0;  // 空置
                                    data[22] = (byte) getLampsNumber();   // 灯具号 （1主2辅3全）

                                    pusher = new Pusher(MyApplication.getIp(), 9966,
                                            5000);
                                    pusher.push0x20Message(uuid, data);
//									for (int i = 0; i < 2; i++) {
//										Thread.sleep(1000);
//										pusher.push0x20Message(uuid, data);
//									}
                                    pusher.push0x20Message(uuid,
                                            new byte[]{0});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (pusher != null) {
                                        try {
                                            pusher.close();
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                // showToast("发送成功");
                            }

                            ;
                        }.start();
                    }
                }

                Toast.makeText(
                        TestPatternFragment.this.getActivity().getApplicationContext(), "关灯", Toast.LENGTH_SHORT).show();

                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            stopProgress();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
            }
        });

        // 继电器开关
        Button btRelaySwitch = (Button) rootView
                .findViewById(R.id.bt_relay_switch);

        btRelaySwitch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(TestPatternFragment.this.getActivity())
                        .setTitle("继电器开启")
                        .setMessage("开启继电器吗？")
                        .setPositiveButton("开启",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        Log.e("DialogInterface",
                                                "DialogInterface 开启");

                                        showProgress();
                                        boolean[] tags = adapter.getTags();
                                        // 先判断选中路灯是否为null
                                        int count = 0;
                                        for (boolean b : tags) {
                                            if (b) {
                                                count++;
                                                break;
                                            }
                                        }
                                        if (count == 0) {
                                            stopProgress();
                                            Toast.makeText(TestPatternFragment.this.getActivity().getApplicationContext(), "选中路灯为空!", Toast.LENGTH_SHORT).show();
                                        }
                                        for (int i = 0; i < tags.length; i++) {
                                            if (tags[i]) {
                                                final byte[] uuid = streetAndDevices.get(i)
                                                        .getByteUuid();
                                                new Thread() {
                                                    public void run() {
                                                        Pusher pusher = null;
                                                        try {
                                                            byte[] data = new byte[11];
                                                            // 获取当前应用的uuid
                                                            MyApplication myApplication = MyApplication.getInstance();
                                                            byte[] appUuid = myApplication.getAppUuid();
                                                            System.arraycopy(appUuid, 0, data, 2, appUuid.length);


                                                            // 0-开、-1关
                                                            data[0] = 29; // 继电器控制指令
                                                            data[1] = 0;  // 设备地址
                                                            data[10] = 0; // 开关指令


                                                            pusher = new Pusher(MyApplication.getIp(), 9966,
                                                                    5000);
                                                            pusher.push0x20Message(uuid, data);
                                                /*	for (int i = 0; i < 2; i++) {
                                                        Thread.sleep(2000);
														pusher.push0x20Message(uuid, data);
													}*/
                                                            pusher.push0x20Message(uuid,
                                                                    new byte[]{0});
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        } finally {
                                                            if (pusher != null) {
                                                                try {
                                                                    pusher.close();
                                                                } catch (Exception e) {
                                                                }
                                                            }
                                                        }
                                                    }

                                                    ;
                                                }.start();
                                            }
                                        }

                                        new Thread() {
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                    stopProgress();
                                                } catch (InterruptedException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                            }

                                            ;
                                        }.start();

                                    }

                                })
                        .setNegativeButton("关闭",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {

                                        Log.e("DialogInterface",
                                                "DialogInterface 关闭");

                                        showProgress();
                                        boolean[] tags = adapter.getTags();
                                        // 先判断选中路灯是否为null
                                        int count = 0;
                                        for (boolean b : tags) {
                                            if (b) {
                                                count++;
                                                break;
                                            }
                                        }
                                        if (count == 0) {
                                            stopProgress();
                                            Toast.makeText(TestPatternFragment.this.getActivity().getApplicationContext(), "选中路灯为空!", Toast.LENGTH_SHORT).show();
                                        }
                                        for (int i = 0; i < tags.length; i++) {
                                            if (tags[i]) {
                                                final byte[] uuid = streetAndDevices.get(i)
                                                        .getByteUuid();
                                                new Thread() {
                                                    public void run() {
                                                        Pusher pusher = null;
                                                        try {
                                                            byte[] data = new byte[11];
                                                            // 获取当前应用的uuid
                                                            MyApplication myApplication = MyApplication.getInstance();
                                                            byte[] appUuid = myApplication.getAppUuid();
                                                            System.arraycopy(appUuid, 0, data, 2, appUuid.length);


                                                            data[0] = 29; // 继电器控制指令
                                                            data[1] = 0;  // 设备地址
                                                            data[10] = -1; // 开关指令

                                                            pusher = new Pusher(MyApplication.getIp(), 9966,
                                                                    5000);
                                                            pusher.push0x20Message(uuid, data);
                                                /*	for (int i = 0; i < 2; i++) {
                                                        Thread.sleep(2000);
														pusher.push0x20Message(uuid, data);
													}*/
                                                            pusher.push0x20Message(uuid,
                                                                    new byte[]{0});
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        } finally {
                                                            if (pusher != null) {
                                                                try {
                                                                    pusher.close();
                                                                } catch (Exception e) {
                                                                }
                                                            }
                                                        }
                                                    }

                                                    ;
                                                }.start();
                                            }
                                        }

                                        new Thread() {
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                    stopProgress();
                                                } catch (InterruptedException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                            }

                                            ;
                                        }.start();

                                    }
                                }).show();

            }
        });


        // 继电器开关
        ToggleButton relaySwitch = (ToggleButton) rootView
                .findViewById(R.id.tb_relay_switch);
        relaySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         final boolean isChecked) {
                showProgress();
                boolean[] tags = adapter.getTags();
                // 先判断选中路灯是否为null
                int count = 0;
                for (boolean b : tags) {
                    if (b) {
                        count++;
                        break;
                    }
                }
                if (count == 0) {
                    stopProgress();
                    Toast.makeText(TestPatternFragment.this.getActivity().getApplicationContext(), "选中路灯为空!", Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < tags.length; i++) {
                    if (tags[i]) {
                        final byte[] uuid = streetAndDevices.get(i)
                                .getByteUuid();
                        new Thread() {
                            public void run() {
                                Pusher pusher = null;
                                try {
                                    byte[] data = new byte[11];
                                    // 获取当前应用的uuid
                                    MyApplication myApplication = MyApplication.getInstance();
                                    byte[] appUuid = myApplication.getAppUuid();
                                    System.arraycopy(appUuid, 0, data, 2, appUuid.length);

                                    if (isChecked) {
                                        // 0-开、-1关
                                        data[0] = 29; // 继电器控制指令
                                        data[1] = 0;  // 设备地址
                                        data[10] = 0; // 开关指令
                                    } else {
                                        data[0] = 29; // 继电器控制指令
                                        data[1] = 0;  // 设备地址
                                        data[10] = -1; // 开关指令
                                    }

                                    pusher = new Pusher(MyApplication.getIp(), 9966,
                                            5000);
                                    pusher.push0x20Message(uuid, data);
                                /*	for (int i = 0; i < 2; i++) {
                                        Thread.sleep(2000);
										pusher.push0x20Message(uuid, data);
									}*/
                                    pusher.push0x20Message(uuid,
                                            new byte[]{0});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (pusher != null) {
                                        try {
                                            pusher.close();
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }

                            ;
                        }.start();
                    }
                }

                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            stopProgress();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
            }

        });


        // 设置继电器
        btRelaySetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                relaySetting(getRelayOrderNub());
            }
        });

        btRelayFullOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                relaySetting(0);
            }
        });

        btRelayAllOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                relaySetting(31);
            }
        });


        // 接收更新广播
        IntentFilter filter = new IntentFilter(TestPatternFragment.TestPatternFragmentBroadcast);
        TestPatternFragment.this.getActivity().getApplicationContext().registerReceiver(dataRefreshReceiver, filter);

        return rootView;
    }


    /**
     * 设置继电器
     * 五个继电器对应用十进制前五位，0代表开，1代表关
     *
     * @param relayOrderNub 开关指令
     */
    private void relaySetting(final int relayOrderNub) {

        showProgress();
        boolean[] tags = adapter.getTags();
        boolean flag = false;
        for (int i = 0; i < tags.length; i++) {
            if (tags[i]) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            showToast("请选择需要控制的电箱。");
            stopProgress();
        }
        for (int i = 0; i < tags.length; i++) {
            if (tags[i]) {

                String url = MapHttpConfiguration.DEVICE_CONTROL_URL;

             /*   String postBody = null;
                try {
                    JSONObject deviceObj = new JSONObject();
                    deviceObj.put("id", "2016A0E0F000001200000050");
                    deviceObj.put("Confirm", 260);
                    JSONObject options = new JSONObject();
                    options.put("Dimming", 0);
                    deviceObj.put("options", options);
                    postBody = deviceObj.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/


                String postBody = null;
                try {
                    JSONObject deviceObj = new JSONObject();
                    deviceObj.put("UUID", electricityBoxList.get(i).getUUID());
                    deviceObj.put("Confirm", 512);
                    JSONObject options = new JSONObject();
                    options.put("Rel_State", relayOrderNub);
                    deviceObj.put("options", options);
                    postBody = deviceObj.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopProgress();
                }

                RequestBody body = FormBody.create(MediaType.parse("application/json"), postBody);

                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("relaySetting" + "失败" + e.toString());
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        LogUtil.e("relaySetting" + "成功" + json);
                        stopProgress();

                    }
                }, loginInfo.getData().getToken().getToken(), body);
            }
        }

    }


    private void initView(View view) {
        // 继电器相关View
        btRelaySetting = (Button) view.findViewById(R.id.bt_relay_setting);
        btRelayFullOpen = (Button) view.findViewById(R.id.bt_relay_full_open);
        btRelayAllOff = (Button) view.findViewById(R.id.bt_relay_all_off);
        cb_relay1 = (CheckBox) view.findViewById(R.id.cb_relay1);
        cb_relay2 = (CheckBox) view.findViewById(R.id.cb_relay2);
        cb_relay3 = (CheckBox) view.findViewById(R.id.cb_relay3);
        cb_relay4 = (CheckBox) view.findViewById(R.id.cb_relay4);
        cb_relay5 = (CheckBox) view.findViewById(R.id.cb_relay5);

    }


    @Override
    public void onResume() {
        super.onResume();
        getProject(loginInfo.getData().getToken().getToken());
    }

    /**
     * 获取项目列表
     */
    public void getProject(final String token) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = MapHttpConfiguration.PROJECT_LIST_URL;

                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToast("连接服务器异常！");
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {


                        String json = response.body().string();
                        LogUtil.e("getProject xxx" + "成功" + json);

                        // 解析返回过来的json
                        Gson gson = new Gson();
                        ProjectJson project = gson.fromJson(json, ProjectJson.class);
                        List<ProjectJson.DataBeanX.ProjectInfo> projectList = project.getData().getData();

                        for (ProjectJson.DataBeanX.ProjectInfo projectInfo : projectList) {

                            try {
                                // 用于同步线程
                                final CountDownLatch latch = new CountDownLatch(1);

                                LogUtil.e("title = " + projectInfo.getTitle());

                                // 获取电箱
                                getDeviceEbox(projectInfo.getTitle(), token, latch);

                                //阻塞当前线程直到latch中数值为零才执行
                                latch.await();


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                }, token, null);
            }

        }).start();

    }


    /**
     * 获取所有电箱
     *
     * @param title 获取的项目名
     * @param token 服务器token
     * @param latch
     */
    public void getDeviceEbox(final String title, final String token, final CountDownLatch latch) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = MapHttpConfiguration.DEVICE_EBOX_URL;

                // 创建请求的参数body
                //   String postBody = "{\"where\":{\"PROJECT\":" + title + "},\"size\":5000}";
                String postBody = "{\"where\":{\"PROJECT\":\"" + title + "\"},\"size\":5000}";
                RequestBody body = FormBody.create(MediaType.parse("application/json"), postBody);

                LogUtil.e("xxx postBody = " + postBody);

                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("xxx" + "失败" + e.toString());
                        showToast("连接服务器异常！");
                        stopProgress();

                        //让latch中的数值减一
                        latch.countDown();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        LogUtil.e("xxx getDeviceEbox " + "成功" + json);

                        // 解析返回过来的json
                        Gson gson = new Gson();
                        DeviceLampJson deviceLampJson = gson.fromJson(json, DeviceLampJson.class);
                        List<DeviceLampJson.DataBeanX.DeviceLamp> projectList = deviceLampJson.getData().getData();
                        if (projectList == null || projectList.size() == 0) {
                            return;
                        }
                        electricityBoxList.addAll(projectList);
                        adapter.setTags(new boolean[electricityBoxList.size()]);

                        // 更新 listview
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //   listView.requestLayout();
                                adapter.notifyDataSetChanged();
                            }
                        });

                    /*    for (DeviceLampJson.DataBeanX.DeviceLamp deviceLamp : projectList) {

                            if (deviceLamp.getLAT().equals("") || deviceLamp.getLNG().equals("")) {
                                break;
                            }
                        }*/

                        //让latch中的数值减一
                        latch.countDown();

                    }
                }, token, body);
            }
        }).start();

    }


    /**
     * 获取变电器
     *
     * @param id 项目id
     */
  /*  public void getElectricTransducer(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("ID", id)
                        .build();
                String url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyOne";

                HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("xxx", "失败" + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        Log.e("xxx", "成功获取变电器设备" + json);
                        Gson gson = new Gson();
                        ElectricTransducer electricTransducer = gson.fromJson(json, ElectricTransducer.class);

                        // 清空之前电箱
                        electricityBoxList.clear();
                        for (int i = 0; i < electricTransducer.getData().size(); i++) {
                            getElectricalBox(electricTransducer.getData().get(i).getId());
                        }
                    }
                }, requestBody);
            }
        }).start();
    }

    */
    /**
     * 获取电箱
     *
     * @param id 变电器id
     *//*
    public void getElectricalBox(final String id) {

        RequestBody requestBody = new FormBody.Builder()
                .add("strTemplate", "{\"ischeck\":$data.rows}")
                .add("ID", id)
                .build();
        String url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyTwo";

        HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("xxx", "失败" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.e("xxx", "成功获取电箱设备" + json);
                Gson gson = new Gson();
                ElectricityBox electricityBox = gson.fromJson(json, ElectricityBox.class);

                    *//*    for (int i = 0; i < electricityBox.getData().size(); i++) {
                            getElectricityState(electricityBox.getData().get(i).getUuid());
                        }*//*

                // 保存在 List中
                electricityBoxList.addAll(electricityBox.getData());


                // 更新 listview
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.changeTags();
                        adapter.notifyDataSetChanged();
                    }
                });

                LogUtil.e("electricityBoxList.size" + electricityBoxList.size());

            }
        }, requestBody);
    }*/

    /*private void showProgress() {
        mProgress = ProgressDialog.show(TestPatternFragment.this.getActivity(),
                "", "Loading...");
    }

    private void stopProgress() {
        mProgress.cancel();
    }

    private void showToast(String msg) {

        if (TestPatternFragment.this != null && TestPatternFragment.this.isAdded()) {
            Toast.makeText(TestPatternFragment.this.getActivity().getApplicationContext(), msg,
                    Toast.LENGTH_LONG).show();
        }

    }*/

    private BroadcastReceiver dataRefreshReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            byte[] data = intent.getByteArrayExtra("data");
            if (data[13] == 41) {
                Message msg = myHandler.obtainMessage();
                msg.what = 1;
                msg.obj = data;
                myHandler.sendMessage(msg);
            }

        }
    };

    public void pushBrightness(final int brightness, int progress, String str) {

        showProgress();
        boolean[] tags = adapter.getTags();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i]) {

                String postBody = null;
                try {

                    // 判断主、辅或者全亮 Dimming，主灯亮度值FirDimming，副灯亮度值SecDimming
                    if (cbM.isChecked() && cbA.isChecked()) {

                        JSONObject deviceObj = new JSONObject();
                        deviceObj.put("UUID", electricityBoxList.get(i).getUUID());
                        deviceObj.put("Confirm", 4);
                        JSONObject options = new JSONObject();
                        options.put("Dimming", brightness);
                        deviceObj.put("options", options);
                        postBody = deviceObj.toString();

                    } else if (cbM.isChecked()) {

                        JSONObject deviceObj = new JSONObject();
                        deviceObj.put("UUID", electricityBoxList.get(i).getUUID());
                        deviceObj.put("Confirm", 4);
                        JSONObject options = new JSONObject();
                        options.put("FirDimming", brightness);
                        deviceObj.put("options", options);
                        postBody = deviceObj.toString();

                    } else if (cbA.isChecked()) {

                        JSONObject deviceObj = new JSONObject();
                        deviceObj.put("UUID", electricityBoxList.get(i).getUUID());
                        deviceObj.put("Confirm", 4);
                        JSONObject options = new JSONObject();
                        options.put("SecDimming", brightness);
                        deviceObj.put("options", options);
                        postBody = deviceObj.toString();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    stopProgress();
                }


                if (postBody == null) {
                    stopProgress();
                    return;
                }

                String url = MapHttpConfiguration.DEVICE_CONTROL_URL;
                RequestBody body = FormBody.create(MediaType.parse("application/json"), postBody);

                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("relaySetting" + "失败" + e.toString());
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        LogUtil.e("relaySetting" + "成功" + json);
                        stopProgress();

                    }
                }, loginInfo.getData().getToken().getToken(), body);


            }
        }

       /* showProgress();
        boolean[] tags = adapter.getTags();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i]) {
                // 创建json指令
                Gson gson = new Gson();
                String jsonStr = "";
                // 判断主、辅或者全亮 Dimming，主灯亮度值FirDimming，副灯亮度值SecDimming
                if (cbM.isChecked() && cbA.isChecked()) {
                    DimmingJson dimmingJson = new DimmingJson();
                    dimmingJson.setConfirm(4);
                    dimmingJson.setDimming(brightness);
                    jsonStr = gson.toJson(dimmingJson) + "#";
                } else if (cbM.isChecked()) {
                    FirDimmingJson firDimmingJson = new FirDimmingJson();
                    firDimmingJson.setConfirm(4);
                    firDimmingJson.setFirDimming(brightness);
                    jsonStr = gson.toJson(firDimmingJson) + "#";
                } else if (cbA.isChecked()) {
                    SecDimmingJson secDimmingJson = new SecDimmingJson();
                    secDimmingJson.setConfirm(4);
                    secDimmingJson.setSecDimming(brightness);
                    jsonStr = gson.toJson(secDimmingJson) + "#";
                }

                LogUtil.e("xxx jsonStr = " + jsonStr);
                if (ZkyOnlineService.heartbeatStatis == null || ZkyOnlineService.heartbeatStatis.getData() == null) {
                    showToast("服务器无法连接，请间隔 1 分钟后再试！");
                    stopProgress();
                    return;
                }

                jsonStr = StringUtil.stringToHexString(jsonStr, ZkyOnlineService.heartbeatStatis.getData().getBKey());
                int type = (HttpConfiguration.PushType.pushData << 4 | HttpConfiguration.NET);
                RequestBody requestBody = new FormBody.Builder()
                        .add("version", "225")
                        .add("type", type + "")
                        .add("key", String.valueOf(ZkyOnlineService.heartbeatStatis.getData().getISessionKey()))
                        .add("uuidFrom", HttpConfiguration._Clientuuid)
                        // .add("uuidTo", "05,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99")
                        //   .add("uuidTo", electricityBoxList.get(i).getUuid())
                        .add("crc", "")
                        .add("data", jsonStr)
                        .build();

                HttpUtil.sendSookiePostHttpRequest(HttpConfiguration.urlSend, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("xxx", "调光失败" + e.toString());
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        String json = response.body().string();
                        Log.e("xxx", "调光返回  " + json);
                        stopProgress();

                    }


                }, requestBody);
            }
        }*/


    }


    protected int getLampsNumber() {
        int number = 0;
        if (cbM.isChecked() && cbA.isChecked()) {
            number = 3;
        } else if (cbM.isChecked()) {
            number = 1;
        } else if (cbA.isChecked()) {
            number = 2;
        }
        return number;

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//		mVolleyQueue.cancelAll(TAG_REQUEST);	
//		mVolleyQueue.getCache().clear();

    }


    /**
     * 获取继电器开关指令码
     *
     * @return
     */
    private int getRelayOrderNub() {

        StringBuilder sb = new StringBuilder();

        sb.append(cb_relay5.isChecked() ? 0 : 1);
        sb.append(cb_relay4.isChecked() ? 0 : 1);
        sb.append(cb_relay3.isChecked() ? 0 : 1);
        sb.append(cb_relay2.isChecked() ? 0 : 1);
        sb.append(cb_relay1.isChecked() ? 0 : 1);

        // dimmer += (tb1 << 0) + (tb2 << 1) + (tb3 << 2)+(tb4 << 3)+(tb5 <<
        // 4);
        // dimmer = (tb5 << 4) + (tb4 << 3) + (tb3 << 2) + (tb2 << 1) + (tb1
        // << 0);
        int dimmer = Integer.parseInt(sb.toString(), 2);

        return dimmer;
    }
}