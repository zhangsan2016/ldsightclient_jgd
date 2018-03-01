package com.ldsight.adapter;

import java.net.ContentHandler;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.volley.toolbox.ImageLoader;
import com.example.ldsightclient_jgd.R;
import com.ldsight.act.AlarmRecordAct;
import com.ldsight.entity.StreetAndDevice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextPaint;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<StreetAndDevice> streetAndDevices;
	private int hour;
	private int minute;

	public MainListAdapter(Context context,
						   ArrayList<StreetAndDevice> streetAndDevices,
						   List<String> cableIsAbnormal) {
		this.context = context;
		this.streetAndDevices = streetAndDevices;
		mInflater = LayoutInflater.from(context);

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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 如果数据不是十分钟之内的设置为不显示
		boolean tenTime = true;  // 标识有无十分种以内的新数据
		MyData myData1 = new MyData();
		MyData myData2 = new MyData();
		String myData = streetAndDevices.get(position).getMb_time();
		// String myData = "Mon Jan 18 22:44:23 CST 2016";
		SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
		try {
			Date dateParse = sdf1.parse(myData);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateParse);
			myData1.setYear(cal.get(Calendar.YEAR));// 获取年份
			myData1.setMonth(cal.get(Calendar.MONTH) + 1);// 获取月份
			myData1.setDay(Integer.parseInt(myData.substring(8, 10)));// 获取日
			myData1.setHour(Integer.parseInt(myData.substring(11, 13)));// 小时
			myData1.setMinute(cal.get(Calendar.MINUTE));// 分

			// 获取系统时间作为比较
			Calendar sysTime = Calendar.getInstance();
			myData2.setYear(sysTime.get(Calendar.YEAR));// 获取年份
			myData2.setMonth(sysTime.get(Calendar.MONTH) + 1);// 获取月份//（月份从0开始要+1）
			myData2.setDay(sysTime.get(Calendar.DATE));// 获取日
			myData2.setHour(sysTime.get(Calendar.HOUR_OF_DAY));// 小时
			myData2.setMinute(sysTime.get(Calendar.MINUTE));// 分


		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "日期参数错误"+ e.toString(), 0).show();
		}

		// 判断是否十分钟之内上传的数据
		if (myData1.getYear() != myData2.getYear()
				|| myData1.getMonth() != myData2.getMonth()
				|| myData1.getDay() != myData2.getDay()
				|| myData1.getHour() != myData2.getHour()
				|| (myData2.getMinute() - myData1.getMinute()) > 10 || myData2.getMinute() - myData1.getMinute() < 0) {


			tenTime = false;
		}



		// 设置View参数
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_list_item, null);
			holder = new ViewHolder();
			holder.streetName = (TextView) convertView
					.findViewById(R.id.txt_street_name);
			holder.lifeCycle = (TextView) convertView
					.findViewById(R.id.txt_life_cycle);
			holder.volt = (TextView) convertView.findViewById(R.id.txt_volt);
			holder.ampere = (TextView) convertView
					.findViewById(R.id.txt_ampere);
			holder.psum = (TextView) convertView.findViewById(R.id.txt_psum);
			holder.electricBoxState = (TextView) convertView
					.findViewById(R.id.tv_electric_box_state);

			holder.energy = (TextView) convertView
					.findViewById(R.id.txt_energy);
