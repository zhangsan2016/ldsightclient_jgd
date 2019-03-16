package com.ldsight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.ElectricityBox;
import com.ldsight.entity.StreetAndDevice;
import com.ldsight.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TestPatternListAdapter extends BaseAdapter {
    private   List<ElectricityBox.ElectricityBoxList> electricityBoxList;
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<StreetAndDevice> streetAndDevices;
    private boolean[] tags;

    public void selectAll() {
        for (int i = 0; i < tags.length; i++) {
            tags[i] = true;
        }
        TestPatternListAdapter.this.notifyDataSetChanged();
    }

    public void smartSelectAll(boolean select) {
        if (select) {
            for (int i = 0; i < tags.length; i++) {
                tags[i] = true;
            }
        } else {
            for (int i = 0; i < tags.length; i++) {
                tags[i] = false;
            }
        }

        TestPatternListAdapter.this.notifyDataSetChanged();
    }

    public void invertSelect() {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i]) {
                tags[i] = false;
            } else {
                tags[i] = true;
            }
            TestPatternListAdapter.this.notifyDataSetChanged();
        }
    }

    public void selectIn(int position) {
        if (tags[position]) {
            tags[position] = false;
        } else {
            tags[position] = true;
        }
        TestPatternListAdapter.this.notifyDataSetChanged();
    }

    public boolean[] getTags() {
        return tags;
    }

    public void setTags(boolean[] tags) {
        this.tags = tags;
    }
    public void changeTags(){
        LogUtil.e("electricityBoxList.size() = " + electricityBoxList.size());
        this.tags = new boolean[electricityBoxList.size()];
    }

    public TestPatternListAdapter(Context context,
                                  List<ElectricityBox.ElectricityBoxList> electricityBoxList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        //	this.streetAndDevices = streetAndDevices;
        this.electricityBoxList = electricityBoxList;
        mInflater = LayoutInflater.from(context);
      //  tags = new boolean[electricityBoxList.size()];
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return electricityBoxList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return electricityBoxList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View
                .inflate(context, R.layout.test_pattern_list_item, null);
        //	TextView uuid = (TextView) view.findViewById(R.id.txt_test_pattern_list_item_uuid);
        TextView streetName = (TextView) view
                .findViewById(R.id.txt_test_pattern_list_item_street_name);
        CheckBox checkBox = (CheckBox) view
                .findViewById(R.id.checkbtn_test_pattern_list_item_selected);
        if(tags != null && tags.length >= position ){
           checkBox.setChecked(tags[position]);
        }
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                tags[position] = isChecked;
            }
        });
        //	uuid.setText(streetAndDevices.get(position).getStreetId());
        streetName.setText(electricityBoxList.get(position).getText());
        return view;
    }
}
