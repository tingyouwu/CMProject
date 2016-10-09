package com.kw.app.widget.view.listview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

@SuppressLint("NewApi")
public class NoScrollGridView extends GridView {
	
	public NoScrollGridView(Context context) {
		this(context, null);
	}
	
	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);
	}
	
	/**  
     * 设置不滚动  
     */  
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)   
    {   
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,   
                MeasureSpec.AT_MOST);   
        super.onMeasure(widthMeasureSpec, expandSpec);   
  
    }   
    
}