package com.kw.app.medicine.mvp.model;

import android.content.Context;

import com.kw.app.medicine.base.BmobUserModel;
import com.kw.app.medicine.data.bmob.FriendBmob;
import com.kw.app.medicine.data.local.FriendRelationDALEx;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.mvp.contract.IContactContract;
import com.kw.app.widget.ICallBack;

import java.util.List;

/**
 * @author wty
 */
public class ContactModel implements IContactContract.IContactModel {

    @Override
    public void refreshFriend(Context context, final ICallBack<List<UserDALEx>> callBack) {

        FriendRelationDALEx relation = FriendRelationDALEx.get().getNewestRelation();

        String updatetime = null;
        if(relation !=null){
            updatetime = relation.getUpdateAt();
        }

        BmobUserModel.getInstance().queryFriends(updatetime, new ICallBack<List<FriendBmob>>() {
            @Override
            public void onSuccess(List<FriendBmob> list) {
                FriendBmob.get().save(list);
                callBack.onSuccess(UserDALEx.get().findAllFriend());
            }

            @Override
            public void onFaild(String msg) {
                callBack.onFaild(msg);
            }
        });
    }
}
