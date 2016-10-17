package com.hl.android.view.component.moudle;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ScrollView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.moudle.carouseimg.GalleryImageAdapter;

public class HLCarouselImageUIComponent extends Gallery implements Component {

	private Camera mCamera = new Camera();
	private Matrix mMatrix = new Matrix();
	private int mCoveflowCenter;
	private MoudleComponentEntity mEntity;
	private Context mContext;
	private float mMaxRotationAngle = 30;
	private int mWidth;
	private int mHeight;
	public HLCarouselImageUIComponent(Context context) {
		super(context);
		mContext = context;
		setStaticTransformationsEnabled(true);
		setChildrenDrawingOrderEnabled(true);
	}

	public HLCarouselImageUIComponent(Context context, ComponentEntity entity) {
		super(context);
		mContext = context;
		mEntity = (MoudleComponentEntity) entity;
		setStaticTransformationsEnabled(true);
		setChildrenDrawingOrderEnabled(true);
	}

	protected int getCenterOfCoverflow() {
		return ((getWidth() - getPaddingLeft() - getPaddingRight()) / 2)
				+ getPaddingLeft();
	}

	protected int getCenterOfView(View view) {
		return view.getLeft() + (view.getWidth() / 2);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		 int selectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();
	        if (selectedIndex < 0) 
	        {
	            return i;
	        }
	        
	        if (i < selectedIndex)
	        {
	            return i;
	        }
	        else if (i >= selectedIndex)
	        {
	            return childCount - 1 - i + selectedIndex;
	        }
	        else
	        {
	            return i;
	        }
	}
	
	protected float calculateOffsetOfCenter(View view) {
		final int pCenter = getCenterOfCoverflow();
		final int cCenter = getCenterOfView(view);

		float offset = (cCenter - pCenter) / (pCenter * 1.0f);
		offset = Math.min(offset, 1.0f);
		offset = Math.max(offset, -1.0f);
		return offset;
	}

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		return false;
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean ret;
		final float offset = calculateOffsetOfCenter(child);
		getTransformationMatrix(child, offset);
		final int saveCount = canvas.save();
		canvas.concat(mMatrix);
		ret = super.drawChild(canvas, child, drawingTime);
		canvas.restoreToCount(saveCount);
		return ret;
	}

	
	
	void getTransformationMatrix(View child, float offset) {
		final int halfWidth = child.getLeft() + (child.getMeasuredWidth() >> 1);
		final int halfHeight = child.getMeasuredHeight() >> 1;
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		float rotationAngle = 0;
		if (childCenter == mCoveflowCenter) {
			rotationAngle = 0;
		} else { // 两侧的childView
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle: mMaxRotationAngle;
			}
		}
		mCamera.save();
		mCamera.translate(-offset * mWidth/5, 0.0f, Math.abs(offset) * mWidth/5);
		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(mMatrix);
		mCamera.restore();
		mMatrix.preTranslate(-halfWidth, -halfHeight);
		mMatrix.postTranslate(halfWidth, halfHeight);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.mEntity = (MoudleComponentEntity) entity;
	}

	@Override
	public void load() {
		mWidth= mEntity.getItemWidth();
		mHeight = mEntity.getItemHeight();
		mWidth = (int) ScreenUtils.getHorScreenValue(mWidth);
		mHeight =(int) ScreenUtils.getVerScreenValue(mHeight);
		ScrollView.LayoutParams lp = new ScrollView.LayoutParams(mWidth, mHeight);
		GalleryImageAdapter adapter = new GalleryImageAdapter(mContext,
				mEntity.getSourceIDList(), lp);
		setAdapter(adapter);
		int selection = Integer.MAX_VALUE / 2;
		while (selection % mEntity.getSourceIDList().size() != 0) {// 保证第一个显示的位置
			selection++;
		}
		setSelection(selection);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}

	@Override
	public void play() {
		// mIsPausing = false;
		// mRunning = true;
	}

	@Override
	public void stop() {
		// mIsPausing = false;
		// mRunning = false;
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// mIsPausing = false;
	}

}
