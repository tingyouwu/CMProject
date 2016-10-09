package com.kw.app.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kw.app.widget.R;

/**
 * @Decription dialog头
 **/
public class DialogHeaderView extends LinearLayout{

    TextView tv_Title;

    public DialogHeaderView(Context context, String title) {
        this(context, null,title);
    }

	private DialogHeaderView(Context context, AttributeSet attrs, String title) {
		super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_header, this);
        tv_Title = (TextView)findViewById(R.id.tv_item_title);
        tv_Title.setText(title);
	}

    public DialogHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_header, this);
        tv_Title = (TextView)findViewById(R.id.tv_item_title);
    }

}
