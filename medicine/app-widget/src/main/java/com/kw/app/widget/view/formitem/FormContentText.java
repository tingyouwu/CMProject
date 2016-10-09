package com.kw.app.widget.view.formitem;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kw.app.widget.R;

public class FormContentText extends LinearLayout implements IFormContent{

    EditText edt_content;
    TextView tv_content;

    private boolean isReadOnly = false;

    public FormContentText(Context context) {
        super(context);
        init(context);
    }

    public FormContentText(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_form_text,this);
        edt_content = (EditText)findViewById(R.id.form_text_edittext);
        tv_content = (TextView)findViewById(R.id.form_text_content);

    }

    @Override
    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        if(isReadOnly){
            edt_content.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
        }else{
            edt_content.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean validate() {
        return TextUtils.isEmpty(edt_content.getText().toString());
    }

    @Override
    public String getValue() {
        return isReadOnly? tv_content.getText().toString():edt_content.getText().toString();
    }

    @Override
    public void setValue(String value) {
        edt_content.setText(value);
        tv_content.setText(value);
    }


    public void setMaxLength(int maxLength){
        InputFilter.LengthFilter lengthfilter = new InputFilter.LengthFilter(maxLength);
        edt_content.setFilters(new InputFilter[] { lengthfilter });
    }

    public void setHint(String hint){
        edt_content.setHint(hint);
    }

    @Override
    public void clearValue() {
        edt_content.setText("");
        tv_content.setText("");
    }


}