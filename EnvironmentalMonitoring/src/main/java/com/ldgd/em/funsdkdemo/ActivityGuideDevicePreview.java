package com.ldgd.em.funsdkdemo;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ldgd.em.R;
import com.ldgd.em.act.ActivityDemo;
import com.ldgd.em.funsdk.support.FunError;
import com.ldgd.em.funsdk.support.FunSupport;
import com.ldgd.em.funsdk.support.models.FunDevice;
import com.ldgd.em.funsdk.support.widget.FunVideoView;
import com.ldgd.em.funsdkdemo.adapter.GridCameraChannelsPreviewsAdapter;

import java.util.ArrayList;
import java.util.List;


public class ActivityGuideDevicePreview extends ActivityDemo implements OnClickListener,OnInfoListener,OnErrorListener{
	
	private GridView gridview;
	private FunVideoView funVideoView;
	private TextView textStart;
	private TextView mTextTitle;
	private ImageButton mBtnBack;
	private GridCameraChannelsPreviewsAdapter cadapter;
	private FunDevice mFunDevice;
	private List<TextView> textvlist = new ArrayList<TextView>();
	private List<FunVideoView> funvideovlist = new ArrayList<FunVideoView>();
	private final int MESSAGE_PLAY_MEDIA = 0x100;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_PLAY_MEDIA:
				playrealvideo();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		init();
		
		Message message = new Message();
		message.what = MESSAGE_PLAY_MEDIA;
		mHandler.sendMessageDelayed(message, 100);
	}
	
	public void init(){
		
		int devid = getIntent().getIntExtra("FUNDEVICE_ID", 0);
		mFunDevice = FunSupport.getInstance().findDeviceById(devid);
		
		setContentView(R.layout.activity_device_camera_channelspreview);
		gridview = (GridView) findViewById(R.id.Frames_grid_view);
		mTextTitle = (TextView) findViewById(R.id.textViewInTopLayout);
		mTextTitle.setText(R.string.device_camera_channels_preview_title);
		mBtnBack = (ImageButton) findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);
		
		cadapter = new GridCameraChannelsPreviewsAdapter(this, 4);
		gridview.setAdapter(cadapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
					stopMedia();
					ActivityGuideDevicePreview.this.setResult(position, null);
					ActivityGuideDevicePreview.this.finish();
			}
		});
	}
	
	public void playrealvideo(){
		for (int i = 0; i < 4; i++) {
			View v = gridview.findViewWithTag(i);
			if ( null != v ) {
				funVideoView = (FunVideoView)v.findViewById(R.id.funVideoView1);
				textStart = (TextView) v.findViewById(R.id.textVideoStat1);
			}
			funVideoView.setOnErrorListener(this);
			funVideoView.setOnInfoListener(this);
			funvideovlist.add(funVideoView);
			textvlist.add(textStart);
			// 显示状态: 正在打开视频...
			textStart.setText(R.string.media_player_opening);
			textStart.setVisibility(View.VISIBLE);
			
//			cadapter.notifyDataSetInvalidated();

			if (mFunDevice.isRemote) {
				funVideoView.setRealDevice(mFunDevice.getDevSn(), i);
			} else {
				String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
				funVideoView.setRealDevice(deviceIp, i);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backBtnInTopLayout:
			    stopMedia();
				finish();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		stopMedia();
		super.onDestroy();
	}
	
	private void stopMedia() {
		if (funvideovlist != null) {
		
		for (int i = 0; i < funvideovlist.size(); i++) {
			
			if (null != funvideovlist.get(i)) {
				funvideovlist.get(i).stopPlayback();
				funvideovlist.get(i).stopRecordVideo();
			}
		}
	    }
	}


	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		// 播放失败
				showToast(getResources().getString(R.string.media_play_error) 
						+ " : " 
						+ FunError.getErrorStr(extra));
				return true;
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			textvlist.get(extra).setText(R.string.media_player_buffering);
			textvlist.get(extra).setVisibility(View.VISIBLE);
		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			textvlist.get(extra).setVisibility(View.GONE);
		}
		return true;
	}
	
}
