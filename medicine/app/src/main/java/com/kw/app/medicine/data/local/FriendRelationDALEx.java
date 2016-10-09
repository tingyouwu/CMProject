package com.kw.app.medicine.data.local;

import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.SqliteDao;

import java.util.List;

/**
 * 好友关系表
 * @author wty
 */
public class FriendRelationDALEx extends SqliteBaseDALEx {

	public static final String FRIENDID = "friendid";
	public static final String STATUS = "status";
	public static final String UPDATEAT = "updateAt";

	@DatabaseField(primaryKey = true,Type = DatabaseField.FieldType.VARCHAR)
	private String relationid;

	@DatabaseField(Type = DatabaseField.FieldType.VARCHAR)
	private String friendid; // 用户名

	@DatabaseField(Type = DatabaseField.FieldType.INT)
	private int status;//是否已经删除  1表示还是好友  0表示已经删除关系

	@DatabaseField(Type = DatabaseField.FieldType.VARCHAR)
	private String updateAt;//修改时间

	public static FriendRelationDALEx get() {
		return SqliteDao.getDao(FriendRelationDALEx.class);
	}

	/**
	 * 获取最新修改时间的一条数据
	 **/
	public FriendRelationDALEx  getNewestRelation(){
		String sql = String.format("select * from %s order by datetime(%s) desc",TABLE_NAME,UPDATEAT);
		List<FriendRelationDALEx> list = findList(sql);
		if(list.size()==0){
			//本地一条数据都没有
			return null;
		}else{
			return list.get(0);
		}
	}

	/**
	 * 判断 userid 是否为我的朋友
	 **/
	public boolean isFriend(String userid){
		String sql = String.format("select null from %s where %s = '%s'",TABLE_NAME,FRIENDID,userid);
		List<FriendRelationDALEx> list = findList(sql);
		if(list.size()>0)
			return true;
		return false;
	}

	public String getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}

	public String getRelationid() {
		return relationid;
	}

	public void setRelationid(String relationid) {
		this.relationid = relationid;
	}

	public String getFriendid() {
		return friendid;
	}

	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
