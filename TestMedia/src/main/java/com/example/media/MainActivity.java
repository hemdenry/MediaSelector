package com.example.media;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hemdenry.media.MediaPick;
import com.hemdenry.media.bean.Folder;
import com.hemdenry.media.bean.Media;
import com.hemdenry.media.config.MediaConfig;
import com.hemdenry.media.listener.MediaViewer;
import com.hemdenry.media.listener.SelectMediaListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;

    private MediaConfig mMediaConfig;
    private MediaViewer mMediaViewer;
    private SelectMediaListener mSelectMediaListener;
    private List<Media> mMediaList;

    public static String MEDIA_DIR = "media";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mMediaConfig.setSelectList(mMediaList);
                MediaPick.getInstance().setMediaConfig(mMediaConfig).open(MainActivity.this);
            }
        });

        initView();
        initImageConfig(true, 1);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mImageAdapter = new ImageAdapter(this);
        mRecyclerView.setAdapter(mImageAdapter);
    }

    private void initImageConfig(boolean isCrop, int size) {
        mSelectMediaListener = new SelectMediaListener() {

            @Override
            public void onSelectMedia(List<Media> mediaList) {
                mMediaList = mediaList;
                mImageAdapter.refreshData(mediaList);
                for (Media media : mediaList) {
                    Log.e("Fanghui", "uri = " + media.getUri());
                }
            }
        };

        mMediaViewer = new MediaViewer() {

            @Override
            public void onDisplayFolder(Context context, Folder folder, ImageView imageView) {
                RequestOptions options = new RequestOptions()
                        .frame(0)
                        .centerCrop()
                        .skipMemoryCache(true);
                Glide.with(context).load(folder.getUri()).apply(options).into(imageView);
            }

            @Override
            public void onDisplayMedia(Context context, Media media, ImageView imageView) {
                RequestOptions options = new RequestOptions()
                        .frame(0)
                        .centerCrop()
                        .skipMemoryCache(true);
                Glide.with(context).load(media.getUri()).apply(options).into(imageView);
            }
        };

        mMediaConfig = new MediaConfig();
        mMediaConfig.mediaViewer(mMediaViewer)
                .handleListener(mSelectMediaListener)
                .provider("com.example.media.fileprovider")
                .isShowCamera(true)
                .maxSize(size)
                .directory(MEDIA_DIR)
                .crop(isCrop, 0.0f, 0.0f, 1080, 2160)
                .showCropFrame(false)
                .freeStyleEnable(true)
                .isShowOnlyImage(true)
                .memorizeHistory(false);
    }
}