package com.kw.app.avcloudlib.annotation;

import com.kw.app.avcloudlib.BaseAVObject;

import java.util.HashMap;
import java.util.Map;

public class AVObjectDao {
	private static Map<Class<? extends BaseAVObject>, BaseAVObject> daos = new HashMap<Class<? extends BaseAVObject>, BaseAVObject>();
	
	public static <T extends BaseAVObject> T getDao(Class<? extends BaseAVObject> clazz){
		BaseAVObject dao = daos.get(clazz);
		if(dao==null){
			try {
				dao = clazz.newInstance();
				daos.put(clazz, dao);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return (T)dao;
	}
	
	public static void clear(){
		daos.clear();
	}
}
