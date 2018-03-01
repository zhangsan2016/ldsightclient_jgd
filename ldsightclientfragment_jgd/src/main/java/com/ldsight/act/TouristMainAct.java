package com.ldsight.act;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.StreetAndDevice;

import java.util.ArrayList;

public class TouristMainAct extends Activity {

	private  ArrayList<StreetAndDevice> streetAndDevices;
	private ListView touristMainLList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.tourist_main);
		touristMainLList = (ListView) this.findViewById(R.id.main_list);
		streetAndDevices = new ArrayList<StreetAndDevice>();
		initData();
		TouristMainListAdapter adapter = new TouristMainListAdapter();

		touristMainLList.setAdapter(adapter);


	}


	/*
	 * 初始化数据
	 */
	private void initData(){

		StreetAndDevice StreetAndDevice1 = new StreetAndDevice();
		StreetAndDevice StreetAndDevice2 = new StreetAndDevice();
		streetAndDevices.add(StreetAndDevice1);
		streetAndDevices.add(StreetAndDevice2);




	}

	class TouristMainListAdapter extends BaseAdapter{

		@Override
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
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(TouristMainAct.this,R.layout.main_list_item, null);
			return convertView;
		}


	}


}


