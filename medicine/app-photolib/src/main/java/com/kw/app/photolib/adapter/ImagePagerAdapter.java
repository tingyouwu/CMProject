package com.kw.app.photolib.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.kw.app.commonlib.utils.ScreenUtil;
import com.kw.app.photolib.R;
import com.kw.app.photolib.bean.ImageSizeBean;
import com.kw.app.widget.adapter.DialogListAdapter;
import com.kw.app.widget.view.xrecyclerview.AVLoadingIndicatorView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @Decription 图片浏览 适配器
 * @author wty
 */
public class ImagePagerAdapter extends PagerAdapter {

    private List<String> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private ImageSizeBean imageSize;
    private ImageView smallImageView = null;

    public ImagePagerAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(datas == null) return 0;
        return datas.size();
    }


    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.item_pager_image, container, false);
        if(view != null){
            final PhotoView imageView = (PhotoView) view.findViewById(R.id.image);

            imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if(context instanceof Activity){
                        ((Activity)context).finish();
                    }
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    List<String> data = new ArrayList<String>();
                    data.add("保存图片");
                    data.add("分享图片");

                    final DialogPlus dialog = DialogPlus.newDialog(context)
                            .setContentHolder(new ListHolder())
                            .setCancelable(true)
                            .setGravity(Gravity.CENTER)
                            .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setContentWidth(ScreenUtil.getScreenWidth(context)/2)
                            .setContentBackgroundResource(R.drawable.bg_dialog_list)
                            .setAdapter(new DialogListAdapter(context, data))
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                    switch (position){
                                        case 0://保存图片
                                            break;
                                        case 1://分享图片
                                            break;
                                        default:
                                            break;
                                    }

                                }
                            })
                            .create();
                    dialog.show();
                    return true;
                }
            });

            if(imageSize!=null){
                //预览imageView
                smallImageView = new ImageView(context);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize.getWidth(), imageSize.getHeight());
                layoutParams.gravity = Gravity.CENTER;
                smallImageView.setLayoutParams(layoutParams);
                smallImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                ((FrameLayout)view).addView(smallImageView);
            }

            //loading
            final AVLoadingIndicatorView loading = new AVLoadingIndicatorView(context);
            loading.setIndicatorId(AVLoadingIndicatorView.LineSpinFadeLoader);
            FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            loadingLayoutParams.gravity = Gravity.CENTER;
            loading.setLayoutParams(loadingLayoutParams);
            ((FrameLayout)view).addView(loading);

            final String imgurl = datas.get(position);

            Glide.with(context)
                    .load(imgurl)
                    .error(R.mipmap.img_error_fail)
                    .into(new GlideDrawableImageViewTarget(imageView) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            if (smallImageView != null) {
                                smallImageView.setVisibility(View.VISIBLE);
                                Glide.with(context).load(imgurl).into(smallImageView);
                            }
                            loading.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            if (smallImageView != null) {
                                smallImageView.setVisibility(View.GONE);
                            }
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            loading.setVisibility(View.GONE);
                            if (smallImageView != null) {
                                smallImageView.setVisibility(View.GONE);
                            }
                        }
                    });

            container.addView(view, 0);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void setDatas(List<String> datas) {
        if(datas != null )
            this.datas = datas;
    }
    public void setImageSize(ImageSizeBean imageSize){
        this.imageSize = imageSize;
    }

}
