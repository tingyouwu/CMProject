package com.kw.app.medicine.data.bmob;

import com.kw.app.commonlib.utils.HanziToPinyinUtil;
import com.kw.app.medicine.data.local.UserDALEx;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 用户信息
 * @author wty
 */
public class UserBmob extends BmobUser {

	private int sex; // 性别  1男 0女

	private String pinyin; // 名字拼音

	private int age;//年龄

	private int role;// 角色  1医生 0非医生

	private String logourl;

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getLogourl() {
		return logourl;
	}

	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}

	public void save(final UserBmob bmob){
		UserDALEx dalex = new UserDALEx();
		dalex.setUserid(bmob.getObjectId());
		dalex.setNickname(bmob.getUsername());
		dalex.setEmail(bmob.getEmail());
		dalex.setMobilePhoneNumber(bmob.getMobilePhoneNumber());
		dalex.setAge(bmob.getAge());
		dalex.setRole(bmob.getRole());
		dalex.setPinyin(bmob.getPinyin());
		dalex.setCreateAt(bmob.getCreatedAt());
		dalex.setUpdateAt(bmob.getUpdatedAt());
		dalex.setSex(bmob.getSex());
		dalex.setLogourl(bmob.getLogourl());
		dalex.saveOrUpdate();
	}

	public void save(final List<UserBmob> list){
		List<UserDALEx> localdalex = bmobToLocal(list);
		UserDALEx.get().saveOrUpdate(localdalex);
	}

	public List<UserDALEx> saveReturn(final List<UserBmob> list){
		List<UserDALEx> localdalex = bmobToLocal(list);
		UserDALEx.get().saveOrUpdate(localdalex);
		return localdalex;
	}

	public List<UserDALEx> bmobToLocal(List<UserBmob> list){
		List<UserDALEx> localdalex = new ArrayList<UserDALEx>();
		for(UserBmob bmob:list){
			UserDALEx dalex = new UserDALEx();
			dalex.setUserid(bmob.getObjectId());
			dalex.setNickname(bmob.getUsername());
			dalex.setEmail(bmob.getEmail());
			dalex.setMobilePhoneNumber(bmob.getMobilePhoneNumber());
			dalex.setAge(bmob.getAge());
			dalex.setRole(bmob.getRole());
			dalex.setPinyin(bmob.getPinyin());
			dalex.setCreateAt(bmob.getCreatedAt());
			dalex.setUpdateAt(bmob.getUpdatedAt());
			dalex.setSex(bmob.getSex());
			dalex.setLogourl(bmob.getLogourl());
			localdalex.add(dalex);
		}
		return localdalex;
	}

	public void setAnnotationField(UserDALEx user){
		setMobilePhoneNumber(user.getMobilePhoneNumber());
		setUsername(user.getNickname());
		setPassword(user.getPassword());
		setPinyin(HanziToPinyinUtil.getShortPinyin(user.getNickname()));
		setAge(user.getAge());
		setRole(user.getRole());
		setSex(user.getSex());
		setEmail(user.getEmail());
		setLogourl(user.getLogourl());
	}
}
