package com.example.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hemdenry.media.bean.Media;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private List<Media> mMediaList;

    public ImageAdapter(Context context) {
        mContext = context;
    }

    public void refreshData(List<Media> list) {
        mMediaList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mMediaList != null) {
            return mMediaList.size();
        }
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_image, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int realPosition = holder.getLayoutPosition();
        Media media = mMediaList.get(realPosition);
        Glide.with(mContext).load(media.getUri()).apply(new RequestOptions().centerCrop()).into(holder.image);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        private ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.item_image);
        }
    }
}