package com.kw.app.medicine.base;

import android.app.Application;
import android.content.Context;

import com.kw.app.bmoblib.BmobModuleManager;
import com.kw.app.commonlib.CommonModuleManager;
import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.medicine.avcloud.AVManager;
import com.kw.app.ormlib.OrmModuleManager;


public class CMApplication extends Application {

	private static Context mApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this.getApplicationContext();

		//运行后您的 App 后您会发现以下三个进程：
		// 1、您的应用进程；
		// 2、您的应用进程: ipc，这是融云的通信进程；
		// 3、io.rong.push，这是融云的推送进程。

		//初始化Bmob功能
		if(getApplicationInfo().packageName.equals(CommonUtil.getMyProcessName())){
			BmobModuleManager.init(mApplication);
			CommonModuleManager.init(mApplication);
			OrmModuleManager.init(mApplication);
			AVManager.init(mApplication);
		}

		/**
		 * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
		 * io.rong.push 为融云 push 进程名称，不可修改。
		 */
		if(getApplicationInfo().packageName.equals(CommonUtil.getMyProcessName()) || "io.rong.push".equals(CommonUtil.getMyProcessName())){
			RongManager.init(this);
		}

	}

	/**
	 * 功能描述：获得一个全局的application对象
	 **/
	public static Context getInstance(){
		return mApplication;
	}


}