//			holder.power = (RelativeLayout) convertView
//					.findViewById(R.id.layout_main_list_item_layout);
			holder.b_volt = (TextView) convertView.findViewById(R.id.txt_Bvolt);
			holder.c_volt = (TextView) convertView.findViewById(R.id.txt_Cvolt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(tenTime){
			holder.lifeCycle.setText(""
					+ streetAndDevices.get(position).getStartTime() + ":"
					+ streetAndDevices.get(position).getEndTime());
			holder.streetName.setText(streetAndDevices.get(position)
					.getStreetName());

			DecimalFormat df = new DecimalFormat("######0.0 ");
			holder.ampere.setText(""
					+ df.format(((long) streetAndDevices.get(position)
					.getMb_a_Ampere()) / 1000) + "A");

			DecimalFormat voltDF = new DecimalFormat("######0.0");
			holder.volt.setText(""
					+ voltDF.format(((double) streetAndDevices.get(position)
					.getMb_a_volt()) / 100) + "V");

			holder.psum.setText(""
					+ voltDF.format(((double) streetAndDevices.get(position)
					.getMb_psum()) / 100000) + "KW");
			// A B C 相电压
			holder.b_volt.setText(df.format(((long) streetAndDevices.get(position)
					.getMb_b_volt()) / 100) + "V");
			holder.c_volt.setText(df.format(((long) streetAndDevices.get(position)
					.getMb_c_volt()) / 100) + "V");

			// 根据当前时间判断节能状态
		/*	Calendar ca = Calendar.getInstance();
			hour = ca.get(Calendar.HOUR_OF_DAY);
			minute = ca.get(Calendar.MINUTE);*/

			// ca.setTime(new java.util.Date());
			// int hour = ca.HOUR_OF_DAY;
			// int minute = ca.MINUTE;
			int energy100StartHour = Integer.parseInt(streetAndDevices
					.get(position).getStartTime().split(":")[0]);
			int energy100EndHour = Integer.parseInt(streetAndDevices.get(position)
					.getStartTime().split(":")[0])
					+ Integer.parseInt(streetAndDevices.get(position)
					.getEnergy100().split(":")[0]);
			int energy100StartMinute = Integer.parseInt(streetAndDevices
					.get(position).getStartTime().split(":")[1]);
			int energy100EndMinute = Integer.parseInt(streetAndDevices
					.get(position).getStartTime().split(":")[1])
					+ Integer.parseInt(streetAndDevices.get(position)
					.getEnergy100().split(":")[1]);

			int energy75StartHour = energy100EndHour;
			int energy75EndHour = energy75StartHour
					+ Integer.parseInt(streetAndDevices.get(position).getEnergy75()
					.split(":")[0]);
			int energy75StartMinute = energy100EndMinute;
			int energy75EndMinute = energy75StartMinute
					+ Integer.parseInt(streetAndDevices.get(position).getEnergy75()
					.split(":")[1]);

			int energy50StartHour = energy75EndHour;
			int energy50EndHour = energy50StartHour
					+ Integer.parseInt(streetAndDevices.get(position).getEnergy50()
					.split(":")[0]);
			int energy50StartMinute = energy75EndMinute;
			int energy50EndMinute = energy50StartMinute
					+ Integer.parseInt(streetAndDevices.get(position).getEnergy50()
					.split(":")[1]);

			int energy25StartHour = energy50EndHour;
			int energy25EndHour = energy25StartHour
					+ Integer.parseInt(streetAndDevices.get(position).getEnergy25()
					.split(":")[0]);
			int energy25StartMinute = energy25EndHour;
			int energy25EndMinute = energy25EndHour
					+ Integer.parseInt(streetAndDevices.get(position).getEnergy25()
					.split(":")[1]);

			if (hour >= energy100StartHour && hour < energy100EndHour) {
				System.out.println("100%");
				holder.energy.setText("100%");
			} else if (hour >= energy75StartHour && hour < energy75EndHour) {
				System.out.println("75%");
				holder.energy.setText("75%");
			} else if (hour >= checkBig24(energy50StartHour)
					&& hour < checkBig24(energy50EndHour)) {
				System.out.println("50%");
				holder.energy.setText("50%");
			} else if (hour >= checkBig24(energy25StartHour)
					&& hour <= checkBig24(energy25EndHour)) {
				System.out.println("25%");
				holder.energy.setText("25%");
			} else {
				System.out.println("0%");
				holder.energy.setText("0%");
			}

		}else{
			holder.lifeCycle.setText(""
					+ streetAndDevices.get(position).getStartTime() + ":"
					+ streetAndDevices.get(position).getEndTime());
			holder.streetName.setText(streetAndDevices.get(position)
					.getStreetName());
		}
		// 获取报警类型
		int alarmType = streetAndDevices.get(position).getAlarmType();
		if(alarmType == 1){
			holder.electricBoxState.setTextColor(context.getResources().getColor(R.color.red));
			holder.electricBoxState.setText("电参异常");
		}else if (alarmType == 2) {
			holder.electricBoxState.setTextColor(context.getResources().getColor(R.color.red));
			holder.electricBoxState.setText("设备不在线");
		}else if (alarmType == 3) {
			holder.electricBoxState.setTextColor(context.getResources().getColor(R.color.red));
			holder.electricBoxState.setText("断缆报警");
		}

		final String alarmDeviceId = streetAndDevices.get(position).getDeviceId();
		final String streetName =  streetAndDevices.get(position).getStreetName();
		LinearLayout aa = (LinearLayout) convertView.findViewById(R.id.ll_alarm_record);
		aa.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent= new Intent(context,AlarmRecordAct.class);
				Bundle bundle = new Bundle();
				bundle.putString("DeviceId", alarmDeviceId);
				bundle.putString("streetName", streetName);
				intent.putExtras(bundle);
				context.startActivity(intent);

			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView streetName;
		TextView lifeCycle;
		TextView volt;
		TextView ampere;
		TextView psum;
		TextView electricBoxState;
		TextView energy;
		RelativeLayout power;
		TextView b_volt;
		TextView c_volt;

	}

	/**
	 * 转换24小时的时间格式
	 *
	 * @param checkTime
	 * @return
	 */
	private int checkBig24(int checkTime) {
		if (checkTime >= 24) {
			// checkTime = (checkTime % 12) == 0 ? 12 : (checkTime % 12);
			checkTime = (checkTime % 12);
		}
		return checkTime;
	}

	/**
	 * 时间参数内部类
	 *
	 * @author Administrator
	 *
	 */
	private class MyData {
		private int year;// 年份
		private int month;// 月份
		private int day;// 获取日
		private int hour;// 小时
		private int minute;// 分

		public MyData() {
			super();
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getMonth() {
			return month;
		}

		public void setMonth(int month) {
			this.month = month;
		}

		public int getDay() {
			return day;
		}

		public void setDay(int day) {
			this.day = day;
		}

		public int getHour() {
			return hour;
		}

		public void setHour(int hour) {
			this.hour = hour;
		}

		public int getMinute() {
			return minute;
		}

		public void setMinute(int minute) {
			this.minute = minute;
		}

	}

}
