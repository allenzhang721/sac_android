package com.hl.android.book.entity;

import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;

public class SubImageItem {
	public String aniType;
	public String aniProperty;
	public long delay;
	public long duration;
	public String sourceID;
	private Bitmap bitmap=null;
	public int mIndex=-1;
	
	
	public Bitmap getBitmap(Context context){
		if(bitmap==null){
			changeSourceID2Bitmap(context);
		}
		return bitmap;
	}
	
	public void changeSourceID2Bitmap(Context context) {
		bitmap=BitmapManager.getBitmapFromCache(sourceID);
		if(bitmap==null){
			bitmap=BitmapUtils.getBitMap(sourceID, context);
			BitmapManager.putBitmapCache(sourceID, bitmap);
		}
	}
}
