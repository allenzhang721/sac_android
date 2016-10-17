package com.hl.android.view.pageflip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.view.pageflip.animation.Rotate3dAnimation;
@SuppressWarnings("unused")
public class TurnPageFlipView extends AbstractPageFlipView {
	private ImageView imageViewRight;
	private ImageView imageViewLeft;
	private int curWidth = BookSetting.SCREEN_WIDTH;
	private int curHeight = BookSetting.SCREEN_HEIGHT;
	private Bitmap currentS;
	public TurnPageFlipView(Context context) {
		super(context);
	}
	public void show() {	
		imageViewRight = new ImageView(this.getContext());
		
		Bitmap bprightb=Bitmap.createBitmap(mCurPageBitmap, curWidth/2, 0, curWidth/2, curHeight);
		Drawable dbr = new BitmapDrawable( bprightb);
		imageViewRight.setBackgroundDrawable(dbr);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				curWidth / 2, curHeight);
		lp.addRule(RelativeLayout.ALIGN_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		this.bookLayout.addView(imageViewRight, lp);
		
		// 左边
		imageViewLeft = new ImageView(this.getContext());
		imageViewLeft.setBackgroundColor(Color.TRANSPARENT);
		Bitmap bpleft=Bitmap.createBitmap(mCurPageBitmap, 0, 0, curWidth/2, curHeight);
		Drawable dbl = new BitmapDrawable( bpleft);
		imageViewLeft.setBackgroundDrawable(dbl);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				curWidth / 2, curHeight);
		lp.addRule(RelativeLayout.ALIGN_LEFT);
		this.bookLayout.addView(imageViewLeft, lp1);

		this.setVisibility(View.VISIBLE);

		//保证公共页在翻页视图的上面
		BookController.getInstance().hlActivity.commonLayout.bringToFront();
	}
	private void setimg()
	{
		imageViewRight = new ImageView(this.getContext());
		
		Bitmap bprightb=Bitmap.createBitmap(mCurPageBitmap, curWidth/2, 0, curWidth/2, curHeight);
		Drawable dbr = new BitmapDrawable( bprightb);
		imageViewRight.setBackgroundDrawable(dbr);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				curWidth / 2, curHeight);
		lp.addRule(RelativeLayout.ALIGN_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		this.bookLayout.addView(imageViewRight, lp);
		
		// 左边
		imageViewLeft = new ImageView(this.getContext());
		imageViewLeft.setBackgroundColor(Color.TRANSPARENT);
		Bitmap bpleft=Bitmap.createBitmap(mCurPageBitmap, 0, 0, curWidth/2, curHeight);
		Drawable dbl = new BitmapDrawable( bpleft);
		imageViewLeft.setBackgroundDrawable(dbl);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				curWidth / 2, curHeight);
		lp.addRule(RelativeLayout.ALIGN_LEFT);
		this.bookLayout.addView(imageViewLeft, lp1);

		this.setVisibility(View.VISIBLE);
	}
	public void hide()
	{
		imageViewLeft.setVisibility(View.GONE);
		imageViewRight.setVisibility(View.GONE);

		this.bookLayout.removeView(imageViewLeft);
		this.bookLayout.removeView(imageViewRight);
		//this.viewPage.setVisibility(View.GONE);
		setBackgroundColor(Color.TRANSPARENT);
		this.setVisibility(View.GONE);
	}
	ActionOnEnd mAction;
	@Override
	public void play(int pageIndex,int newPageIndex,ActionOnEnd action) {
		mAction = action;
		currentS=BookController.getInstance().getCurrentSnapShotCashImage();//BookController.getInstance().getViewPage().getCurrentScreen();
		setimg();
		imageViewLeft.bringToFront();
		imageViewRight.bringToFront();
		if(pageIndex<newPageIndex)
			applyRotationnext(imageViewRight, 0, -180);
		else
			applyRotationpre(imageViewLeft, 0, 180);

	}

	private void applyRotationpre(View view, float start, float end) {
		// Find the center of the container
		final float centerX = curWidth / 2.0f;
		final float centerY = curHeight / 2.0f;
		imageViewLeft.bringToFront();
		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotationl = new Rotate3dAnimation(0, 90,
				centerX, centerY, 310.0f, true);
		rotationl.setDuration(HLSetting.FlipTime/2);
		rotationl.setFillAfter(true);
		//rotationl.setInterpolator(new AccelerateInterpolator());
		rotationl.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				imageViewRight.setVisibility(View.GONE);
				Matrix mx=new Matrix();
				mx.setScale(1, -1);
				
				Bitmap bprightb1=BookController.getInstance().getCurrentSnapShotCashImage();
				mx.setRotate(180);
				Bitmap bprightb=Bitmap.createBitmap(bprightb1, 0, 0, curWidth/2, curHeight,mx,true);
				imageViewRight.setVisibility(View.VISIBLE);

				Drawable dbr = new BitmapDrawable( bprightb);
				imageViewLeft.setBackgroundDrawable(dbr);
				//imageViewLeft.setBackgroundColor(Color.TRANSPARENT);
				//imageViewLeft.setImageBitmap(bprightb);
				final Rotate3dAnimation _rotationl = new Rotate3dAnimation(91,180,
						centerX, centerY, 310.0f, true);
				_rotationl.setDuration(HLSetting.FlipTime/2);
				_rotationl.setFillAfter(true);
				//_rotationl.setInterpolator(new AccelerateInterpolator());
				
				_rotationl.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						BookController.getInstance().hlActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								BookController.getInstance().revokeCommonPage();
								hide();
								BookController.getInstance().startPlay();
							}
						});
						
					}
				});
				imageViewLeft.startAnimation(_rotationl);

			}
		});
		
		imageViewLeft.startAnimation(rotationl);
		
	}
	private void applyRotationnext(View view, float start, float end) {
		final Bitmap bprightb1 = this.getRootView().getDrawingCache();
		// Find the center of the container
		final float centerX = curWidth / 2.0f;
		final float centerY = curHeight / 2.0f;
		
		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotation = new Rotate3dAnimation(0, -90,
				0, centerY, 310.0f, true);
		rotation.setDuration(HLSetting.FlipTime/2);
		rotation.setFillAfter(true);
		//rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(mAction!= null){
					mAction.doAction();
				}
				
				imageViewLeft.setVisibility(View.GONE);
				Matrix mx=new Matrix();
				mx.setScale(-1, 1);
				
				Bitmap bprightb1=BookController.getInstance().getCurrentSnapShotCashImage();
				mx.setRotate(180);
				Bitmap bprightb=Bitmap.createBitmap(bprightb1, 0, 0, curWidth/2, curHeight,mx,true);

				imageViewLeft.setVisibility(View.VISIBLE);

				Drawable dbr = new BitmapDrawable( bprightb1);
				imageViewRight.setBackgroundDrawable(dbr);
				final Rotate3dAnimation _rotation = new Rotate3dAnimation(-90, -180,
						0, centerY, 310.0f, true);
				_rotation.setDuration(HLSetting.FlipTime/2);
				_rotation.setFillAfter(true);
				//_rotation.setInterpolator(new AccelerateInterpolator());
				
				_rotation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						hide();
						BookController.getInstance().startPlay();
					}
				});
				imageViewRight.startAnimation(_rotation);

			}
		});
		
		imageViewRight.startAnimation(rotation);
		
	}
}
