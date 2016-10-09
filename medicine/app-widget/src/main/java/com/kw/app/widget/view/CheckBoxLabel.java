package com.kw.app.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kw.app.widget.R;

public class CheckBoxLabel extends LinearLayout implements OnClickListener{

	private CheckBox cb;
	private TextView tv;
	public CheckBoxLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.layout_checkboxlabel, this);
		cb = (CheckBox) findViewById(R.id.checkboxlabel_cb);
		tv = (TextView) findViewById(R.id.checkboxlabel_tv);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxLabel);
		String labelText = ta.getString(R.styleable.CheckBoxLabel_label);
		String labelTextColor = ta.getString(R.styleable.CheckBoxLabel_labelTextColor);
		int labelTextSize = ta.getInt(R.styleable.CheckBoxLabel_labelTextSize,13);
		
		tv.setTextSize(labelTextSize);
		if(!TextUtils.isEmpty(labelText)){
			tv.setText(labelText);
		}
		
		if(!TextUtils.isEmpty(labelTextColor)){
			tv.setTextColor(Color.parseColor(labelTextColor));
		}
		
        ta.recycle();
		setOnClickListener(this);
	}

	public boolean isCheck(){
		return cb.isChecked();
	}

	@Override
	public void onClick(View v) {
		cb.setChecked(!cb.isChecked());
	}
	
	public void setCheck(boolean check){
		cb.setChecked(check);
	}
	
	public void setLabel(String label){
		tv.setText(label);
	}
	
	public void setOnCheckChangeListener(OnCheckedChangeListener listener){
		cb.setOnCheckedChangeListener(listener);
	}
	
}
