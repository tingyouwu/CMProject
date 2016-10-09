package com.kw.app.widget.view.formitem;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kw.app.widget.R;

public class FormItemView extends RelativeLayout {

    public static final int Type_Text           = 1;
    public static final int Type_Int            = 2;
    public static final int Type_Password       = 3;
    public static final int Type_Decimal        = 4;
    public static final int Type_Money          = 5;
    public static final int Type_SingleSelect   = 6;
    public static final int Type_Location       = 8;
    public static final int Type_Date           = 9;
    public static final int Type_DateTime       = 10;

    TextView view_require;
    TextView tv_label;
    FrameLayout layout_content;

    private boolean         autoFillContent    = true;
    private int             contentType        = Type_Text;
    private IFormContent    content;
    protected boolean       isReadOnly         = false;
    private boolean         isRequired         = false;
    private String          label              = "";
    private String          hint               = "";
    private int             maxLenth           = 999;
    private String          selectOption       = "";
    private String          defaultDate        = "";



    public FormItemView(Context context) {
        super(context);
        init(context);
    }
    public FormItemView(Context context, boolean autoFillContent) {
        super(context);
        this.autoFillContent = autoFillContent;
        init(context);
    }


    public FormItemView(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FormItemView);

        label = ta.getString(R.styleable.FormItemView_formLabel);
        isRequired = ta.getBoolean(R.styleable.FormItemView_formRequired, false);
        isReadOnly = ta.getBoolean(R.styleable.FormItemView_formReadOnly, false);

        hint = ta.getString(R.styleable.FormItemView_formHint);
        maxLenth = ta.getInt(R.styleable.FormItemView_formMaxLength, 9999);
        selectOption = ta.getString(R.styleable.FormItemView_formSelectOption);
        defaultDate = ta.getString(R.styleable.FormItemView_formDefaultDate);

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String name = attrs.getAttributeName(i);
            String value = attrs.getAttributeValue(i);
            if ("formContentType".equals(name) && value != null) {
                if(!isInEditMode()){
                    contentType = Integer.valueOf(value);
                }

            }
        }
        ta.recycle();
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_formitem, this);

        view_require = (TextView)findViewById(R.id.form_label_required);
        tv_label = (TextView)findViewById(R.id.form_label_textview);
        layout_content= (FrameLayout)findViewById(R.id.form_label_content);

        setProperty();
        if(autoFillContent){
            fillContent(createContent(context, contentType));
        }


    }

    public void fillContent(IFormContent content){
        layout_content.removeAllViews();
        if(content!=null && content instanceof View){
            this.content = content;
            layout_content.addView((View) content);
            setContentProperty();
        }
    }

    protected IFormContent createContent(Context context,int type){

        IFormContent content;
        switch(type){

            case Type_Text:
                content = new FormContentText(context);
                break;
            case Type_Int:
                content = new FormContentText(context);
                break;
            case Type_Password:
                content = new FormContentText(context);
                break;
            case Type_Decimal:
                content = new FormContentText(context);
                break;
            case Type_Money:
                content = new FormContentText(context);
                break;
            case Type_SingleSelect:
                content = new FormContentSelect(context);
                break;
            case Type_Location:
                content = new FormContentText(context);
                break;
            case Type_Date:
                content = new FormContentText(context);
                break;
            case Type_DateTime:
                content = new FormContentText(context);
                break;
            default:
                content = new FormContentText(context);
                break;
        }


        return content;
    }

    protected void setProperty(){
        setLabel(label);
        setIsRequired(isRequired);
    }

    public void setContentProperty(){
        if(content==null)return;
        setHint(hint);
        setIsReadOnly(isReadOnly);
        if(content instanceof FormContentText){
            ((FormContentText)content).setMaxLength(maxLenth);
        }

        if(content instanceof FormContentSelect){
            ((FormContentSelect)content).addOptions(selectOption, true);
        }
    }

    public void setValue(String value){
        content.setValue(value);
    }

    public void setLabel(String label){
        this.label = label;
        if(TextUtils.isEmpty(label)){
            tv_label.setText("");
        }else{
            tv_label.setText(label);
        }

    }

    public void setIsRequired(boolean isRequired) {
        view_require.setVisibility(isRequired ? View.VISIBLE : View.INVISIBLE);
    }

    public void setIsReadOnly(boolean isReadOnly){
        if(content!=null){
            content.setIsReadOnly(isReadOnly);
        }

    }

    public void setHint(String hint){

        if(contentType == Type_Text){
            this.hint = TextUtils.isEmpty(hint) ? "请输入" + getLabel() : hint;
        }else{
            this.hint = TextUtils.isEmpty(hint) ? "请选择" + getLabel() : hint;
        }
        if(content!=null){
            content.setHint(this.hint);
        }

    }

    public <T extends IFormContent> T getContent(){
        return (T)content;
    }

    public String getLabel(){
        return label;
    }

    public String getHint() {
        if (TextUtils.isEmpty(hint)) {
            if(content instanceof FormContentText){
                return TextUtils.isEmpty(hint) ? "请输入" + getLabel() : hint;
            }else{
                return TextUtils.isEmpty(hint) ? "请选择" + getLabel() : hint;
            }
        }
        return hint;
    }
}