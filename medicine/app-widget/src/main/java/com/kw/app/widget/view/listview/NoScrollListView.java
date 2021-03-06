package com.kw.app.widget.view.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

@SuppressLint("NewApi")
public class NoScrollListView extends ListView {

	public NoScrollListView(Context context) {
		super(context);
		init();
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
