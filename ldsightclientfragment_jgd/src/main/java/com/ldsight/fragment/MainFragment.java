package com.ldsight.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ldsightclient_jgd.R;
import com.google.gson.Gson;
import com.ldsight.act.DeviceMainAct;
import com.ldsight.adapter.MainListAdapter;
import com.ldsight.dao.MakeSampleHttpRequest;
import com.ldsight.entity.ElectricTransducer;
import com.ldsight.entity.LoginInfo;
import com.ldsight.entity.ProjectItem;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.service.OnlineService;
import com.ldsight.service.UpdateService;
import com.ldsight.util.CustomUtils;
import com.ldsight.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainFragment extends Fragment {
	private Context mContext;
	public static String DataRefresh = "DataRefresh"; // 广播接收者-
	private final String TAG_REQUEST = "MainFragment";
	private ListView listView;
	private JsonObjectRequest jsonObjRequest;
	private RequestQueue mVolleyQueue;
	private ArrayList<StreetAndDevice> streetAndDevices;
	private List<String> cableIsAbnormal = new ArrayList<String>();
	MainListAdapter adapter;
	private ProgressDialog mProgress;
	/*
	 * 版本信息
	 */
	private int newVersionCode;
	private String newVersionName;
	/*
	 * 提示框
	 */
	private Dialog dialog;

	private boolean updata = false;

	private LoginInfo loginInfo;
	private Object electricalBox;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// 接受更新广播
		IntentFilter filter = new IntentFilter(MainFragment.DataRefresh);
		MainFragment.this.getActivity().getApplicationContext()
				.registerReceiver(dataRefreshReceiver, filter);

		// 获取传递过来的数据
        loginInfo  = (LoginInfo) getArguments().getSerializable("loginInfo");


		View rootView = inflater.inflate(R.layout.main_fragment, container,
				false);
		listView = (ListView) rootView.findViewById(R.id.main_list);

		streetAndDevices = new ArrayList<StreetAndDevice>();
		if (!updata) {
			// showProgress();
			makeSampleHttpRequest(loginInfo.getData().get(0).getID());
		}

		adapter = new MainListAdapter(MainFragment.this.getActivity(),
				streetAndDevices, cableIsAbnormal);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {


				Intent intent = new Intent(MainFragment.this.getActivity()
						.getApplicationContext(), DeviceMainAct.class);
				Bundle bundle = new Bundle();
				bundle.putString("streetId", streetAndDevices.get(position)
						.getStreetId());
				intent.putExtras(bundle);
				// startActivity(intent);
				startActivityForResult(intent, 1);

			}

		});

		// 启动心跳包服务
		Intent intent = new Intent(MainFragment.this.getActivity()
				.getApplicationContext(), OnlineService.class);
		MainFragment.this.getActivity().startService(intent);


		return rootView;

	}

    /**
     *  获取项目信息
     * @param id
     */
	private void makeSampleHttpRequest(final String id) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				RequestBody requestBody = new FormBody.Builder()
						.add("strTemplate", "{\"ischeck\":$data.rows}")
						.add("ID", id)
						.build();
				String url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyPrject";
				// url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyPrject";

				HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

					@Override
					public void onFailure(Call call, IOException e) {
						Log.e("xxx", "失败" + e.toString());
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						String json = response.body().string();
						Log.e("xxx", "成功获取电箱设备" + json);
						Headers headers = response.headers();
						Log.e("xxx", "headers      " + response.headers());
						Gson gson = new Gson();
						ProjectItem projectItem = gson.fromJson(json, ProjectItem.class);

						for (int i = 0; i < projectItem.getData().size(); i++) {
							getElectricTransducer(projectItem.getData().get(i).getId());
						}


					}
				},requestBody);
			}
		}).start();

	}


	/**
	 *  获取变电器
	 * @param id 项目id
	 */
	public void getElectricTransducer(final String id) {
	   new Thread(new Runnable() {
		   @Override
		   public void run() {
			   RequestBody requestBody = new FormBody.Builder()
					   .add("strTemplate", "{\"ischeck\":$data.rows}")
					   .add("ID", id)
					   .build();
			   String url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyOne";

			   HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

				   @Override
				   public void onFailure(Call call, IOException e) {
					   Log.e("xxx", "失败" + e.toString());
				   }

				   @Override
				   public void onResponse(Call call, Response response) throws IOException {
					   String json = response.body().string();
					   Log.e("xxx", "成功获取变电器设备" + json);
					   Gson gson = new Gson();
					   ElectricTransducer electricTransducer = gson.fromJson(json, ElectricTransducer.class);

					   for (int i = 0; i <  electricTransducer.getData().size(); i++) {
						   	getElectricalBox(electricTransducer.getData().get(i).getId());
					   }

				   }
			   },requestBody);
		   }
	   }).start();
	}

	/**
	 *  获取电箱
	 * @param id  变电器id
	 */
	public void getElectricalBox(final String id) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				RequestBody requestBody = new FormBody.Builder()
						.add("strTemplate", "{\"ischeck\":$data.rows}")
						.add("ID", id)
						.build();
				String url = "http://47.99.168.98:9002/api/IOTDevice.asmx/QueryTopologyTwo";

				HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

					@Override
					public void onFailure(Call call, IOException e) {
						Log.e("xxx", "失败" + e.toString());
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						String json = response.body().string();
						Log.e("xxx", "成功获取电箱设备" + json);
						Gson gson = new Gson();
						ElectricTransducer electricTransducer = gson.fromJson(json, ElectricTransducer.class);

				/*		for (int i = 0; i <  electricTransducer.getData().size(); i++) {
						}*/

					}
				},requestBody);
			}
		}).start();

	}




	private BroadcastReceiver dataRefreshReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			// showProgress();
		//	makeSampleHttpRequest();
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (dialog != null) {
			dialog.dismiss();
		}
		MainFragment.this.getActivity().getApplicationContext()
				.unregisterReceiver(dataRefreshReceiver);
		super.onDestroy();

	}

	private void showProgress() {
		mProgress = ProgressDialog.show(mContext, "", "Loading...");

	}

	private void stopProgress() {
		if (mProgress != null) {
			mProgress.cancel();
		}
	}

	private void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}


	public void setElectricalBox(Object electricalBox) {
		this.electricalBox = electricalBox;
	}



	class checkNewestVersionAsyncTask extends AsyncTask<Void, Void, Boolean> {
		// 后台执行，比较耗时的操作都放在这个位置
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (postCheckNewestVersionCommand()) {
				int vercode = CustomUtils.getVersionCode(mContext); // 用到前面第一节写的方法
				if (newVersionCode > vercode) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (result) {// 如果有最新版本
				System.out.println("下载最新版本");

				doNewVersionUpdate(); // 更新新版本
			} else {
				// notNewVersionDlgShow(); // 提示当前为最新版本
				System.out.println("当前最新版本");
			}
			super.onPostExecute(result);
		}

	}

	/**
	 * 从服务器获取当前最新版本号，如果成功返回TURE，如果失败，返回FALSE
	 *
	 * @return
	 */
	private Boolean postCheckNewestVersionCommand() {
		MakeSampleHttpRequest mshr = new MakeSampleHttpRequest(
				MainFragment.this.getActivity());
		// JSONObject response = mshr.getVersion();
		JSONObject response = mshr.post_to_server();
		try {
			newVersionCode = response.getInt("verCode");
			newVersionName = response.getString("verName");
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 提示更新新版本
	 */
	private void doNewVersionUpdate() {
		int verCode = CustomUtils.getVersionCode(mContext);
		String verName = CustomUtils.getVersionName(mContext);

		/*
		 * String str=
		 * "当前版本："+verName+" Code:"+verCode+" ,发现新版本："+newVersionName+
		 * " Code:"+newVersionCode+" ,是否更新？";
		 */
		String str = "发现新版本,是否更新？";
		Dialog dialog = new AlertDialog.Builder(mContext)
				.setTitle("软件更新")
				.setMessage(str)
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								/*
								 * m_progressDlg.setTitle("正在下载");
								 * m_progressDlg.setMessage("请稍候...");
								 * downFile(Common.UPDATESOFTADDRESS); //开始下载
								 */

								// 开启更新服务UpdateService
								// 这里为了把update更好模块化，可以传一些updateService依赖的值
								// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
								Intent updateIntent = new Intent(mContext,
										UpdateService.class);
								updateIntent.putExtra("titleId",
										R.string.app_name);
								MainFragment.this.getActivity().startService(
										updateIntent);
								dialog.dismiss();
							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int whichButton) {
								// 点击"取消"按钮之后退出程序
								// dialog.finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;

	}

}
