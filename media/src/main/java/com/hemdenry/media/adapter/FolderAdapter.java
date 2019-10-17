package com.hemdenry.media.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hemdenry.media.MediaPick;
import com.hemdenry.media.R;
import com.hemdenry.media.bean.Folder;
import com.hemdenry.media.listener.MediaViewer;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnFolderItemClickListener onFolderItemClickListener;
    private MediaViewer mMediaViewer = MediaPick.getInstance().getMediaConfig().getMediaViewer();

    public FolderAdapter(Context context) {
        this.mContext = context;
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.media_item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int realPosition = holder.getLayoutPosition();
        if (mCursor == null || mCursor.getCount() == 0) {
            return;
        }
        mCursor.moveToPosition(realPosition);
        final Folder folder = Folder.valueOf(mCursor);
        holder.tvFolderName.setText(folder.getName());
        holder.tvFolderMediaCount.setText(String.valueOf(folder.getCount()));
        if (mMediaViewer != null) {
            mMediaViewer.onDisplayFolder(mContext, folder, holder.ivFolderImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onFolderItemClickListener.onFolderItemClick(folder);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivFolderImage;
        private TextView tvFolderName;
        private TextView tvFolderMediaCount;

        public ViewHolder(View view) {
            super(view);
            ivFolderImage = view.findViewById(R.id.folder_image);
            tvFolderName = view.findViewById(R.id.folder_name);
            tvFolderMediaCount = view.findViewById(R.id.folder_media_count);
        }
    }

    public interface OnFolderItemClickListener {
        void onFolderItemClick(Folder folder);
    }

    public void setOnFolderItemClickListener(OnFolderItemClickListener onFolderItemClickListener) {
        this.onFolderItemClickListener = onFolderItemClickListener;
    }
}