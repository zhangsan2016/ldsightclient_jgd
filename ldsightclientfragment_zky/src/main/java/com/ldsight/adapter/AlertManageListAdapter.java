package com.ldsight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ldsightclient_jgd.R;
import com.ldsight.application.MyApplication;
import com.ldsight.entity.StreetAndDevice;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.util.ArrayList;
import java.util.Arrays;

public class AlertManageListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<StreetAndDevice> streetAndDevices;
	private boolean[] tags;

	public void selectAll() {
		for (int i = 0; i < tags.length; i++) {
			tags[i] = true;
		}
		AlertManageListAdapter.this.notifyDataSetChanged();
	}

	public void invertSelect() {
		for (int i = 0; i < tags.length; i++) {
			if (tags[i]) {
				tags[i] = false;
			} else {
				tags[i] = true;
			}
			AlertManageListAdapter.this.notifyDataSetChanged();
		}
	}

	public boolean[] getTags() {
		return tags;
	}

	public void setTags(boolean[] tags) {
		this.tags = tags;
	}

	public AlertManageListAdapter(Context context,
								  ArrayList<StreetAndDevice> streetAndDevices) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.streetAndDevices = streetAndDevices;
		mInflater = LayoutInflater.from(context);
		tags = new boolean[this.streetAndDevices.size()];
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return streetAndDevices.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = View
				.inflate(context, R.layout.alert_manage_list_item, null);
		TextView txtStreetId = (TextView) view
				.findViewById(R.id.txt_alert_manage_list_item_id);
		TextView streetName = (TextView) view
				.findViewById(R.id.txt_alert_manage_list_item_street_name);
		ToggleButton toggleButton = (ToggleButton) view
				.findViewById(R.id.toggleButton1);
		toggleButton.setChecked(tags[position]);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				tags[position] = isChecked;
				if (isChecked) {
					System.out.println("开报警");
					new Thread() {
						public void run() {
							Pusher pusher = null;
							try {
								byte[] data = new byte[] { 5, 1 };
								System.out.println(Arrays.toString(data));
								pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
								pusher.push0x20Message(
										streetAndDevices.get(position)
												.getByteUuid(), data);
								for (int i = 0; i < 2; i++) {
									Thread.sleep(1000);
									pusher.push0x20Message(streetAndDevices
											.get(position).getByteUuid(), data);
								}
								pusher.push0x20Message(
										streetAndDevices.get(position)
												.getByteUuid(),
										new byte[] { 0 });
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (pusher != null) {
									try {
										pusher.close();
									} catch (Exception e) {
									}
								}
							}
							// showToast("发送成功");
						};
					}.start();
				} else {
					System.out.println("关报警");
					new Thread() {
						public void run() {
							Pusher pusher = null;
							try {
								byte[] data = new byte[] { 5, 0 };
								System.out.println(Arrays.toString(data));
								pusher = new Pusher(MyApplication.getIp(), 9966, 5000);
								pusher.push0x20Message(
										streetAndDevices.get(position)
												.getByteUuid(), data);
								for (int i = 0; i < 2; i++) {
									Thread.sleep(1000);
									pusher.push0x20Message(streetAndDevices
											.get(position).getByteUuid(), data);
								}
								pusher.push0x20Message(
										streetAndDevices.get(position)
												.getByteUuid(),
										new byte[] { 0 });
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (pusher != null) {
									try {
										pusher.close();
									} catch (Exception e) {
									}
								}
							}
							// showToast("发送成功");
						};
					}.start();
				}
			}
		});
		txtStreetId.setText("(" + streetAndDevices.get(position).getStreetId() + ")");
		streetName.setText(streetAndDevices.get(position).getStreetName());
		return view;
	}
}
