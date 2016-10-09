package com.kw.app.widget.view.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kw.app.widget.R;

/**
 * @Descrition 导航按钮
 **/
public class NavigationButton extends LinearLayout {
    private ImageView imageView;
    private TextView textView;
    private LinearLayout root;
    public NavigationButton(Context context){
        this(context,null);
    }

    public NavigationButton(Context context, AttributeSet attr){
        super(context,attr);
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.navigation_button,this);
        textView = (TextView)findViewById(R.id.navigation_btn_tv);
        imageView = (ImageView)findViewById(R.id.navigation_btn_img);
        root = (LinearLayout)findViewById(R.id.navigation_btn_root);
    }

    public final void setImageResource(int resource){
        this.imageView.setImageResource(resource);
    }

    public final void setText(String text){
        this.textView.setText(text);
        this.textView.setVisibility(View.VISIBLE);
    }

    public void setEnabled(boolean enable){
        super.setEnabled(enable);
        textView.setEnabled(enable);
    }

    protected void setRootGravity(int gravity){
        root.setGravity(gravity);
    }

    public void setButton(int resource,OnClickListener listener){
        setOnClickListener(listener);
        show();
        textView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resource);
    }

    public void setButton(String text,OnClickListener listener){
        setOnClickListener(listener);
        show();
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
    }

    public void setButton(int resource,String text,OnClickListener listener){
        setOnClickListener(listener);
        show();
        textView.setText(text);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resource);
    }

    public void show(){
        setVisibility(View.VISIBLE);
        setClickable(true);
    }

    public void hide(){
        hide(false);
    }

    public void hide(boolean isGone){
        if(isGone){
            setVisibility(View.GONE);
        }else{
            setVisibility(View.INVISIBLE);
        }
        setClickable(false);
    }

}