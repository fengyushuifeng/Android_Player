package com.ledongli.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ledongli.player.R;
import com.ledongli.player.net.bean.MovieItemBean;
import com.ledongli.player.utils.SPUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nathen
 * On 2016/02/07 01:20
 */
public class VideoListAdapter extends BaseAdapter {

    public interface ItemClickCallback{
        void onCollectClick(int posi,boolean isChecked);
    }

    Context context;
//    ItemClickCallback itemClickCallback;
    ArrayList<MovieItemBean> dataList;

    public VideoListAdapter(Context context, ArrayList<MovieItemBean> dataList) {
//        this.itemClickCallback = itemClickCallback;
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        if (null == dataList){
            return 0;
        }
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_videolist, null);
            viewHolder.cbCollectStatus = (CheckBox)convertView.findViewById(R.id.item_list_cb_like);
            viewHolder.ivThumb = (ImageView)convertView.findViewById(R.id.item_videolist_iv_thumb);
            viewHolder.tvOnShowTime = (TextView)convertView.findViewById(R.id.item_videolist_tv_time);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.item_videolist_tv_title);
            viewHolder.tvDuration = (TextView)convertView.findViewById(R.id.item_videolist_tv_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //
        final int posi = position;
        viewHolder.cbCollectStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SPUtils.addCollectVideo(context,dataList.get(posi));
                }else{
                    SPUtils.removeCollectVideo(context.getApplicationContext(),dataList.get(posi).id);
                }
            }
        });
        if (SPUtils.isVideoCollected(context,dataList.get(position).id)){
            viewHolder.cbCollectStatus.setChecked(true);
        }else{
            viewHolder.cbCollectStatus.setChecked(false);
        }

        Picasso.with(convertView.getContext())
                .load(dataList.get(position).getCoverimageUrl())
                .into(viewHolder.ivThumb);
        viewHolder.tvTitle.setText(dataList.get(position).title);
        viewHolder.tvOnShowTime.setText(dataList.get(position).getOnShowTimeStr());
        viewHolder.tvDuration.setText(dataList.get(position).getDurationStr());
        return convertView;
    }

    class ViewHolder {
        CheckBox cbCollectStatus;
        ImageView ivThumb;
        TextView tvTitle;
        TextView tvOnShowTime;
        TextView tvDuration;
    }
}
