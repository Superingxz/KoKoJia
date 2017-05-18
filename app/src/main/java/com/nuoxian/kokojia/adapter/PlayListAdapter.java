package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.M3U8File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/28.
 */
public class PlayListAdapter extends BaseAdapter {

    private Context context;
    private List<M3U8File> filesList;

    public PlayListAdapter(Context context,List<M3U8File> filesList){
        this.context = context;
        this.filesList = filesList;
    }

    @Override
    public int getCount() {
        return filesList.size();
    }

    @Override
    public Object getItem(int position) {
        return filesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.play_list_item,parent,false);
            ViewHolder vh = new ViewHolder();
            vh.fileName = (TextView) convertView.findViewById(R.id.tv_fileName);
            vh.filePath = (TextView) convertView.findViewById(R.id.tv_filePath);
            convertView.setTag(vh);
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();
        vh.fileName.setText(filesList.get(position).getFileName());
        vh.filePath.setText(filesList.get(position).getFilePath());

        return convertView;
    }

    class ViewHolder{
        private TextView fileName,filePath;
    }
}
