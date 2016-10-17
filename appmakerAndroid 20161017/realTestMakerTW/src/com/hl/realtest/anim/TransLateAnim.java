package com.hl.realtest.anim;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class TransLateAnim extends Animation {
	private int mTottleMoveY;
	private int mGoBackMoveY;
	private int mCenterX;
	private int mCenterY;
	private float mFirstRange;
	private static final float X_TRANSLATE = 0.0f;
	private float a, b, c;
	private float mYtranslate;

	public TransLateAnim(int tottleMoveY, int goBackMoveY, float firstRange) {
		super();
		this.mTottleMoveY = tottleMoveY;
		this.mGoBackMoveY = goBackMoveY;
		this.mFirstRange = firstRange;
	}

	/*
	 * @Override protected void applyTransformation(float interpolatedTime,
	 * Transformation t) { Matrix matrix = t.getMatrix(); if (interpolatedTime
	 * <= 0.8f) { matrix.setTranslate(0.0f, mTottleMoveY * 1.25f *
	 * interpolatedTime); } else if (interpolatedTime <= 0.9f) {
	 * matrix.setTranslate(0.0f, mTottleMoveY + 8.0f * mGoBackMoveY - 10.0f *
	 * mGoBackMoveY * interpolatedTime); } else { matrix.setTranslate(0.0f,
	 * 10.0f * mGoBackMoveY * interpolatedTime + mTottleMoveY - 10.0f *
	 * mGoBackMoveY); } matrix.preTranslate(-mCenterX, -mCenterY); }
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		Matrix mMatrix = t.getMatrix();
		if (interpolatedTime <= mFirstRange) {
			a = mTottleMoveY / (float) Math.pow(mFirstRange, 2);
			b=0.0f;
			c=0.0f;
		} else {
			a = -4 * mGoBackMoveY 
					/ (float) Math.pow(mFirstRange - 1, 2);
			b = 4 * mGoBackMoveY * (mFirstRange + 1)
					/ (float) Math.pow(mFirstRange - 1, 2);
			c = mTottleMoveY -4 * mGoBackMoveY * mFirstRange
					/ (float) Math.pow(mFirstRange - 1, 2);
		}
		mYtranslate = a * (float) Math.pow(interpolatedTime, 2);
		mYtranslate += b * interpolatedTime;
		mYtranslate += c;
		mMatrix.setTranslate(X_TRANSLATE, mYtranslate);
		mMatrix.preTranslate(-mCenterX, -mCenterY);
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCenterX = 0;
		mCenterY = mTottleMoveY;
		setInterpolator(new LinearInterpolator());
	}
}
