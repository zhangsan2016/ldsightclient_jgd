package com.ldgd.em.act;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.ldgd.em.R;
import com.ldgd.em.adapter.DeviceListAdapter;
import com.ldgd.em.common.DialogWaitting;
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
 * Created by ldgd on 2017/3/11.
 * 介绍：
 */

public class BaiduMapAct extends Activity implements OnFunDeviceListener {

    private final int MESSAGE_DELAY_FINISH = 0x100;

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);

    // 标记集合
    ArrayList<Marker> markerList = new ArrayList<Marker>();

    private InfoWindow mInfoWindow;
    /**
     * 设备信息
     */
    List<Monitoring> monitorings;
    /**
     * 当前选中的监控设备
     */
    private Monitoring seletMonitoring;

    private Toast mToast = null;
    private FunDevice mFunDevice = null;
    private String mCurrDevSn = null;
    private FunDevType mCurrDevType = null;
    private FunDevType mCurrDevIpType = null;
    private DialogWaitting mWaitDialog = null;

    private MySqliteHelper helper;

    // 如果是设备类型特定的话,固定一个就可以了
    private final FunDevType[] mSupportDevTypes = {FunDevType.EE_DEV_NORMAL_MONITOR,
            FunDevType.EE_DEV_INTELLIGENTSOCKET, FunDevType.EE_DEV_SMALLEYE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_baidu_map);

        // 获取数据库帮助类对象
        helper = SQLiteDALMonitoring.getIntance(this);

        initView();

        // 初始化地图
        initMap();

        // 设置登录方式为本地登录
        FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);
        // 监听设备类事件
        FunSupport.getInstance().registerOnFunDeviceListener(this);
        //  mFunDevice.devType = mSupportDevTypes[0];
        mCurrDevType = mSupportDevTypes[0];




    }

    /**
     * 初始化百度地图
     */
    private void initMap() {
        mBaiduMap = mMapView.getMap();

        // 设置地图缩放级别
        // MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);

        // 初始化覆盖物
        initOverlay();

        // 覆盖物点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {

                // 获取传递过来的信息
                final Monitoring monitoring = (Monitoring) marker.getExtraInfo().get("monitoring");

                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                button.setText(monitoring.getName() + "进入监控");
                InfoWindow.OnInfoWindowClickListener listener = null;
                button.setTextColor(getResources().getColor(R.color.black));

                // 添加地图覆盖物点击事件
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        seletMonitoring = monitoring;
                        requestDeviceStatus(seletMonitoring.getSerialumber());
                    }
                });
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(button, ll, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);


                return true;
            }
        });
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
    }

    /**
     * 初始化覆盖物
     */
    public void initOverlay() {

        // 初始化监控设备
       //  initMonitorings();
        initMonitorings2();

        for (int i = 0; i < monitorings.size(); i++) {
            Monitoring monitoring = monitorings.get(i);
            LatLng latLng = new LatLng(monitoring.getLongitude(), monitoring.getLatitude());

            if (i == 0) {
                // 设置地图显示的范围
                mBaiduMap.setMapStatusLimits(new LatLngBounds.Builder().include(latLng).include(latLng).build());
            }


            MarkerOptions ooA = new MarkerOptions().position(latLng).icon(bdA)
                    .zIndex(9).draggable(true);
            // 掉下动画
           // ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
            Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));

            // 添加到地图
            Bundle bundle = new Bundle();
            bundle.putSerializable("monitoring", monitoring);
            mMarker.setExtraInfo(bundle);
            markerList.add(mMarker);
        }


    /*    mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(
                        BaiduMapAct.this,
                        "拖拽结束，新位置：" + marker.getPosition().latitude + ", "
                                + marker.getPosition().longitude,
                        Toast.LENGTH_LONG).show();
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });*/


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


    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
       mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        initOverlay();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        super.onDestroy();
        // 回收 bitmap 资源
        bdA.recycle();

        // 注销设备事件监听
        FunSupport.getInstance().removeOnFunDeviceListener(this);

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
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

    public void showToast(int resid) {
        if (resid > 0) {
            if (null != mToast) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, resid, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public void showWaitDialog(int resid) {
        if (null == mWaitDialog) {
            mWaitDialog = new DialogWaitting(this);
        }
        mWaitDialog.show(resid);
    }

    public void hideWaitDialog() {
        if ( null != mWaitDialog ) {
            mWaitDialog.dismiss();
        }
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

                       // mFunDevice.devType = mSupportDevTypes[0];
                        DeviceActivitys.startDeviceActivity(BaiduMapAct.this, mFunDevice,seletMonitoring);
                    }

                    mFunDevice = null;
                    //  finish();
                }
                break;
            }
        }

    };

    private void initMonitorings2() {
        // 获取数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        int totalNum = SQLiteDALMonitoring.getDataCount(db, "Monitoring");
        Log.e("totalNum", "totalNum =" + totalNum);

        if (totalNum == 0) {
            List<Monitoring> monitorings = initMonitorings();
            for (int i = 0; i < monitorings.size(); i++) {
                Monitoring monitoring = monitorings.get(i);
                SQLiteDALMonitoring.InsertMonitoring(db, "Monitoring", monitoring);
            }

        }
        Cursor cursor = SQLiteDALMonitoring.selectAllMonitoring(db);
        if (cursor != null) {

            monitorings = SQLiteDALMonitoring.cursorToList(cursor);

            for (int i = 0; i < monitorings.size(); i++) {
                Monitoring monitoring = monitorings.get(i);
            //    Log.e("aa",monitoring.toString());
            }
        }


    }

}
