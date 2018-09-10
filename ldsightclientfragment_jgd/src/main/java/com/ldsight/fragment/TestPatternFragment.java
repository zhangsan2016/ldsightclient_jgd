package com.ldsight.fragment;

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
import com.ldsight.adapter.TestPatternListAdapter;
import com.ldsight.application.MyApplication;
import com.ldsight.entity.CheckUser;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.util.LogUtil;

import org.ddpush.im.v1.client.appserver.Pusher;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class TestPatternFragment extends Fragment {
    public static String TestPatternFragmentBroadcast = "TestPatternFragmentBroadcast"; // 广播接收者
    private JsonObjectRequest jsonObjRequest;
    private RequestQueue mVolleyQueue;
    private final String TAG_REQUEST = "MY_TAG";
    private ArrayList<StreetAndDevice> streetAndDevices;
    TestPatternListAdapter adapter;
    private ProgressDialog mProgress;
    private ListView listView;
    private Button stop_lights; // 关灯
    // 选择框
    private CheckBox cbM;
    private CheckBox cbA;

    // 设置继电器
    private Button btRelaySetting;
    private CheckBox cb_relay1, cb_relay2, cb_relay3, cb_relay4, cb_relay5;

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

        ;
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_pattern_fragment,
                container, false);
        listView = (ListView) rootView.findViewById(R.id.test_pattern_list);
        streetAndDevices = new ArrayList<StreetAndDevice>();
        mVolleyQueue = Volley.newRequestQueue(this.getActivity()
                .getApplicationContext());

        showProgress();
        // 初始化View
        initView(rootView);
        makeSampleHttpRequest();
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


        btRelaySetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                relaySetting();
            }
        });

        // 接收更新广播
        IntentFilter filter = new IntentFilter(TestPatternFragment.TestPatternFragmentBroadcast);
        TestPatternFragment.this.getActivity().getApplicationContext().registerReceiver(dataRefreshReceiver, filter);

        return rootView;
    }


    /**
     * 设置继电器
     */
    private void relaySetting() {
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

                            int switchOrderNub = getRelayOrderNub();
                            // 0-开、-1关
                            data[0] = 29; // 继电器控制指令
                            data[1] = 0;  // 设备地址
                            data[10] = (byte) switchOrderNub; // 开关指令
LogUtil.e("switchOrderNub ===" + switchOrderNub);

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
        cb_relay1 = (CheckBox) view.findViewById(R.id.cb_relay1);
        cb_relay2 = (CheckBox) view.findViewById(R.id.cb_relay2);
        cb_relay3 = (CheckBox) view.findViewById(R.id.cb_relay3);
        cb_relay4 = (CheckBox) view.findViewById(R.id.cb_relay4);
        cb_relay5 = (CheckBox) view.findViewById(R.id.cb_relay5);

    }


    private void makeSampleHttpRequest() {
        String ip = getString(R.string.ip);
        String url = "http://" + ip + ":8080/ldsight/deviceAction";

        jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("streetAndDevices")) {
                                try {
                                    JSONArray jsonArr = response
                                            .getJSONArray("streetAndDevices");
                                    streetAndDevices.clear();

                                    for (int i = 0; i < jsonArr.length(); i++) {
                                        JSONObject temp = jsonArr
                                                .getJSONObject(i);
                                        if (temp.isNull("deviceParam")) {
                                            continue;
                                        }
                                        JSONObject deviceParam = temp
                                                .getJSONObject("deviceParam");
                                        JSONObject street = temp
                                                .getJSONObject("street");

                                        // 根据用户显示不同的数据
                                        CheckUser cku = CheckUser.getInstance();
                                        String streetId = street
                                                .getString("streetId");
                                        if (cku.getUserName().equals("zky")) {
                                            if (!streetId.equals("SZ1018")
                                                    && !streetId
                                                    .equals("SZ1019")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "mys")) {
                                            if (!streetId.equals("SZ1023")
                                                    && !streetId
                                                    .equals("SZ1024")
                                                    && !streetId
                                                    .equals("SZ1025")) {
                                                continue;
                                            }
                                        }else if (cku.getUserName().equals(
                                                "csazf")) {
                                            if (!streetId.equals("SZ1061")
                                                    && !streetId
                                                    .equals("SZ1062")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "ysdx")) {
                                            if (!streetId.equals("SZ1012")
                                                    && !streetId
                                                    .equals("SZ1013")
                                                    && !streetId
                                                    .equals("SZ1014")
                                                    && !streetId
                                                    .equals("SZ1015")
                                                    && !streetId
                                                    .equals("SZ1016")
                                                    && !streetId
                                                    .equals("SZ1017")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "zky2")) {
                                            if (!streetId.equals("SZ1018")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "zst")) {
                                            if (!streetId.equals("SZ1019")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "admin")) {
                                            if (!streetId.equals("SZ1001")
                                                    && !streetId
                                                    .equals("SZ1002")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "ldgd")) {
                                            if (!streetId.equals("SZ1010")
                                                    && !streetId
                                                    .equals("SZ1003")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "ynyl")) {
                                            if (!streetId.equals("SZ1032")
                                                    && !streetId
                                                    .equals("SZ1033")
                                                    && !streetId
                                                    .equals("SZ1034")
                                                    && !streetId
                                                    .equals("SZ1035")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "sxtc")) {
                                            if (!streetId.equals("SZ1036")
                                                    && !streetId
                                                    .equals("SZ1037")
                                                    && !streetId
                                                    .equals("SZ1038")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "zj312")) {
                                            if (!streetId.equals("SZ1043")
                                                    && !streetId
                                                    .equals("SZ1044")
                                                    && !streetId
                                                    .equals("SZ1045")
                                                    && !streetId
                                                    .equals("SZ1046")
                                                    && !streetId
                                                    .equals("SZ1047")
                                                    && !streetId
                                                    .equals("SZ1048")
                                                    && !streetId
                                                    .equals("SZ1049")
                                                    && !streetId
                                                    .equals("SZ1050")
                                                    && !streetId
                                                    .equals("SZ1051")
                                                    && !streetId
                                                    .equals("SZ1052")
                                                    && !streetId.equals("SZ1053")&& !streetId.equals("SZ1054")
                                                    && !streetId.equals("SZ1056")
                                                    && !streetId.equals("SZ1057")
                                                    && !streetId.equals("SZ1058")) {
                                                continue;
                                            }
                                        }else{
                                            return;
                                        }

                                        StreetAndDevice streetAndDevice = new StreetAndDevice();
                                        streetAndDevice.setCityId(street
                                                .getString("cityId"));
                                        streetAndDevice.setEndTime(street
                                                .getString("endTime"));
                                        streetAndDevice.setStartTime(street
                                                .getString("startTime"));
                                        streetAndDevice.setDeviceId(street
                                                .getString("deviceId"));
                                        streetAndDevice.setDeviceParamId(deviceParam
                                                .getInt("deviceParamId"));
                                        streetAndDevice
                                                .setMb_a_Ampere(deviceParam
                                                        .getInt("mb_a_Ampere"));
                                        streetAndDevice
                                                .setMb_a_volt(deviceParam
                                                        .getInt("mb_a_volt"));
                                        streetAndDevice.setMb_addr(deviceParam
                                                .getInt("mb_addr"));
                                        streetAndDevice
                                                .setMb_b_Ampere(deviceParam
                                                        .getInt("mb_b_Ampere"));
                                        streetAndDevice
                                                .setMb_b_volt(deviceParam
                                                        .getInt("mb_b_volt"));
                                        streetAndDevice
                                                .setMb_c_Ampere(deviceParam
                                                        .getInt("mb_c_Ampere"));
                                        streetAndDevice
                                                .setMb_c_volt(deviceParam
                                                        .getInt("mb_c_volt"));
                                        streetAndDevice.setMb_func(deviceParam
                                                .getInt("mb_func"));
                                        streetAndDevice.setMb_hz(deviceParam
                                                .getInt("mb_hz"));
                                        streetAndDevice.setMb_ned(deviceParam
                                                .getInt("mb_ned"));
                                        streetAndDevice.setMb_pa(deviceParam
                                                .getInt("mb_pa"));
                                        streetAndDevice.setMb_pb(deviceParam
                                                .getInt("mb_pb"));
                                        streetAndDevice.setMb_pc(deviceParam
                                                .getInt("mb_pc"));
                                        streetAndDevice.setMb_pfav(deviceParam
                                                .getInt("mb_pfav"));
                                        streetAndDevice.setMb_psum(deviceParam
                                                .getInt("mb_psum"));
                                        streetAndDevice.setMb_qa(deviceParam
                                                .getInt("mb_qa"));
                                        streetAndDevice.setMb_qb(deviceParam
                                                .getInt("mb_qb"));
                                        streetAndDevice.setMb_qc(deviceParam
                                                .getInt("mb_qc"));
                                        streetAndDevice.setMb_qsum(deviceParam
                                                .getInt("mb_qsum"));
                                        streetAndDevice.setMb_size(deviceParam
                                                .getInt("mb_size"));
                                        streetAndDevice.setMb_ssum(deviceParam
                                                .getInt("mb_ssum"));
                                        streetAndDevice.setMb_time(deviceParam
                                                .getString("mb_time"));
                                        streetAndDevice.setMb_yed(deviceParam
                                                .getInt("mb_yed"));
                                        streetAndDevice.setStreetId(street
                                                .getString("streetId"));
                                        streetAndDevice.setStreetName(street
                                                .getString("streetName"));
                                        streetAndDevice.setUuid(street
                                                .getString("uuid"));


                                        streetAndDevices.add(streetAndDevice);

                                    }
                                    try {
                                        adapter = new TestPatternListAdapter(
                                                TestPatternFragment.this
                                                        .getActivity(),
                                                streetAndDevices);
                                        adapter.notifyDataSetChanged();
                                        listView.setAdapter(adapter);
                                    } catch (Exception e) {
                                        System.out.println("tes空指针异常"
                                                + e.getMessage().toString());

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showToast("error:" + e.getMessage().toString());
                                    System.out.println("xxerror:" + e.getMessage().toString());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // showToast("JSON parse error");
                        }
                        stopProgress();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                } else if (error instanceof ClientError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }

                stopProgress();
                //		showToast("error :" + error.getMessage());
            }
        });

        // Set a retry policy in case of SocketTimeout & ConnectionTimeout
        // Exceptions. Volley does retry for you if you have specified the
        // policy.
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
        //	mVolleyQueue.start();
    }

    private void showProgress() {
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

    }

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
                System.out.println("pushBrightness 当前uuid" + Arrays.toString(streetAndDevices
                        .get(i).getByteUuid()));
                final byte[] uuid = streetAndDevices.get(i)
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
                }.start();
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