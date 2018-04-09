package com.ldgd.em.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldgd.em.R;
import com.ldgd.em.domain.Monitoring;

import java.util.List;

/**
 * 作者：杨光福 on 2016/7/18 10:16
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：NetVideoPager的适配器
 */
public class DeviceListAdapter extends BaseAdapter {

    private  Context context;
    private final List<Monitoring> monitoringItems;

    public DeviceListAdapter(Context context, List<Monitoring> monitoringItems){
        this.context = context;
        this.monitoringItems = monitoringItems;
    }

    @Override
    public int getCount() {
        return monitoringItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if(convertView ==null){
            convertView = View.inflate(context, R.layout.item_monitoring_pager,null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_serialumber = (TextView) convertView.findViewById(R.id.tv_serialumber);

            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHoder) convertView.getTag();
        }

        //根据position得到列表中对应位置的数据
        Monitoring monitoringItem = monitoringItems.get(position);
        viewHoder.tv_name.setText(monitoringItem.getName());
        viewHoder.tv_serialumber.setText(monitoringItem.getSerialumber());
        //1.使用xUtils3请求图片
//        x.image().bind(viewHoder.iv_icon,mediaItem.getImageUrl());
        //2.使用Glide请求图片
//        Glide.with(context).load(mediaItem.getImageUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHoder.iv_icon);

        //3.使用Picasso 请求图片
//        Picasso.with(context).load(mediaItem.getImageUrl())
////                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHoder.iv_icon);


        return convertView;
    }


    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;   // 监控名
        TextView tv_serialumber;  // 序列号
    }

}

