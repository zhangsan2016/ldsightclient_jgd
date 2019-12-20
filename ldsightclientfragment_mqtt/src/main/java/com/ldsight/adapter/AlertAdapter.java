package com.ldsight.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.AlertDevice;

import java.util.List;

public class AlertAdapter extends BaseAdapter {

	private List<AlertDevice> alertDevices;
	private Context context;
	String streetName;

	// 构造函数(上下文，集合，街道名)
	public AlertAdapter(Context context, List<AlertDevice> alertDevices,
						String streetName) {
		this.context = context;
		this.alertDevices = alertDevices;
		this.streetName = streetName;
	}

	@Override
	public int getCount() {
		return alertDevices.size();
	}

	@Override
	public Object getItem(int position) {
		return alertDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.alarm_record_item,
					null);
			holder = new ViewHolder();
			holder.streetName = (TextView) convertView
					.findViewById(R.id.tv_street_name);
			holder.alarmType = (TextView) convertView
					.findViewById(R.id.tv_alarm_type);
			holder.date = (TextView) convertView
					.findViewById(R.id.tv_alarm_date);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AlertDevice ad = alertDevices.get(position);
		holder.streetName.setText(streetName);

		if (ad.getAlarmType() == 1) {
			holder.alarmType.setText("电参异常");
		} else if (ad.getAlarmType() == 2) {
			holder.alarmType.setText("设备不在线");
		} else if (ad.getAlarmType() == 3) {
			holder.alarmType.setText("断缆报警");
		}

		holder.date.setText(ad.getDate());

		return convertView;
	}

	class ViewHolder {
		TextView streetName;
		TextView alarmType;
		TextView date;

	}

}
