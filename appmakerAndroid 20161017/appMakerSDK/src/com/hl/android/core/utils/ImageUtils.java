package com.hl.android.core.utils;

import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
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
			try{
				v.setBackgroundResource(id);
			}catch(Exception e){
				System.gc();
			}
		}catch(Error er){
			
		}
	}
	
	/**
	 * 获得按钮点击效果的drawabel
	 * @param btnResource  正常的图片
	 * @param btnSelectResource 按下的效果
	 * @param context
	 * @return
	 */
	public static StateListDrawable getButtonDrawable(String btnResource,String btnSelectResource,Context context){
		StateListDrawable btnDrawable = new StateListDrawable();
		BitmapDrawable dbg = BitmapUtils.getBitmapDrawable(context,btnResource);
		BitmapDrawable selectdbg = BitmapUtils.getBitmapDrawable(context,btnSelectResource);
		btnDrawable.addState(new int[] { android.R.attr.state_pressed }, selectdbg);
		btnDrawable.addState(new int[] { android.R.attr.state_focused }, dbg);
		btnDrawable.addState(new int[] { android.R.attr.state_enabled }, dbg);
		btnDrawable.addState(new int[] {}, dbg);
		return btnDrawable;
	}
	
	public static void recyleBitmapList(Collection<Bitmap> bitmaps){
		for(Bitmap b:bitmaps){
			b.recycle();
		}
		bitmaps.clear();
	}
}
