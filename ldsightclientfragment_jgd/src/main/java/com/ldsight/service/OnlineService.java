package com.ldsight.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

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
import com.ldsight.act.BrightenMain;
import com.ldsight.act.BrightenTiming;
import com.ldsight.act.DeviceMainAct;
import com.ldsight.act.DeviceTiming;
import com.ldsight.act.MainAct;
import com.ldsight.act.SingleLightAct;
import com.ldsight.act.SingleLightControlAct;
import com.ldsight.act.SingleLightDialogItemAct;
import com.ldsight.act.SingleLightSettingAct;
import com.ldsight.act.WarningInformationAct;
import com.ldsight.application.MyApplication;
import com.ldsight.fragment.SettingFragment;
import com.ldsight.fragment.TestPatternFragment;
import com.ldsight.util.Util;

import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.UDPClientBase;
import org.json.JSONObject;

import java.util.Arrays;

public class OnlineService extends Service {
	WakeLock wakeLock;
	MyUdpClient myUdpClient;
	private RequestQueue mVolleyQueue;
	private JsonObjectRequest jsonObjRequest;
	private final String TAG_REQUEST = "MY_TAG";
	private SharedPreferences preferences;

	public class MyUdpClient extends UDPClientBase {

		public MyUdpClient(byte[] uuid, int appid, String serverAddr,
						   int serverPort) throws Exception {
			super(uuid, appid, serverAddr, serverPort);
		}

		@Override
		public boolean hasNetworkConnection() {
			return Util.hasNetwork(OnlineService.this);
		}

		@Override
		public void trySystemSleep() {
			tryReleaseWakeLock();
		}

