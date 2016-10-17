package com.hl.android.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class AppUtils {
	private static final String Mc_String_DataPath = "HLData";

	private static String appPath = "";

	/**
	 * 获得当前应用的数据文件存放位置
	 * 
	 * @param activity
	 * @return
	 */
	public static String getAppPath(Activity activity) {
		if (StringUtils.isEmpty(appPath)) {
			appPath = activity
					.getApplication()
					.getDir(Mc_String_DataPath,
							Application.MODE_WORLD_WRITEABLE).getAbsolutePath();
		}
		return appPath;
	}

	
	private static String appSdPath;

	/**
	 * 取得本应用在系统中SD卡的的文件夹路径。以“/”结尾。<br>
	 * 如果没有Sd卡，且默认内置的sd卡（/mnt/sdcard）不存在，返回空。<br>
	 * 例如：/mnt/sdcard/
	 * */
	public static String getAppSdPath() {

		if (appSdPath != null)
			return appSdPath;
		String strPath = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			strPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			appSdPath = strPath.endsWith(File.separator) ? strPath : strPath
					+ File.separator;
		}
		if (strPath == null) {
			strPath = "/mnt/sdcard/";
		}
		if (new File(strPath).exists()) {
			appSdPath = strPath;
			return appSdPath;
		} else {
			return null;
		}
	}

	public static String getAppDataPath(Activity activity) {
		String appDataPath = "";
		String sdPath = getAppSdPath();

		if (sdPath != null) {
			appDataPath = StringUtils.contactForPath(sdPath,
					activity.getPackageName(), Mc_String_DataPath);
		} else {
			appDataPath = getAppPath(activity);
		}

		return appDataPath;
	}

	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}
	
	/**
	 * detect the package is installed
	 * @param activity current activity
	 * @param packageName 
	 * @return true if it is installed otherwise false
	 */
	public static boolean detectPackage(Context activity, String packageName) {
		PackageInfo packageInfo = null;
		// "com.adobe.flashplayer"
		try {
			packageInfo = activity.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
		 
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
	public static void installAssetsApk(Activity activity,String apkFile,int requestCode) throws IOException {
//		// 1check package
//		if (detectPackage(activity, packageName))
//			return;
		// 2copy apk
		InputStream fis = activity.getAssets().open(apkFile);
		String apkSdFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hl/apk/" + apkFile;
		boolean copyResult = FileUtils.copyFile(fis, apkSdFile);
		if(!copyResult)return;
		// 3 launch the intent
		 Intent intent = new Intent();
		 intent.setAction(Intent.ACTION_VIEW);
		 intent.setDataAndType(Uri.fromFile(new File(apkSdFile)),  "application/vnd.android.package-archive");
		 activity.startActivityForResult(intent, requestCode);
	}
}
