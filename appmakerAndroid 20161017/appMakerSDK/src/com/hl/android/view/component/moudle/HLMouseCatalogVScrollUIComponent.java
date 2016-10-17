package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.moudle.SubCatalogVScrollUIComponent.MyRect;

public class HLMouseCatalogVScrollUIComponent extends LinearLayout implements Component{
	private SubCatalogVScrollUIComponent leftView;
	private SubCatalogVScrollUIComponent middleView;
	private SubCatalogVScrollUIComponent rightView;
	private float lMT = 1.0f;
	private float mMT = 1.0f;
	private float rMT = 1.0f;
	private ArrayList<Bitmap> leftViewBitmaps;
	private ArrayList<Bitmap> middleViewBitmaps;
	private ArrayList<Bitmap> rightViewBitmaps;
	private ArrayList<Integer> leftViewIndexs;
	private ArrayList<Integer> middleViewIndexs;
	private ArrayList<Integer> rightViewIndexs;
	private ComponentEntity mEntity;
	private Context mContext;
	private int mWidth;
	private float mHeight;
	protected boolean waitDoUpEvent;

	public HLMouseCatalogVScrollUIComponent(Context context,ComponentEntity entity) {
		super(context);
		mContext=context;
		mEntity=entity;
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity=entity;
	}