		@Override
		public void onPushMessage(Message message) {
			// 测试
			System.out.println("message = "+ Arrays.toString(message.getData()));

			if (message == null) {
				return;
			}
			if (message.getData() == null || message.getData().length == 0) {
				return;
			}
			if (message.getData()[4] == 2) {
				System.out.println("操作信息回传");
				Intent intent = new Intent();
				intent.setAction(DeviceMainAct.DeviceMainFilter);
				sendBroadcast(intent);
			}
			if (message.getData()[4] == 10
					&& message.getData()[message.getData().length - 2] == 4) {
				System.out.println("报警信息");
				String streetUuid = "";
				for (int i = 0; i < 8; i++) {
					streetUuid += message.getData()[5 + i];
					if (i < 7)
						streetUuid += ",";
				}
				makeSampleHttpRequest(streetUuid,
						message.getData()[message.getData().length - 1]);
			}
			if (message.getData()[13] == 114) {
				// 获取电参数

				Intent intent = new Intent();
				intent.setAction(SingleLightSettingAct.SINGLELIGHTSETTINGACT_RECEIVER);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);

			/*	System.out.println("获取电参数");
				Intent intent = new Intent();
				intent.setAction(SingleLightControlAct.DeviceParamFilter);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);*/
			}else if(message.getData()[13] == 116){
				// 读系统时间返回
				Intent intent = new Intent();
				intent.setAction(SingleLightSettingAct.SINGLELIGHTSETTINGACT_RECEIVER);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);

			}else if(message.getData()[13] == -63){
				// 安置房控制全开返回
				Intent intent = new Intent();
				intent.setAction(BrightenMain.DATA_REFRESH_FILTER);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);

			}else if(message.getData()[13] == -47){
				// 校时返回
				Intent intent = new Intent();
				intent.setAction(SingleLightSettingAct.SINGLELIGHTSETTINGACT_RECEIVER);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);

			} else if (message.getData()[13] == 30) {
				// 显示湿度温度光强度
				// 获取电参数
				Intent intent = new Intent();
				intent.setAction(DeviceMainAct.DeviceTemperatureFilter);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);
			}else if(message.getData()[13] == 9 || message.getData()[13] == 5){
				// 单灯数据 9(单灯查询9，单灯数据和读设备信息列表（0x500）5)
				if(message.getData()[13] == 5){
					// 判断数据长度区分data是单灯数据或读设备信息列表
					if(message.getData()[4] == 44){
						//读设备信息列表
						Intent intent = new Intent();
						intent.setAction(BrightenMain.DATA_REFRESH_FILTER);
						intent.putExtra("data", message.getData());
						sendBroadcast(intent);
					}else {
						// 单灯数据
						Intent intent = new Intent();
						intent.setAction(SingleLightAct.lightPoleTagParameterFilter);
						intent.putExtra("data", message.getData());
						sendBroadcast(intent);
					}


				}else  {
					Intent intent = new Intent();
					intent.setAction(SingleLightDialogItemAct.QuerySingleLampReceiver);
					intent.putExtra("data", message.getData());
					sendBroadcast(intent);
					intent.setAction(SingleLightControlAct.DeviceParamFilter);
					sendBroadcast(intent);
				}

			}else if(message.getData()[13] == 48){
				// 检查当前设备状态(返回日期参数)
				System.out.println("检查当前设备状态");
				Intent intent = new Intent();
				intent.setAction(DeviceMainAct.DeviceStateFilter);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);

			}else if (message.getData()[5] == 32) {
				System.out.println("message.getData()" + Arrays.toString(message.getData()));
				// 报警信息
				String exceptionTypes = null;
				if(message.getData()[7] == 1){
					exceptionTypes = "电参异常";
				}else if(message.getData()[7] == 2){
					exceptionTypes = "设备不在线";
				}else if(message.getData()[7] == 3){
					exceptionTypes = "断缆报警";
				}
				//  通知点击后跳转的intent
				Intent warningIntent = new Intent(OnlineService.this,WarningInformationAct.class);
				warningIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				warningIntent.putExtra("data", message.getData());
				// notifyUser(32, "报警信息",  "洛丁光电电箱" + exceptionTypes,null);
				notifyUser2(32, "报警信息",  "洛丁光电电箱" + exceptionTypes,warningIntent);

			}else if(message.getData()[13] == 41) {
				// 继电器应答
				Intent intent = new Intent();
				intent.setAction(TestPatternFragment.TestPatternFragmentBroadcast);
				intent.putExtra("data", message.getData());
				sendBroadcast(intent);
			}else if(message.getData()[13] == -62 || message.getData()[13] == 66 ) {

				// -62全定时 , 66单灯定时
				if(message.getData()[13] == -62){
					// 定时应答
					Intent intent = new Intent();
					intent.setAction(DeviceTiming.receiver);
					intent.putExtra("data", message.getData());
					sendBroadcast(intent);
					//sendOrderedBroadcast(intent, null);

					intent.setAction(BrightenTiming.BRIGHTEN_TIMING_RECELVER);
					intent.putExtra("data", message.getData());
					sendBroadcast(intent);

				}else if(message.getData()[13] == 66){

					Intent intent = new Intent();
					intent.setAction(BrightenTiming.BRIGHTEN_TIMING_RECELVER);
					intent.putExtra("data", message.getData());
					sendBroadcast(intent);
				}


			}

		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		resetClient();
		return super.onStartCommand(intent, flags, startId);
	}

	protected void resetClient() {
		if (this.myUdpClient != null) {
			try {
				myUdpClient.stop();
			} catch (Exception e) {
			}
		}
		try {
			// Property property = new Property(getApplicationContext());
			// String[] uuidStr = property.getContent("useruuid").split(",");
			// byte[] uuid = new byte[8];
			// for (int i = 0; i < uuidStr.length; i++) {
			// uuid[i] = Byte.parseByte(uuidStr[i]);
			// }
			// // 测试输出uuid
			// System.out.println(Arrays.toString(uuid));
			// 获取当前应用的uuid
			MyApplication myApplication = MyApplication.getInstance();
			byte[] uuid = myApplication.getAppUuid();
			// System.out.println("心跳包 = " + Arrays.toString(uuid));
			myUdpClient = new MyUdpClient(uuid, 1, this.getResources()
					.getString(R.string.ip), 9966);
			myUdpClient.setHeartbeatInterval(50);
			myUdpClient.start();
			// System.out.println("心跳包开启成功");
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.getApplicationContext(),
					"操作失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void notifyUser(int id, String title, String content,
						   String tickerText) {
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification();
		Intent intent = new Intent(this, MainAct.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_ONE_SHOT);
		n.contentIntent = pi;

		n.setLatestEventInfo(this, title, content, pi);
		preferences = getApplicationContext().getSharedPreferences(
				SettingFragment.SYSTEM_CONSTANT, 0);
		boolean alertSound = preferences.getBoolean(
				SettingFragment.ALERT_SOUND, false);
		if (alertSound) {
			n.defaults = Notification.DEFAULT_ALL;
		}
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.flags |= Notification.FLAG_AUTO_CANCEL;

		n.icon = R.drawable.luoding_logo;
		n.when = System.currentTimeMillis();
		n.tickerText = tickerText;
		notificationManager.notify(id, n);
	}

	public void notifyUser2(int id, String title, String content,
							Intent warningIntent) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent pi = PendingIntent.getActivity(this, 0, warningIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 通过Notification.Builder来创建通知，注意API Level
		// API11之后才支持
		Notification notify2 = new Notification.Builder(this)
				.setSmallIcon(R.drawable.download_notification_logo) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
				// icon)
				.setTicker("TickerText:" + "您有新的消息，请注意查收！")// 设置在status
				// bar上显示的提示文字
				.setContentTitle(title)// 设置在下拉status
				// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
				.setContentText(content)// TextView中显示的详细内容
				.setContentIntent(pi) // 关联PendingIntent
				// .setNumber(1) //
				// 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
				.getNotification(); // 需要注意build()是在API level
		// 16及之后增加的，在API11中可以使用getNotificatin()来代替
		notify2.flags |= Notification.FLAG_AUTO_CANCEL;
		notify2.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
		manager.notify(id, notify2);

	}

	protected void tryReleaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld() == true) {
			wakeLock.release();
		}
	}

	private void makeSampleHttpRequest(String streetUuid, final int errorId) {
		mVolleyQueue = Volley.newRequestQueue(this);
		String ip = getString(R.string.ip);
		String url = "http://" + ip + ":8080/ldsight/streetNameAction";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("uuid", streetUuid);

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.has("streetName")) {
						try {
							String streetName = response
									.getString("streetName");
							notifyUser(32, "报警信息", streetName + " 第"
									+ errorId + "号 电缆异常!", streetName
									+ " 电缆异常!");

							// 通知mainAct更新自定义Adapter
							Intent updatEcableReceiver = new Intent(
									MainAct.UDER);
							updatEcableReceiver.putExtra("streetName",
									streetName);
							updatEcableReceiver.putExtra("errorId",
									String.valueOf(errorId));
							sendBroadcast(updatEcableReceiver);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle your error types accordingly.For Timeout & No
				// connection error, you can show 'retry' button.
				// For AuthFailure, you can re login with user
				// credentials.
				// For ClientError, 400 & 401, Errors happening on
				// client side when sending api request.
				// In this case you can check how client is forming the
				// api and debug accordingly.
				// For ServerError 5xx, you can do retry or handle
				// accordingly.
				if (error instanceof NetworkError) {
				} else if (error instanceof ClientError) {
				} else if (error instanceof ServerError) {
				} else if (error instanceof AuthFailureError) {
				} else if (error instanceof ParseError) {
				} else if (error instanceof NoConnectionError) {
				} else if (error instanceof TimeoutError) {
				}
			}
		});

		// Set a retry policy in case of SocketTimeout & ConnectionTimeout
		// Exceptions. Volley does retry for you if you have specified the
		// policy.
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
		// mVolleyQueue.start();
	}

}
