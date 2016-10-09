package com.kw.app.widget.view.imageview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Jaeger on 16/2/24.
 *实现图像根据按下抬起动作变化颜色
 */
public class ColorFilterImageView extends ImageView implements View.OnTouchListener {

    public ColorFilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorFilterImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:  // 按下时图像变灰
                setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                break;
            case MotionEvent.ACTION_UP:   // 手指离开或取消操作时恢复原色
            case MotionEvent.ACTION_CANCEL:
                clearColorFilter();
                break;
            default:
                break;
        }
        return false;
    }

}