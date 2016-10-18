package com.kw.app.medicine.data.avcloud;

import android.text.TextUtils;

import com.avos.avoscloud.AVUser;

/**
 * 用户
 */
public class UserAVCloud extends AVUser {

    //此处为我们的默认实现，当然你也可以自行实现
    public static final Creator CREATOR = AVObjectCreator.instance;

    public static final String PINYIN = "pinyin";
    public static final String SEX = "sex";
    public static final String AGE = "age";
    public static final String ROLE = "role";
    public static final String LOGOURL = "logourl";

    public void setAge(int age){
        this.put(AGE,age);
    }

    public void setRole(int role){
        this.put(ROLE,role);
    }

    public void setSex(int sex){
        this.put(SEX,sex);
    }

    public void setLogourl(String logourl){
        this.put(LOGOURL, TextUtils.isEmpty(logourl)?"":logourl);
    }

    @Override
    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        if(TextUtils.isEmpty(mobilePhoneNumber))return;
        super.setMobilePhoneNumber(mobilePhoneNumber);
    }

    public void setPinyin(String pinyin){
        this.put(PINYIN,TextUtils.isEmpty(pinyin)?"":pinyin);
    }

    public int getAge(){
        return this.getInt(AGE);
    }

    public int getRole(){
        return this.getInt(ROLE);
    }

    public int getSex(){
        return this.getInt(SEX);
    }

    public String getLogourl(){
        return this.getString(LOGOURL);
    }

    public String getPinyin(){
        return this.getString(PINYIN);
    }
}
