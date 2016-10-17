package com.hl.common;

import android.view.View;

/**
 * 需要专门建立一个工具类来管理图片
 * 以防止内存总是出错
 * @author zhaoq
 *
 */
public class ImageUtils {
	public static void setViewBackground(View v,int id){
		try{
			v.setBackgroundResource(id);
		}catch(Exception e){
			System.gc();
		}
	}
}
