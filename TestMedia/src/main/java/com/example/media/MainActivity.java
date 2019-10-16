package com.example.media;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hemdenry.media.MediaPick;
import com.hemdenry.media.bean.Media;
import com.hemdenry.media.config.MediaConfig;
import com.hemdenry.media.listener.SelectMediaListener;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;

    private MediaConfig mMediaConfig;
    private SelectMediaListener mSelectMediaListener;
    private List<Media> mMediaList;

    public static String MEDIA_DIR = File.separator + "media";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //mMediaConfig.setSelectList(mImageList);
                MediaPick.getInstance().setMediaConfig(mMediaConfig).open(MainActivity.this);
            }
        });

        initView();
        initImageConfig(true, 3);
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
                    Log.e("Fanghui", "path = " + media.getPath());
                }
            }
        };

        mMediaConfig = new MediaConfig.Builder()
                .handleListener(mSelectMediaListener)
                .provider("com.example.media.fileprovider")
                .isShowCamera(true)
                .maxSize(size)
                .filePath(MEDIA_DIR)
                .crop(isCrop, 0.0f, 0.0f, 1080, 2160)
                .showCropFrame(false)
                .freeStyleEnable(true)
                .isShowOnlyVideo(true)
                .memorizeHistory(false)
                .build();
    }
}