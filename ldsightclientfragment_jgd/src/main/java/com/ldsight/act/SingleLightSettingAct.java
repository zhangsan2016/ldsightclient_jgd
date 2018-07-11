package com.ldsight.act;

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
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ldsightclient_jgd.R;
import com.ldsight.application.MyApplication;
import com.ldsight.crc.CopyOfcheckCRC;
import com.ldsight.util.LogUtil;
import com.ldsight.util.StringUtil;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.text.DecimalFormat;
import java.util.Arrays;

public class SingleLightSettingAct extends Activity implements OnClickListener {
    public static String SINGLELIGHTSETTINGACT_RECEIVER = "SingleLightSettingActDataRefreshFilter"; // 广播接收者
    private TextView tv_deviceid;
    private RelativeLayout top;
    private LinearLayout llPrevDeviceMain;
    private RelativeLayout rlDeviceMainRefresh;
    private TextView tvDate;
    private LinearLayout llElectricParameter1;
    private TextView textView3;
    private TextView txtPsum;
    private LinearLayout llElectricParameter2;
    private TextView textView4;
    private TextView txtEnergy;
    private TextView textView5;
    private LinearLayout llElectricParameter4;
    private TextView txtVolt;
    private LinearLayout llElectricParameter5;
    private TextView txtBvolt;
    private TextView txtCvolt;
    private TextView textView2;
    private LinearLayout llElectricParameter7;
    private TextView txtAmpere;
    private LinearLayout llElectricParameter8;
    private TextView txtAmpereb;
    private TextView txtAmperec;
    private TextView tvElectricBoxState;
    private LinearLayout llElectricParameter10;
    private LinearLayout llElectricParameter11;
    private RelativeLayout rlyPrimaryTiming;
    private LinearLayout llSpacingStartTime1;
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private CheckBox cb6;
    private CheckBox cb7;
    private CheckBox cb8;
    // 定时
    private Button btSetting;
    // 校准服务器时间
    private Button btCalibrateTimeServer;
    private ProgressDialog mProgress;
    // 当前的设备uuid
    private byte[] deviceUuid;
    // 当前设备id
    private int deviceId;
    /**
     * ImageView全关全开控制
     */
    private ImageView iv_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.single_light_setting_activity);

        // 初始化View
        findViews();

        // 获取传递过来的参数
        initData();

        // 刷新
        refresh();

        // 注册广播
        initBroadCast();

    }

    private void initData() {

        // 当前设备uuid
        deviceUuid = (byte[]) getIntent().getByteArrayExtra("uuid");
        // 当前设备id
        deviceId = getIntent().getIntExtra("deviceId", 0);
        DecimalFormat df = new DecimalFormat("000");
        tv_deviceid.setText("设备号：" + df.format(deviceId));

    }

    /**
     * 动态注册广播
     */
    private void initBroadCast() {

        // 动态注册通知
        IntentFilter filter = new IntentFilter(SINGLELIGHTSETTINGACT_RECEIVER);
        registerReceiver(singleLightSettingActReceiver, filter);

    }


    private void findViews() {
        top = (RelativeLayout) findViewById(R.id.top);
        iv_control = (ImageView) this.findViewById(R.id.iv_control);
        llPrevDeviceMain = (LinearLayout) findViewById(R.id.ll_prev_device_main);
        rlDeviceMainRefresh = (RelativeLayout) findViewById(R.id.rl_device_main_refresh);
        tvDate = (TextView) findViewById(R.id.tv_date);
        llElectricParameter1 = (LinearLayout) findViewById(R.id.ll_electric_parameter1);
        textView3 = (TextView) findViewById(R.id.textView3);
        txtPsum = (TextView) findViewById(R.id.txt_psum);
        llElectricParameter2 = (LinearLayout) findViewById(R.id.ll_electric_parameter2);
        textView4 = (TextView) findViewById(R.id.textView4);
        txtEnergy = (TextView) findViewById(R.id.txt_energy);
        textView5 = (TextView) findViewById(R.id.textView5);
        llElectricParameter4 = (LinearLayout) findViewById(R.id.ll_electric_parameter4);
        txtVolt = (TextView) findViewById(R.id.txt_volt);
        llElectricParameter5 = (LinearLayout) findViewById(R.id.ll_electric_parameter5);
        txtBvolt = (TextView) findViewById(R.id.txt_Bvolt);
        txtCvolt = (TextView) findViewById(R.id.txt_Cvolt);
        textView2 = (TextView) findViewById(R.id.textView2);
        llElectricParameter7 = (LinearLayout) findViewById(R.id.ll_electric_parameter7);
        txtAmpere = (TextView) findViewById(R.id.txt_ampere);
        llElectricParameter8 = (LinearLayout) findViewById(R.id.ll_electric_parameter8);
        txtAmpereb = (TextView) findViewById(R.id.txt_ampereb);
        txtAmperec = (TextView) findViewById(R.id.txt_amperec);
        tvElectricBoxState = (TextView) findViewById(R.id.tv_electric_box_state);
        llElectricParameter10 = (LinearLayout) findViewById(R.id.ll_electric_parameter10);
        llElectricParameter11 = (LinearLayout) findViewById(R.id.ll_electric_parameter11);
        rlyPrimaryTiming = (RelativeLayout) findViewById(R.id.rly_primary_timing);
        llSpacingStartTime1 = (LinearLayout) findViewById(R.id.ll_spacing_start_time1);
        tv_deviceid = (TextView) findViewById(R.id.tv_deviceid);
        cb1 = (CheckBox) findViewById(R.id.cb_1);
        cb2 = (CheckBox) findViewById(R.id.cb_2);
        cb3 = (CheckBox) findViewById(R.id.cb_3);
        cb4 = (CheckBox) findViewById(R.id.cb_4);
        cb5 = (CheckBox) findViewById(R.id.cb_5);
        cb6 = (CheckBox) findViewById(R.id.cb_6);
        cb7 = (CheckBox) findViewById(R.id.cb_7);
        cb8 = (CheckBox) findViewById(R.id.cb_8);

        btSetting = (Button) findViewById(R.id.bt_setting);
        btCalibrateTimeServer = (Button) findViewById(R.id.btn_calibrate_time_server);
        btSetting.setOnClickListener(this);
        rlyPrimaryTiming.setOnClickListener(this);
        rlDeviceMainRefresh.setOnClickListener(this);
        btCalibrateTimeServer.setOnClickListener(this);
        llPrevDeviceMain.setOnClickListener(this);
        iv_control.setOnClickListener(this);

    }

    private void refresh() {

        new Thread() {
            public void run() {
                Pusher pusher = null;
                try {
                    // 显示加载提示框
                    showProgress();

                    // 获取电参指令数据
                    byte[] electricParameterData = getElectricParameterData();

                    // 获取系统时间指令数据
                    byte[] SystemTimeData = getSystemTimeData();

                    pusher = new Pusher(MyApplication.getIp(), 9966, 2000);

                    System.out.println("getElectricParameter crc后 = "
                            + Arrays.toString(electricParameterData));
                    pusher.push0x20Message(deviceUuid, electricParameterData);

                    sleep(1000);

                    System.out.println("SystemTimeData crc后 = "
                            + Arrays.toString(SystemTimeData));

                    pusher.push0x20Message(deviceUuid, SystemTimeData);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // 关闭加载提示框
                    myHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            stopProgress();
                        }
                    }, 2000);
                }
                // 关闭pusher
                if (pusher != null) {
                    try {
                        pusher.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

        }.start();

    }

    @Override
    public void onClick(View v) {

        if (v == btSetting) {

            // 单灯设置按钮
            singleLampSettingPush();

        } else if (v == rlyPrimaryTiming) {

            // 单灯定时
            Intent intent = new Intent(SingleLightSettingAct.this,
                    BrightenTiming.class);
            intent.putExtra("uuid", deviceUuid);
            intent.putExtra("deviceId", deviceId);
            startActivityForResult(intent, 0);

        } else if (v == rlDeviceMainRefresh) {

            // 刷新
            refresh();

        } else if (v == btCalibrateTimeServer) {

            // 校准服务器时间
            calibrateTimeServer();

        } else if (v == llPrevDeviceMain) {

            // 返回
            this.finish();
        } else if (v == iv_control) {

        }

    }

    /**
     * 显示对话框--全开、全关
     */
    private void showDlertDialog() {

        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleLightSettingAct.this);
        //    设置Title的图标
        // builder.setIcon(R.drawable.ic_launcher);
        //    设置Title的内容
        builder.setTitle("全控制");
        //    设置Content来显示一个信息
        builder.setMessage("开关指令");
        //    设置一个PositiveButton
        builder.setPositiveButton("全开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 光强度bit类型转换十进制代表八个设置状态
                StringBuffer sb = new StringBuffer();
                sb.append(11111111);
                singleLampSettingPush(sb);
                //  Toast.makeText(BrightenMain.this, "positive: " + which, Toast.LENGTH_SHORT).show();
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("全关", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 光强度bit类型转换十进制代表八个设置状态
                StringBuffer sb = new StringBuffer();
                sb.append(00000000);
                singleLampSettingPush(sb);

                //  Toast.makeText(BrightenMain.this, "negative: " + which, Toast.LENGTH_SHORT).show();
            }
        });

        //    显示出该对话框
        builder.show();
    }

    private void calibrateTimeServer() {

        new Thread() {
            public void run() {
                // 加载提示
                showProgress();

                Pusher pusher = null;

                try {
                    // 获取当前应用的uuid
                    byte[] uuidApp = MyApplication.getInstance().getAppUuid();

                    // 获取时间信息
                    Time t = new Time(); // or Time t=new Time("GMT+8");
                    t.setToNow(); // 取得系统时间。
                    int year = t.year;
                    int month = t.month;
                    int date = t.monthDay;
                    int hour = t.hour; // 0-23
                    int minute = t.minute;
                    int second = t.second;
                    int week = t.weekDay;
                    System.out.println(year + "年" + (month + 1) + "月" + date
                            + "日" + "星期" + (week) + "  " + hour + "时" + minute
                            + "分" + second + "秒");

                    byte[] data = new byte[17];
                    data[0] = -47; // 指令（0xD100）
                    data[1] = 0;
                    // data[2] = (byte) deviceId; // 设备地址
                    System.arraycopy(uuidApp, 0, data, 2, uuidApp.length); // 自身uuid
                    data[10] = (byte) (year % 100);
                    data[11] = (byte) (month + 1);
                    data[12] = (byte) date;
                    data[13] = (byte) week;
                    data[14] = (byte) hour;
                    data[15] = (byte) minute;
                    data[16] = (byte) second;

                    pusher = new Pusher(MyApplication.getIp(), 9966, 5000);

                    // crc校验
                    byte[] crc = CopyOfcheckCRC.crc(data);
                    System.out.println("校时 data = " + Arrays.toString(crc));
                    System.out.println("校时 uuidApp = "
                            + Arrays.toString(uuidApp));
                    pusher.push0x20Message(deviceUuid, crc);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pusher != null) {
                        try {
                            pusher.close();
                        } catch (Exception e) {
                        }
                    }

                    // 关闭加载提示框
                    myHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            stopProgress();
                        }
                    }, 4000);
                }
            }
        }.start();

    }

    private void singleLampSettingPush() {

        new Thread() {
            public void run() {
                // 加载提示
                showProgress();
                // 光强度bit类型转换十进制代表八个设置状态
                StringBuffer sb = new StringBuffer();
                sb.append(cb8.isChecked() ? 1 : 0);
                sb.append(cb7.isChecked() ? 1 : 0);
                sb.append(cb6.isChecked() ? 1 : 0);
                sb.append(cb5.isChecked() ? 1 : 0);
                sb.append(cb4.isChecked() ? 1 : 0);
                sb.append(cb3.isChecked() ? 1 : 0);
                sb.append(cb2.isChecked() ? 1 : 0);
                sb.append(cb1.isChecked() ? 1 : 0);
                byte luminance = StringUtil.bitToByte(sb.toString());

                Pusher pusher = null;
                // 获取当前应用的uuid
                byte[] uuidApp = MyApplication.getInstance().getAppUuid();
                try {
                    byte[] data = new byte[11];
                    data[0] = 65; // 指令（0X41）
                    data[1] = (byte) deviceId;
                    System.arraycopy(uuidApp, 0, data, 2, uuidApp.length); // 自身uuid
                    data[10] = luminance;

                    pusher = new Pusher(MyApplication.getIp(), 9966, 2000);

                    // crc校验
                    byte[] crc = CopyOfcheckCRC.crc(data);
                    System.out.println("控制 data = " + Arrays.toString(crc));
                    pusher.push0x20Message(deviceUuid, crc);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pusher != null) {
                        try {
                            pusher.close();
                        } catch (Exception e) {
                        }
                    }

                    // 关闭加载提示框
                    myHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            stopProgress();
                        }
                    }, 4000);
                }
            }
        }.start();

    }

    private void singleLampSettingPush(final StringBuffer start) {

        new Thread() {
            public void run() {
                // 加载提示
                showProgress();

                byte luminance = StringUtil.bitToByte(start.toString());

                Pusher pusher = null;
                // 获取当前应用的uuid
                byte[] uuidApp = MyApplication.getInstance().getAppUuid();
                try {
                    byte[] data = new byte[11];
                    data[0] = 65; // 指令（0X41）
                    data[1] = (byte) deviceId;
                    System.arraycopy(uuidApp, 0, data, 2, uuidApp.length); // 自身uuid
                    data[10] = luminance;

                    pusher = new Pusher(MyApplication.getIp(), 9966, 2000);

                    // crc校验
                    byte[] crc = CopyOfcheckCRC.crc(data);
                    System.out.println("控制 data = " + Arrays.toString(crc));
                    pusher.push0x20Message(deviceUuid, crc);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pusher != null) {
                        try {
                            pusher.close();
                        } catch (Exception e) {
                        }
                    }

                    // 关闭加载提示框
                    myHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            stopProgress();
                        }
                    }, 4000);
                }
            }
        }.start();

    }

    private byte[] getElectricParameterData() {

        // 获取当前应用的uuid
        byte[] appUuid = MyApplication.getAppUuid();

        byte[] data = new byte[10];
        data[0] = 82; // 指令(0x52)
        data[1] = (byte) deviceId; // 设备地址

        System.arraycopy(appUuid, 0, data, 2, appUuid.length);

        // crc校验
        byte[] crc = CopyOfcheckCRC.crc(data);

        return crc;

    }

    private byte[] getSystemTimeData() {

        // 获取当前应用的uuid
        byte[] appUuid = MyApplication.getAppUuid();

        byte[] data = new byte[10];
        data[0] = 84; // 指令(0x54)
        data[1] = (byte) deviceId; // 设备地址

        System.arraycopy(appUuid, 0, data, 2, appUuid.length);

        // crc校验
        byte[] crc = CopyOfcheckCRC.crc(data);

        return crc;

    }

    private BroadcastReceiver singleLightSettingActReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // 根据action过滤广播内容
            if (SINGLELIGHTSETTINGACT_RECEIVER.equals(intent.getAction())) {

                byte[] data = intent.getByteArrayExtra("data");

                Message msg = myHandler.obtainMessage();
                msg.what = 2;
                msg.obj = data;
                myHandler.sendMessage(msg);

            }

        }

    };

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case -1:

                    // 关闭进度提示框
                    if (mProgress != null) {
                        mProgress.dismiss();
                    }
                    break;

                case 0:

                    // 显示进度提示框
                    String text = (String) msg.obj;
                    mProgress = ProgressDialog.show(SingleLightSettingAct.this,
                            text, "Loading...", true, false);
                    break;
                case 1:

                    // 输出handler
                    String str = (String) msg.obj;
                    Toast.makeText(SingleLightSettingAct.this, str,
                            Toast.LENGTH_SHORT).show();
                    break;

                case 2:

                    // 接收拦截数据
                    // 获取data
                    byte[] data = (byte[]) msg.obj;

                    LogUtil.e("obj == " + Arrays.toString(data));

                    switch (data[13]) {
                        case 114:

                            // 读取电参返回
                            System.out.println(" 接收拦截数据  case 114 接收 data = "
                                    + Arrays.toString(data));
                            stopProgress();

                            // A相电压
                            int voltageA = StringUtil.bytesToInt2(Arrays.copyOfRange(
                                    data, 15, 17));
                            // B相电压
                            int voltageB = StringUtil.bytesToInt2(Arrays.copyOfRange(
                                    data, 17, 19));
                            // C相电压
                            int voltageC = StringUtil.bytesToInt2(Arrays.copyOfRange(
                                    data, 19, 21));

                            // A相电流
                            int electricityA = StringUtil.bytes2int(Arrays.copyOfRange(
                                    data, 21, 25));
                            // B相电流
                            int electricityB = StringUtil.bytes2int(Arrays.copyOfRange(
                                    data, 25, 29));
                            // C相电流
                            int electricityC = StringUtil.bytes2int(Arrays.copyOfRange(
                                    data, 29, 33));

                            // 总有功功率
                            int totalActivePower = StringUtil.bytes2int(Arrays
                                    .copyOfRange(data, 61, 65));

                            // 保留两位小数
                            DecimalFormat df = new DecimalFormat("######0.00");

                            // 显示数据
                            txtVolt.setText(df.format((float) voltageA / 100));
                            txtBvolt.setText(df.format((float) voltageB / 100));
                            txtCvolt.setText(df.format((float) voltageC / 100));

                            txtAmpere.setText(checkParameter(electricityA) + "");
                            txtAmpereb.setText(checkParameter(electricityB) + "");
                            txtAmperec.setText(checkParameter(electricityC) + "");

                            txtPsum.setText(checkParameter(totalActivePower) + "");


						/*	txtAmpere.setText(df.format((float)electricityA / 100) + "");
                            txtAmpereb.setText(df.format((float)electricityB / 100) + "");
							txtAmperec.setText(df.format((float)electricityC / 100) + "");

							txtPsum.setText(df.format((float)totalActivePower / 100) + "");*/

                            break;
                        case 116:

                            // 读系统时间返回
                            System.out.println(" 接收拦截数据  case 116 接收 data = "
                                    + Arrays.toString(data));

                            int year = data[15];
                            int month = data[16];
                            int date = data[17];
                            int hour = data[18];
                            int minute = data[19];
                            int second = data[20];

                            // 2016-08-13 16:12:27
                            tvDate.setText("20" + year + "-" + month + "-" + date + " "
                                    + hour + ":" + minute + ":" + second);

//							Toast.makeText(SingleLightSettingAct.this,"20" + year + "-" + month + "-" + date + " "
//									+ hour + ":" + minute + ":" + second,Toast.LENGTH_SHORT).show();

                            stopProgress();

                            break;

                        case -47:

                            stopProgress();
                            // 校时返回
                            if (data[14] == 0) {
                                Toast.makeText(SingleLightSettingAct.this, "校时成功！",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SingleLightSettingAct.this, "校时失败！",
                                        Toast.LENGTH_LONG).show();
                            }

                            break;

                    }

                    break;

            }

        }
    };

    /**
     * 检查参数是否大于1000
     *
     * @param parameter
     */
    private String checkParameter(int parameter) {
        StringBuffer bf = new StringBuffer();
        if (parameter > 100 || parameter < 0) {
            bf.append("参数异常");
        } else {
            bf.append(parameter);
        }

        return bf.toString();
    }

    private void showProgress() {
        myHandler.sendEmptyMessage(0);
    }

    private void stopProgress() {
        myHandler.sendEmptyMessage(-1);
        /*
         * if (mProgress != null) { mProgress.dismiss(); }
		 */
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(singleLightSettingActReceiver);
        super.onDestroy();

    }

}
