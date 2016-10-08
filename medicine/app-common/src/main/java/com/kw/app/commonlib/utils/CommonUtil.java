package com.kw.app.commonlib.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class CommonUtil {

	/**
	 * 功能描述：获取当前数据库版本
	 **/
	public static int getDbVersion(Context context){
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData.getInt("dbversion");
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 *	功能描述：获取版本号(内部识别号)
	 **/
	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 *	功能描述： 获取版本号
	 **/
	public static String getVersion(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取应用名称
	 * @param context
	 */
	public static String getApplicationName(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * @Decription 隐藏或者显示输入法
	 **/
	public static void keyboardControl(Context context,boolean show,EditText editText){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(show){
			//显示键盘
			imm.showSoftInput(editText, 0);
		}else{
			//隐藏键盘
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}

	/**
	 * 判断程序是否在前台运行
	 * @param context
	 * @return
	 *   true 在后台运行  false 在前台运行
	 */
	public static boolean isAppIsInBackground(Context context) {
		boolean isInBackground = true;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				//前台程序
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(context.getPackageName())) {
							isInBackground = false;
						}
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName())) {
				isInBackground = false;
			}
		}

		return isInBackground;
	}

	/**
	 * 判断SDCard是否存在,并可写
	 *
	 * @return
	 */
	public static boolean checkSDCard() {
		String  flag = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(flag)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前运行的进程名
	 * @return
	 */
	public static String getMyProcessName() {
		try {
			File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
			BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
			String processName = mBufferedReader.readLine().trim();
			mBufferedReader.close();
			return processName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
