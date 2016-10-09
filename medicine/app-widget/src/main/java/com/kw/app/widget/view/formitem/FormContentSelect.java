package com.kw.app.widget.view.formitem;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kw.app.widget.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormContentSelect extends LinearLayout implements IFormContent{

    EditText edt_content;
    TextView tv_content;
    ImageView iv_trangle;

    SingleOptionItem selected;

    private List<SingleOptionItem>  options = new ArrayList<SingleOptionItem>();
    private boolean isReadOnly = false;

    public FormContentSelect(Context context) {
        super(context);
        init(context);
    }

    public FormContentSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_form_select, this);
        edt_content = (EditText)findViewById(R.id.form_select_edittext);
        tv_content = (TextView)findViewById(R.id.form_select_content);
        iv_trangle = (ImageView)findViewById(R.id.form_select_img);

        edt_content.setFocusable(false);
        tv_content.setFocusable(false);
        edt_content.setOnClickListener(clickListener);
        tv_content.setOnClickListener(clickListener);
        this.setOnClickListener(clickListener);
    }

    @Override
    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        if(isReadOnly){
            edt_content.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
            iv_trangle.setVisibility(View.GONE);
        }else{
            edt_content.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.GONE);
            iv_trangle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean validate() {
        return selected!=null;
    }

    @Override
    public String getValue() {
        return selected==null?"":selected.getId();
    }

    @Override
    public void setValue(String value) {

        for(SingleOptionItem option:options){
            if(option.getId().equals(value)){
                this.selected = option;
            }
        }
        if(this.selected!=null){
            edt_content.setText(selected.getText());
            tv_content.setText(selected.getText());
        }


    }

    private void setValue(SingleOptionItem option){
        this.selected = option;
        edt_content.setText(selected.getText());
        tv_content.setText(selected.getText());
    }

    public void setHint(String hint){
        edt_content.setHint(hint);
    }

    @Override
    public void clearValue() {
        this.selected = null;
        edt_content.setText("");
        tv_content.setText("");
    }

    OnClickListener clickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            if(isReadOnly)return;
            showSingleOptionsDialog();
        }
    };


    public void showSingleOptionsDialog() {
        List<String> ops = new ArrayList<String>();
        for(SingleOptionItem option:options){
            ops.add(option.getOption());
        }
        showSingleChoiceDialog(ops.toArray(new String[ops.size()]));

    }

    /**
     * 弹出选项
     */
    private void showSingleChoiceDialog(final String[] optionsText) {
        if(optionsText==null)return;
    }

    /**
     * 增加选项的值
     **/
    public void addOption(SingleOptionItem option){
        this.options.add(option);
    }

    public void addOptions(List<SingleOptionItem> values){
        this.options.addAll(values);
    }

    /**
     * 自动增加默认“无”选项
     **/
    public void addOptions(List<SingleOptionItem> values,boolean hasEmptyValue){
        if(hasEmptyValue) {
            addOption(new SingleOptionItem("无", "", ""));
        }
        addOptions(values);
    }

    public void addOptions(SingleOptionItem[] values){
        addOptions(Arrays.asList(values));
    }

    public void addOptions(SingleOptionItem[] values,boolean hasEmptyValue){
        addOptions(Arrays.asList(values), hasEmptyValue);
    }

    public void addOptions(String selectOption,boolean hasEmptyValue){
        if(!TextUtils.isEmpty(selectOption)){
            String[] optionsArray = selectOption.split("\\|");
            List<SingleOptionItem> options = new ArrayList<SingleOptionItem>();
            for(String o:optionsArray){
                if(!TextUtils.isEmpty(o)){
                    options.add(new SingleOptionItem(o, o));
                }
            }
            addOptions(options,hasEmptyValue);
        }
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener clickListener){
        this.onOptionSelectedListener = clickListener;
    }

    protected OnOptionSelectedListener onOptionSelectedListener;
    public interface OnOptionSelectedListener{
        boolean onSelected(SingleOptionItem option);
    }
}