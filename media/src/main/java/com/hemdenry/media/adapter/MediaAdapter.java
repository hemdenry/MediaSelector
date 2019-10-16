package com.hemdenry.media.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testmediapicture.R;
import com.hemdenry.media.MediaPick;
import com.hemdenry.media.bean.Media;
import com.hemdenry.media.config.MediaConfig;
import com.hemdenry.media.listener.MediaViewer;

import java.util.List;
import java.util.Locale;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    private boolean isShowCamera;
    private List<Media> mSelectList;
    private OnAdapterItemClickListener mOnAdapterItemClickListener;
    private MediaConfig mMediaConfig = MediaPick.getInstance().getMediaConfig();
    private MediaViewer mMediaViewer = MediaPick.getInstance().getMediaConfig().getMediaViewer();

    private final static int HEAD = 0;
    private final static int ITEM = 1;

    public interface OnAdapterItemClickListener {

        void onOpenCamera();

        void onClickMedia(Media media);
    }

    public MediaAdapter(Context context) {
        mContext = context;
        mSelectList = mMediaConfig.getSelectList();
        isShowCamera = mMediaConfig.isShowCamera() && (mMediaConfig.isShowOnlyImage() || mMediaConfig.isShowOnlyVideo());
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public void setAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mOnAdapterItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera && position == 0) {
            return HEAD;
        }
        return ITEM;
    }

    @Override
    public int getItemCount() {
        if (isShowCamera) {
            if (mCursor != null) {
                return mCursor.getCount() + 1;
            } else {
                return 1;
            }
        } else {
            if (mCursor != null) {
                return mCursor.getCount();
            } else {
                return 0;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new CameraHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_camera, parent, false));
        } else {
            return new MediaHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_media, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final int realPosition = viewHolder.getLayoutPosition();
        switch (getItemViewType(position)) {
            case HEAD:
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mSelectList.size() >= mMediaConfig.getMaxSize()) {//当选择数量达到上限时，禁止继续添加
                            Toast.makeText(mContext, String.format(Locale.CHINA, mContext.getResources().getString(R.string.format_max_count), mMediaConfig.getMaxSize()), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mOnAdapterItemClickListener != null) {
                            mOnAdapterItemClickListener.onOpenCamera();
                        }
                    }
                });
                break;
            case ITEM:
                if (isShowCamera) {
                    mCursor.moveToPosition(realPosition - 1);
                } else {
                    mCursor.moveToPosition(realPosition);
                }
                final Media media = Media.valueOf(mCursor);
                final MediaHolder holder = (MediaHolder) viewHolder;
                if (mMediaViewer != null) {
                    mMediaViewer.onDisplayMedia(mContext, media, holder.media);
                }
                if (mMediaConfig.getMaxSize() > 1) {
                    holder.checkBox.setVisibility(View.VISIBLE);
                    if (mSelectList.contains(media)) {
                        holder.mask.setVisibility(View.VISIBLE);
                        holder.checkBox.setChecked(true);
                    } else {
                        holder.mask.setVisibility(View.GONE);
                        holder.checkBox.setChecked(false);
                    }
                } else {
                    holder.mask.setVisibility(View.GONE);
                    holder.checkBox.setVisibility(View.GONE);
                }
                if (media.isVideo()) {
                    holder.videoMark.setVisibility(View.VISIBLE);
                } else {
                    holder.videoMark.setVisibility(View.GONE);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mMediaConfig.getMaxSize() > 1) {
                            if (mSelectList.contains(media)) {
                                mSelectList.remove(media);
                                holder.checkBox.setChecked(false);
                                holder.mask.setVisibility(View.GONE);
                            } else {
                                if (mSelectList.size() >= mMediaConfig.getMaxSize()) {//当选择数量达到上限时，禁止继续添加
                                    Toast.makeText(mContext, String.format(Locale.CHINA, mContext.getResources().getString(R.string.format_max_count), mMediaConfig.getMaxSize()), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mSelectList.add(media);
                                holder.checkBox.setChecked(true);
                                holder.mask.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mSelectList.clear();
                            mSelectList.add(media);
                        }
                        if (mOnAdapterItemClickListener != null) {
                            mOnAdapterItemClickListener.onClickMedia(media);
                        }
                    }
                });
                break;
        }
    }

    private class MediaHolder extends RecyclerView.ViewHolder {

        private ImageView media;
        private View mask;
        private CheckBox checkBox;
        private ImageView videoMark;

        private MediaHolder(View view) {
            super(view);
            media = view.findViewById(R.id.item_media);
            mask = view.findViewById(R.id.item_mask);
            checkBox = view.findViewById(R.id.item_checkbox);
            videoMark = view.findViewById(R.id.item_video_mark);
        }
    }

    private class CameraHolder extends RecyclerView.ViewHolder {
        private CameraHolder(View itemView) {
            super(itemView);
        }
    }
}