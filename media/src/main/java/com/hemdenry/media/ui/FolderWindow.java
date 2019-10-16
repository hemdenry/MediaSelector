package com.hemdenry.media.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmediapicture.R;
import com.hemdenry.media.adapter.FolderAdapter;

public class FolderWindow extends PopupWindow {

    private Context mContext;
    private View mView;
    private RecyclerView mRecyclerView;
    private FolderAdapter mFolderAdapter;

    public FolderWindow(Context context, FolderAdapter folderAdapter) {
        super(context);
        mContext = context;
        mFolderAdapter = folderAdapter;
        mView = LayoutInflater.from(mContext).inflate(R.layout.media_window_folder, null);
        setContentView(mView);

        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.WindowAnimation);
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.white));
        setBackgroundDrawable(dw);

        initView();
    }

    private void initView() {
        mRecyclerView = mView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mFolderAdapter);
    }

    public void showPopupWindow(View parent) {
        if (isShowing()) {
            dismiss();
        } else {
            showAsDropDown(parent, 0, 0);
        }
    }
}