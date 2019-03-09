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
import com.ldsight.entity.ElectricTransducer;
import com.ldsight.entity.ElectricityBox;
import com.ldsight.entity.LoginInfo;
import com.ldsight.entity.ProjectItem;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.entity.ZkyJson;
import com.ldsight.service.ZkyOnlineService;
import com.ldsight.util.HttpConfiguration;
import com.ldsight.util.HttpUtil;
import com.ldsight.util.LogUtil;
import com.ldsight.util.StringUtil;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestPatternFragment  extends BaseFragment {
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
    private Button btRelayFullOpen,btRelayAllOff;
    private CheckBox cb_relay1, cb_relay2, cb_relay3, cb_relay4, cb_relay5;
    private LoginInfo loginInfo;


    public TestPatternFragment(Context context) {
        super(context);
    }

    /**
     *  电箱列表
     */
    private List<ElectricityBox.ElectricityBoxList> electricityBoxList  = new ArrayList<ElectricityBox.ElectricityBoxList>();

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
        adapter = new TestPatternListAdapter(TestPatternFragment.this.getActivity(),electricityBoxList);
        listView.setAdapter(adapter);

        streetAndDevices = new ArrayList<StreetAndDevice>();
        mVolleyQueue = Volley.newRequestQueue(this.getActivity()
                .getApplicationContext());

        // 获取传递过来的数据
        loginInfo = (LoginInfo)  getActivity().getIntent().getSerializableExtra("loginInfo");

      //  showProgress();
        // 初始化View
        initView(rootView);


        // 联网获取信息
        makeSampleHttpRequest(loginInfo.getData().get(0).getID());


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
     *  设置继电器
     * 五个继电器对应用十进制前五位，0代表开，1代表关
     * @param relayOrderNub 开关指令
     */
    private void relaySetting(final int relayOrderNub) {
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
                            data[10] = (byte) relayOrderNub; // 开关指令

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


    private synchronized void makeSampleHttpRequest(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("ID", id)
                        .build();
                String url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyPrject";
                // url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyPrject";

                HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("xxx", "失败" + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        Log.e("xxx", "成功获取项目信息" + json);
                        Headers headers = response.headers();
                        Log.e("xxx", "headers      " + response.headers());
                        Gson gson = new Gson();
                        ProjectItem projectItem = gson.fromJson(json, ProjectItem.class);


                        if(projectItem != null){
                            for (int i = 0; i < projectItem.getData().size(); i++) {
                                getElectricTransducer(projectItem.getData().get(i).getId());
                            }
                        }else{
                            showToast("连接服务器失败！");
                        }
                    }
                }, requestBody);
            }
        }).start();
    }

    /**
     * 获取变电器
     *
     * @param id 项目id
     */
    public  void getElectricTransducer(final String id) {
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

                        for (int i = 0; i < electricTransducer.getData().size(); i++) {
                            getElectricalBox(electricTransducer.getData().get(i).getId());
                        }

                    }
                }, requestBody);
            }
        }).start();
    }

    /**
     * 获取电箱
     *
     * @param id 变电器id
     */
    public  void getElectricalBox(final String id) {

        new Thread(new Runnable() {
            @Override
            public void run() {
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

                    /*    for (int i = 0; i < electricityBox.getData().size(); i++) {
                            getElectricityState(electricityBox.getData().get(i).getUuid());
                        }*/

                        // 保存在 List中
                        electricityBoxList.addAll(electricityBox.getData());

                        // 更新 listview

                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.changeTags();
                                listView.requestLayout();
                                adapter.notifyDataSetChanged();
                            }
                        });


                    }
                }, requestBody);
            }
        }).start();

    }

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
                LogUtil.e("i = " + i +"xxx Name = " + electricityBoxList.get(i).getText().trim());

                // 创建json指令
                Gson gson = new Gson();
                ZkyJson zkyJson = new ZkyJson();
                zkyJson.setConfirm("4");
                zkyJson.setDimming(brightness+"");
                String jsonStr = gson.toJson(zkyJson) + "#";

                LogUtil.e("xxx jsonStr = " + jsonStr);

              //  jsonStr  = StringUtil.stringToHexString(jsonStr, ZkyOnlineService.heartbeatStatis.getData().getBKey());
               jsonStr  = StringUtil.stringToHexString("{\"Confirm\":4,\"Dimming\":0}#", ZkyOnlineService.heartbeatStatis.getData().getBKey());
                int type = (HttpConfiguration.PushType.pushData << 4 | HttpConfiguration.NET);
                RequestBody requestBody = new FormBody.Builder()
                        .add("version", "225")
                        .add("type",  type + "")
                        .add("key", String.valueOf(ZkyOnlineService.heartbeatStatis.getData().getISessionKey()))
                        .add("uuidFrom", HttpConfiguration._Clientuuid)
                        .add("uuidTo", "05,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99")
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





            /*    final byte[] uuid = streetAndDevices.get(i)
                        .getByteUuid();
                new Thread() {
                    public void run() {
                        Pusher pusher = null;
                        try {
                            // 获取当前应用的uuid
                            MyApplication myApplication = MyApplication.getInstance();
                            byte[] appUuid = myApplication.getAppUuid();

                            byte[] data = new byte[23];
                            data[0] = -63;  // 指令(C1)
                            data[1] = 0;     // 设备地址
                            System.arraycopy(appUuid, 0, data, 2, appUuid.length); // Appuuid
                            data[10] = (byte) brightness; // 光照
                            data[11] = 0;  // 空置
                            data[22] = (byte) getLampsNumber();   // 灯具号 （1主2辅3全）

                            System.out.println(Arrays.toString(data));
                            pusher = new Pusher(MyApplication.getIp(),
                                    9966, 5000);
                            pusher.push0x20Message(uuid, data);
//							for (int i = 0; i < 2; i++) {
//								Thread.sleep(1000);
//								pusher.push0x20Message(uuid, data);
//							}
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
                }.start();*/
            }
        }

//		Toast.makeText(
//				TestPatternFragment.this.getActivity()
//						.getApplicationContext(),
//						str + progress, 0).show();
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