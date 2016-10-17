package com.hl.realtest.anim;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class ScaleAnim extends Animation {
	// private static final float FIRST_RANGE = 0.5f;
	// private static final float SECOND_RANGE = 0.75f;
	private float mFirstRange;
	private float mSecondRange;
	private float mGoBackScaleTo;
	private float a, b;
	private float mScale;
	public ScaleAnim(float firstRange, float secondRange, float goBackTo) {
		super();
		this.mFirstRange = firstRange;
		this.mSecondRange = secondRange;
		this.mGoBackScaleTo = goBackTo;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		Matrix mMatrix = t.getMatrix();
		if (interpolatedTime <= mFirstRange) {
			a=1.0f / mFirstRange;
			b=0.0f;
			
		} else if (interpolatedTime <= mSecondRange) {
			a=(mGoBackScaleTo-1.0f)/(mSecondRange-mFirstRange);
			b=1.0f-a*mFirstRange;
		} else {
			a=(1-mGoBackScaleTo)/(1-mSecondRange);
			b=1-a;
		}
		mScale=a* interpolatedTime+b;
		mMatrix.setScale(mScale, mScale);
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		setInterpolator(new LinearInterpolator());
	}
}
