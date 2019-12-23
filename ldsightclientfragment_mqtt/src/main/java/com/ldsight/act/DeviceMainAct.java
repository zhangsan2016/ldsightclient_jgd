package com.ldsight.act;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ldsight.application.MyApplication;
import com.ldsight.base.BaseActivity;
import com.ldsight.component.DeviceTable;
import com.ldsight.entity.ElectricityBox;
import com.ldsight.entity.ElectricityDeviceStatus;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.entity.zkyjson.ZkyJson;
import com.ldsight.service.ZkyOnlineService;
import com.ldsight.util.HttpConfiguration;
import com.ldsight.util.HttpUtil;
import com.ldsight.util.LogUtil;
import com.ldsight.util.StringUtil;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ddpush.im.v1.client.appserver.Pusher;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.ldsightclient_jgd.R.id.txt_life_cycle;
import static com.example.ldsightclient_jgd.R.id.txt_volt;

public class DeviceMainAct extends BaseActivity {
    // 主灯
    public static final int PRINCIPAL = 1;
    // 辅灯
    public static final int SUBSIDIARY = 2;
    public static String DeviceMainFilter = "devicemainfilter";
    public static String UpdateCableParameterFilter = "updateCableParameter"; // 广播接收者
    public static String DeviceTemperatureFilter = "DeviceTemperatureFilter"; // 湿度温度照明度接受
    public static String DeviceStateFilter = "DeviceStateFilter"; // 设备状态（系统日期，照明度...）
    TimePickerDialog startTimePickerDialog;
    TimePickerDialog endTimePickerDialog;
    // 主灯辅灯定时按钮
    private RelativeLayout rly_primary_timing, rly_subsidiary_timing;

    private DeviceTable deviceTable;
    private int startHour;
    private int startMinute;

    private int endHour;
    private int endMinute;
    private JsonObjectRequest jsonObjRequest;
    private RequestQueue mVolleyQueue;

    String strStartTime = "";
    String strEndTime = "";

    int energy100Hour;
    int energy100Minute;

    int energy75Hour;
    int energy75Minute;

    int energy50Hour;
    int energy50Minute;

    int energy25Hour;
    int energy25Minute;

    boolean tag;

    // 当前操作，1代表开关，2代表亮度调节
    private int currentCon = 1;
    // 全局的StreetAndDevice变量
    StreetAndDevice streetAndDevice = new StreetAndDevice();
    private final String TAG_REQUEST = "MY_TAG";

    TextView txtStartTime, txtEndTime;// 定时开始结束时间
    // LinearLayout llStarttime, llEndTime; // 定时开始结束时间
    TextView txtStreetName;
    TextView txtVolt, txtVoltB, txtVoltC;
    TextView txtAmpere, txt_ampereb, txt_amperec;
    TextView txtPsum;
    TextView txtLifeCycle;
    // ToggleButton toggleButton;
    // 当前路段的id
    private String streetId;
    // 参数湿度温度光强
    private TextView shiDu, wenDu, guangQiangDu;
    /**
     * 进度条参数
     */
    // 进度百分比
    private TextView tv_progress1, tv_progress2, tv_progress3, tv_progress4,
            tv_progress5, tv_progress6;
    // 持续的开始时间
    private TextView tv_spacing_start_time1, tv_spacing_start_time2,
            tv_spacing_start_time3, tv_spacing_start_time4,
            tv_spacing_start_time5, tv_spacing_start_time6;


    // 光照使能、报警灯开关
    private ToggleButton lightMake, alarmLampSwitch;
    // 校时
    private Button calibrationTime;
    // 查询当前状态
    private Button currentState;
    // 当前状态（时间日期和亮度）
    private TextView stateDate, stateBrightness;
    // 主灯、辅灯定时时间
    private byte[] mainSixSectionDimmerIntensity;
    private byte[] assistSixSectionDimmerIntensity;


    /**
     * 电箱状态
     */
    private List<ElectricityDeviceStatus> electricityDeviceStatuses;
    private ElectricityBox.ElectricityBoxList electricityBox;
    private LinearLayout ll_prev_device_main;
    // 刷新
    private LinearLayout ll_device_main_refresh;

    // 更新界面
    private final int UPDATE_VIEW = 10;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case UPDATE_VIEW:
                    if (electricityDeviceStatuses != null) {
                        // 获取所有当前参数
                        ElectricityDeviceStatus electricityDeviceStatuse = electricityDeviceStatuses.get(0);
                        // 设置光照度
                        guangQiangDu.setText(electricityDeviceStatuse.getIllu() + "");
                        // 设置湿度
                        String dimming = electricityDeviceStatuse.getTemp().equals("") ? "NULL" : electricityDeviceStatuse.getTemp() + "";
                        shiDu.setText(dimming);
                        // 设置总功率
                        txtPsum.setText(electricityDeviceStatuse.getTot_p_fac());
                        // 设置A 、 B 、 C 电压
                        txtVolt.setText(electricityDeviceStatuse.getA_v());
                        txtVoltB.setText(electricityDeviceStatuse.getB_v());
                        txtVoltC.setText(electricityDeviceStatuse.getC_v());
                        // 设置A 、 B 、 C 电流
                        txtAmpere.setText(electricityDeviceStatuse.getA_c());
                        txt_ampereb.setText(electricityDeviceStatuse.getB_c());
                        txt_amperec.setText(electricityDeviceStatuse.getC_c());
                        // 设置当前时间
                        stateDate.setText(electricityDeviceStatuse.getTime());
                        // 路灯名称
                        txtStreetName.setText(electricityBox.getText());
                        // 定时时间
                        txtLifeCycle.setText(electricityDeviceStatuse.getFir_tt_Fir() + " - " + electricityDeviceStatuse.getSix_tt_Fir());

                    }

