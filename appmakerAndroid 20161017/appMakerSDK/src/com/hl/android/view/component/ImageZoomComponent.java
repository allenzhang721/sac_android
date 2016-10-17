package com.hl.android.view.component;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;
import com.hl.android.view.component.listener.OnComponentCallbackListener;
import com.hl.android.view.component.zoom.ImageViewTouch;

/**
 * 图片zoom操作
 * 
 * @author webcat
 * 
 */
@SuppressLint("NewApi")
public class ImageZoomComponent extends ImageViewTouch implements Component,ComponentListener,ComponentPost {
	public ComponentEntity entity = null;
	public AnimationSet animationset = null;
	public ImageZoomComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageZoomComponent(Context context, ComponentEntity entity) {
		super(context, null);
		this.setEntity(entity);
	}

	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;
	}
 
	@Override
	public void load() {

		try {
			if (HLSetting.IsResourceSD)
				load(FileUtils.getInstance().getFileInputStream(
						this.getEntity().localSourceId.trim()));
			else
				load(FileUtils.getInstance().getFileInputStream(getContext(),
						this.getEntity().localSourceId.trim()));
		} catch (OutOfMemoryError e) {
			Log.e("hl", "load error",e);
		}
	}
	Bitmap bitmap = null;

	Bitmap resizeBmp = null;

	Bitmap bp = null;
	@Override
	public void load(InputStream is) {
		
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inTempStorage = new byte[500 * 1024];
		try {
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		} catch (Exception e) {
			// TODO: handle exception
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 2;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int aa = this.getLayoutParams().width;
		int bb = this.getLayoutParams().height;

		if (this.entity.getRotation() != 0.0f) {

			resizeBmp = Bitmap.createScaledBitmap(bitmap,
					(int) this.getEntity().oldWidth,
					(int) this.getEntity().oldHeight, true);
			bitmap.recycle();
			Matrix mx = new Matrix();

			mx.setRotate(this.entity.getRotation());

			bp = Bitmap.createBitmap(resizeBmp, 0, 0,
					(int) this.getEntity().oldWidth,
					(int) this.getEntity().oldHeight, mx, true);
			resizeBmp.recycle();
			this.setImageBitmapReset(bp,true);
			// Drawable dbr = new BitmapDrawable(bp);
			// // this.setBackgroundDrawable(dbr);
			// this.setImageDrawable(dbr);
		} else {
			if (bitmap != null){
				resizeBmp = Bitmap.createScaledBitmap(bitmap, aa, bb, true);
				bitmap.recycle();
				this.setImageBitmapReset(resizeBmp,true);
				BitmapUtils.recycleBitmap(bitmap);
			}
			
		}
//
//		int aa = this.getLayoutParams().width;
//		int bb = this.getLayoutParams().height;
//
//		Bitmap resizeBmp = Bitmap.createScaledBitmap(bitmap, aa, bb, true);
		//this.setImageBitmapReset(bitmap, true);
	}
 

	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		this.clearAnimation();
		this.setVisibility(View.GONE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
	}

	@Override
	public void registerCallbackListener(
			OnComponentCallbackListener callbackListner) {		
	}

	MyCount1 count = null;
	
	public class MyCount1 extends CountDownTimer {
		public MyCount1(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			setVisibility(View.VISIBLE);
			startAnimation(animationset);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}

	@Override
	public void callBackListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recyle() {
		/*BitmapUtils.recycleBitmap(bitmap);*/
		
		if (null != bitmap) {
			bitmap.recycle();
			bitmap = null;
		}
		if (null != resizeBmp) {
			resizeBmp.recycle();
			resizeBmp = null;
		}
		
		if (bp != null){
			BitmapUtils.recycleBitmap(bp);
		}

		this.setImageBitmap(null);
	}
}
