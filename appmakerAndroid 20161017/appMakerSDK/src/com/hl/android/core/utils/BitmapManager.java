package com.hl.android.core.utils;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

/**
 * bitmap的管理类
 * 这里获得的bitmap不允许自行进行回收，必须调用管理类的回收方法
 * 所有的图片都会从这里先行获取
 * 使用软引用来避免内存溢出
 * @author zhaoq
 *
 */
public class BitmapManager {
	//软引用存放的容器类，所有的bitmap都会先缓存在这个软引用中
	public static HashMap<String,SoftReference<Bitmap>> softMap = new HashMap<String, SoftReference<Bitmap>>();
	
	/**
	 * 从缓存中获取bitmap
	 * @param key  绝对路径
	 * @return 缓存的bitmap，可能为null
	 */
	public static Bitmap getBitmapFromCache(String key){
		synchronized(softMap){
			SoftReference<Bitmap> softBitmap = softMap.get(key);
			if(softBitmap !=null){
				Bitmap bitmap = softBitmap.get();
				if(bitmap == null)softMap.remove(key);
				return bitmap;
			}else{
				return null;
			}
		}
	}
	
	/**
	 * 将图片缓存上，同时也要注意所有的句柄都需要被去掉
	 * @param key
	 * @param bitmap
	 */
	public static void putBitmapCache(String key,Bitmap bitmap){
		SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
		synchronized(softMap){
			softMap.put(key, softBitmap);
		}
	}
}
