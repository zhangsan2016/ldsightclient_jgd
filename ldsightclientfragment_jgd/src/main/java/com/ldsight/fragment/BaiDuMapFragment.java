package com.ldsight.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ldsight.entity.CheckUser;

public class BaiDuMapFragment extends Fragment implements CloudListener {

	private static final String LTAG = BaiDuMapFragment.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 电箱号、经纬度
	private EditText et_electricity_box_number, et_position;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(BaiDuMapFragment.this.getActivity()
				.getApplicationContext());
		View rootView = inflater.inflate(R.layout.baidu_map_activity,
				container, false);
		// 获取地图控件引用
		mMapView = (MapView) rootView.findViewById(R.id.bmapView);
		// 初始化地图
		mBaiduMap = mMapView.getMap();
		// 初始化视图
		initView();
		// 初始化云管理器
		CloudManager.getInstance().init(BaiDuMapFragment.this);

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

		return rootView;
	}

	private void initView() {

	}

	/**
	 * 本地检索
	 */
	private void regionSearch() {
		/*
		 * LocalSearchInfo info = new LocalSearchInfo(); // info.ak =
		 * "QKCPc0iDgZ0Q5iVmuEv8buL9"; info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
		 * info.geoTableId = 126973; info.tags = ""; info.q = "中科院"; //
		 * info.region = "深圳市"; info.region = "重庆市";
		 * CloudManager.getInstance().localSearch(info);
		 */
		// 当前用户帐号
		CheckUser cku = CheckUser.getInstance();
		if(cku.getUserName() != null && cku.getUserName().equals("admin")){
			LocalSearchInfo info = new LocalSearchInfo();
			info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
			info.geoTableId = 143670;
			info.tags = "";
			info.q = "谷阳大道";
			info.region = "镇江市";
			CloudManager.getInstance().localSearch(info);
		}else if(cku.getUserName() != null && cku.getUserName().equals("ldgd")){
			System.out.println("ldgd执行");
			LocalSearchInfo info = new LocalSearchInfo();
			info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
			info.geoTableId = 143673;
			info.tags = "";
			info.q = "洛丁";
			info.region = "深圳";
			CloudManager.getInstance().localSearch(info);
		}else if(cku.getUserName() != null && cku.getUserName().equals("ysdx")){
			LocalSearchInfo info = new LocalSearchInfo();
			info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
			info.geoTableId = 126973;
			info.tags = "";
			info.q = "永善";
			info.region = "云南市";
			CloudManager.getInstance().localSearch(info);
		}else if(cku.getUserName() != null && cku.getUserName().equals("zky")){
			LocalSearchInfo info = new LocalSearchInfo();
			info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
			info.geoTableId = 143666;
			info.tags = "";
			info.q = "中科院";
			info.region = "重庆";
			CloudManager.getInstance().localSearch(info);
		}

//		LocalSearchInfo info = new LocalSearchInfo();
//		// info.ak = "QKCPc0iDgZ0Q5iVmuEv8buL9";
//		info.ak = "eWKnMxBzUqvxh5isNvn6qk4W";
//		info.geoTableId = 126973;
//		info.tags = "";
//		info.q = "永善";
//		// info.region = "深圳市";
//		info.region = "云南市";
//		CloudManager.getInstance().localSearch(info);
	}

	@Override
	public void onResume() {
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		// mMapView.onResume();
		super.onResume();

	}

	@Override
	public void onPause() {
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		super.onPause();

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// 在activity执行onPause时执行mMapView. onDestroy ()，实现地图生命周期管理
		mMapView.onDestroy();
		super.onDestroyView();
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
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds); // 设置显示在屏幕中的地图地理范围
			System.out.println("MapStatusUpdate = " + u);
			try {
				mBaiduMap.animateMapStatus(u);
			} catch (Exception e) {
				System.out.println("空指针异常： " + result.poiList.size());
			}

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
					View location = View.inflate(BaiDuMapFragment.this
									.getActivity().getApplicationContext(),
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
		Toast.makeText(
				BaiDuMapFragment.this.getActivity().getApplicationContext(),
				msg, Toast.LENGTH_LONG).show();
	}

}
