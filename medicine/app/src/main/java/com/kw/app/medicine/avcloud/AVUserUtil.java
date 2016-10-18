package com.kw.app.medicine.avcloud;

import com.avos.avoscloud.AVUser;
import com.kw.app.commonlib.utils.HanziToPinyinUtil;
import com.kw.app.commonlib.utils.TimeUtil;
import com.kw.app.medicine.data.avcloud.UserAVCloud;
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
    public static UserDALEx convertToLocal(UserAVCloud avuser){
        UserDALEx user = new UserDALEx();
        user.setUserid(avuser.getObjectId());
        user.setNickname(avuser.getUsername());
        user.setEmail(avuser.getEmail());
        user.setMobilePhoneNumber(avuser.getMobilePhoneNumber());
        user.setSex(avuser.getSex());
        user.setPinyin(avuser.getPinyin());
        user.setAge(avuser.getAge());
        user.setRole(avuser.getRole());
        user.setCreateAt(TimeUtil.dateToString(avuser.getCreatedAt()));
        user.setUpdateAt(TimeUtil.dateToString(avuser.getUpdatedAt()));
        user.setLogourl(avuser.getLogourl());
        return user;
    }

    /**
     * @Decription 把AVUser转成本地的UserDALEx对象
     **/
    public static List<UserDALEx> convertToLocal(List<UserAVCloud> list){
        List<UserDALEx> userlist = new ArrayList<UserDALEx>();
        for(UserAVCloud user:list){
            userlist.add(convertToLocal(user));
        }
        return userlist;
    }

    /**
     * @Decription 把本地的UserDALEx对象转成AVUser
     **/
    public static UserAVCloud convertToAVUser(UserDALEx user){
        UserAVCloud avuser = new UserAVCloud();
        avuser.setUsername(user.getNickname());//名称
        avuser.setPassword(user.getPassword());//密码
        avuser.setEmail(user.getEmail());//邮箱
        avuser.setAge(user.getAge());//年龄
        avuser.setRole(user.getRole());//角色
        avuser.setLogourl(user.getLogourl());//头像路径
        avuser.setPinyin(HanziToPinyinUtil.getShortPinyin(user.getNickname()));//拼音首字母
        avuser.setMobilePhoneNumber(user.getMobilePhoneNumber());
        return avuser;
    }

}
