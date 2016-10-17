package com.hl.android.core.helper.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 用于立方体切换动画中一面切入或切出
 * @author wangdayong
 * @version 1.0
 * @createed 2013-11-14
 */
public class MyAnimation4CubeEffect extends Animation {

	private final float mFromDegree;// 旋转前的角度
	private final float mToDegree;// 旋转后的角度
	private Camera mCamera;
	private int mHalfWidth;
	private int mHalfHeight;
	private String mDdirection;

	public MyAnimation4CubeEffect(float fromDegree, float toDegree, String direction) {
		mFromDegree = fromDegree;
		mToDegree = toDegree;
		mCamera = new Camera();
		mDdirection = direction;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mHalfWidth = width / 2;
		mHalfHeight = height / 2;
	}

	@Override
	protected void applyTransformation(float interpolatedTime,
			Transformation t) {
		Matrix matrix = t.getMatrix();
		float degree = mFromDegree + (mToDegree - mFromDegree)
				* interpolatedTime;
		mCamera.save();
		if (degree >= 82.0f) {
			if (mDdirection.equals("left") || mDdirection.equals("right")) {
				mCamera.rotateY(90.0f);
			} else {
				mCamera.rotateX(-90.0f);
			}
		} else if (degree <= -82.0f) {
			if (mDdirection.equals("left") || mDdirection.equals("right")) {
				mCamera.rotateY(90.0f);
			} else {
				mCamera.rotateX(-90.0f);
			}
		} else {
			if (mDdirection.equals("left") || mDdirection.equals("right")) {
				mCamera.translate(0, 0, mHalfWidth);
				mCamera.rotateY(degree);
				mCamera.translate(0, 0, -mHalfWidth);
			} else {
				mCamera.translate(0, 0, mHalfHeight);
				mCamera.rotateX(-degree);
				mCamera.translate(0, 0, -mHalfHeight);
			}

		}
		mCamera.getMatrix(matrix);
		mCamera.restore();
		matrix.preTranslate(-mHalfWidth, -mHalfHeight);
		matrix.postTranslate(mHalfWidth, mHalfHeight);
	}
}
