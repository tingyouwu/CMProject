package com.kw.app.medicine.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kw.app.commonlib.base.AppConstant;
import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.mvp.contract.IMyAccountContract;
import com.kw.app.medicine.mvp.presenter.MyAccountPresenter;
import com.kw.app.photolib.activity.ImageSelectorActivity;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.adapter.DialogCenterListAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 作者：samsung on 2016/9/23 14:02
 * 邮箱：kuangminan456123@163.com
 */
public class MyAccountActivity extends BaseActivity<MyAccountPresenter> implements IMyAccountContract.IMyAccountView{

    @Bind(R.id.img_my_portrait)
    ImageView mImageView;
    @Bind(R.id.tv_my_username)
    TextView mMyName;
    @Bind(R.id.tv_my_id)
    TextView mMyId;

    @OnClick({R.id.rl_my_portrait,R.id.rl_my_username})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_my_portrait:
                showPhotoDialog();
                break;
            case R.id.rl_my_username:
                startActivity(new Intent(this, UpdateNameActivity.class));
                break;
        }
    }

    @Override
    public MyAccountPresenter getPresenter() {
        return new MyAccountPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //昵称更改返回后需要重新设置
        String name = PreferenceUtil.getInstance().getLastName();
        mMyName.setText(name);
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("修改个人信息");
        mMyName.setText(PreferenceUtil.getInstance().getLastName());
        mMyId.setText(PreferenceUtil.getInstance().getLastAccount());
        ImageLoaderUtil.loadCircle(this, PreferenceUtil.getInstance().getLogoUrl(), R.mipmap.icon_launcher,mImageView);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_my_count;
    }

    private void updateImageToBmob(Uri uri){
        if(super.submit()){
            mPresenter.updateHeadImage(this, uri);
        }
    }

    /**
     * 弹出底部框
     */
    @TargetApi(23)
    private void showPhotoDialog() {
        List<String> data = new ArrayList<String>();
        data.add("选择图片");
        data.add("取消");

        final DialogPlus dialog = DialogPlus.newDialog(MyAccountActivity.this)
                .setContentHolder(new ListHolder())
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentBackgroundResource(R.drawable.bg_dialog_list)
                .setAdapter(new DialogCenterListAdapter(MyAccountActivity.this, data))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        switch (position){
                            case 0://选择图片
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //适配android6.0动态权限
                                    RxPermissions.getInstance(MyAccountActivity.this)
                                            .request(Manifest.permission.CAMERA,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.RECORD_AUDIO)
                                            .subscribe(new Action1<Boolean>() {
                                                @Override
                                                public void call(Boolean aBoolean) {
                                                    if(aBoolean){
                                                        // All requested permissions are granted
                                                        ImageSelectorActivity.start(MyAccountActivity.this,1,ImageSelectorActivity.MODE_SINGLE,true,
                                                        false,true,null);
                                                    }else{
                                                        // At least one permission is denied
                                                    }
                                                }
                                            });
                                }else{
                                    ImageSelectorActivity.start(MyAccountActivity.this,1,ImageSelectorActivity.MODE_SINGLE,true, false,true,null);
                                }
                                dialog.dismiss();
                                break;
                            case 1://取消
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }

                    }
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//从选择图片页面返回
            if (requestCode == AppConstant.ActivityResult.Request_Image) {
                //拿到返回的图片路径
                ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                if(images != null && images.size()>0){
                    updateImageToBmob(Uri.fromFile(new File(images.get(0))));
                }
            }
        }
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    @Override
    public void updateIcon(String path) {
        ImageLoaderUtil.loadCircle(this, PreferenceUtil.getInstance().getLogoUrl(), R.mipmap.icon_launcher,mImageView);
    }
}
