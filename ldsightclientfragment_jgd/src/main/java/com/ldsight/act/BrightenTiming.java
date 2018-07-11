package com.ldsight.act;

import android.app.Activity;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ldsightclient_jgd.R;
import com.ldsight.application.MyApplication;
import com.ldsight.crc.CopyOfcheckCRC;
import com.ldsight.util.StringUtil;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.util.Arrays;

public class BrightenTiming extends Activity {
    public static String BRIGHTEN_TIMING_RECELVER = "BRIGHTEN_TIMING_RECELVER";
    /**
     * 关闭定时
     */
    private ToggleButton tb_switch;
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

    // 标识
    private int advocateComplementaryCode;
    private ProgressDialog mProgress;

    private CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10, cb11,
            cb12, cb13, cb14, cb15, cb16, cb17, cb18, cb19, cb20, cb21, cb22,
            cb23, cb24, cb25, cb26, cb27, cb28, cb29, cb30, cb31, cb32, cb33,
            cb34, cb35, cb36, cb37, cb38, cb39, cb40, cb41, cb42, cb43, cb44,
            cb45, cb46, cb47, cb48;
    private int deviceId;

    // 当前uuid
    private byte[] uuid;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {

            byte[] data = (byte[]) msg.obj;

            switch (msg.what) {
                case -1:
                    if (mProgress != null) {
                        mProgress.dismiss();
                    }
                    break;

                case 0:
                    String text = (String) msg.obj;
                    mProgress = ProgressDialog.show(BrightenTiming.this, text,
                            "Loading...", true, false);
                    break;
                case 1:
                    String str = (String) msg.obj;
                    Toast.makeText(BrightenTiming.this, str, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                    // 返回定时时间
                    Intent intent1 = new Intent();
                    intent1.putExtra("timeData", timeData);
                    setResult(advocateComplementaryCode, intent1);
                    finish();
                    break;
                case 66:
                    // 单灯定时返回
                    if (data[15] == 0) {
                        Toast.makeText(BrightenTiming.this, "单灯定时成功！ID = " + data[14], Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BrightenTiming.this, "单灯定时失败！ID = " + data[14], Toast.LENGTH_SHORT).show();
                    }

                    break;
                case -62:

                    // 全定时返回
                    if (data[15] == 0) {
                        Toast.makeText(BrightenTiming.this, "定时成功！ ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BrightenTiming.this, "定时失败！", Toast.LENGTH_SHORT).show();
                    }

                    break;

            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.brighten_timing);
        // 动态注册通知
        IntentFilter filter = new IntentFilter(BrightenTiming.BRIGHTEN_TIMING_RECELVER);
        registerReceiver(MyReceiver, filter);

        initView();
        initListeners();
        initVariable();

    }

    private void initVariable() {

        uuid = (byte[]) getIntent().getByteArrayExtra("uuid");
        deviceId = getIntent().getIntExtra("deviceId", 0);

        // 初始化时间参数
        // setTime(timingTime);

        startTimePickerDialog = new TimePickerDialog(BrightenTiming.this,
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
                }, Integer.parseInt(txtStartTime.getText().toString().trim()
                .split(":")[0]), Integer.parseInt(txtStartTime
                .getText().toString().trim().split(":")[1]), true);

        endTimePickerDialog = new TimePickerDialog(BrightenTiming.this,
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
                }, Integer.parseInt(txtEndTime.getText().toString().trim()
                .split(":")[0]), Integer.parseInt(txtEndTime.getText()
                .toString().trim().split(":")[1]), true);

    }

    private void setTime(byte[] time) {
        txtStartTime.setText(time[0] + ":" + isLessTen(time[1]));
        txtEndTime.setText(time[15] + ":" + isLessTen(time[16]));

        tv_spacing_start_time1.setText(time[0] + ":" + time[1]);
        sb_brightness1.setProgress(time[2]);
        sb_brightness2.setProgress(time[5]);
        sb_brightness3.setProgress(time[8]);
        sb_brightness4.setProgress(time[11]);
        sb_brightness5.setProgress(time[14]);

        tv_spacing_start_time2.setText(time[3] + ":" + isLessTen(time[4]));
        tv_spacing_start_time3.setText(time[6] + ":" + isLessTen(time[7]));
        tv_spacing_start_time4.setText(time[9] + ":" + isLessTen(time[10]));
        tv_spacing_start_time5.setText(time[12] + ":" + isLessTen(time[13]));
        tv_spacing_start_time6.setText(time[15] + ":" + isLessTen(time[16]));
        // 六段不可点击
        sb_brightness6.setEnabled(false);
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
        // sb_brightness1
        // .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        // sb_brightness2
        // .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        // sb_brightness3
        // .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        // sb_brightness4
        // .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        // sb_brightness5
        // .setOnSeekBarChangeListener(new MySeekBarChangeListener());
        // sb_brightness6
        // .setOnSeekBarChangeListener(new MySeekBarChangeListener());

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


        tb_switch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

    }

