package com.hl.realtest;

import android.app.Activity;

import com.hl.android.core.utils.ScreenUtils;
/**
 * 按照屏幕的适应性计算最新的页面ui
 * @author Administrator
 *
 */
public class ScreenAdapter {
	public static final int init_width = 1536;
	public static final int init_height = 2048;
	
	private static int actual_width = init_width;
	private static int actual_height = init_height;
	
	private static float width_rate = 1.0f;
	private static float height_rate = 1.0f;
	public static void setScreenSize(Activity activity){
		actual_width = ScreenUtils.getScreenWidth(activity);
		actual_height = ScreenUtils.getScreenHeight(activity);
		
		width_rate = (float)actual_width/(float)init_width;
		height_rate = (float)actual_height/(float)init_height;
	}
	
	
	/**
	 * 按照宽度计算
	 * @param w
	 * @return
	 */
	public static int calcWidth(int w){
		return (int) (w*width_rate);
	}
	
	/**
	 * 按照宽度计算
	 * @param w
	 * @return
	 */
	public static int calcWidth(float w){
		return (int) (w*width_rate);
	}

	/**
	 * 按照高度计算
	 * @param h
	 * @return
	 */
	public static int calcHeight(int h){
		return (int) (h*height_rate);
	}
}
