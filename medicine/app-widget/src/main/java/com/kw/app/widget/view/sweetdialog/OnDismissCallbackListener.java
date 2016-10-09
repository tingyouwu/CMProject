package com.kw.app.widget.view.sweetdialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @Decription sweetdialog回调事件
 **/
public class OnDismissCallbackListener implements SweetAlertDialog.OnSweetClickListener {
        public String msg;
        public int alertType = SweetAlertDialog.SUCCESS_TYPE;
        public OnDismissCallbackListener(String msg){
            this.msg = msg;
        }

       public OnDismissCallbackListener(String msg, int alertType){
            this.msg = msg;
            this.alertType = alertType;
        }

        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.cancel();
            onCallback();
        }

        public void onCallback(){}

    }