	@Override
	public void load() {
		mWidth=getLayoutParams().width;
		mHeight=getLayoutParams().height;
		leftViewBitmaps=new ArrayList<Bitmap>();
		middleViewBitmaps=new ArrayList<Bitmap>();
		rightViewBitmaps=new ArrayList<Bitmap>();
		leftViewIndexs=new ArrayList<Integer>();
		middleViewIndexs=new ArrayList<Integer>();
		rightViewIndexs=new ArrayList<Integer>();
		for (int i = 0; i <((MoudleComponentEntity)(mEntity)).leftRenderBean.size(); i++) {
			String sourceid=((MoudleComponentEntity)(mEntity)).leftRenderBean.get(i).sourceID;
			Bitmap bitmap=BitmapUtils.getBitMap(sourceid, mContext);
			int sourceIndex=((MoudleComponentEntity)(mEntity)).leftRenderBean.get(i).sourceIndex;
			leftViewBitmaps.add(bitmap);
			leftViewIndexs.add(sourceIndex);
		}
		for (int i = 0; i <((MoudleComponentEntity)(mEntity)).middleRenderBean.size(); i++) {
			String sourceid=((MoudleComponentEntity)(mEntity)).middleRenderBean.get(i).sourceID;
			Bitmap bitmap=BitmapUtils.getBitMap(sourceid, mContext);
			int sourceIndex=((MoudleComponentEntity)(mEntity)).middleRenderBean.get(i).sourceIndex;
			middleViewBitmaps.add(bitmap);
			middleViewIndexs.add(sourceIndex);
		}
		for (int i = 0; i <((MoudleComponentEntity)(mEntity)).rightRenderBean.size(); i++) {
			String sourceid=((MoudleComponentEntity)(mEntity)).rightRenderBean.get(i).sourceID;
			Bitmap bitmap=BitmapUtils.getBitMap(sourceid, mContext);
			int sourceIndex=((MoudleComponentEntity)(mEntity)).rightRenderBean.get(i).sourceIndex;
			rightViewBitmaps.add(bitmap);
			rightViewIndexs.add(sourceIndex);
		}
		leftView = new SubCatalogVScrollUIComponent(mContext,leftViewBitmaps,leftViewIndexs,mWidth/3,mHeight);
		middleView = new SubCatalogVScrollUIComponent(mContext,middleViewBitmaps,middleViewIndexs,mWidth/3+mWidth%3,mHeight);
		rightView = new SubCatalogVScrollUIComponent(mContext,rightViewBitmaps,rightViewIndexs,mWidth/3,mHeight);
		setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams layoutParams=new LayoutParams(mWidth/3, LayoutParams.WRAP_CONTENT);
		LayoutParams layoutParams4middle=new LayoutParams(mWidth/3+mWidth%3, LayoutParams.WRAP_CONTENT);
		addView(leftView,layoutParams);
		addView(middleView,layoutParams4middle);
		addView(rightView,layoutParams);
		leftView.doBeginAnim();
		middleView.doBeginAnim();
		rightView.doBeginAnim();
		setClickable(true);
		SimpleOnGestureListener listener = new SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent event) {
				waitDoUpEvent=true;
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				waitDoUpEvent=true;
			}
			@Override
			public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
					float arg2, float arg3) {
				
				for (int i = 0; i < leftView.getRects().size(); i++) {
					leftView.getRects().get(i).mPositionY -= (int)(arg3 * lMT);
				}
				for (int i = 0; i < middleView.getRects().size(); i++) {
					middleView.getRects().get(i).mPositionY -= (int)(arg3 * mMT);
				}
				for (int i = 0; i < rightView.getRects().size(); i++) {
					rightView.getRects().get(i).mPositionY -= (int)(arg3 * rMT);
				}
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				leftView.moveAutoWidthSpeed(velocityY * 0.01f * lMT);
				middleView.moveAutoWidthSpeed(velocityY * 0.01f * mMT);
				rightView.moveAutoWidthSpeed(velocityY * 0.01f * rMT);
				return false;
			}
		};
		final GestureDetector detector = new GestureDetector(mContext, listener);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(leftView.startBeginAnim||middleView.startBeginAnim||rightView.startBeginAnim){
					return false;
				}else{
					if(event.getAction()==MotionEvent.ACTION_DOWN){
						leftView.mIsMoveAuto=false;
						middleView.mIsMoveAuto=false;
						rightView.mIsMoveAuto=false;
					}
					if (event.getX() <= mWidth/3.0f) {
						lMT = 1.0f;
						mMT = 1.8f;
						rMT = 1.5f;
					} else if (event.getX() <= mWidth*2/3.0f) {
						lMT = 1.8f;
						mMT = 1.0f;
						rMT = 1.5f;
					} else {
						lMT = 1.5f;
						mMT = 1.8f;
						rMT = 1.0f;
					}
					detector.onTouchEvent(event);
					if(event.getAction()==MotionEvent.ACTION_UP){
						if(waitDoUpEvent){
							if (event.getX() <= mWidth/3.0f) {
								for (int i = 0; i < leftView.getRects().size(); i++) {
									MyRect curRect=leftView.getRects().get(i);
									if(touchInTheRect(event, 0, curRect.mPositionY, curRect.mInWidth, curRect.mInHeight)){
										doClickItemEvent(curRect.mIndex);
										break;
									}
								}
							} else if (event.getX() <= mWidth*2/3.0f) {
								for (int i = 0; i < middleView.getRects().size(); i++) {
									MyRect curRect=middleView.getRects().get(i);
									if(touchInTheRect(event, mWidth/3.0f, curRect.mPositionY, curRect.mInWidth, curRect.mInHeight)){
										doClickItemEvent(curRect.mIndex);
										break;
									}
								}
							} else {
								for (int i = 0; i < rightView.getRects().size(); i++) {
									MyRect curRect=rightView.getRects().get(i);
									if(touchInTheRect(event, mWidth*2/3.0f, curRect.mPositionY, curRect.mInWidth, curRect.mInHeight)){
										doClickItemEvent(curRect.mIndex);
										break;
									}
								}
							}
						}
						waitDoUpEvent=false;
					}
				}
				return false;
			}
		});
	}
	
	private boolean touchInTheRect(MotionEvent event, float x, float y, float width,
			float height) {
			if (event.getX()>x) {
				if (event.getX() < x+width) {
					if (event.getY() > y) {
						if (event.getY() < y + height) {
							return true;
						}
					}
				}
			}
			return false;
	}

	private void doClickItemEvent(int i) {
		for (BehaviorEntity behavior : mEntity.behaviors) {
			if(behavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_ITEM_CLICK")){
				BehaviorHelper.doBeheavorForList(behavior, i,
						mEntity.componentId);
			}
		}
	}
	
	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		for (int i = 0; i < leftView.getRects().size(); i++) {
			BitmapUtils.recycleBitmap(leftView.getRects().get(i).mDrawBitmap);
		}
		for (int i = 0; i < middleView.getRects().size(); i++) {
			BitmapUtils.recycleBitmap(middleView.getRects().get(i).mDrawBitmap);
		}
		for (int i = 0; i < rightView.getRects().size(); i++) {
			BitmapUtils.recycleBitmap(rightView.getRects().get(i).mDrawBitmap);
		}
		BitmapUtils.recycleBitmaps(leftViewBitmaps);
		BitmapUtils.recycleBitmaps(middleViewBitmaps);
		BitmapUtils.recycleBitmaps(rightViewBitmaps);
	}

	@Override
	public void hide() {
		this.setVisibility(View.INVISIBLE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}
