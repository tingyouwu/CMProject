package com.kw.app.medicine.avcloud;

import android.text.TextUtils;

import com.avos.avoscloud.AVUser;
import com.kw.app.commonlib.utils.HanziToPinyinUtil;
import com.kw.app.commonlib.utils.TimeUtil;
import com.kw.app.medicine.data.local.UserDALEx;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :wty
 */
public class AVUserUtil{

    /**
     * @Decription 把AVUser转成本地的UserDALEx对象
     **/
    public static UserDALEx convertToLocal(AVUser avuser){
        UserDALEx user = new UserDALEx();
        user.setUserid(avuser.getObjectId());
        user.setNickname(avuser.getUsername());
        user.setEmail(avuser.getEmail());
        user.setMobilePhoneNumber(avuser.getMobilePhoneNumber());
        user.setSex(avuser.getInt(UserDALEx.SEX));
        user.setPinyin(avuser.getString(UserDALEx.PINYIN));
        user.setAge(avuser.getInt(UserDALEx.AGE));
        user.setRole(avuser.getInt(UserDALEx.ROLE));
        user.setCreateAt(TimeUtil.dateToString(avuser.getCreatedAt()));
        user.setUpdateAt(TimeUtil.dateToString(avuser.getUpdatedAt()));
        user.setLogourl(avuser.getString(UserDALEx.LOGOURL));
        return user;
    }

    /**
     * @Decription 把AVUser转成本地的UserDALEx对象
     **/
    public static List<UserDALEx> convertToLocal(List<AVUser> list){
        List<UserDALEx> userlist = new ArrayList<UserDALEx>();
        for(AVUser user:list){
            userlist.add(convertToLocal(user));
        }
        return userlist;
    }

    /**
     * @Decription 把本地的UserDALEx对象转成AVUser
     **/
    public static AVUser convertToAVUser(UserDALEx user){
        AVUser avuser = new AVUser();
        avuser.setUsername(user.getNickname());//名称
        avuser.setPassword(user.getPassword());//密码
        avuser.setEmail(user.getEmail());//邮箱
        avuser.put(UserDALEx.AGE,user.getAge());
        avuser.put(UserDALEx.ROLE,user.getRole());
        avuser.put(UserDALEx.SEX,user.getSex());

        if(!TextUtils.isEmpty(user.getLogourl())){
            avuser.put(UserDALEx.LOGOURL,user.getLogourl());
        }

        if(!TextUtils.isEmpty(HanziToPinyinUtil.getShortPinyin(user.getNickname()))){
            avuser.put(UserDALEx.PINYIN, HanziToPinyinUtil.getShortPinyin(user.getNickname()));

        }

        if(!TextUtils.isEmpty(user.getMobilePhoneNumber())){
            avuser.setMobilePhoneNumber(user.getMobilePhoneNumber());
        }
        return avuser;
    }

}
