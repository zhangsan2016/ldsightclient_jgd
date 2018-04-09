package com.ldgd.em.act;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

import com.ldgd.em.R;
import com.ldgd.em.funsdk.support.DeviceActivitys;
import com.ldgd.em.funsdk.support.FunDevicePassword;
import com.ldgd.em.funsdk.support.FunSupport;
import com.ldgd.em.funsdk.support.OnFunDeviceListener;
import com.ldgd.em.funsdk.support.models.FunDevStatus;
import com.ldgd.em.funsdk.support.models.FunDevType;
import com.ldgd.em.funsdk.support.models.FunDevice;
import com.ldgd.em.funsdk.support.models.FunLoginType;
import com.lib.FunSDK;

/**
 * Created by lenovo on 2017/3/11.
 */

public class SNLogin extends ActivityDemo implements OnFunDeviceListener {
    private final int MESSAGE_DELAY_FINISH = 0x100;

    private FunDevice mFunDevice = null;
    private String mCurrDevSn = null;
    private FunDevType mCurrDevType = null;

    // 定义当前支持通过序列号登录的设备类型
    // 如果是设备类型特定的话,固定一个就可以了
    private final FunDevType[] mSupportDevTypes = { FunDevType.EE_DEV_NORMAL_MONITOR,
            FunDevType.EE_DEV_INTELLIGENTSOCKET, FunDevType.EE_DEV_SMALLEYE };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sn_login);

        mCurrDevType = mSupportDevTypes[0];

        // 设置登录方式为本地登录
        FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);
        // 监听设备类事件
        FunSupport.getInstance().registerOnFunDeviceListener(this);



    }

    public void login(View view){
        requestDeviceStatus(null);
    }
    public void startMap(View view){

//        Intent intent = new Intent(this,BaiduMapAct.class);
//        startActivity(intent);

        Intent intent = new Intent(this,DeviceList.class);
       startActivity(intent);
    }


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DELAY_FINISH: {
                    hideWaitDialog();

                    // 启动/打开设备操作界面
                    if (null != mFunDevice) {

                        // 传入用户名/密码
                        mFunDevice.loginName = "";
                        if (mFunDevice.loginName.length() == 0) {
                            // 用户名默认是:admin
                            mFunDevice.loginName = "admin";
                        }
                        mFunDevice.loginPsw = "1234567";

                        //Save the password to local file
                        FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(),  mFunDevice.loginPsw);
                        FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin",  mFunDevice.loginPsw);

                        mFunDevice.devType = FunDevType.EE_DEV_NORMAL_MONITOR;
                        DeviceActivitys.startDeviceActivity(SNLogin.this, mFunDevice);
                    }

                    mFunDevice = null;
                    finish();
                }
                break;
            }
        }

    };



    // 设备登录
    private void requestDeviceStatus(String SN) {
        String devSN = "8caf1f62f6795bec";

        if (devSN.length() == 0) {
            showToast(R.string.device_login_error_sn);
            return;
        }

        mFunDevice = null;
        mCurrDevSn = devSN;

        showWaitDialog(R.string.device_stauts_unknown);

        FunSupport.getInstance().requestDeviceStatus(mCurrDevType, devSN);
    }



    @Override
    protected void onDestroy() {

        // 注销设备事件监听
        FunSupport.getInstance().removeOnFunDeviceListener(this);

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }


    @Override
    public void onDeviceListChanged() {

    }

    @Override
    public void onDeviceStatusChanged(FunDevice funDevice) {

        // 设备状态变化,如果是当前登录的设备查询之后是在线的,打开设备操作界面
        if (null != mCurrDevSn && mCurrDevSn.equals(funDevice.getDevSn())) {

            mFunDevice = funDevice;

            showToast(R.string.device_get_status_success);

            hideWaitDialog();

            if (funDevice.devStatus == FunDevStatus.STATUS_ONLINE) {
                // 如果设备在线,获取设备信息
                if ((funDevice.devType == null || funDevice.devType == FunDevType.EE_DEV_UNKNOWN)) {
                    funDevice.devType = mCurrDevType;
                }

                if (null != mHandler) {
                    mHandler.removeMessages(MESSAGE_DELAY_FINISH);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_DELAY_FINISH, 1000);
                }
            } else {
                // 设备不在线
                showToast(R.string.device_stauts_offline);
            }
        }

    }

    @Override
    public void onDeviceAddedSuccess() {

    }

    @Override
    public void onDeviceAddedFailed(Integer errCode) {

    }

    @Override
    public void onDeviceRemovedSuccess() {

    }

    @Override
    public void onDeviceRemovedFailed(Integer errCode) {

    }

    @Override
    public void onAPDeviceListChanged() {

    }

    @Override
    public void onLanDeviceListChanged() {

    }
}
