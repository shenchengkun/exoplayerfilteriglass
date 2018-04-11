package com.iglassus.exoplayerfilter.youtubeData;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iglassus.exoplayerfilter.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ListAdapter extends Adapter<ListAdapter.ListHolder> {
    public List<data> myData;
    private AdapterView.OnItemClickListener mClickListener;
    private OnItemClickListener mOnItemClickListener;

    public static class ListHolder extends ViewHolder implements OnClickListener {
        public static final String ListKey = "LIST";
        private TextView channel;
        private TextView dates;
        private TextView duration;
        private ImageView image;
        private ImageView playlist;
        private TextView title;

        public ListHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.title = (TextView) itemView.findViewById(R.id.title);
            //this.channel = (TextView) itemView.findViewById(R.id.channel);
            //this.playlist = (ImageView) itemView.findViewById(R.id.playlist);
            this.dates = (TextView) itemView.findViewById(R.id.date);
            this.duration = (TextView) itemView.findViewById(R.id.duration);
        }

        public void bind(data myData) {
            Picasso.with(this.image.getContext()).load(myData.getImageId()).fit().centerInside().into(this.image);
            this.title.setText(myData.getTitle() + "");
            this.dates.setText(myData.getDate() + myData.getCount());
            //this.channel.setText(myData.getChannel() + "");
            this.duration.setText(myData.getDuration()+"•"+myData.getChannel());
        }

        public void onClick(View view) {
            System.out.println("clicked");
        }
    }

    public ListAdapter(List<data> myData) {
        this.myData = myData;
    }

    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false));
    }

    public void onBindViewHolder(ListHolder holder, final int position) {
        holder.bind((data) this.myData.get(position));
        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    public int getItemCount() {
        return this.myData.size();
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener=onItemClickListener;
    }
}