    private class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            // 开启定时或关闭定时
            if (isChecked) {
                timeData = new byte[18];
                timeData[2] = 12;
                timeData[5] = 12;
                timeData[8] = 12;
                timeData[11] = 12;
                timeData[14] = 12;
                timeData[17] = 12;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 3; i++) {
                            try {
                                Thread.sleep(2000); // 让当前线程休眠两秒钟
                                pusher(timeData);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                }).start();

            } else {
                getTimingParameter();
            }

        }
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

        txtStartTime = (TextView) findViewById(R.id.txt_device_main_start_time);
        txtEndTime = (TextView) findViewById(R.id.txt_device_main_end_time);

        okDevice = (Button) this.findViewById(R.id.btn_ok_device_main);

        // 开始结束时间设置
        llStarttime = (LinearLayout) this
                .findViewById(R.id.ll_device_main_start_time);
        llEndTime = (LinearLayout) this
                .findViewById(R.id.ll_device_main_end_time);

        cb1 = (CheckBox) findViewById(R.id.cb_1);
        cb2 = (CheckBox) findViewById(R.id.cb_2);
        cb3 = (CheckBox) findViewById(R.id.cb_3);
        cb4 = (CheckBox) findViewById(R.id.cb_4);
        cb5 = (CheckBox) findViewById(R.id.cb_5);
        cb6 = (CheckBox) findViewById(R.id.cb_6);
        cb7 = (CheckBox) findViewById(R.id.cb_7);
        cb8 = (CheckBox) findViewById(R.id.cb_8);

        cb9 = (CheckBox) findViewById(R.id.cb_9);
        cb10 = (CheckBox) findViewById(R.id.cb_10);
        cb11 = (CheckBox) findViewById(R.id.cb_11);
        cb12 = (CheckBox) findViewById(R.id.cb_12);
        cb13 = (CheckBox) findViewById(R.id.cb_13);
        cb14 = (CheckBox) findViewById(R.id.cb_14);
        cb15 = (CheckBox) findViewById(R.id.cb_15);
        cb16 = (CheckBox) findViewById(R.id.cb_16);

        cb17 = (CheckBox) findViewById(R.id.cb_17);
        cb18 = (CheckBox) findViewById(R.id.cb_18);
        cb19 = (CheckBox) findViewById(R.id.cb_19);
        cb20 = (CheckBox) findViewById(R.id.cb_20);
        cb21 = (CheckBox) findViewById(R.id.cb_21);
        cb22 = (CheckBox) findViewById(R.id.cb_22);
        cb23 = (CheckBox) findViewById(R.id.cb_23);
        cb24 = (CheckBox) findViewById(R.id.cb_24);

        cb25 = (CheckBox) findViewById(R.id.cb_25);
        cb26 = (CheckBox) findViewById(R.id.cb_26);
        cb27 = (CheckBox) findViewById(R.id.cb_27);
        cb28 = (CheckBox) findViewById(R.id.cb_28);
        cb29 = (CheckBox) findViewById(R.id.cb_29);
        cb30 = (CheckBox) findViewById(R.id.cb_30);
        cb31 = (CheckBox) findViewById(R.id.cb_31);
        cb32 = (CheckBox) findViewById(R.id.cb_32);

        cb33 = (CheckBox) findViewById(R.id.cb_33);
        cb34 = (CheckBox) findViewById(R.id.cb_34);
        cb35 = (CheckBox) findViewById(R.id.cb_35);
        cb36 = (CheckBox) findViewById(R.id.cb_36);
        cb37 = (CheckBox) findViewById(R.id.cb_37);
        cb38 = (CheckBox) findViewById(R.id.cb_38);
        cb39 = (CheckBox) findViewById(R.id.cb_39);
        cb40 = (CheckBox) findViewById(R.id.cb_40);

        cb41 = (CheckBox) findViewById(R.id.cb_41);
        cb42 = (CheckBox) findViewById(R.id.cb_42);
        cb43 = (CheckBox) findViewById(R.id.cb_43);
        cb44 = (CheckBox) findViewById(R.id.cb_44);
        cb45 = (CheckBox) findViewById(R.id.cb_45);
        cb46 = (CheckBox) findViewById(R.id.cb_46);
        cb47 = (CheckBox) findViewById(R.id.cb_47);
        cb48 = (CheckBox) findViewById(R.id.cb_48);

        tb_switch = (ToggleButton) this.findViewById(R.id.tb_start_or_stop_switch);

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
                    // 设置六段定时
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
                    new TimePickerDialog(BrightenTiming.this,
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
                    new TimePickerDialog(BrightenTiming.this,
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
                    new TimePickerDialog(BrightenTiming.this,
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
                    new TimePickerDialog(BrightenTiming.this,
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
                    new TimePickerDialog(BrightenTiming.this,
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
                    new TimePickerDialog(BrightenTiming.this,
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
                    if (startTimePickerDialog != null) {
                        startTimePickerDialog.show();
                    }

                    break;
                case R.id.ll_device_main_end_time:

                    if (endTimePickerDialog != null) {
                        endTimePickerDialog.show();
                    }
                    break;
            }
        }

    }

    private byte[] timeData;

    private void getTimingParameter() {
        timeData = new byte[18];
        // 定时开始结束时间
        String[] txtStartTimeAry = txtStartTime.getText().toString().trim()
                .split(":");
        String[] txtEndTimeAry = txtEndTime.getText().toString().trim()
                .split(":");
        // 六段调光时间
        String[] timingTime1 = tv_spacing_start_time1.getText().toString()
                .trim().split(":");
        String[] timingTime2 = tv_spacing_start_time2.getText().toString()
                .trim().split(":");
        String[] timingTime3 = tv_spacing_start_time3.getText().toString()
                .trim().split(":");
        String[] timingTime4 = tv_spacing_start_time4.getText().toString()
                .trim().split(":");
        String[] timingTime5 = tv_spacing_start_time5.getText().toString()
                .trim().split(":");
        String[] timingTime6 = tv_spacing_start_time6.getText().toString()
                .trim().split(":");
        // 六段调光亮度
        /*
         * int progress1 = sb_brightness1.getProgress(); int progress2 =
		 * sb_brightness2.getProgress(); int progress3 =
		 * sb_brightness3.getProgress(); int progress4 =
		 * sb_brightness4.getProgress(); int progress5 =
		 * sb_brightness5.getProgress(); int progress6 =
		 * sb_brightness6.getProgress();
		 */

        // 封装到data数组

        timeData[0] = (byte) Integer.parseInt(timingTime1[0].trim());
        timeData[1] = (byte) Integer.parseInt(timingTime1[1].trim());
        timeData[2] = (byte) getStateStreetLamp(1);
        System.out.println("getStateStreetLamp = " + timeData[2]);

        timeData[3] = (byte) Integer.parseInt(timingTime2[0].trim());
        timeData[4] = (byte) Integer.parseInt(timingTime2[1].trim());
        timeData[5] = (byte) getStateStreetLamp(2);

        timeData[6] = (byte) Integer.parseInt(timingTime3[0].trim());
        timeData[7] = (byte) Integer.parseInt(timingTime3[1].trim());
        timeData[8] = (byte) getStateStreetLamp(3);

        timeData[9] = (byte) Integer.parseInt(timingTime4[0].trim());
        timeData[10] = (byte) Integer.parseInt(timingTime4[1].trim());
        timeData[11] = (byte) getStateStreetLamp(4);

        timeData[12] = (byte) Integer.parseInt(timingTime5[0].trim());
        timeData[13] = (byte) Integer.parseInt(timingTime5[1].trim());
        timeData[14] = (byte) getStateStreetLamp(5);

        timeData[15] = (byte) Integer.parseInt(timingTime6[0].trim());
        timeData[16] = (byte) Integer.parseInt(timingTime6[1].trim());
        timeData[17] = (byte) getStateStreetLamp(6);

		/*
         * Intent intent = new Intent(); intent.putExtra("parameter", data);
		 * setResult(advocateComplementaryCode, intent);
		 */

        // 推送定时到下位机
        pusher(timeData);

    }

    ;

    /**
     * 根据位置获取定时状态
     *
     * @param location
     * @return
     */
    private byte getStateStreetLamp(int location) {

        StringBuffer sb = new StringBuffer();
        switch (location) {
            case 1:

                sb.append(cb8.isChecked() ? 1 : 0);
                sb.append(cb7.isChecked() ? 1 : 0);
                sb.append(cb6.isChecked() ? 1 : 0);
                sb.append(cb5.isChecked() ? 1 : 0);
                sb.append(cb4.isChecked() ? 1 : 0);
                sb.append(cb3.isChecked() ? 1 : 0);
                sb.append(cb2.isChecked() ? 1 : 0);
                sb.append(cb1.isChecked() ? 1 : 0);

                break;
            case 2:

                sb.append(cb16.isChecked() ? 1 : 0);
                sb.append(cb15.isChecked() ? 1 : 0);
                sb.append(cb14.isChecked() ? 1 : 0);
                sb.append(cb13.isChecked() ? 1 : 0);
                sb.append(cb12.isChecked() ? 1 : 0);
                sb.append(cb11.isChecked() ? 1 : 0);
                sb.append(cb10.isChecked() ? 1 : 0);
                sb.append(cb9.isChecked() ? 1 : 0);

                break;
            case 3:

                sb.append(cb24.isChecked() ? 1 : 0);
                sb.append(cb23.isChecked() ? 1 : 0);
                sb.append(cb22.isChecked() ? 1 : 0);
                sb.append(cb21.isChecked() ? 1 : 0);
                sb.append(cb20.isChecked() ? 1 : 0);
                sb.append(cb19.isChecked() ? 1 : 0);
                sb.append(cb18.isChecked() ? 1 : 0);
                sb.append(cb17.isChecked() ? 1 : 0);

                break;
            case 4:

                sb.append(cb32.isChecked() ? 1 : 0);
                sb.append(cb31.isChecked() ? 1 : 0);
                sb.append(cb30.isChecked() ? 1 : 0);
                sb.append(cb29.isChecked() ? 1 : 0);
                sb.append(cb28.isChecked() ? 1 : 0);
                sb.append(cb27.isChecked() ? 1 : 0);
                sb.append(cb26.isChecked() ? 1 : 0);
                sb.append(cb25.isChecked() ? 1 : 0);

                break;
            case 5:

                sb.append(cb40.isChecked() ? 1 : 0);
                sb.append(cb39.isChecked() ? 1 : 0);
                sb.append(cb38.isChecked() ? 1 : 0);
                sb.append(cb37.isChecked() ? 1 : 0);
                sb.append(cb36.isChecked() ? 1 : 0);
                sb.append(cb35.isChecked() ? 1 : 0);
                sb.append(cb34.isChecked() ? 1 : 0);
                sb.append(cb33.isChecked() ? 1 : 0);

                break;
            case 6:

                sb.append(cb48.isChecked() ? 1 : 0);
                sb.append(cb47.isChecked() ? 1 : 0);
                sb.append(cb46.isChecked() ? 1 : 0);
                sb.append(cb45.isChecked() ? 1 : 0);
                sb.append(cb44.isChecked() ? 1 : 0);
                sb.append(cb43.isChecked() ? 1 : 0);
                sb.append(cb42.isChecked() ? 1 : 0);
                sb.append(cb41.isChecked() ? 1 : 0);

                break;

        }

        System.out.println("sb = " + sb.toString());
        return StringUtil.bitToByte(sb.toString());
    }

    private void pusher(final byte[] timing) {
        new Thread() {
            public void run() {
                Pusher pusher = null;
                // 获取当前应用的uuid
                byte[] uuidApp = MyApplication.getInstance().getAppUuid();
                try {
                    byte[] data = new byte[30];

                    // 判断单灯定时或全定时(deviceId == 0表示全定时)
                    if (deviceId != 0) {
                        // 单灯定时
                        data[0] = 66; // 指令

                    } else {
                        data[0] = -62; // 指令
                    }
                    data[1] = (byte) deviceId; // 设备地址
                    System.arraycopy(uuidApp, 0, data, 2, uuidApp.length); // 自身uuid
                    System.arraycopy(timing, 0, data, 10, timing.length); // 定时时间

                    pusher = new Pusher(MyApplication.getIp(), 9966, 5000);

                    // crc校验
                    byte[] crc = CopyOfcheckCRC.crc(data);
                    System.out.println("定时crc = " + Arrays.toString(crc));
                    pusher.push0x20Message(uuid, crc);

                    myHandler.sendEmptyMessageDelayed(-1, 4000);

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

            // 根据action过滤广播内容
            if (BRIGHTEN_TIMING_RECELVER.equals(intent.getAction())) {

                byte[] data = intent.getByteArrayExtra("data");

                Message msg = myHandler.obtainMessage();
                msg.what = data[13];
                msg.obj = data;
                myHandler.sendMessage(msg);

            }
        }
    };

    private void showProgress() {
        myHandler.sendEmptyMessage(0);
    }

    private void stopProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 注销接收者
        unregisterReceiver(MyReceiver);
        if (mProgress != null) {
            mProgress.dismiss();
        }

    }

}
