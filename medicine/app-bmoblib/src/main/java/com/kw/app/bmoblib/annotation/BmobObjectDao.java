package com.kw.app.bmoblib.annotation;

import com.kw.app.bmoblib.BaseBmobObject;

import java.util.HashMap;
import java.util.Map;

public class BmobObjectDao {
	private static Map<Class<? extends BaseBmobObject>, BaseBmobObject> daos = new HashMap<Class<? extends BaseBmobObject>, BaseBmobObject>();
	
	public static <T extends BaseBmobObject> T getDao(Class<? extends BaseBmobObject> clazz){
		BaseBmobObject dao = daos.get(clazz);
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
