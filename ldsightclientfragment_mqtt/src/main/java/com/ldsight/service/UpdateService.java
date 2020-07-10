package com.ldsight.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.ldsightclient_jgd.R;
import com.ldsight.act.ParameterAct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateService extends Service {
	// 标题
	private int titleId = 0;
	// 下载地址
	String downloadPath = "http://121.40.194.91:8080/Androidnew/ass/ldsightclientfragment_2.apk";

	// 文件存储
	private File updateDir = null;
	private File updateFile = null;
	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	private final int SET_PROGRESS = 2;
	private boolean canceledDownload = false;
	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	/***
	 * 创建通知栏
	 */
	RemoteViews contentView;
	// 这样的下载代码很多，我就不做过多的说明
	int downloadCount = 0;
	int currentSize = 0;
	long totalSize = 0;
	int updateTotalSize = 0;

	/* 下载包安装路径 */
	public static final String savePath = "/sdcard/test/"; // 路径
	public static final String saveFileName = "ldgd"; // 包名
	/* 广播 */
	private final static String ACTION_CANCEL_DOWNLOAD_APK = "action_cancel_download_apk";

	// 在onStartCommand()方法中准备相关的下载工作：
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 获取传值
		titleId = intent.getIntExtra("titleId", 0);
		String updatedir = intent.getStringExtra("updatedir");
		if(updatedir != null && updatedir!= ""){
			downloadPath = updatedir;
		}
		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(),
					saveFileName);
			updateFile = new File(updateDir.getPath(), getResources()
					.getString(titleId) + ".apk");
		}

		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();

		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, ParameterAct.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0);
		// 设置通知栏显示内容
		updateNotification.icon = R.drawable.download_notification_logo;
		updateNotification.tickerText = "开始下载";
		updateNotification.setLatestEventInfo(this, "洛丁智慧城市app", "0%",
				updatePendingIntent);
		// 发出通知
		updateNotificationManager.notify(0, updateNotification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SET_PROGRESS:
					int rate = msg.arg1;
					// app.setDownload(true);
					// if (rate < 100) {
					RemoteViews contentview = updateNotification.contentView;
					contentview.setTextViewText(R.id.tv_progress, rate + "%");
					contentview.setProgressBar(R.id.progressbar, 100, rate, false);
					updateNotificationManager.notify(0, updateNotification);
					// }
					break;
				case DOWNLOAD_COMPLETE:
					// 点击安装PendingIntent
					Uri uri = Uri.fromFile(updateFile);
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setDataAndType(uri,
							"application/vnd.android.package-archive");

					updatePendingIntent = PendingIntent.getActivity(
							UpdateService.this, 0, installIntent, 0);

					updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
					updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
					updateNotification.setLatestEventInfo(UpdateService.this,
							"洛丁光电app", "下载完成,点击安装。", updatePendingIntent);
					updateNotificationManager.notify(0, updateNotification);

					// 停止服务
					stopService(updateIntent);
					stopSelf();// 停掉服务自身
					break;
				case DOWNLOAD_FAIL:
					// 下载失败
					if (msg.arg1 == -1) {
						updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
						updateNotification.setLatestEventInfo(UpdateService.this,
								"洛丁光电app", "下载失败", updatePendingIntent);
						updateNotificationManager.notify(0, updateNotification);
					}
					break;
				default:
					stopService(updateIntent);
					stopSelf();// 停掉服务自身
			}
		}
	};

	private int lastRate = 0;

	public void downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		Message message = updateHandler.obtainMessage();
		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			totalSize = 0;
			updateNotification.contentView = new RemoteViews(getPackageName(),
					R.layout.download_notification_layout);
			updateNotification.contentView.setTextColor(R.id.name, 0xffffffff);
			updateNotification.contentView.setTextViewText(R.id.name,
					"洛丁光电app 正在下载...");
			// 发送广播关闭下载事件
			Intent btnCancelIntent = new Intent(ACTION_CANCEL_DOWNLOAD_APK);
			PendingIntent pendButtonIntent = PendingIntent.getBroadcast(this,
					0, btnCancelIntent, 0);
			updateNotification.contentView.setOnClickPendingIntent(
					R.id.ivDelete, pendButtonIntent);
			while ( (readsize = is.read(buffer)) > 0 && !canceledDownload) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;

				/***
				 * 在这里我们用自定的view来显示Notification
				 */
				int progress = (int) (totalSize * 100 / updateTotalSize);
				if (progress >= lastRate + 1) {
					Message msg = updateHandler.obtainMessage();
					msg.what = SET_PROGRESS;
					msg.arg1 = progress;
					updateHandler.sendMessage(msg);
					lastRate = progress;
				}
			}

		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.flush();
				fos.close();
			}
		}
		if(canceledDownload){
			return;
		}

		Log.e("xxx","totalSize = " + totalSize + "    updateTotalSize = " + updateTotalSize);
		if(totalSize == updateTotalSize){
			// 下载成功
			message.what = DOWNLOAD_COMPLETE;
			updateHandler.sendMessage(message);
		}else{
			// 下载失败
			if(totalSize != -1){  // 判断是否手动关闭
				message.what = DOWNLOAD_FAIL;
				updateHandler.sendMessage(message);
			}

		}
	}

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;

			try {
				// 增加权限<USES-PERMISSION
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				// 下载函数
				// 增加权限<USES-PERMISSION
				// android:name="android.permission.INTERNET">;
				downloadUpdateFile(downloadPath, updateFile);

			} catch (Exception ex) {
				ex.printStackTrace();
				// 下载失败
				message.what = DOWNLOAD_FAIL;
				message.arg1 = -1;
				updateHandler.sendMessage(message);
			}
		}
	}

	public static class onclickCancelListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(0);

			Intent updateIntent = new Intent(context, UpdateService.class);
			context.stopService(updateIntent);
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// canceledDownload = true;
		// 测试
		System.out.println("service onDestroy执行");
		super.onDestroy();

	}
}
