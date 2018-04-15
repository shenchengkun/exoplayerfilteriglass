package com.iglassus.exoplayerfilter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeItemAdapter extends BaseAdapter{

    private final Context context;
    private String[] titles;
    private String[] subtitles;
    private int[] icons;

    public HomeItemAdapter(Context context,String[] titles,String[] subtitles,int[] icons) {
        this.context=context;
        this.titles=titles;
        this.subtitles=subtitles;
        this.icons=icons;
    }

    @Override
    public int getCount() {
        return titles.length;
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
        convertView= LayoutInflater.from(context).inflate(R.layout.home_item,parent,false);
        TextView title=convertView.findViewById(R.id.title),subtile=convertView.findViewById(R.id.subtitle);
        ImageView imageView=convertView.findViewById(R.id.homeIC);
        title.setText(titles[position]);
        subtile.setText(subtitles[position]);
        imageView.setImageResource(icons[position]);
        return convertView;
    }
}
