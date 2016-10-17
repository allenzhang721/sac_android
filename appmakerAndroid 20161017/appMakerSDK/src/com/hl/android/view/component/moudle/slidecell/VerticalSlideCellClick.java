package com.hl.android.view.component.moudle.slidecell;

import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;

@SuppressLint("HandlerLeak")
public class VerticalSlideCellClick extends ScrollView implements Component,
ComponentPost {
	LinearLayout bodyLay;
	Context mContext;
	MoudleComponentEntity mEntity;

	private ArrayList<String> mNormalImageList;
	private ArrayList<String> mDownImageList;
	private int cellNum = 1;
	
	private LinearLayout.LayoutParams itemLp;

	BitmapFactory.Options _option;
	public static boolean CHANGEBUTTON = false;
	private int itemWidth, itemHeight;
	private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
	private int speed = 90;
	public AnimationSet animationset = null;
	private int cellSize = 0;
	
	public VerticalSlideCellClick(Context context, ComponentEntity entity) {
		super(context);
		mContext = context;
		this.mEntity = (MoudleComponentEntity) entity;
		bodyLay = new LinearLayout(context);
		bodyLay.setOrientation(LinearLayout.VERTICAL);
		this.setHorizontalScrollBarEnabled(false);
	}

	public VerticalSlideCellClick(Context context) {
		super(context);
		mContext = context;
		bodyLay = new LinearLayout(context);
	}

	@Override
	public ComponentEntity getEntity() {
		return this.mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.mEntity = (MoudleComponentEntity) entity;
	}

	@Override
	public void load() {
		
		itemWidth = mEntity.getItemWidth();
		itemHeight = mEntity.getItemHeight();
		itemWidth = (int) ScreenUtils.getHorScreenValue(itemWidth);
		itemHeight = (int) ScreenUtils.getVerScreenValue(itemHeight);

		itemLp = new android.widget.LinearLayout.LayoutParams(itemWidth,itemHeight);
	

		mNormalImageList = mEntity.getSourceIDList();
		mDownImageList = mEntity.getDownIDList();
		cellNum = mEntity.getCellNumber();

		int imgSize = mNormalImageList.size();
		// 计算子元素个数
		cellSize = imgSize / cellNum;
		if ((imgSize % cellNum) > 0) {
			cellSize++;
		}
		loadView(cellSize);
		
		
	}

	public void loadView(int cellSize) {
		LayoutParams lp = new LayoutParams(this.getLayoutParams().width,
				this.getLayoutParams().height);
		
		int imgSize = mNormalImageList.size();
		for (int i = 0; i < cellSize; i++) {
			LinearLayout cellLay = new LinearLayout(mContext);
			cellLay.setOrientation(LinearLayout.HORIZONTAL);
			cellLay.setGravity(Gravity.TOP);
			for(int j=0;j<cellNum;j++){
				int imgIndex = (i*cellNum)+j;
				if(imgIndex>=imgSize){
					return;
				}
				cellLay.addView(loadItemView(imgIndex),itemLp);
			}
			bodyLay.addView(cellLay);
		}

		bodyLay.measure(
				MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY));
		this.addView(bodyLay, lp);
		this.bringToFront();
	}

	private View loadItemView(int index) {
		Bitmap bitmap = BitmapUtils.getBitMap(mNormalImageList.get(index), mContext,itemWidth,itemHeight);
		bitmapList.add(bitmap);
		Drawable dbg = new BitmapDrawable(bitmap);
		ImageButton ib = new ImageButton(mContext);
		ib.setBackgroundDrawable(dbg);
		ib.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(itemHeight,
						MeasureSpec.EXACTLY));
		ib.setOnTouchListener(new ItemTouchListener());
		ib.setTag(index);
		return ib;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	private int direct = 0;
	private int FLAG_MESSAGE = 0;

	/*@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		if (oldTouchValue == 0) {
			oldTouchValue = touchevent.getX();
		}

		switch (touchevent.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			isMove = false;
			oldTouchValue = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			oldTouchValue = 0;
			if (null != currentButton) {
				Drawable dbg1 = new BitmapDrawable(
						this.bitmapList.get(currentIndex));
				currentButton.setBackgroundDrawable(dbg1);
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			isMove = true;
			break;
		}
		}
		return super.onTouchEvent(touchevent);
	}*/

	boolean isMove = false;
	ImageButton currentButton = null;
	int currentIndex = -1;
	boolean clickUp = false;

	@Override
	public void load(InputStream is) {

	}

	private int x = 0;
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (x == getScrollX()) {
				if (direct == 1) {
					direct = -1;
				} else {
					direct = 1;
				}
			} else {
				x = getScrollX();
			}
			doScroll();
			this.sendMessageDelayed(this.obtainMessage(FLAG_MESSAGE), speed);
		}
	};

	private void doScroll() {
		if (this.direct < 0) {
			this.smoothScrollBy(-1, 0);
		} else if (this.direct > 0) {
			this.smoothScrollBy(1, 0);
		}
	}

	@Override
	public void setRotation(float rotation) {
		d("setRotation nothing");

	}

	@Override
	public void play() {
		direct = 1;
		mHandler.sendEmptyMessage(FLAG_MESSAGE);
	}

	@Override
	public void stop() {
		d("stop nothing");
		recyle();
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
		d("resume nothing");
	}

	@Override
	public void pause() {
		d("pause nothing");
	}

	Bitmap bitmap = null;

	@Override
	public void recyle() {
		if (null != bitmapList) {
			BitmapUtils.recycleBitmaps(bitmapList);
		}
		if (null != bodyLay) {
			bodyLay.removeAllViews();
			bodyLay = null;
		}
		if (null != bitmap) {
			BitmapUtils.recycleBitmap(bitmap);
		}
		this.removeAllViews();
		System.gc();
	}

	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (isMove == false && clickUp == false) {
				String downImage = mDownImageList.get(currentIndex);
				Drawable dbg1 = new BitmapDrawable(BitmapUtils.getBitMap(downImage, mContext,itemWidth,itemHeight));
				currentButton.setBackgroundDrawable(dbg1);
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}

	private int downIndex = -1;
	
	private  int lastIndex = -1;
	
	private ImageButton downButton = null;
	/**
	 * 子项目的点击事件
	 * @author zhaoq
	 *
	 */
	public class ItemTouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			currentIndex = (Integer) v.getTag();
			currentButton = ((ImageButton) v);
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				isMove = false;
				clickUp = false;
				lastIndex = currentIndex;
				/*MyCount mc = new MyCount(100, 100);
				mc.start();
*/
			/*	String downImage = mDownImageList.get(currentIndex);
				Drawable dbgnew = new BitmapDrawable(BitmapUtils.getBitMap(downImage, itemLp, mContext));
				currentButton.setBackgroundDrawable(dbgnew);*/
				break;
			}
			case MotionEvent.ACTION_UP: {
				//Drawable dbg1 = new BitmapDrawable(bitmapList.get(currentIndex));
				//((ImageButton) v).setBackgroundDrawable(dbg1);
				clickUp = true;
				if(lastIndex != currentIndex){
					return false;
				}
				if(downIndex == currentIndex){
					return false;
				}else{
					//先将原来的那个按下的图片恢复原状
					if(downButton!=null && downIndex>=0){
						//回收原来的bitmap
						BitmapDrawable obd = (BitmapDrawable) downButton.getBackground();
						String downImage = mNormalImageList.get(downIndex);
						Drawable dbgnew = new BitmapDrawable(BitmapUtils.getBitMap(downImage, mContext,itemWidth,itemHeight));
						downButton.setBackgroundDrawable(dbgnew);
						
						obd.getBitmap().recycle();
					}
				}
				

				BitmapDrawable obd = (BitmapDrawable) currentButton.getBackground();
				String downImage = mDownImageList.get(currentIndex);
				Drawable dbgnew = new BitmapDrawable(BitmapUtils.getBitMap(downImage, mContext,itemWidth,itemHeight));
				currentButton.setBackgroundDrawable(dbgnew);
				obd.getBitmap().recycle();
				
				downButton = currentButton;
				downIndex = currentIndex;
				
				for (BehaviorEntity behavior : mEntity.behaviors) {
					//此处整理了分号分割的问题 ,为什么这么搞就是因为要兼容老版本  by zhaoq
					BehaviorHelper.doBeheavorForList(behavior, currentIndex+1, 	mEntity.componentId);
				}
				break;
			}
			}
			return true;
		}
	}

	private void d(String message) {
		boolean flag = true;
		if (flag) {
			Log.d("hl", message + "    || id is " + this);
		}
	}
}
