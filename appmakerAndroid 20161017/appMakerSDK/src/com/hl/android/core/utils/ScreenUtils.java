package com.hl.android.core.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.hl.android.common.BookSetting;

/**
 * 展示ui计算大小的工具类
 * @author zhaoq
 *
 */
public class ScreenUtils {
	
	public static float getHorScreenValue(float value){
		//此处加上千分之一个像素是为避免取整时由于精度问题导致的误差
		if(BookSetting.FITSCREEN_TENSILE){
			return value*BookSetting.PAGE_RATIOX+0.001f;
		}
		return value*BookSetting.PAGE_RATIO+0.001f;
	}
	public static float getVerScreenValue(float value){
		//此处加上千分之一个像素是为避免取整时由于精度问题导致的误差
		if(BookSetting.FITSCREEN_TENSILE){
			return value*BookSetting.PAGE_RATIOY+0.001f;
		}
		return value*BookSetting.PAGE_RATIO+0.001f;
	}
	

	/**
	 * 获取屏幕宽度
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity){
		if(activity == null){
			Log.d("hl", "获取屏幕宽度的传入的activity为空");
			return 0;
		}
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}
	/**
	 * 获取屏幕高度
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity){
		if(activity == null){
			Log.d("hl", "获取屏幕高度的传入的activity为空");
			return 0;
		}
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}
	
	/**
	* 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	*/
	public static int dip2px(Context context, float dpValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (dpValue * scale + 0.5f);
	}

	/**
	* 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	*/
	public static int px2dip(Context context, float pxValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (pxValue / scale + 0.5f);
	}
	
	
	public static int getAPILevel(){
		return Integer.valueOf(android.os.Build.VERSION.SDK);
	}
	
	public static String getAppPath(){
		return Environment.getDataDirectory().getAbsolutePath().toString();
	}
}
