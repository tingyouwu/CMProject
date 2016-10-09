package com.kw.app.medicine.data.local;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.DatabaseField.FieldType;
import com.kw.app.ormlib.annotation.SqliteDao;

import java.util.List;

/**
 * 用户信息
 * @author wty
 */
public class UserDALEx extends SqliteBaseDALEx {

	public static final int User_Doctor = 1;//医生
	public static final int User_Not_Doctor = 0;//非医生
	public static final String PINYIN = "pinyin";
	public static final String USERNAME = "username";

	@DatabaseField(primaryKey = true,Type = FieldType.VARCHAR)
	private String userid;

	@DatabaseField(Type = FieldType.VARCHAR)
	private String username; // 用户的用户名

	@DatabaseField(Type = FieldType.VARCHAR)
	private String password;

	@DatabaseField(Type = FieldType.VARCHAR)
	private String email;

	@DatabaseField(Type = FieldType.VARCHAR)
	private String mobilePhoneNumber;

	@DatabaseField(Type = FieldType.INT)
	private int sex; // 性别  1男 0女

	@DatabaseField(Type = FieldType.VARCHAR)
	private String pinyin; // 名字拼音

	@DatabaseField(Type = FieldType.INT)
	private int age;//年龄

	@DatabaseField(Type = FieldType.INT)
	private int role;// 角色  1医生 0非医生

	@DatabaseField(Type = FieldType.VARCHAR)
	private String createAt;//创建时间

	@DatabaseField(Type = FieldType.VARCHAR)
	private String updateAt;//修改时间

	@DatabaseField(Type = FieldType.VARCHAR)
	private String logourl;//头像

	public static UserDALEx get() {
		return SqliteDao.getDao(UserDALEx.class);
	}

	/**
	 * 获取我的好友列表
	 **/
	public List<UserDALEx> findAllFriend(){
		String sql = String.format("select * from %s where exists (select null from %s where %s.userid = friendid and status = 1) order by %s asc",
				TABLE_NAME,FriendRelationDALEx.get().getTableName(),TABLE_NAME,PINYIN);
		   return findList(sql
		   );
	}

	/**
	 * 根据关键字搜索朋友
	 **/
	public List<UserDALEx> findAllFriendByFilter(String filterString){
		String sql = String.format("select * from %s where %s or %s and exists (select null from %s where %s.userid = friendid and status = 1) order by %s asc",
				TABLE_NAME,PINYIN + " like " + "'%" + filterString + "%'",USERNAME + " like " + " '%" + filterString + "%' ",FriendRelationDALEx.get().getTableName(),TABLE_NAME,PINYIN);
		AppLogUtil.d(sql);
		return findList(sql
		);

	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public String getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getNickname() {
		return username;
	}

	public void setNickname(String nickname) {
		this.username = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLogourl() {
		return logourl;
	}

	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}
}
