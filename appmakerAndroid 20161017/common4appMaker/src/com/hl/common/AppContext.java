package com.hl.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * 与应用相关的一些工具
 * @author zhaoq
 *
 */
public class AppContext {
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
	
	/**
	 * detect the package is installed
	 * @param activity current activity
	 * @param packageName 
	 * @return true if it is installed otherwise false
	 */
	public static boolean detectPackage(Activity activity, String packageName) {
		PackageInfo packageInfo;
		// "com.adobe.flashplayer"
		try {
			packageInfo = activity.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		return packageInfo != null;
	}

	/**
	 * 安装asset下面自带的apk文件 1首先检测是否已经安装，如果已经安装，直接返回 2如果未安装，首先将分assets下的apk拷贝到sd卡上
	 * 3启动安装
	 * 
	 * @param packageName
	 *            安装apk的包文件名
	 * @param apkFile
	 *            apk在assets下的文件路径
	 * @throws IOException 
	 */
	public static void installAssetsApk(Activity activity, String packageName,
			String apkFile) throws IOException {
		// 1check package
		if (detectPackage(activity, packageName))
			return;
		// 2copy apk
		InputStream fis = activity.getAssets().open(apkFile);
		String apkSdFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hl/apk/" + apkFile;
		boolean copyResult = FileUtils.copyFile(fis, apkSdFile);
		if(!copyResult)return;
		// 3 launch the intent
		 Intent intent = new Intent();
		 intent.setAction(Intent.ACTION_VIEW);
		 intent.setDataAndType(Uri.fromFile(new File(apkSdFile)),  "application/vnd.android.package-archive");
		 activity.startActivity(intent);
	}
}
