package com.kw.app.widget.view;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @Decription 有些方法不需要重写  故自己给个默认
 */
public class MyTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}