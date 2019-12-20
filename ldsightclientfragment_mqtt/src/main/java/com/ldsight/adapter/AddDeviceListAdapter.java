package com.ldsight.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.StreetAndDevice;

import java.util.ArrayList;

public class AddDeviceListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<StreetAndDevice> streetAndDevices;

	public AddDeviceListAdapter(Context context,
								ArrayList<StreetAndDevice> streetAndDevices) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.streetAndDevices = streetAndDevices;
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
		// TODO Auto-generated method stub
		View view = View.inflate(context, R.layout.add_device_list_item, null);

		TextView idTxt = (TextView) view
				.findViewById(R.id.txt_add_device_list_item_id);
		TextView streetNameTxt = (TextView) view
				.findViewById(R.id.txt_add_device_list_item_street_name);

		idTxt.setText("(" + streetAndDevices.get(position).getStreetId()  + ")");
		streetNameTxt.setText(streetAndDevices.get(position).getStreetName());
		/*Button editButton = (Button) view
				.findViewById(R.id.btn_add_device_edit);
		editButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, DeviceParamAct.class);
				intent.putExtra("streetid", streetAndDevices.get(position)
						.getStreetId());
				context.startActivity(intent);
			}
		});*/
		return view;
	}
}
