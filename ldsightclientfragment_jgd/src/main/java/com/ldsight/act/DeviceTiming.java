package com.ldsight.act;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ldsightclient_jgd.R;
import com.google.gson.Gson;
import com.ldsight.application.MyApplication;
import com.ldsight.base.BaseActivity;
import com.ldsight.entity.ElectricityDeviceStatus;
import com.ldsight.entity.zkyjson.MasterTimingJson;
import com.ldsight.entity.zkyjson.SubsidiaryTimingJson;
import com.ldsight.service.ZkyOnlineService;
import com.ldsight.util.HttpConfiguration;
import com.ldsight.util.HttpUtil;
import com.ldsight.util.LogUtil;
import com.ldsight.util.StringUtil;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.ldsightclient_jgd.R.id.txt_device_main_start_time;

public class DeviceTiming extends BaseActivity {
    public static String receiver = "DEVICE_TIMING_RECELVER";
    // 定时开始结束时间
    TextView txtStartTime, txtEndTime;
    // 开始时间小时分钟
    private int startHour, startMinute, endHour, endMinute;
    // 开始时间结束时间字符串
    private String strStartTime = "";
    private String strEndTime = "";
    // 定时开始结束时间设置按钮
    private LinearLayout llStarttime, llEndTime;
    private TimePickerDialog startTimePickerDialog, endTimePickerDialog;
    private TextView tv_spacing_start_time1, tv_spacing_start_time2,
            tv_spacing_start_time3, tv_spacing_start_time4,
            tv_spacing_start_time5, tv_spacing_start_time6;
    // 确认按钮
    private Button okDevice;
    // 进度百分比
    private TextView tv_progress1, tv_progress2, tv_progress3, tv_progress4,
            tv_progress5, tv_progress6;
    private LinearLayout ll_spacing_start_time1, ll_spacing_start_time2,
            ll_spacing_start_time3, ll_spacing_start_time4,
            ll_spacing_start_time5, ll_spacing_start_time6;
    // 进度条
    private SeekBar sb_brightness1, sb_brightness2, sb_brightness3,
            sb_brightness4, sb_brightness5, sb_brightness6;

    // 主辅灯标识
    private int advocateComplementaryCode;
    private ProgressDialog mProgress;
    // 定时数据返回标识
    private boolean timingToken;

