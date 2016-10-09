package com.kw.app.medicine.base;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.kw.app.medicine.R;
import com.kw.app.medicine.activity.MainActivity;

/**
 * @author wty
 * 管理通知栏显示
 **/
public class CMNotificationManager {

	/**
	 * 在状态栏显示通知
	 * @param title 显示标题
	 * @param content 显示内容
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void showNotification(Context context, String title,String content,Bundle params) {
		showNotification(context,title,content,true,true,params);
	}

	/**
	 * 在状态栏显示通知
	 * @param title 显示标题
	 * @param content 显示内容
	 * @param sound  是否显示声音
	 * @param vibrate 是否显示震动
	 * @param params 跳转时携带的参数
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void showNotification(Context context, String title,String content, boolean sound,boolean vibrate,Bundle params) {
		// 创建一个NotificationManager的引用
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(context,MainActivity.class); // 点击该通知后要跳转的Activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		if(params!=null){
			notificationIntent.putExtras(params);
		}
		PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);

		int defaults = Notification.DEFAULT_LIGHTS;
		if(sound){
			defaults |= Notification.DEFAULT_SOUND;
		}
		if(vibrate){
			defaults |=Notification.DEFAULT_VIBRATE;
		}

		Bitmap largetIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_launcher);

		NotificationCompat.Builder mBulider = new NotificationCompat.Builder(context);
		mBulider.setContentTitle(title)//设置通知栏标题
				.setTicker(content)//通知首次出现在通知栏，带上升动画效果的
				.setContentText(content)
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
				.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setDefaults(defaults)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
				.setLargeIcon(largetIcon)
				.setSmallIcon(R.mipmap.icon_launcher)
				.setContentIntent(contentItent);

		notificationManager.notify(0,mBulider.build());
	}

	public static void clearNotification(Context context) {
		// 启动后删除之前我们定义的通知
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}
}
