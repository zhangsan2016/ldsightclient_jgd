package com.ldsight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.application.MyApplication;
import com.ldsight.entity.SingleLightDevice2;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.util.List;

public class SingleLightControlAdapter extends BaseAdapter {
	private List<SingleLightDevice2> singleLightDeviceList;
	private Context context;
	private byte[] uuid;

	public SingleLightControlAdapter(Context context,
									 List<SingleLightDevice2> DeviceList) {
		this.context = context;
		singleLightDeviceList = DeviceList;

	}

	public void setUUID(byte[] pUuid){
		this.uuid = pUuid;
	}

	@Override
	public int getCount() {
		return singleLightDeviceList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return singleLightDeviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.single_light_control_item, null);
			holder = new ViewHolder();
			holder.deviceId = (TextView) convertView.findViewById(R.id.id);
			holder.lineState = (TextView) convertView
					.findViewById(R.id.line_state);
			holder.stat_light = (Button) convertView
					.findViewById(R.id.stat_light);
			holder.stop_light = (Button) convertView
					.findViewById(R.id.stop_light);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.deviceId.setText(singleLightDeviceList.get(position)
				.getDeviceId());
		holder.lineState.setText(singleLightDeviceList.get(position)
				.getLineState() + "");
		holder.stat_light.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread() {
					public void run() {
						Pusher pusher = null;
						// 指令 设备地址 光强
						byte[] data = new byte[] {
								65,
								Byte.parseByte(singleLightDeviceList
										.get(position).getDeviceId().trim()),
								100 };
						try {
							pusher = new Pusher(context.getResources()
									.getString(R.string.ip), 9966, 5000);
							pusher.push0x20Message(
									uuid, data);
							// pusher.push0x20Message(new byte[]{3, 86, -128,
							// 32, 48, 6,
							// 17, 18}, data);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (pusher != null) {
								try {
									pusher.close();
								} catch (Exception e) {
								}
							}
						}
					};
				}.start();

			}
		});

		holder.stop_light.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						Pusher pusher = null;
						// 指令 设备地址 光强
						byte[] data = new byte[] {
								65,
								Byte.parseByte(singleLightDeviceList
										.get(position).getDeviceId().trim()), 0 };
						try {

							pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
							pusher.push0x20Message(
									uuid, data);
							// pusher.push0x20Message(new byte[]{3, 86, -128,
							// 32, 48, 6,
							// 17, 18}, data);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (pusher != null) {
								try {
									pusher.close();
								} catch (Exception e) {
								}
							}
						}
					};
				}.start();
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView deviceId;
		TextView lineState;
		Button stat_light;
		Button stop_light;
		TextView psum;
		TextView cable1;
		TextView cable2;
		TextView cable3;
		TextView cable4;
		TextView energy;
		RelativeLayout power;
		TextView b_volt;
		TextView c_volt;

	}

}
