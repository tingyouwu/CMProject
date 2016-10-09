package com.kw.app.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ScreenUtil;
import com.kw.app.widget.R;

public class UnreadTextView extends TextView {

	private final static int MaxCount = 99;

	public UnreadTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setGravity(Gravity.CENTER);
		setTextSize(12);
        setMinWidth(ScreenUtil.dp2px(context, 18));
        setMinHeight(ScreenUtil.dp2px(context, 18));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UnreadView);
        int unread = ta.getInt(R.styleable.UnreadView_unread, 0);

        setTextColor(getResources().getColor(R.color.white));
        setUnread(unread);
        ta.recycle();
	}
	
	public void setUnread(int unread){

		if(unread<=0){
			setVisibility(View.GONE);
			setText("0");
		}else{

			setVisibility(View.VISIBLE);
			if(unread>MaxCount){
				setText("99+");
                setMinWidth(ScreenUtil.dp2px(getContext(), 25));
                setBackgroundResource(R.drawable.circle_red2);
			}else{
                setMinWidth(ScreenUtil.dp2px(getContext(), 18));
                setBackgroundResource(R.mipmap.img_red_point3x);
				setText(""+unread);
			}
		}
	}


}
