package com.kw.app.medicine.mvp.presenter;

import android.content.Context;

import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.imlib.bean.RongConversation;
import com.kw.app.medicine.mvp.contract.IMessageContract;
import com.kw.app.medicine.mvp.model.MessageModel;
import com.kw.app.widget.ICallBack;

import java.util.List;

/**
 * @author wty
 */
public class MessagePresenter extends BasePresenter<IMessageContract.IMessageView> {

    private IMessageContract.IMessageModel mMessageModel;

    public MessagePresenter(){
        mMessageModel = new MessageModel();
    }

    /**
     * @Description 加载所有对话
     **/
    public void getAllConversations(Context context){
        if(!mView.checkNet()){
            mView.showNoNet();
            return ;
        }

         mMessageModel.refreshMessage(context, new ICallBack<List<RongConversation>>() {
             @Override
             public void onSuccess(List<RongConversation> data) {
                 mView.refreshMessage(data);
             }

             @Override
             public void onFaild(String msg) {
             }
         });
    }

    /**
     * @Description 刷新所有对话
     **/
    public void refreshConversations(Context context){
        if(!mView.checkNet()){
            mView.showNoNet();
            mView.onRefreshComplete();
            return ;
        }

        mMessageModel.refreshMessage(context, new ICallBack<List<RongConversation>>() {
            @Override
            public void onSuccess(List<RongConversation> data) {
                mView.refreshMessage(data);
                mView.onRefreshComplete();
            }

            @Override
            public void onFaild(String msg) {
                mView.onRefreshComplete();

            }
        });
    }
}
