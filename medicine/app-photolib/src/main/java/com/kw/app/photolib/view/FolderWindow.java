package com.kw.app.photolib.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;


import com.kw.app.commonlib.utils.ScreenUtil;
import com.kw.app.commonlib.utils.StatusBarUtil;
import com.kw.app.photolib.R;
import com.kw.app.photolib.adapter.ImageFolderAdapter;
import com.kw.app.photolib.bean.FolderModel;
import com.kw.app.widget.view.DivItemDecoration;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Decription 显示文件夹弹窗
 */
public class FolderWindow extends PopupWindow {

    private Context context;
    private View window;
    private RecyclerView recyclerView;
    private ImageFolderAdapter adapter;
    private boolean isDismiss = false;

    public FolderWindow(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.window_folder, null);
        setContentView(window);
        setWidth(ScreenUtil.getScreenWidth(context));
        setHeight(ScreenUtil.getScreenHeight(context) - ScreenUtil.dp2px(context, 90) - StatusBarUtil.getStatusBarHeight(context));
        setAnimationStyle(R.style.WindowStyle);
        setFocusable(true);
        setOutsideTouchable(true);
        update();
        setBackgroundDrawable(new ColorDrawable(Color.argb(153, 0, 0, 0)));

        initView();
        setPopupWindowTouchModal(this, false);
    }

    /**
     * 初始化view，设置布局
     **/
    private void initView() {
        adapter = new ImageFolderAdapter(context);
        recyclerView = (RecyclerView) window.findViewById(R.id.window_folder_list);
        recyclerView.addItemDecoration(new DivItemDecoration(10, false));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    public void bindFolder(List<FolderModel> folders){
        adapter.addData(folders);
    }
    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_in);
        recyclerView.startAnimation(animation);
    }
    public void setOnItemClickListener(ImageFolderAdapter.OnItemClickListener onItemClickListener){
        adapter.setOnItemClickListener(onItemClickListener);
    }
    @Override
    public void dismiss() {
        if(isDismiss){
            return;
        }
        isDismiss = true;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.down_out);
        recyclerView.startAnimation(animation);
        dismiss();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                FolderWindow.super.dismiss();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void setPopupWindowTouchModal(PopupWindow popupWindow,boolean touchModal) {
        if (null == popupWindow) {
            return;
        }
        Method method;
        try {
            method = PopupWindow.class.getDeclaredMethod("setTouchModal",boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
