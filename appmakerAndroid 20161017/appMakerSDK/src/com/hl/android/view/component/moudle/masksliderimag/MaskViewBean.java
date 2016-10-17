package com.hl.android.view.component.moudle.masksliderimag;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.text.TextPaint;

import com.hl.android.R;
import com.hl.android.book.entity.moudle.MaskBean;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;

/**
 * 绘制单个视图的对象类
 * @author zhaoq
 * @version 1.0
 * @createed 2013-10-10
 */
public class MaskViewBean {
	public Bitmap mBitmap;
	public MaskBean mMaskBean;
	private Context mContext;
	

	private Bitmap soundBitmap;
	
	/**
	 * 用来区分当前绘制的是大图还是小图
	 */
	private boolean isBig = false;
	
	public MaskViewBean(Context context,MaskBean maskBean){
		mMaskBean = maskBean;
		mContext = context;
		mBitmap = BitmapUtils.getBitMap(mMaskBean.imgSource, mContext);
		if(!StringUtils.isEmpty(maskBean.audioSourceID)){
			soundBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sound);
		}
	}
	
	/**
	 * 绘制视图
	 * @param canvas  
	 * @param dst  目标区域主要是用到偏移量
	 * @param isPortlet  是否是局部视图  true要绘制小视图  false大视图
	 */
	public void drawMaskView(Canvas canvas,RectF dst,Boolean isPortlet,boolean drawtext){
		if(isPortlet){
			canvas.drawBitmap(mBitmap, null, dst, null);
		}else{
			drawBigView(canvas,dst,drawtext);
		}
	}
	/**
	 * 绘制大视图
	 * @param canvas
	 * @param dst
	 */
	private void drawBigView(Canvas canvas,RectF dst,boolean drawtext){
		TextPaint paint = new TextPaint();
		paint.setTextSize(ScreenUtils.dip2px(mContext, 20));
		paint.setColor(Color.WHITE);
		int x = (int) ((dst.width() - paint.measureText(mMaskBean.title))/2);
		if(drawtext)
		canvas.drawText(mMaskBean.title, x, ScreenUtils.dip2px(mContext, 50)/2, paint);
		
		float top = ScreenUtils.dip2px(mContext, 60);
		//中间图片的使用范围
		RectF bitmapDst = new RectF(dst.left, top, dst.right,  BookSetting.BOOK_HEIGHT-top);
		canvas.drawBitmap(mBitmap, null, bitmapDst, null);
		
		x = (int) ((dst.width() - paint.measureText(mMaskBean.dec))/2);
		if(drawtext)
		canvas.drawText(mMaskBean.dec,  x, dst.bottom - ScreenUtils.dip2px(mContext, 50)/2, paint);
		

		RectF soundDst = new RectF(bitmapDst.left + 20, bitmapDst.bottom - 80, dst.left + 80, bitmapDst.bottom - 20);
		if(soundBitmap != null){
			canvas.drawBitmap(soundBitmap, null, soundDst, null);
		}
	}
	
	
	
	/**
	 * 播放我的音乐
	 * @param media
	 */
	public void playMedia(MediaPlayer  media){
//		if(!isBig)return;
		media.reset();
		AssetFileDescriptor ass = null;
		FileInputStream fis = null;
		String mediaFile = mMaskBean.audioSourceID;
		if(StringUtils.isEmpty(mediaFile))return;
		try {
			if (HLSetting.IsResourceSD) {
				String filePath = FileUtils.getInstance().getFilePath(
						mediaFile);
				String privatePath = mContext.getFilesDir()
						.getAbsolutePath();
				if (filePath.contains(privatePath)) {
					fis = new FileInputStream(new File(filePath));
					FileDescriptor fd = fis.getFD();
					media.setDataSource(fd);
				}else{
					media.setDataSource(filePath);
				}

			} else {				
				ass = FileUtils.getInstance().getFileFD(mContext,mediaFile);
				media.setDataSource(ass.getFileDescriptor(),
						ass.getStartOffset(), ass.getLength());
			}
			media.prepare();
			media.start();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (ass != null) {
					ass.close();
					ass = null;
				}
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void recyle(){
		BitmapUtils.recycleBitmap(mBitmap);
		BitmapUtils.recycleBitmap(soundBitmap);
		
	}
}
