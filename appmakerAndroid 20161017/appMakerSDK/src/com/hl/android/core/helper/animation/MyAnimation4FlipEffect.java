package com.hl.android.core.helper.animation;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 用于翻转切换动画
 * @author wangdayong
 * @version 1.0
 * @createed 2013-11-14
 */
public class MyAnimation4FlipEffect extends Animation {

	private final float mFromDegree;// 旋转前的角度
	private final float mToDegree;// 旋转后的角度
	private Camera mCamera;
	private int mWidth;
	private int mHeight;
	private boolean changeBitmap;
	private ImageView mImageView;
	private Bitmap mNextBitmap;
	private String mDdirection;
	public MyAnimation4FlipEffect(float fromDegree, float toDegree,ImageView imageView,Bitmap nextBitmap,String direction) {
		mFromDegree = fromDegree;
		mToDegree = toDegree;
		mCamera = new Camera();
		mImageView=imageView;
		mNextBitmap=nextBitmap;
		mDdirection = direction;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mWidth = width;
		mHeight = height;
	}

	@Override
	protected void applyTransformation(float interpolatedTime,Transformation t) {
		Matrix matrix = t.getMatrix();
		float cameraty=200*interpolatedTime;
		float degree = mFromDegree + (mToDegree - mFromDegree) * interpolatedTime;
		//翻转到一半时切换图片和角度
		if(interpolatedTime>=0.5f){
			if(!changeBitmap){
				changeBitmap=true;
				mImageView.setImageBitmap(mNextBitmap);
				mImageView.setScaleType(ScaleType.FIT_XY);
			}
			degree=degree-180;
			cameraty=200*(1-interpolatedTime);
		}
		Log.d("ww", "degree:"+degree);
		mCamera.save();
		mCamera.translate(0, 0, cameraty);
		if (mDdirection.equals("left")) {
			mCamera.rotateY(-degree);
		}else if(mDdirection.equals("right")){
			mCamera.rotateY(degree);
		}else if(mDdirection.equals("up")){
			mCamera.rotateX(degree);
		}else if(mDdirection.equals("down")){
			mCamera.rotateX(-degree);
		}
		mCamera.getMatrix(matrix);
		mCamera.restore();
		matrix.preTranslate(-mWidth/2.0f,-mHeight/2.0f);
		matrix.postTranslate(mWidth/2.0f,mHeight/2.0f);
	}
}
