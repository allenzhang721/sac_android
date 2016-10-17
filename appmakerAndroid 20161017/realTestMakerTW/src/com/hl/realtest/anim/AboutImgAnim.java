package com.hl.realtest.anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AboutImgAnim extends Animation {
	private float mFromX;
	private float mToX;
	private float mFromY;
	private float mToY;

	private int mPivotXType = ABSOLUTE;
	private int mPivotYType = ABSOLUTE;
	private float mPivotXValue = 0.0f;
	private float mPivotYValue = 0.0f;

	private float mPivotX;
	private float mPivotY;

	/***
	 * Constructor to use when building a ScaleAnimation from code
	 * 
	 * @param fromX
	 *            Horizontal scaling factor to apply at the start of the
	 *            animation
	 * @param toX
	 *            Horizontal scaling factor to apply at the end of the animation
	 * @param fromY
	 *            Vertical scaling factor to apply at the start of the animation
	 * @param toY
	 *            Vertical scaling factor to apply at the end of the animation
	 */
	public AboutImgAnim(float fromX, float toX, float fromY, float toY) {
		mFromX = fromX;
		mToX = toX;
		mFromY = fromY;
		mToY = toY;
		mPivotX = 0;
		mPivotY = 0;
	}

	/***
	 * Constructor to use when building a ScaleAnimation from code
	 * 
	 * @param fromX
	 *            Horizontal scaling factor to apply at the start of the
	 *            animation
	 * @param toX
	 *            Horizontal scaling factor to apply at the end of the animation
	 * @param fromY
	 *            Vertical scaling factor to apply at the start of the animation
	 * @param toY
	 *            Vertical scaling factor to apply at the end of the animation
	 * @param pivotX
	 *            The X coordinate of the point about which the object is being
	 *            scaled, specified as an absolute number where 0 is the left
	 *            edge. (This point remains fixed while the object changes
	 *            size.)
	 * @param pivotY
	 *            The Y coordinate of the point about which the object is being
	 *            scaled, specified as an absolute number where 0 is the top
	 *            edge. (This point remains fixed while the object changes
	 *            size.)
	 */
	public AboutImgAnim(float fromX, float toX, float fromY, float toY,
			float pivotX, float pivotY) {
		mFromX = fromX;
		mToX = toX;
		mFromY = fromY;
		mToY = toY;

		mPivotXType = ABSOLUTE;
		mPivotYType = ABSOLUTE;
		mPivotXValue = pivotX;
		mPivotYValue = pivotY;
	}

	/***
	 * Constructor to use when building a ScaleAnimation from code
	 * 
	 * @param fromX
	 *            Horizontal scaling factor to apply at the start of the
	 *            animation
	 * @param toX
	 *            Horizontal scaling factor to apply at the end of the animation
	 * @param fromY
	 *            Vertical scaling factor to apply at the start of the animation
	 * @param toY
	 *            Vertical scaling factor to apply at the end of the animation
	 * @param pivotXType
	 *            Specifies how pivotXValue should be interpreted. One of
	 *            Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
	 *            Animation.RELATIVE_TO_PARENT.
	 * @param pivotXValue
	 *            The X coordinate of the point about which the object is being
	 *            scaled, specified as an absolute number where 0 is the left
	 *            edge. (This point remains fixed while the object changes
	 *            size.) This value can either be an absolute number if
	 *            pivotXType is ABSOLUTE, or a percentage (where 1.0 is 100%)
	 *            otherwise.
	 * @param pivotYType
	 *            Specifies how pivotYValue should be interpreted. One of
	 *            Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
	 *            Animation.RELATIVE_TO_PARENT.
	 * @param pivotYValue
	 *            The Y coordinate of the point about which the object is being
	 *            scaled, specified as an absolute number where 0 is the top
	 *            edge. (This point remains fixed while the object changes
	 *            size.) This value can either be an absolute number if
	 *            pivotYType is ABSOLUTE, or a percentage (where 1.0 is 100%)
	 *            otherwise.
	 */
	public AboutImgAnim(float fromX, float toX, float fromY, float toY,
			int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
		mFromX = fromX;
		mToX = toX;
		mFromY = fromY;
		mToY = toY;

		mPivotXValue = pivotXValue;
		mPivotXType = pivotXType;
		mPivotYValue = pivotYValue;
		mPivotYType = pivotYType;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float sx = 1.0f;
		float sy = 1.0f;
		if (interpolatedTime <= 0.8f) {
			float rate = interpolatedTime/0.8f;
			if (mFromX != 1.0f || mToX != 1.0f) {
				sx = mFromX + ((mToX - mFromX) * rate);
			}
			if (mFromY != 1.0f || mToY != 1.0f) {
				sy = mFromY + ((mToY - mFromY) * rate);
			}
		} else if(interpolatedTime <= 0.9f){
			float rate =interpolatedTime - 0.8f;
			sx = mToX -  (0.1f*mToX)*rate;
			sy = mToY -  (0.1f*mToY)*rate;
		 }else {
			float rate =interpolatedTime;
			sx = 0.9f*mToX +  (0.1f*mToX)*rate;
			sy = 0.9f*mToY +  (0.1f*mToY)*rate;
			/*sx = mToX - ((0.75f-interpolatedTime)/0.25f)*(mToX - mFromX) + ((1.00f-interpolatedTime)/0.25f)*(mToX - mFromX);
			sy = mToY - ((0.75f-interpolatedTime)/0.25f)*(mToY - mFromY)+ ((1.00f-interpolatedTime)/0.25f)*(mToY - mFromY);
				
			if (mFromX != 1.0f || mToX != 1.0f) {
				sx = mFromX + ((mToX - mFromX) * interpolatedTime);
			}
			if (mFromY != 1.0f || mToY != 1.0f) {
				sy = mFromY + ((mToY - mFromY) * interpolatedTime);
			}*/
		}

		if (mPivotX == 0 && mPivotY == 0) {
			t.getMatrix().setScale(sx, sy);
		} else {
			t.getMatrix().setScale(sx, sy, mPivotX, mPivotY);
		}
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);

		mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth);
		mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight);
	}
}