    // 当前uuid
    private byte[] uuid;
    // 传递过来的电参
    private ElectricityDeviceStatus electricityDeviceStatus;
    // 开关灯的小时分钟
    private String startTimeH, startTimeM, endTimeH, endTimeM;
    // 其他四段段调光
    private String timeTwoH,timeTwoM,timeThirH,timeThirM,timeFourH,timeFourM,timeFifH,timeFifM,timeSixH,timeSixM;
    // 亮度
    private int brightness1,brightness2,brightness3,brightness4,brightness5;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 判断定时是否成功
                    if (!timingToken) {
                        Toast.makeText(DeviceTiming.this, "定时失败!", Toast.LENGTH_SHORT).show();
                        stopProgress();
                    }
                    break;
                case 2:
                    // 返回定时时间
                    Intent intent1 = new Intent();
                    intent1.putExtra("timeData", timeData);
                    setResult(advocateComplementaryCode, intent1);
                    finish();
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.device_timing);
        // 动态注册通知
        IntentFilter filter = new IntentFilter(DeviceTiming.receiver);
        registerReceiver(MyReceiver, filter);

        initView();
        initListeners();
        initVariable();

    }

    private void initVariable() {

        // 获取传递的参数
        // 获取传递过来的参数
        Bundle bundle = getIntent().getExtras();
        advocateComplementaryCode = (Integer) getIntent().getSerializableExtra(
                "primary_timing");
        electricityDeviceStatus = (ElectricityDeviceStatus) bundle.getSerializable("electricityDeviceStatus");

        // 初始化开关灯时间
        initTime();

        // 初始化时间参数
        setTime();

        // 初始化开始时间和结束时间弹窗
        startTimeOrendTimeDialog();


    }

    private void initTime() {

        // 开关灯时间
        if (electricityDeviceStatus == null) {
            return ;
        }else {
            // 判断设置主灯还是辅灯
          if(advocateComplementaryCode == DeviceMainAct.PRINCIPAL){

              // 主灯获取开始时间和结束时间
              String[] startTimeArrays = electricityDeviceStatus.getFir_tt_Fir().split(":");
              startTimeH = String.format("%02d", Integer.parseInt(startTimeArrays[0]));
              startTimeM = String.format("%02d", Integer.parseInt(startTimeArrays[1]));
              brightness1 = Integer.parseInt(electricityDeviceStatus.getFir_tp_Fir());

              String[] endTimeArrays = electricityDeviceStatus.getSix_tt_Fir().split(":");
              endTimeH = String.format("%02d", Integer.parseInt(endTimeArrays[0]));
              endTimeM = String.format("%02d", Integer.parseInt(endTimeArrays[1]));
              brightness2 = Integer.parseInt( electricityDeviceStatus.getSec_tp_Fir());

              //  五段
              String[] timeTwoArrays = electricityDeviceStatus.getSec_tt_Fir().split(":");
              timeTwoH = String.format("%02d", Integer.parseInt(timeTwoArrays[0]));
              timeTwoM = String.format("%02d", Integer.parseInt(timeTwoArrays[1]));
              brightness3 = Integer.parseInt(electricityDeviceStatus.getThir_tp_Fir());

              String[] timeThirArrays = electricityDeviceStatus.getThir_tt_Fir().split(":");
              timeThirH = String.format("%02d", Integer.parseInt(timeThirArrays[0]));
              timeThirM = String.format("%02d", Integer.parseInt(timeThirArrays[1]));
              brightness4 = Integer.parseInt(electricityDeviceStatus.getFour_tp_Fir());

              String[] timeFourArrays = electricityDeviceStatus.getFour_tt_Fir().split(":");
              timeFourH = String.format("%02d", Integer.parseInt(timeFourArrays[0]));
              timeFourM = String.format("%02d", Integer.parseInt(timeFourArrays[1]));
              brightness5 = Integer.parseInt(electricityDeviceStatus.getFif_tp_Fir());

              String[] timeFifArrays = electricityDeviceStatus.getFif_tt_Fir().split(":");
              timeFifH = String.format("%02d", Integer.parseInt(timeFifArrays[0]));
              timeFifM = String.format("%02d", Integer.parseInt(timeFifArrays[1]));

              String[] timeSixArrays = electricityDeviceStatus.getSix_tt_Fir().split(":");
              timeSixH = String.format("%02d", Integer.parseInt(timeSixArrays[0]));
              timeSixM = String.format("%02d", Integer.parseInt(timeSixArrays[1]));



          }else if(advocateComplementaryCode == DeviceMainAct.SUBSIDIARY){

              // 辅灯获取开始时间和结束时间
              String[] startTimeArrays = electricityDeviceStatus.getFir_tt_Sec().split(":");
              startTimeH = String.format("%02d", Integer.parseInt(startTimeArrays[0]));
              startTimeM = String.format("%02d", Integer.parseInt(startTimeArrays[1]));
              brightness1 = Integer.parseInt(electricityDeviceStatus.getFir_tp_Sec());

              String[] endTimeArrays = electricityDeviceStatus.getSix_tt_Sec().split(":");
              endTimeH = String.format("%02d", Integer.parseInt(endTimeArrays[0]));
              endTimeM = String.format("%02d", Integer.parseInt(endTimeArrays[1]));
              brightness2 = Integer.parseInt( electricityDeviceStatus.getSec_tp_Sec());

              // 五段
              String[] timeTwoArrays = electricityDeviceStatus.getSec_tt_Sec().split(":");
              timeTwoH = String.format("%02d", Integer.parseInt(timeTwoArrays[0]));
              timeTwoM = String.format("%02d", Integer.parseInt(timeTwoArrays[1]));
              brightness3 = Integer.parseInt(electricityDeviceStatus.getThir_tp_Sec());

              String[] timeThirArrays = electricityDeviceStatus.getThir_tt_Sec().split(":");
              timeThirH = String.format("%02d", Integer.parseInt(timeThirArrays[0]));
              timeThirM = String.format("%02d", Integer.parseInt(timeThirArrays[1]));
              brightness4 = Integer.parseInt(electricityDeviceStatus.getFour_tp_Sec());

              String[] timeFourArrays = electricityDeviceStatus.getFour_tt_Sec().split(":");
              timeFourH = String.format("%02d", Integer.parseInt(timeFourArrays[0]));
              timeFourM = String.format("%02d", Integer.parseInt(timeFourArrays[1]));
              brightness5 = Integer.parseInt(electricityDeviceStatus.getFif_tp_Sec());

              String[] timeFifArrays = electricityDeviceStatus.getFif_tt_Sec().split(":");
              timeFifH = String.format("%02d", Integer.parseInt(timeFifArrays[0]));
              timeFifM = String.format("%02d", Integer.parseInt(timeFifArrays[1]));

              String[] timeSixArrays = electricityDeviceStatus.getSix_tt_Sec().split(":");
              timeSixH = String.format("%02d", Integer.parseInt(timeSixArrays[0]));
              timeSixM = String.format("%02d", Integer.parseInt(timeSixArrays[1]));

          }
        }
    }

    private void startTimeOrendTimeDialog() {

        startTimePickerDialog = new TimePickerDialog(DeviceTiming.this,
                new OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String strStartHour = "" + hourOfDay;
                        String strStartMinute = "" + minute;

                        if (hourOfDay / 10 <= 0) {
                            strStartHour = "0" + strStartHour;
                        }
                        if (minute / 10 <= 0) {
                            strStartMinute = "0" + strStartMinute;
                        }
                        startHour = hourOfDay;
                        startMinute = minute;
                        strStartTime = strStartHour + " : " + strStartMinute;
                        txtStartTime.setText(strStartTime);
                        tv_spacing_start_time1.setText(strStartTime);
                    }
                }, Integer.parseInt(startTimeH), Integer.parseInt(startTimeM), true);

        endTimePickerDialog = new TimePickerDialog(DeviceTiming.this,
                new OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
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
                        txtEndTime.setText(strEndTime);
                        tv_spacing_start_time6.setText(strEndTime);

                    }
                }, Integer.parseInt(endTimeH), Integer.parseInt(endTimeM), true);
    }

    private void setTime() {

        // 开关灯时间
        txtStartTime.setText(startTimeH + ":" + startTimeM);
        txtEndTime.setText(endTimeH + ":" + endTimeM);

        // 设置六段亮度
        sb_brightness1.setProgress(brightness1);
        sb_brightness2.setProgress(brightness2);
        sb_brightness3.setProgress(brightness3);
        sb_brightness4.setProgress(brightness4);
        sb_brightness5.setProgress(brightness5);

        // 设置六段定时时间
        tv_spacing_start_time1.setText(startTimeH + ":" + startTimeM);
        tv_spacing_start_time2.setText(timeTwoH + ":" + timeTwoM);
        tv_spacing_start_time3.setText(timeFifH + ":" +  timeFifM);
        tv_spacing_start_time4.setText(timeFourH + ":" + timeFourM);
        tv_spacing_start_time5.setText(timeFifH + ":" + timeFifM);
        tv_spacing_start_time6.setText(timeSixH + ":" + timeSixM);

        // 六段不可点击
        sb_brightness6.setEnabled(false);

	/*	tv_spacing_start_time1.setText(time[0] + ":" + time[1]);
        sb_brightness1.setProgress(time[2]);
		sb_brightness2.setProgress(time[5]);
		sb_brightness3.setProgress(time[8]);
		sb_brightness4.setProgress(time[11]);
		sb_brightness5.setProgress(time[14]);

		tv_spacing_start_time2.setText(time[3] + ":" + isLessTen(time[4]));
		tv_spacing_start_time3.setText(time[6] + ":" + isLessTen(time[7]) );
		tv_spacing_start_time4.setText(time[9] + ":" + isLessTen(time[10]));
		tv_spacing_start_time5.setText(time[12] + ":" + isLessTen(time[13]));
		tv_spacing_start_time6.setText(time[15] + ":" + isLessTen(time[16]));
		// 六段不可点击
		sb_brightness6.setEnabled(false);*/
    }

    /**
     * 小于10的数字后面加0
     *
     * @return
     */
    private String isLessTen(byte time) {
        String timeStr = Integer.toString(time);
        if (time < 10) {
            timeStr = "0" + time;
        }
        return timeStr;
    }

    private void initListeners() {
        // 进度更新
        sb_brightness1
                .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        sb_brightness2
                .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        sb_brightness3
                .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        sb_brightness4
                .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        sb_brightness5
                .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        sb_brightness6
                .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        // 六段时间设置
        ll_spacing_start_time1
                .setOnClickListener(new MyOnclickCancelListener());
        ll_spacing_start_time2
                .setOnClickListener(new MyOnclickCancelListener());
        ll_spacing_start_time3
                .setOnClickListener(new MyOnclickCancelListener());
        ll_spacing_start_time4
                .setOnClickListener(new MyOnclickCancelListener());
        ll_spacing_start_time5
                .setOnClickListener(new MyOnclickCancelListener());
        ll_spacing_start_time6
                .setOnClickListener(new MyOnclickCancelListener());
        // 开始结束时间设置
        llStarttime.setOnClickListener(new MyOnclickCancelListener());
        llEndTime.setOnClickListener(new MyOnclickCancelListener());
        // 设置
        okDevice.setOnClickListener(new MyOnclickCancelListener());

    }

    private void initView() {
        // 六个阶段的时间LinearLayout
        ll_spacing_start_time1 = (LinearLayout) this
                .findViewById(R.id.ll_spacing_start_time1);
        ll_spacing_start_time2 = (LinearLayout) this
                .findViewById(R.id.ll_spacing_start_time2);
        ll_spacing_start_time3 = (LinearLayout) this
                .findViewById(R.id.ll_spacing_start_time3);
        ll_spacing_start_time4 = (LinearLayout) this
                .findViewById(R.id.ll_spacing_start_time4);
        ll_spacing_start_time5 = (LinearLayout) this
                .findViewById(R.id.ll_spacing_start_time5);
        ll_spacing_start_time6 = (LinearLayout) this
                .findViewById(R.id.ll_spacing_start_time6);

        sb_brightness1 = (SeekBar) this.findViewById(R.id.sb_brightness1);
        sb_brightness2 = (SeekBar) this.findViewById(R.id.sb_brightness2);
        sb_brightness3 = (SeekBar) this.findViewById(R.id.sb_brightness3);
        sb_brightness4 = (SeekBar) this.findViewById(R.id.sb_brightness4);
        sb_brightness5 = (SeekBar) this.findViewById(R.id.sb_brightness5);
        sb_brightness6 = (SeekBar) this.findViewById(R.id.sb_brightness6);

        // 进度条界面参数初始化
        tv_progress1 = (TextView) this.findViewById(R.id.tv_progress1);
        tv_progress2 = (TextView) this.findViewById(R.id.tv_progress2);
        tv_progress3 = (TextView) this.findViewById(R.id.tv_progress3);
        tv_progress4 = (TextView) this.findViewById(R.id.tv_progress4);
        tv_progress5 = (TextView) this.findViewById(R.id.tv_progress5);
        tv_progress6 = (TextView) this.findViewById(R.id.tv_progress6);

        // 六个阶段的textView
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

        txtStartTime = (TextView) findViewById(txt_device_main_start_time);
        txtEndTime = (TextView) findViewById(R.id.txt_device_main_end_time);

        okDevice = (Button) this.findViewById(R.id.btn_ok_device_main);

        // 开始结束时间设置
        llStarttime = (LinearLayout) this
                .findViewById(R.id.ll_device_main_start_time);
        llEndTime = (LinearLayout) this
                .findViewById(R.id.ll_device_main_end_time);

    }

    private class MySeekBarChangeListener implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.sb_brightness1:
                    tv_progress1.setText(progress + "%");
                    break;
                case R.id.sb_brightness2:
                    tv_progress2.setText(progress + "%");
                    break;
                case R.id.sb_brightness3:
                    tv_progress3.setText(progress + "%");
                    break;
                case R.id.sb_brightness4:
                    tv_progress4.setText(progress + "%");
                    break;
                case R.id.sb_brightness5:
                    tv_progress5.setText(progress + "%");
                    break;
                case R.id.sb_brightness6:
                    System.out.println("sb_brightness6");
                    tv_progress6.setText(progress + "%");
                    break;

            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }

    private class MyOnclickCancelListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.btn_ok_device_main:
                    showProgress();
                    new Thread() {
                        public void run() {
                            getTimingParameter();
                            try {
                                sleep(5000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            // finish();

                        }

                    }.start();

                    break;
                case R.id.ll_spacing_start_time1:
                    new TimePickerDialog(DeviceTiming.this,
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
                    new TimePickerDialog(DeviceTiming.this,
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
                    new TimePickerDialog(DeviceTiming.this,
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
                    new TimePickerDialog(DeviceTiming.this,
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
                    new TimePickerDialog(DeviceTiming.this,
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
                    new TimePickerDialog(DeviceTiming.this,
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
                case R.id.ll_device_main_start_time:
                    startTimePickerDialog.show();
                    break;
                case R.id.ll_device_main_end_time:
                    endTimePickerDialog.show();
                    break;
            }
        }

    }

    private byte[] timeData;

    private void getTimingParameter() {

        // 定时开始结束时间
     /*   String txtStartTimeAry = txtStartTime.getText().toString().trim();
        String txtEndTimeAry = txtEndTime.getText().toString().trim();*/

        // 六段调光时间
        String timingTime1 = tv_spacing_start_time1.getText().toString().replaceAll(" ","");
        String timingTime2 = tv_spacing_start_time2.getText().toString().replaceAll(" ","");
        String timingTime3 = tv_spacing_start_time3.getText().toString().replaceAll(" ","");
        String timingTime4 = tv_spacing_start_time4.getText().toString().replaceAll(" ","");
        String timingTime5 = tv_spacing_start_time5.getText().toString().replaceAll(" ","");
        String timingTime6 = tv_spacing_start_time6.getText().toString().replaceAll(" ","");

        // 六段调光亮度
        int progress1 = sb_brightness1.getProgress();
        int progress2 = sb_brightness2.getProgress();
        int progress3 = sb_brightness3.getProgress();
        int progress4 = sb_brightness4.getProgress();
        int progress5 = sb_brightness5.getProgress();
        int progress6 = sb_brightness6.getProgress();



        // 创建json指令
        Gson gson = new Gson();
        String jsonStr ="";
        // 判断主辅灯
        if(advocateComplementaryCode == DeviceMainAct.PRINCIPAL){

            // 主灯定时指令
            MasterTimingJson zkyJson = new MasterTimingJson();
            zkyJson.setConfirm(3);

            zkyJson.setFir_tt_Fir(timingTime1);
            zkyJson.setFir_tp_Fir(progress1);
            zkyJson.setSec_tt_Fir(timingTime2);
            zkyJson.setSec_tp_Fir(progress2);
            zkyJson.setThir_tt_Fir(timingTime3);
            zkyJson.setThir_tp_Fir(progress3);
            zkyJson.setFour_tt_Fir(timingTime4);
            zkyJson.setFour_tp_Fir(progress4);
            zkyJson.setFif_tt_Fir(timingTime5);
            zkyJson.setFif_tp_Fir(progress5 );
            zkyJson.setSix_tt_Fir(timingTime6);
            zkyJson.setSix_tp_Fir(progress6);
            jsonStr = gson.toJson(zkyJson) + "#";

        }else if(advocateComplementaryCode == DeviceMainAct.SUBSIDIARY){
            // 辅灯定时指令
            SubsidiaryTimingJson zkyJson = new SubsidiaryTimingJson();
            zkyJson.setConfirm(7);

            zkyJson.setFir_tt_Sec(timingTime1);
            zkyJson.setFir_tp_Sec(progress1);
            zkyJson.setSec_tt_Sec(timingTime2);
            zkyJson.setSec_tp_Sec(progress2);
            zkyJson.setThir_tt_Sec(timingTime3);
            zkyJson.setThir_tp_Sec(progress3);
            zkyJson.setFour_tt_Sec(timingTime4);
            zkyJson.setFour_tp_Sec(progress4);
            zkyJson.setFif_tt_Sec(timingTime5);
            zkyJson.setFif_tp_Sec(progress5);
            zkyJson.setSix_tt_Sec(timingTime6);
            zkyJson.setSix_tp_Sec(progress6);
            jsonStr = gson.toJson(zkyJson) + "#";

        }
        LogUtil.e("xxx jsonStr = " + jsonStr);

        jsonStr  = StringUtil.stringToHexString(jsonStr, ZkyOnlineService.heartbeatStatis.getData().getBKey());
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
                Log.e("xxx", "定时指令发送失败！" + e.toString());
                showToast("定时指令发送失败！");
                stopProgress();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String json = response.body().string();

                Log.e("xxx", "定时指令发送成功！" + json);

                showToast("定时指令发送成功！");

                stopProgress();

            }


        }, requestBody);

        // 推送定时到下位机
        pusher(timeData);

    }


    private void pusher(final byte[] timing) {
        new Thread() {
            public void run() {
                Pusher pusher = null;
                // 获取当前应用的uuid
                byte[] uuidApp = MyApplication.getInstance().getAppUuid();
                try {
                    byte[] data = new byte[29];
                    data[0] = -62; // 指令
                    data[1] = 0; // 设备地址
                    System.arraycopy(uuidApp, 0, data, 2, uuidApp.length); // uuid
                    System.arraycopy(timing, 0, data, 10, timing.length); // 定时时间
                    data[28] = (byte) advocateComplementaryCode; // 设备号  // 1主灯2辅灯0全部

                    pusher = new Pusher(MyApplication.getIp(), 9966, 5000);

                    System.out.println("data = " + Arrays.toString(data));
                    pusher.push0x20Message(uuid, data);
                    timingToken = false;
                    myHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            myHandler.sendEmptyMessage(1);
                        }
                    }, 5000);
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
        }.start();

    }

    /**
     * 广播接收
     */
    private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra("data");
            if (data[13] == -62) {
                if (data[15] == 0) {
                    String msg = "";
                    if (data[16] == 1) {
                        msg = "主灯定时设置成功！";
                    } else {
                        msg = "辅灯定时设置成功！";
                    }
                    Toast.makeText(DeviceTiming.this, msg, Toast.LENGTH_SHORT).show();
                    timingToken = true;
                    stopProgress();


                    myHandler.sendEmptyMessage(2);
                }
            }


        }
    };

/*    private void showProgress() {
        mProgress = ProgressDialog.show(this, "", "Loading...", true, false);
    }

    private void stopProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }*/

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 关闭接收者
        unregisterReceiver(MyReceiver);

    }

}
