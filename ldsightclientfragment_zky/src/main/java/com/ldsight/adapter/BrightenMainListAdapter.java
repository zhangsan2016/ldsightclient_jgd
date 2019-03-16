package com.ldsight.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.act.BrightenMainListDialogItemAct;
import com.ldsight.entity.BrightenDevice;

import java.text.DecimalFormat;
import java.util.List;

public class BrightenMainListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BrightenDevice> brightenDevices;
    private LayoutInflater mInflater;
    DecimalFormat df;

    public BrightenMainListAdapter(Context context,
                                   List<BrightenDevice> brightenDevices) {

        this.mContext = context;
        this.brightenDevices = brightenDevices;
        mInflater = LayoutInflater.from(mContext);

        df = new DecimalFormat("000");
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return brightenDevices.size();
    }

    @Override
    public BrightenDevice getItem(int id) {
        // TODO Auto-generated method stub
        return brightenDevices.get(id);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public int getItemViewType(int position) {
    return  position % 2;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.brighten_main_item, null);


            if (getItemViewType(position) == 0) {
                convertView.setBackgroundResource(R.drawable.brighten_main_listview_item_bg);
            }
            holder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            holder.tv_state = (TextView) convertView
                    .findViewById(R.id.tv_state);
            holder.tv_details = (TextView) convertView
                    .findViewById(R.id.tv_details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BrightenDevice brightenDevice = brightenDevices.get(position);

        int id = brightenDevice.getDeviceId();
        int status = brightenDevice.getStatus();

        holder.tv_id.setText(df.format(id));
        holder.tv_state.setText(status + "");

        // 设置详情点击事件
        holder.tv_details.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,
                        BrightenMainListDialogItemAct.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("equipment_information", brightenDevice);
                intent.putExtras(mBundle);
                mContext.startActivity(intent);

				
			/*	Intent intent = new Intent(mContext,
                        ElectricBoxInformationDialog.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("equipment_information", brightenDevice);
				intent.putExtras(mBundle);
				mContext.startActivity(intent);*/

            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tv_id;
        TextView tv_state;
        TextView tv_details;

    }

}
