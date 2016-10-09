package com.kw.app.widget.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kw.app.widget.R;

@SuppressLint("NewApi")
public class HomeItemView extends RelativeLayout implements OnClickListener{

	TextView tv_count;
    TextView tv_Label;
    ImageView iv_img;
    ImageView iv_point;
    Class<? extends Activity> clazz;

    View iv_underline;
    View iv_divide;


	public HomeItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_item_home, this);
        iv_img = (ImageView)findViewById(R.id.item_crm_img);
        tv_Label = (TextView)findViewById(R.id.item_crm_label);
        tv_count = (TextView)findViewById(R.id.item_crm_count);
        iv_point = (ImageView)findViewById(R.id.item_crm_point);
        iv_underline = findViewById(R.id.item_crm_underline);
        iv_divide= findViewById(R.id.item_crm_divide);

        tv_count.setVisibility(View.GONE);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HomeItemView);
        Boolean underline = ta.getBoolean(R.styleable.HomeItemView_itemUnderline, false);
        Boolean divide = ta.getBoolean(R.styleable.HomeItemView_itemDivide,true);
        Boolean icon = ta.getBoolean(R.styleable.HomeItemView_itemIcon,true);
        String label = ta.getString(R.styleable.HomeItemView_itemText);
        int resourceId = ta.getResourceId(R.styleable.HomeItemView_itemImage, 0);

        if(resourceId != 0){
            iv_img.setImageResource(resourceId);
        }
        tv_Label.setText(label);
        iv_underline.setVisibility(underline ? View.VISIBLE : View.GONE);
        iv_divide.setVisibility(divide?View.VISIBLE:View.GONE);
        iv_img.setVisibility(icon?View.VISIBLE:View.GONE);

        setPoint(View.GONE);
        setOnClickListener(this);
        ta.recycle();
	}
	

    public void setPoint(int visable){
        iv_point.setVisibility(visable);
    }

    public void setCount(int count){
        this.tv_count.setVisibility(View.VISIBLE);
        this.tv_count.setText("" + count);
    }


    @Override
    public void onClick(View v) {
        if(clazz!=null){
            getContext().startActivity(new Intent(getContext(),clazz));
        }
    }

    public void setNextPage(Class<? extends Activity> clazz){
        this.clazz = clazz;
    }

}
