package com.ldsight.act;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.example.ldsightclient_jgd.R;

public class BaiDuMapAct extends Activity implements CloudListener {
	private static final String LTAG = BaiDuMapAct.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 电箱号、经纬度
	private EditText et_electricity_box_number, et_position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		this.setContentView(R.layout.baidu_map_activity);
		// 初始化视图
		initView();
		// 初始化云管理器
		CloudManager.getInstance().init(BaiDuMapAct.this);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		// 初始化地图
		mBaiduMap = mMapView.getMap();

		/*
		 * mBaiduMap = mMapView.getMap(); // 开启定位图层
		 * mBaiduMap.setMyLocationEnabled(true); // 构造定位数据 MyLocationData
		 * locData = new MyLocationData.Builder()
		 * .accuracy(location.getRadius()) // 此处设置开发者获取到的方向信息，顺时针0-360
		 * .direction(100).latitude(location.getLatitude())
		 * .longitude(location.getLongitude()).build(); // 设置定位数据
		 * mBaiduMap.setMyLocationData(locData); //
		 * 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标） mCurrentMarker =
		 * BitmapDescriptorFactory .fromResource(R.drawable.icon_geo);
		 * MyLocationConfiguration config = new
		 * MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
		 * mBaiduMap.setMyLocationConfiguration(); // 当不需要定位图层时关闭定位图层
		 * mBaiduMap.setMyLocationEnabled(false);
		 */

		// 发起本地检索
		regionSearch();

	}

	private void initView() {

	}

	/**
	 * 本地检索
	 */
	private void regionSearch() {
		new Thread() {
			public void run() {
				/*LocalSearchInfo info = new LocalSearchInfo();
				// info.ak = "QKCPc0iDgZ0Q5iVmuEv8buL9";
				info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
				info.geoTableId = 126973;
				info.tags = "";
				info.q = "中科院";
				// info.region = "深圳市";
				info.region = "重庆市";
				CloudManager.getInstance().localSearch(info);*/

				LocalSearchInfo info = new LocalSearchInfo();
				// info.ak = "QKCPc0iDgZ0Q5iVmuEv8buL9";
				info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
				info.geoTableId = 126973;
				info.tags = "";
				info.q = "永善";
				// info.region = "深圳市";
				info.region = "云南市";
				CloudManager.getInstance().localSearch(info);
			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		super.onResume();

	}

	@Override
	protected void onPause() {
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		super.onPause();

	}

	@Override
	public void onGetDetailSearchResult(DetailSearchResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetSearchResult(CloudSearchResult result, int error) {
		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			Log.d(LTAG,
					"onGetSearchResult, result length: "
							+ result.poiList.size());
			mBaiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory
					.fromResource(R.drawable.icon_gcoding);

			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
			// 设置图标点击事件
			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(final Marker marker) {
					InfoWindow mInfoWindow;
					LatLng position = marker.getPosition();
					final double latitude = position.latitude;
					final double longitude = position.longitude;
					// showToast("latitude" + latitude + "\n  " + "longitude" +
					// longitude );

					// 将marker所在的经纬度的信息转化成屏幕上的坐标
					Point p = mBaiduMap.getProjection().toScreenLocation(
							position);
					p.y -= 30;
					LatLng llInfo = mBaiduMap.getProjection()
							.fromScreenLocation(p);
					View location = View.inflate(BaiDuMapAct.this,
							R.layout.baidu_map_marker_info_item, null);
					mInfoWindow = new InfoWindow(location, llInfo, 0);
					// 显示InfoWindow
					mBaiduMap.showInfoWindow(mInfoWindow);

					return false;
				}
			});

		} else {
			System.out.println("NULL");
		}

	}

	private void showToast(String msg) {
		Toast.makeText(BaiDuMapAct.this, msg, Toast.LENGTH_LONG).show();
	}

}
