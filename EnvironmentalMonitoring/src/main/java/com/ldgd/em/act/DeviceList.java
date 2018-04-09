package com.ldgd.em.act;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ldgd.em.R;
import com.ldgd.em.adapter.DeviceListAdapter;
import com.ldgd.em.db.MySqliteHelper;
import com.ldgd.em.domain.Monitoring;
import com.ldgd.em.funsdk.support.DeviceActivitys;
import com.ldgd.em.funsdk.support.FunDevicePassword;
import com.ldgd.em.funsdk.support.FunSupport;
import com.ldgd.em.funsdk.support.OnFunDeviceListener;
import com.ldgd.em.funsdk.support.models.FunDevStatus;
import com.ldgd.em.funsdk.support.models.FunDevType;
import com.ldgd.em.funsdk.support.models.FunDevice;
import com.ldgd.em.funsdk.support.models.FunLoginType;
import com.ldgd.em.sqlitedal.SQLiteDALMonitoring;
import com.lib.FunSDK;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ldgd on 2017/3/6.
 * 介绍：
 */

public class DeviceList extends ActivityDemo implements OnFunDeviceListener {

    private FunDevice mFunDevice = null;
    private String mCurrDevSn = null;
    private FunDevType mCurrDevType = null;
    private FunDevType mCurrDevIpType = null;

    private final int MESSAGE_DELAY_FINISH = 0x100;
    /**
     * 设备信息
     */
    private List<Monitoring> monitorings;
    /**
     * 当前选中的监控设备
     */
    private Monitoring seletMonitoring;

    private ListView deviceList;
    private DeviceListAdapter deviceListAdapter;

    // 如果是设备类型特定的话,固定一个就可以了
    private final FunDevType[] mSupportDevTypes = {FunDevType.EE_DEV_NORMAL_MONITOR,
            FunDevType.EE_DEV_INTELLIGENTSOCKET, FunDevType.EE_DEV_SMALLEYE};


    private MySqliteHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_monitoring_device_list);

        // 获取数据库帮助类对象
        helper = SQLiteDALMonitoring.getIntance(this);

        // 设置登录方式为本地登录
        FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);
        // 监听设备类事件
        FunSupport.getInstance().registerOnFunDeviceListener(this);
        //  mFunDevice.devType = mSupportDevTypes[0];

        initView();
        //   initParameter();
        initParameter2();

        // listView Item点击事件
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (monitorings != null && monitorings.size() > 0) {

                    String serialumber = monitorings.get(position).getSerialumber();
                    seletMonitoring = monitorings.get(position);
                    requestDeviceStatus(serialumber);

                }
            }
        });

    }

    private void initParameter2() {
        // 获取数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        int totalNum = SQLiteDALMonitoring.getDataCount(db, "Monitoring");
        Log.e("totalNum", "totalNum =" + totalNum);

        if (totalNum == 0) {
            List<Monitoring> monitorings = initMonitorings();
            for (int i = 0; i < monitorings.size(); i++) {
                Log.e("Monitoring = ", monitorings.toString());
                Monitoring monitoring = monitorings.get(i);
                SQLiteDALMonitoring.InsertMonitoring(db, "Monitoring", monitoring);
            }

        }
        Cursor cursor = SQLiteDALMonitoring.selectAllMonitoring(db);
        if (cursor != null) {

            monitorings = SQLiteDALMonitoring.cursorToList(cursor);
            deviceListAdapter = new DeviceListAdapter(this, monitorings);
            deviceList.setAdapter(deviceListAdapter);


            for (int i = 0; i < monitorings.size(); i++) {
                Monitoring monitoring = monitorings.get(i);
                Log.e("Monitoring = ", monitoring.toString() + "");
            }
        }


    }

    @Override
    protected void onResume() {

        initParameter2();
        super.onResume();
    }

    private void initParameter() {
        initMonitorings();

        deviceListAdapter = new DeviceListAdapter(this, monitorings);
        deviceList.setAdapter(deviceListAdapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (monitorings != null && monitorings.size() > 0) {

                    String serialumber = monitorings.get(position).getSerialumber();
                    seletMonitoring = monitorings.get(position);
                    requestDeviceStatus(serialumber);

                }


            }
        });


    }

    private void initView() {
        deviceList = (ListView) this.findViewById(R.id.lv_device_list);
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
                        mFunDevice.loginPsw = seletMonitoring.getPassword();

                        //Save the password to local file
                        FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(), mFunDevice.loginPsw);
                        FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", mFunDevice.loginPsw);

                        mFunDevice.devType = mSupportDevTypes[0];
                        DeviceActivitys.startDeviceActivity(DeviceList.this, mFunDevice, seletMonitoring);
                    }

                    mFunDevice = null;
                    //  finish();
                }
                break;
            }
        }

    };


    public void start(View view) {
        requestDeviceStatus(null);

    }

    // 设备登录
    private void requestDeviceStatus(String devSN) {
        //  String devSN = "8caf1f62f6795bec";

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

    @Override
    protected void onDestroy() {

        // 注销设备事件监听
        FunSupport.getInstance().removeOnFunDeviceListener(this);

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }


    /**
     * 初始化监控设备（xml中获取）
     */
    private List<Monitoring> initMonitorings() {

        monitorings = new ArrayList();
        try {
            // 读取xml文件
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(this.getAssets().open("monitoring_equipment.xml"));
            Element element = document.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("Monitoring");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Monitoring monitoring = new Monitoring();
                Element monitoringElement = (Element) nodeList.item(i);

                monitoring.setId(Integer.parseInt(monitoringElement.getAttribute("id")));
                monitoring.setName(monitoringElement.getElementsByTagName("name").item(0).getTextContent());
                monitoring.setPassword(monitoringElement.getElementsByTagName("password").item(0).getTextContent());
                monitoring.setSerialumber(monitoringElement.getElementsByTagName("serialumber").item(0).getTextContent());
                monitoring.setLongitude(Double.valueOf(monitoringElement.getElementsByTagName("longitude").item(0).getTextContent()));
                monitoring.setLatitude(Double.valueOf(monitoringElement.getElementsByTagName("latitude").item(0).getTextContent()));
                monitoring.setIp(monitoringElement.getElementsByTagName("ip").item(0).getTextContent());
                monitoring.setUuid(monitoringElement.getElementsByTagName("uuid").item(0).getTextContent());

              /*  NodeList nodel =   monitoringElement.getElementsByTagName("ip");
                Node node =  nodel.item(0);
                node .getTextContent();*/

                monitorings.add(monitoring);

            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return monitorings;
    }


}