                    break;
            }

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.device_main);
        mVolleyQueue = Volley.newRequestQueue(this);

        // 获取传递过来的参数
        Bundle bundle = getIntent().getExtras();
        streetId = bundle.getString("streetId");
      //  electricityBox = (ElectricityBox.ElectricityBoxList) bundle.getSerializable("electricityBox");

        // 获取当前电箱状态
     //   getElectricityState(electricityBox.getUuid());

        // 初始化视图
        initView();

        // 设置点击界面
        initSetOnClick();

        //	Toast.makeText(this,"name = " + electricityBox.getText()+"uuid = " + electricityBox.getUuid(),Toast.LENGTH_SHORT).show();


    }


    /**
     * 根据 uuid 获取电箱状态
     *
     * @param uuid 电箱uuid
     */
    private void getElectricityState(final String uuid) {

        //   Toast.makeText(this, "name = " + electricityBox.getText() + "uuid = " + uuid, Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                String str = "select * from $TABLE where  TYPE=1 and uuid= '" + uuid + "'";
                String url = "http://47.99.168.98:9003/API/IOTDataFill.asmx/Fill";

                RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "\\{\"ElectricityBoxStatus\":$data.rows}")
                        .add("iTable", "1")
                        .add("iRelateID", "1")
                        .add("strSql", str)
                        .add("strNameObject", "data")
                        .build();

                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("xxx", "失败" + e.toString());
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        String json = response.body().string();

                        Log.e("xxx", "成功获取电箱设备状态" + json);
                    /*    Gson gson = new Gson();
                        ElectricTransducer electricTransducer = gson.fromJson(json, ElectricTransducer.class);

                        for (int i = 0; i < electricTransducer.getData().size(); i++) {
                            getElectricityState(electricTransducer.getData().get(i).getUuid());
                        }*/
                        electricityDeviceStatuses = parseJson(json);
                        // 更新界面
                        handler.sendEmptyMessage(UPDATE_VIEW);
                        stopProgress();

                    }


                }, requestBody);
            }
        }).start();
    }


    /**
     * 解析json
     *
     * @param result
     * @return
     */
    private List<ElectricityDeviceStatus> parseJson(
            String result) {

        Gson gson = new Gson();

        StringBuffer stringBuffer = new StringBuffer(result);
        String str = stringBuffer.substring(stringBuffer.indexOf("ElectricityBoxStatus") - 3, stringBuffer.length() - 2);
        // 去掉转义符
        String unescapeStr = StringEscapeUtils.unescapeJava(str);


        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(unescapeStr).getAsJsonObject();
        JsonArray jaStatus = jsonObject.getAsJsonArray("ElectricityBoxStatus");

        LogUtil.e("unescapeStr " + unescapeStr);
        List<ElectricityDeviceStatus> beanOnes = gson.fromJson(jaStatus.toString(),
                new TypeToken<List<ElectricityDeviceStatus>>() {
                }.getType());

        return beanOnes;
    }


    private void initSetOnClick() {
        rly_primary_timing.setOnClickListener(new MyOnclickCancelListener());
        rly_subsidiary_timing.setOnClickListener(new MyOnclickCancelListener());

        // 校时
        calibrationTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 校时
                setCurrentTime(electricityBox.getUuid());
            }
        });

        lightMake.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // 光照使能
                if (isChecked) {

                    new Thread() {
                        public void run() {
                            Pusher pusher = null;
                            try {
                                byte[] data = new byte[]{-57, 0, 1};

                                // byte[] data2 = new byte[] {0x81,55,55};
                                System.out.println(Arrays.toString(data));
                                pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
                                /*
								 * pusher.push0x20Message(
								 * streetAndDevice.getByteUuid(), data);
								 */
                                pusher.push0x20Message(
                                        streetAndDevice.getByteUuid(), data);

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
                    }.start();
                } else {
                    new Thread() {
                        public void run() {
                            Pusher pusher = null;
                            try {
                                byte[] data = new byte[]{-57, 0, 0};

                                // byte[] data2 = new byte[] {0x81,55,55};
                                System.out.println(Arrays.toString(data));
                                pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
								/*
								 * pusher.push0x20Message(
								 * streetAndDevice.getByteUuid(), data);
								 */
                                pusher.push0x20Message(
                                        streetAndDevice.getByteUuid(), data);

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
                    }.start();
                }
            }
        });

        LinearLayout ll = (LinearLayout) this
                .findViewById(R.id.ll_single_light_control);
        ll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent singleLightIntent = new Intent(DeviceMainAct.this,
                        SingleLightAct.class);
                Bundle mBundle = new Bundle();
                mBundle.putByteArray("uuid", streetAndDevice.getByteUuid());
				/*
				 * // 测试 System.out.println("streetAndDevice.getByteUuid() = " +
				 * Arrays.toString(streetAndDevice.getByteUuid()));
				 */
                singleLightIntent.putExtras(mBundle);
                startActivity(singleLightIntent);
            }
        });

        // 查询当前状态
        currentState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                new Thread() {
                    public void run() {

                        Pusher pusher = null;

                        // 获取当前应用的uuid
                        byte[] uuid = MyApplication.getInstance().getAppUuid();
                        try {
                            byte[] data = new byte[10];
                            System.arraycopy(uuid, 0, data, 2, uuid.length);
                            data[0] = 48;
                            data[1] = 0;

                            pusher = new Pusher(MyApplication.getIp(), 9966, 10000);
                            pusher.push0x20Message(
                                    streetAndDevice.getByteUuid(), data);
                            // pusher.push0x20Message(new byte[] { 3, 86,
                            // -128,32, 48, 6, 17, 18 }, data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (pusher != null) {
                                try {
                                    pusher.close();
                                    sleep(2000);
                                    stopProgress();
                                } catch (Exception e) {
                                }
                            }
                        }
                        // showToast("发送成功");
                    }
                }.start();
            }
        });
        // 报警灯开关
        alarmLampSwitch
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 final boolean isChecked) {
                        showProgress();
                        new Thread() {
                            public void run() {
                                Pusher pusher = null;
                                // 获取当前应用的uuid
                                byte[] uuid = MyApplication.getInstance()
                                        .getAppUuid();
                                try {
                                    byte[] data = new byte[11];
                                    data[0] = 41;
                                    data[1] = 0;
                                    System.arraycopy(uuid, 0, data, 2,
                                            uuid.length);
                                    if (isChecked) {
                                        data[10] = 1;
                                    } else {
                                        data[10] = 0;
                                    }
                                    pusher = new Pusher(MyApplication.getIp(), 9966,
                                            10000);
                                    pusher.push0x20Message(
                                            streetAndDevice.getByteUuid(), data);
                                    // pusher.push0x20Message(new
                                    // byte[]{3,86,-128,32,48,81,5,34}, data);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showToast(e.getMessage());
                                    stopProgress();
                                } finally {
                                    if (pusher != null) {
                                        try {
                                            pusher.close();
                                            sleep(2000);
                                            stopProgress();
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                // showToast("发送成功");
                            }
                        }.start();
                    }
                });

        // 返回back
        ll_prev_device_main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceMainAct.this.finish();
            }
        });

        // 刷新
        ll_device_main_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取当前电箱状态
                showProgress();
                getElectricityState(electricityBox.getUuid());
            }
        });

    }

    /**
     * 校时
     *
     * @param uuidTo
     */
    private void setCurrentTime(String uuidTo) {

        showProgress();

        // 创建json指令
        Gson gson = new Gson();
        ZkyJson zkyJson = new ZkyJson();
        zkyJson.setConfirm(2);

        // 获取当前日期 ，按照日期格式 19:03:04:03:21:19:01
        SimpleDateFormat sdf1 = new SimpleDateFormat("yy-MM-dd-");
        SimpleDateFormat sdf2 = new SimpleDateFormat("-HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        cal.setTime(new Date());
        String date = sdf1.format(new Date()) + week_index + sdf2.format(new Date());
        zkyJson.setTime(date);


        // {"Confirm":"2","Time":"19:03:07:02:39:57:4"}#

        String jsonStr = gson.toJson(zkyJson) + "#";
        LogUtil.e("校时 jsonStr = " + jsonStr);

        if(ZkyOnlineService.heartbeatStatis == null || ZkyOnlineService.heartbeatStatis.getData() == null){
            showToast("服务器无法连接，请稍后再试！");
            stopProgress();
           return;
        }
        jsonStr  = StringUtil.stringToHexString(jsonStr, ZkyOnlineService.heartbeatStatis.getData().getBKey());

        LogUtil.e("uuidTo = " + uuidTo);
        int type = (HttpConfiguration.PushType.pushData << 4 | HttpConfiguration.NET);
        RequestBody requestBody = new FormBody.Builder()
                .add("version", "225")
                .add("type",  type + "")
                .add("key", String.valueOf(ZkyOnlineService.heartbeatStatis.getData().getISessionKey()))
                .add("uuidFrom", HttpConfiguration._Clientuuid)
               // .add("uuidTo", "05,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99")
               .add("uuidTo", electricityDeviceStatuses.get(0).getUUID())
                .add("crc", "")
                .add("data", jsonStr)
                .build();

        HttpUtil.sendHttpRequest(HttpConfiguration.urlSend, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("xxx", "校时失败" + e.toString());
                showToast("校时失败！");
                stopProgress();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String json = response.body().string();
                Log.e("xxx", "校时返回  " + json);
                showToast("校时成功！");
                stopProgress();

            }


        }, requestBody);
    }

    private void initView() {

        txtStartTime = (TextView) findViewById(R.id.txt_device_main_start_time);
        // llStarttime =(LinearLayout)
        // findViewById(R.id.ll_device_main_start_time);
        txtEndTime = (TextView) findViewById(R.id.txt_device_main_end_time);
        // llEndTime = (LinearLayout)
        findViewById(R.id.ll_device_main_end_time);

        txtStreetName = (TextView) findViewById(R.id.txt_street_name);
        txtVolt = (TextView) findViewById(txt_volt);
        txtAmpere = (TextView) findViewById(R.id.txt_ampere);
        txt_ampereb = (TextView) findViewById(R.id.txt_ampereb);
        txt_amperec = (TextView) findViewById(R.id.txt_amperec);

        txtPsum = (TextView) findViewById(R.id.txt_psum);
        txtLifeCycle = (TextView) findViewById(txt_life_cycle);
        // 电压B、C
        txtVoltB = (TextView) findViewById(R.id.txt_Bvolt);
        txtVoltC = (TextView) findViewById(R.id.txt_Cvolt);

        // deviceTable = (DeviceTable)
        // findViewById(R.id.layout_device_main_table);
        // // 湿度温度光照度
        shiDu = (TextView) this.findViewById(R.id.tv_shidu);
        //wenDu = (TextView) this.findViewById(R.id.tv_wendu);
        guangQiangDu = (TextView) this.findViewById(R.id.tv_guangqiangdu);

        // 查询当前状态(日期与亮度)
        stateDate = (TextView) this.findViewById(R.id.tv_date);
        stateBrightness = (TextView) this.findViewById(R.id.tv_brightness);
        // 进度条界面参数初始化
        tv_progress1 = (TextView) this.findViewById(R.id.tv_progress1);
        tv_progress2 = (TextView) this.findViewById(R.id.tv_progress2);
        tv_progress3 = (TextView) this.findViewById(R.id.tv_progress3);
        tv_progress4 = (TextView) this.findViewById(R.id.tv_progress4);
        tv_progress5 = (TextView) this.findViewById(R.id.tv_progress5);
        tv_progress6 = (TextView) this.findViewById(R.id.tv_progress6);

        // 六个阶段的时间
        tv_spacing_start_time1 = (TextView) this
                .findViewById(R.id.tv_spacing_start_time1);
        tv_spacing_start_time2 = (TextView) this
                .findViewById(R.id.tv_spacing_start_time2);
        tv_spacing_start_time3 = (TextView) this
                .findViewById(R.id.tv_spacing_start_time3);
        tv_spacing_start_time4 = (TextView) this
                .findViewById(R.id.tv_spacing_start_time4);
        tv_spacing_start_time5 = (TextView) this
                .findViewById(R.id.tv_spacing_start_time5);
        tv_spacing_start_time6 = (TextView) this
                .findViewById(R.id.tv_spacing_start_time6);


        // 光照使能和校时
        lightMake = (ToggleButton) this.findViewById(R.id.tb_Light_make);
        calibrationTime = (Button) this.findViewById(R.id.bt_calibration_time);
        // 当前状态
        currentState = (Button) this.findViewById(R.id.bt_current_state);
        // 报警灯
        alarmLampSwitch = (ToggleButton) this
                .findViewById(R.id.tv_alarm_lamp_switch);
        rly_primary_timing = (RelativeLayout) this
                .findViewById(R.id.rly_primary_timing);
        rly_subsidiary_timing = (RelativeLayout) this
                .findViewById(R.id.rly_subsidiary_timing);

        // 返回
        ll_prev_device_main = (LinearLayout) this.findViewById(R.id.ll_prev_device_main);
        // 刷新
        ll_device_main_refresh = (LinearLayout) this.findViewById(R.id.ll_device_main_refresh);
    }


    public void makeStreetAndDeviceHttpRequest(String streetId) {
        if (streetId.equals("") || streetId == null) {
            return;
        }
        String ip = getString(R.string.ip);
        String url = "http://" + ip + ":8080/ldsight/streetAndDeviceAction";
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("streetId", streetId);

        jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
                builder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("streetAndDevice")) {
                        try {
                            JSONObject jsonObject = response
                                    .getJSONObject("streetAndDevice");
                            JSONObject deviceParam = jsonObject
                                    .getJSONObject("deviceParam");
                            JSONObject street = jsonObject
                                    .getJSONObject("street");
                            streetAndDevice.setCityId(street
                                    .getString("cityId"));
                            streetAndDevice.setEndTime(street
                                    .getString("endTime"));
                            streetAndDevice.setStartTime(street
                                    .getString("startTime"));
                            streetAndDevice.setDeviceId(street
                                    .getString("deviceId"));
                            streetAndDevice
                                    .setDeviceParamId(deviceParam
                                            .getInt("deviceParamId"));
                            streetAndDevice.setMb_a_Ampere(deviceParam
                                    .getInt("mb_a_Ampere"));
                            streetAndDevice.setMb_a_volt(deviceParam
                                    .getInt("mb_a_volt"));
                            streetAndDevice.setMb_addr(deviceParam
                                    .getInt("mb_addr"));
                            streetAndDevice.setMb_b_Ampere(deviceParam
                                    .getInt("mb_b_Ampere"));
                            streetAndDevice.setMb_b_volt(deviceParam
                                    .getInt("mb_b_volt"));
                            streetAndDevice.setMb_c_Ampere(deviceParam
                                    .getInt("mb_c_Ampere"));
                            streetAndDevice.setMb_c_volt(deviceParam
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
                            streetAndDevice.setEnergy100(street
                                    .getString("energy100"));
                            streetAndDevice.setEnergy75(street
                                    .getString("energy75"));
                            streetAndDevice.setEnergy50(street
                                    .getString("energy50"));
                            streetAndDevice.setEnergy25(street
                                    .getString("energy25"));
                            streetAndDevice.setLightSwitch(street
                                    .getInt("lightSwitch"));

                            txtStreetName.setText(streetAndDevice
                                    .getStreetName());
                            // txtStartTime.setText(streetAndDevice
                            // .getStartTime());
                            // txtEndTime.setText(streetAndDevice
                            // .getEndTime());

									/*
									 * DecimalFormat voltDF = new DecimalFormat(
									 * "######0.0"); txtVolt.setText("" +
									 * voltDF.format((double) streetAndDevice
									 * .getMb_a_volt() / 100) + "V");
									 *
									 * // txtVolt.setText("" // + ((double)
									 * streetAndDevice // .getMb_a_volt()) /
									 * 100);
									 *
									 * txtAmpere.setText("" +
									 * (streetAndDevice.getMb_a_Ampere()) +
									 * "A"); txtPsum.setText("" +
									 * streetAndDevice.getMb_psum() + "KW");
									 */

                            DecimalFormat df = new DecimalFormat(
                                    "######0.0 ");
                            txtAmpere.setText(""
                                    + df.format(((long) streetAndDevice
                                    .getMb_a_Ampere()) / 1000)
                                    + "A");

                            txt_ampereb.setText(""
                                    + df.format(((long) streetAndDevice
                                    .getMb_b_Ampere()) / 1000)
                                    + "A");
                            txt_amperec.setText(""
                                    + df.format(((long) streetAndDevice
                                    .getMb_c_Ampere()) / 1000)
                                    + "A");

                            DecimalFormat voltDF = new DecimalFormat(
                                    "######0.0");
                            txtVolt.setText(""
                                    + voltDF.format(((double) streetAndDevice
                                    .getMb_a_volt()) / 100)
                                    + "V");
                            txtVoltB.setText(""
                                    + voltDF.format(((double) streetAndDevice
                                    .getMb_b_volt()) / 100)
                                    + "V");
                            txtVoltC.setText(""
                                    + voltDF.format(((double) streetAndDevice
                                    .getMb_c_volt()) / 100)
                                    + "V");

                            txtPsum.setText(""
                                    + voltDF.format(((double) streetAndDevice
                                    .getMb_psum()) / 100000)
                                    + "KW");

                            txtLifeCycle.setText(""
                                    + streetAndDevice.getStartTime()
                                    + ":"
                                    + streetAndDevice.getEndTime());

                            energy100Hour = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy100().split(":")[0]);
                            energy100Minute = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy100().split(":")[1]);

                            energy75Hour = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy75().split(":")[0]);
                            energy75Minute = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy75().split(":")[1]);

                            energy50Hour = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy50().split(":")[0]);
                            energy50Minute = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy50().split(":")[1]);

                            energy25Hour = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy25().split(":")[0]);
                            energy25Minute = Integer
                                    .parseInt(streetAndDevice
                                            .getEnergy25().split(":")[1]);

                            // deviceTable.init100PickerDialog(
                            // energy100Hour, energy100Minute);
                            // deviceTable.init75PickerDialog(
                            // energy100Hour, energy75Minute);
                            // deviceTable.init50PickerDialog(
                            // energy100Hour, energy50Minute);
                            // deviceTable.init25PickerDialog(
                            // energy100Hour, energy25Minute);

                            startHour = Integer
                                    .parseInt(streetAndDevice
                                            .getStartTime().split(":")[0]);
                            startMinute = Integer
                                    .parseInt(streetAndDevice
                                            .getStartTime().split(":")[1]);

                            endHour = Integer.parseInt(streetAndDevice
                                    .getEndTime().split(":")[0]);
                            endMinute = Integer
                                    .parseInt(streetAndDevice
                                            .getEndTime().split(":")[1]);

                            // txtStartTime.setText(streetAndDevice
                            // .getStartTime());
                            // txtEndTime.setText(streetAndDevice
                            // .getEndTime());

                            // if (streetAndDevice.getLightSwitch() ==
                            // 1) {
                            // toggleButton.setChecked(true);
                            // } else {
                            // toggleButton.setChecked(false);
                            // }
                            // toggleButton
                            // .setOnCheckedChangeListener(new
                            // OnCheckedChangeListener() {
                            // public void onCheckedChanged(
                            // CompoundButton buttonView,
                            // boolean isChecked) {
                            // Conf.CURRENT_CONF = Conf.SINGLE_SWITCH;
                            // currentCon = 1;
                            // if (isChecked) {
                            // showProgress();
                            // streetAndDevice
                            // .setLightSwitch(1);
                            // // showToast("开灯");
                            // new Thread() {
                            // public void run() {
                            // Pusher pusher = null;
                            // try {
                            // byte[] data = new byte[] {
                            // 1,
                            // 1 };
                            // System.out
                            // .println(Arrays
                            // .toString(data));
                            // pusher = new Pusher(
                            // "121.40.194.91",
                            // 9966,
                            // 5000);
                            // tag = true;
                            // while (tag == true) {
                            // pusher.push0x20Message(
                            // streetAndDevice
                            // .getByteUuid(),
                            // data);
                            // Thread.sleep(1000);
                            // }
                            // pusher.push0x20Message(
                            // streetAndDevice
                            // .getByteUuid(),
                            // new byte[] { 0 });
                            // } catch (Exception e) {
                            // e.printStackTrace();
                            // } finally {
                            // if (pusher != null) {
                            // try {
                            // pusher.close();
                            // } catch (Exception e) {
                            // }
                            // }
                            // }
                            // // showToast("发送成功");
                            // }
                            // }.start();
                            // } else {
                            // showProgress();
                            // streetAndDevice
                            // .setLightSwitch(2);
                            // // showToast("关灯");
                            // new Thread() {
                            // public void run() {
                            // Pusher pusher = null;
                            // try {
                            // byte[] data = new byte[] {
                            // 1,
                            // 2 };
                            // System.out
                            // .println(Arrays
                            // .toString(data));
                            // pusher = new Pusher(
                            // "121.40.194.91",
                            // 9966,
                            // 5000);
                            // tag = true;
                            // while (tag == true) {
                            // pusher.push0x20Message(
                            // streetAndDevice
                            // .getByteUuid(),
                            // data);
                            // Thread.sleep(1000);
                            // }
                            // pusher.push0x20Message(
                            // streetAndDevice
                            // .getByteUuid(),
                            // new byte[] { 0 });
                            // } catch (Exception e) {
                            // e.printStackTrace();
                            // } finally {
                            // if (pusher != null) {
                            // try {
                            // pusher.close();
                            // } catch (Exception e) {
                            // }
                            // }
                            // }
                            // // showToast("发送成功");
                            // }
                            // }.start();
                            // }
                            // }
                            // });

                            startTimePickerDialog = new TimePickerDialog(
                                    DeviceMainAct.this,
                                    new OnTimeSetListener() {
                                        public void onTimeSet(
                                                TimePicker view,
                                                int hourOfDay,
                                                int minute) {
                                            String strStartHour = ""
                                                    + hourOfDay;
                                            String strStartMinute = ""
                                                    + minute;

                                            if (hourOfDay / 10 <= 0) {
                                                strStartHour = "0"
                                                        + strStartHour;
                                            }
                                            if (minute / 10 <= 0) {
                                                strStartMinute = "0"
                                                        + strStartMinute;
                                            }
                                            startHour = hourOfDay;
                                            startMinute = minute;
                                            strStartTime = strStartHour
                                                    + " : "
                                                    + strStartMinute;
                                            txtStartTime
                                                    .setText(strStartTime);
                                            streetAndDevice
                                                    .setStartTime(strStartTime);
                                            tv_spacing_start_time1
                                                    .setText(strStartTime);
                                        }
                                    },
                                    Integer.parseInt(streetAndDevice
                                            .getStartTime().split(":")[0]),
                                    Integer.parseInt(streetAndDevice
                                            .getStartTime().split(":")[1]),
                                    true);

                            endTimePickerDialog = new TimePickerDialog(
                                    DeviceMainAct.this,
                                    new OnTimeSetListener() {
                                        public void onTimeSet(
                                                TimePicker view,
                                                int hourOfDay,
                                                int minute) {
                                            String strEndHour = ""
                                                    + hourOfDay;
                                            String strEndMinute = ""
                                                    + minute;

                                            if (hourOfDay / 10 <= 0) {
                                                strEndHour = "0"
                                                        + strEndHour;
                                            }
                                            if (minute / 10 <= 0) {
                                                strEndMinute = "0"
                                                        + strEndMinute;
                                            }
                                            endHour = hourOfDay;
                                            endMinute = minute;
                                            strEndTime = strEndHour
                                                    + " : "
                                                    + strEndMinute;
                                            streetAndDevice
                                                    .setEndTime(strEndTime);
                                            txtEndTime
                                                    .setText(strEndTime);
                                            tv_spacing_start_time6
                                                    .setText(strEndTime);

                                        }
                                    },
                                    Integer.parseInt(streetAndDevice
                                            .getEndTime().split(":")[0]),
                                    Integer.parseInt(streetAndDevice
                                            .getEndTime().split(":")[1]),
                                    true);

                            // 设置节能状态
                            setEnergySavingState();
						/*	// 初始化进度条
							setSeekBar();*/

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
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
        // mVolleyQueue.start();
    }

    private void makeSaveStreetHttpRequest()
            throws UnsupportedEncodingException {
        String ip = getString(R.string.ip);
        String url = "http://" + ip + ":8080/ldsight/updateStreetAction";
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("cityId", streetAndDevice.getCityId());
        builder.appendQueryParameter("deviceId", streetAndDevice.getDeviceId());
        builder.appendQueryParameter("endTime", streetAndDevice.getEndTime());
        builder.appendQueryParameter("startTime",
                streetAndDevice.getStartTime());
        builder.appendQueryParameter("streetId", streetAndDevice.getStreetId());
        builder.appendQueryParameter("streetName",
                streetAndDevice.getStreetName());
        builder.appendQueryParameter("energy100",
                streetAndDevice.getEnergy100());
        builder.appendQueryParameter("energy25", streetAndDevice.getEnergy25());
        builder.appendQueryParameter("energy75", streetAndDevice.getEnergy75());
        builder.appendQueryParameter("energy50", streetAndDevice.getEnergy50());
        builder.appendQueryParameter("lightSwitch",
                "" + streetAndDevice.getLightSwitch());
        builder.appendQueryParameter("uuid", "" + streetAndDevice.getUuid());

        jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
                builder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("JSON parse error");
                }
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            stopProgress();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                } else if (error instanceof ClientError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            stopProgress();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
                showToast(error.getMessage());
            }
        });
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
        // mVolleyQueue.start();
    }

    private void makeLightSwitchHttpRequest() {
        String ip = getString(R.string.ip);
        String url = "http://" + ip + ":8080/ldsight/lightSwitchAction";
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("streetId", streetAndDevice.getStreetId());
        builder.appendQueryParameter("light_switch",
                "" + streetAndDevice.getLightSwitch());

        jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
                builder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("JSON parse error");
                }
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            stopProgress();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                } else if (error instanceof ClientError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            stopProgress();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
                showToast(error.getMessage());
            }
        });
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
        // mVolleyQueue.start();
    }


    /**
     * 设置节能状态
     */

    private void setEnergySavingState() {
        TextView energySavingState = (TextView) DeviceMainAct.this
                .findViewById(R.id.txt_energy);

        // 设置节能状态
        // 根据当前时间判断节能状态
        Calendar ca = Calendar.getInstance();
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        int minute = ca.get(Calendar.MINUTE);

        // ca.setTime(new java.util.Date());
        // int hour = ca.HOUR_OF_DAY;
        // int minute = ca.MINUTE;
        int energy100StartHour = Integer.parseInt(streetAndDevice
                .getStartTime().split(":")[0]);
        int energy100EndHour = Integer.parseInt(streetAndDevice

                .getStartTime().split(":")[0])
                + Integer
                .parseInt(streetAndDevice.getEnergy100().split(":")[0]);
        int energy100StartMinute = Integer.parseInt(streetAndDevice
                .getStartTime().split(":")[1]);
        int energy100EndMinute = Integer.parseInt(streetAndDevice
                .getStartTime().split(":")[1])
                + Integer
                .parseInt(streetAndDevice.getEnergy100().split(":")[1]);

        int energy75StartHour = energy100EndHour;
        int energy75EndHour = energy75StartHour
                + Integer.parseInt(streetAndDevice.getEnergy75().split(":")[0]);
        int energy75StartMinute = energy100EndMinute;
        int energy75EndMinute = energy75StartMinute
                + Integer.parseInt(streetAndDevice.getEnergy75().split(":")[1]);

        int energy50StartHour = energy75EndHour;
        int energy50EndHour = energy50StartHour
                + Integer.parseInt(streetAndDevice.getEnergy50().split(":")[0]);
        int energy50StartMinute = energy75EndMinute;
        int energy50EndMinute = energy50StartMinute
                + Integer.parseInt(streetAndDevice.getEnergy50().split(":")[1]);

        int energy25StartHour = energy50EndHour;
        int energy25EndHour = energy25StartHour
                + Integer.parseInt(streetAndDevice.getEnergy25().split(":")[0]);
        int energy25StartMinute = energy25EndHour;
        int energy25EndMinute = energy25EndHour
                + Integer.parseInt(streetAndDevice.getEnergy25().split(":")[1]);

        if (hour >= energy100StartHour && hour < energy100EndHour) {
            System.out.println("100%");
            energySavingState.setText("100%");
        } else if (hour >= energy75StartHour && hour < energy75EndHour) {
            System.out.println("75%");
            energySavingState.setText("75%");
        } else if (hour >= checkBig24(energy50StartHour)
                && hour < checkBig24(energy50EndHour)) {
            System.out.println("50%");
            energySavingState.setText("50%");
        } else if (hour >= checkBig24(energy25StartHour)
                && hour <= checkBig24(energy25EndHour)) {
            System.out.println("25%");
            energySavingState.setText("25%");
        } else {
            System.out.println("0%");
            energySavingState.setText("0%");
        }

    }

    /**
     * 转换24小时的时间格式
     *
     * @param checkTime
     * @return
     */
    private int checkBig24(int checkTime) {
        if (checkTime >= 24) {
            // checkTime = (checkTime % 12) == 0 ? 12 : (checkTime % 12);
            checkTime = (checkTime % 12);
        }
        return checkTime;
    }

    /**
     * 判断时间值，不能大于24
     *
     * @param time
     */
    private int checkMax24(int time) {
        time = time % 24;
        return time;
    }

    /**
     * 时间监听
     *
     * @author Administrator
     */
    private class timeListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_spacing_start_time1:
                    new TimePickerDialog(DeviceMainAct.this,
                            new OnTimeSetListener() {
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    String strEndHour = "" + hourOfDay;
                                    String strEndMinute = "" + minute;

                                    if (hourOfDay / 10 <= 0) {
                                        strEndHour = "0" + strEndHour;
                                    }
                                    if (minute / 10 <= 0) {
                                        strEndMinute = "0" + strEndMinute;
                                    }
                                    endHour = hourOfDay;
                                    endMinute = minute;
                                    strEndTime = strEndHour + " : " + strEndMinute;
                                    tv_spacing_start_time1.setText(strEndTime);
                                    txtStartTime.setText(strEndTime);
                                }
                            }, Integer.parseInt(tv_spacing_start_time1.getText()
                            .toString().trim().split(":")[0].trim()),
                            Integer.parseInt(tv_spacing_start_time1.getText()
                                    .toString().trim().split(":")[1].trim()), true)
                            .show();
                    break;
                case R.id.ll_spacing_start_time2:
                    new TimePickerDialog(DeviceMainAct.this,
                            new OnTimeSetListener() {
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    String strEndHour = "" + hourOfDay;
                                    String strEndMinute = "" + minute;

                                    if (hourOfDay / 10 <= 0) {
                                        strEndHour = "0" + strEndHour;
                                    }
                                    if (minute / 10 <= 0) {
                                        strEndMinute = "0" + strEndMinute;
                                    }
                                    endHour = hourOfDay;
                                    endMinute = minute;
                                    strEndTime = strEndHour + " : " + strEndMinute;
                                    tv_spacing_start_time2.setText(strEndTime);
                                }
                            }, Integer.parseInt(tv_spacing_start_time2.getText()
                            .toString().trim().split(":")[0].trim()),
                            Integer.parseInt(tv_spacing_start_time2.getText()
                                    .toString().trim().split(":")[1].trim()), true)
                            .show();
                    break;
                case R.id.ll_spacing_start_time3:
                    new TimePickerDialog(DeviceMainAct.this,
                            new OnTimeSetListener() {
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    String strEndHour = "" + hourOfDay;
                                    String strEndMinute = "" + minute;

                                    if (hourOfDay / 10 <= 0) {
                                        strEndHour = "0" + strEndHour;
                                    }
                                    if (minute / 10 <= 0) {
                                        strEndMinute = "0" + strEndMinute;
                                    }
                                    endHour = hourOfDay;
                                    endMinute = minute;
                                    strEndTime = strEndHour + " : " + strEndMinute;
                                    tv_spacing_start_time3.setText(strEndTime);
                                }
                            }, Integer.parseInt(tv_spacing_start_time3.getText()
                            .toString().trim().split(":")[0].trim()),
                            Integer.parseInt(tv_spacing_start_time3.getText()
                                    .toString().trim().split(":")[1].trim()), true)
                            .show();
                    break;
                case R.id.ll_spacing_start_time4:
                    new TimePickerDialog(DeviceMainAct.this,
                            new OnTimeSetListener() {
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    String strEndHour = "" + hourOfDay;
                                    String strEndMinute = "" + minute;

                                    if (hourOfDay / 10 <= 0) {
                                        strEndHour = "0" + strEndHour;
                                    }
                                    if (minute / 10 <= 0) {
                                        strEndMinute = "0" + strEndMinute;
                                    }
                                    endHour = hourOfDay;
                                    endMinute = minute;
                                    strEndTime = strEndHour + " : " + strEndMinute;
                                    tv_spacing_start_time4.setText(strEndTime);
                                }
                            }, Integer.parseInt(tv_spacing_start_time4.getText()
                            .toString().trim().split(":")[0].trim()),
                            Integer.parseInt(tv_spacing_start_time4.getText()
                                    .toString().trim().split(":")[1].trim()), true)
                            .show();
                    break;
                case R.id.ll_spacing_start_time5:
                    new TimePickerDialog(DeviceMainAct.this,
                            new OnTimeSetListener() {
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    String strEndHour = "" + hourOfDay;
                                    String strEndMinute = "" + minute;

                                    if (hourOfDay / 10 <= 0) {
                                        strEndHour = "0" + strEndHour;
                                    }
                                    if (minute / 10 <= 0) {
                                        strEndMinute = "0" + strEndMinute;
                                    }
                                    endHour = hourOfDay;
                                    endMinute = minute;
                                    strEndTime = strEndHour + " : " + strEndMinute;
                                    tv_spacing_start_time5.setText(strEndTime);
                                }
                            }, Integer.parseInt(tv_spacing_start_time5.getText()
                            .toString().trim().split(":")[0].trim()),
                            Integer.parseInt(tv_spacing_start_time5.getText()
                                    .toString().trim().split(":")[1].trim()), true)
                            .show();
                    break;
                case R.id.ll_spacing_start_time6:
                    new TimePickerDialog(DeviceMainAct.this,
                            new OnTimeSetListener() {
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    String strEndHour = "" + hourOfDay;
                                    String strEndMinute = "" + minute;

                                    if (hourOfDay / 10 <= 0) {
                                        strEndHour = "0" + strEndHour;
                                    }
                                    if (minute / 10 <= 0) {
                                        strEndMinute = "0" + strEndMinute;
                                    }
                                    endHour = hourOfDay;
                                    endMinute = minute;
                                    strEndTime = strEndHour + " : " + strEndMinute;
                                    tv_spacing_start_time6.setText(strEndTime);
                                    txtEndTime.setText(strEndTime);
                                }
                            }, Integer.parseInt(tv_spacing_start_time6.getText()
                            .toString().trim().split(":")[0].trim()),
                            Integer.parseInt(tv_spacing_start_time6.getText()
                                    .toString().trim().split(":")[1].trim()), true)
                            .show();
                    break;

            }
        }
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
	/*	Intent intent = new Intent();
		intent.setAction(MainFragment.DataRefresh);
		sendBroadcast(intent);
		unregisterReceiver(deviceMainReceiver);
		unregisterReceiver(cableParameterReceiver);
		unregisterReceiver(DeviceTemperatureReceiver);*/
    }

    private class MyOnclickCancelListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent;
            int startTimeHour;
            int startTimeMinute;
            int enTimeHour;
            int enTimeMinute;
            switch (v.getId()) {
                case R.id.rly_primary_timing:
                    if (electricityDeviceStatuses != null) {
                        intent = new Intent(DeviceMainAct.this, DeviceTiming.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("primary_timing", PRINCIPAL);
                        bundle.putSerializable("electricityDeviceStatus", electricityDeviceStatuses.get(0));
                        intent.putExtras(bundle);
                        DeviceMainAct.this.startActivityForResult(intent, 0);
                    }else{
                        showToast("当前状态为空！");
                    }

                    break;
                case R.id.rly_subsidiary_timing:
                    if (electricityDeviceStatuses != null) {
                        intent = new Intent(DeviceMainAct.this, DeviceTiming.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("primary_timing", SUBSIDIARY);
                        bundle2.putSerializable("electricityDeviceStatus", electricityDeviceStatuses.get(0));
                        intent.putExtras(bundle2);
                        DeviceMainAct.this.startActivityForResult(intent, 0);
                    }else{
                        showToast("当前状态为空！");
                    }
                    break;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            // 判断主辅灯,更新当前主辅灯定时状态
            if (resultCode == 1) {
                mainSixSectionDimmerIntensity = data.getByteArrayExtra("timeData");

            } else if (resultCode == 2) {
                assistSixSectionDimmerIntensity = data.getByteArrayExtra("timeData");
            }


        }
    }




}