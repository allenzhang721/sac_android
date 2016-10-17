package com.hl.android.core.helper;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.ScreenUtils;

public class BookHelper {
	/**
	 * 设置数据展示大小的方法类
	 * return 如果返回是
	 */
	public static void setPageScreenSize(Activity activity,String bookType){
		if (bookType.contains("hor_ver")) {
			if(BookSetting.FIX_SIZE){
				BookSetting.SCREEN_WIDTH = BookSetting.INIT_SCREEN_WIDTH;
				BookSetting.SCREEN_HEIGHT = BookSetting.INIT_SCREEN_HEIGHT;
//				BookSetting.BOOK_WIDTH = BookSetting.INIT_SCREEN_WIDTH;
//				BookSetting.BOOK_HEIGHT = BookSetting.INIT_SCREEN_HEIGHT;
			}else{
				BookSetting.SCREEN_WIDTH = ScreenUtils.getScreenWidth(activity);
				BookSetting.SCREEN_HEIGHT =ScreenUtils.getScreenHeight(activity);
			}
			BookSetting.IS_HOR_VER = true;
		} else {
			if (!bookType.contains("_hor")) {
				BookSetting.IS_HOR = false;
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				if(BookSetting.FIX_SIZE){
					BookSetting.SCREEN_WIDTH = BookSetting.INIT_SCREEN_WIDTH;
					BookSetting.SCREEN_HEIGHT = BookSetting.INIT_SCREEN_HEIGHT;
				}else{
					BookSetting.SCREEN_WIDTH = ScreenUtils.getScreenWidth(activity);
					BookSetting.SCREEN_HEIGHT =ScreenUtils.getScreenHeight(activity);
				}
				
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				if(BookSetting.FIX_SIZE){
					BookSetting.SCREEN_WIDTH = BookSetting.INIT_SCREEN_HEIGHT;
					BookSetting.SCREEN_HEIGHT = BookSetting.INIT_SCREEN_WIDTH;
				}else{

					BookSetting.SCREEN_WIDTH = ScreenUtils.getScreenHeight(activity);
					BookSetting.SCREEN_HEIGHT = ScreenUtils.getScreenWidth(activity);
				}
				
			}
			if (BookSetting.IS_HOR) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				if(BookSetting.FIX_SIZE){
					BookSetting.SCREEN_WIDTH = Math.max(
							BookSetting.INIT_SCREEN_HEIGHT,
							BookSetting.INIT_SCREEN_WIDTH);
					BookSetting.SCREEN_HEIGHT = Math.min(
							BookSetting.INIT_SCREEN_HEIGHT,
							BookSetting.INIT_SCREEN_WIDTH);
					
				}else{
					BookSetting.SCREEN_WIDTH = Math.max(ScreenUtils.getScreenHeight(activity),ScreenUtils.getScreenWidth(activity));
					BookSetting.SCREEN_HEIGHT = Math.min(ScreenUtils.getScreenHeight(activity),ScreenUtils.getScreenWidth(activity));
				}
				
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				if(BookSetting.FIX_SIZE){
					BookSetting.SCREEN_WIDTH = Math.min(
							BookSetting.INIT_SCREEN_HEIGHT,
							BookSetting.INIT_SCREEN_WIDTH);
					BookSetting.SCREEN_HEIGHT = Math.max(
							BookSetting.INIT_SCREEN_HEIGHT,
							BookSetting.INIT_SCREEN_WIDTH);
				}else{
					BookSetting.SCREEN_WIDTH = Math.min(ScreenUtils.getScreenHeight(activity),ScreenUtils.getScreenWidth(activity));
					BookSetting.SCREEN_HEIGHT = Math.max(ScreenUtils.getScreenHeight(activity),ScreenUtils.getScreenWidth(activity));
				}
			}
		}
		
	}

	public static void setupScreen(Activity activity) {
		HLSetting.display = activity.getWindowManager().getDefaultDisplay();
		if (BookSetting.IS_HOR) {
			

			if(BookSetting.FIX_SIZE){
				BookSetting.SCREEN_WIDTH = Math.max(
						BookSetting.INIT_SCREEN_HEIGHT,
						BookSetting.INIT_SCREEN_WIDTH);
				BookSetting.SCREEN_HEIGHT = Math.min(
						BookSetting.INIT_SCREEN_HEIGHT,
						BookSetting.INIT_SCREEN_WIDTH);
				
			}else{
				BookSetting.SCREEN_WIDTH = Math.max(
						HLSetting.display.getHeight(),
						HLSetting.display.getWidth());
				BookSetting.SCREEN_HEIGHT = Math.min(
						HLSetting.display.getHeight(),
						HLSetting.display.getWidth());
			}
			
		} else {

			if(BookSetting.FIX_SIZE){
				BookSetting.SCREEN_WIDTH = Math.min(
						BookSetting.INIT_SCREEN_HEIGHT,
						BookSetting.INIT_SCREEN_WIDTH);
				BookSetting.SCREEN_HEIGHT = Math.max(
						BookSetting.INIT_SCREEN_HEIGHT,
						BookSetting.INIT_SCREEN_WIDTH);
				
			}else{
				BookSetting.SCREEN_WIDTH = Math.min(
						HLSetting.display.getHeight(),
						HLSetting.display.getWidth());
				BookSetting.SCREEN_HEIGHT = Math.max(
						HLSetting.display.getHeight(),
						HLSetting.display.getWidth());
			}
			
		}
//		if(BookSetting.FIX_SIZE){
//			BookSetting.BOOK_WIDTH = BookSetting.INIT_SCREEN_WIDTH;
//			BookSetting.BOOK_HEIGHT = BookSetting.INIT_SCREEN_HEIGHT;
//		}else{
//			BookSetting.BOOK_WIDTH = (int) ScreenUtils.getHorScreenValue(BookSetting.SCREEN_WIDTH);
//			BookSetting.BOOK_HEIGHT = (int) ScreenUtils.getVerScreenValue(BookSetting.SCREEN_HEIGHT);
//		}
	}
}
