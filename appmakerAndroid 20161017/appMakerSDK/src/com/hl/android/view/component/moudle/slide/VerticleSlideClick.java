package com.hl.android.view.component.moudle.slide;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;

public class VerticleSlideClick extends ScrollView implements Component,
		ComponentPost {
	LinearLayout galleryrl;
	Context _con;
	ComponentEntity entity;
	BitmapFactory.Options _option = new BitmapFactory.Options();
	private ArrayList<String> imagelist;
	public static boolean CHANGEBUTTON = false;
	public static boolean ISHORIZONTAL = true;
	private int itemWidth, itemHeight;
	private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
	private ArrayList<String> downIDList = null;
	private boolean cantMove=false;

	public VerticleSlideClick(Context context, ComponentEntity entity) {
		super(context);
		_con = context;
		this.entity = entity;
		galleryrl = new LinearLayout(context);
		imagelist = new ArrayList<String>();
		this.setVerticalScrollBarEnabled(false);
	}

	public VerticleSlideClick(Context context) {
		super(context);
		_con = context;
		galleryrl = new LinearLayout(context);
	}

	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = (MoudleComponentEntity) entity;
	}

	@Override
	public void load() {
		itemWidth = ((MoudleComponentEntity) this.entity).getItemWidth();
		itemHeight = ((MoudleComponentEntity) this.entity).getItemHeight();

		itemHeight = getLayoutParams().width*itemHeight/itemWidth;
		itemWidth =  getLayoutParams().width;

		if (null != ((MoudleComponentEntity) this.entity).getSourceIDList()
				&& ((MoudleComponentEntity) this.entity).getSourceIDList()
						.size() > 0) {
			this.imagelist = ((MoudleComponentEntity) this.entity)
					.getSourceIDList();
			this.downIDList = ((MoudleComponentEntity) this.entity)
					.getDownIDList();
			LayoutParams lp = new LayoutParams(this.getLayoutParams().width,
					this.getLayoutParams().height);

			int imageCount;

			imageCount = imagelist.size();

			for (int i = 0; i < imageCount; i++) {
				if (HLSetting.IsResourceSD)
					load(FileUtils.getInstance().getFileInputStream(
							imagelist.get(i)));
				else
					load(FileUtils.getInstance().getFileInputStream(
							getContext(), imagelist.get(i)));

			}

			galleryrl.setOrientation(LinearLayout.VERTICAL);

			galleryrl
					.measure(MeasureSpec.makeMeasureSpec(lp.width,
							MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
							lp.height, MeasureSpec.EXACTLY));
			this.addView(galleryrl, lp);
		}

	}
	Bitmap bitmap = null;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	private float oldTouchValue = 0;

	@Override
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
			BookController.getInstance().runBehavior(getEntity(),"BEHAVIOR_ON_MOUSE_UP");
			oldTouchValue = 0;
			if (null != currentButton) {
				Bitmap bitmap = bitmapList.get(currentIndex);
				currentButton.setImageBitmap(bitmap);
				cantMove=false;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			isMove = true;
			break;
		}
		}
		if(!cantMove){
			return super.onTouchEvent(touchevent);
		}
		return true;

	}

	class runview implements Runnable {
		String _name;
		int _index;

		public runview(String name, int index) {
			_name = name;
			_index = index;
		}

		@Override
		public void run() {
			try {
				if (HLSetting.IsResourceSD)
					load(FileUtils.getInstance().getFileInputStream(_name));
				else
					load(FileUtils.getInstance().getFileInputStream(
							getContext(), _name));

			} catch (OutOfMemoryError e) {
				Log.e("hl", "load  error",e);
			}

		}

	}

	boolean isMove = false;
	ImageView currentButton = null;
	int currentIndex = -1;
	boolean clickUp = false;

	@Override
	public void load(InputStream is) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 2;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		}

		bitmapList.add(bitmap);
		ImageView ib = new ImageView(_con);
		ib.setScaleType(ScaleType.FIT_XY);
		ib.setImageBitmap(bitmap);
		ib.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(this.getLayoutParams().height,
						MeasureSpec.EXACTLY));
		LayoutParams lp = new LayoutParams(itemWidth, itemHeight);
		lp.gravity = Gravity.CENTER_VERTICAL;
		galleryrl.addView(ib, lp);
		ib.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int i = galleryrl.indexOfChild(v);
				currentIndex = i;
				currentButton = ((ImageView) v);
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN: {
					BookController.getInstance().runBehavior(getEntity(),"BEHAVIOR_ON_MOUSE_DOWN");
					isMove = false;
					clickUp = false;
					MyCount mc = new MyCount(300, 100);
					mc.start();
					break;
				}
				case MotionEvent.ACTION_UP: {
					clickUp = true;
					Bitmap bitmap = bitmapList.get(i);
					currentButton.setImageBitmap(bitmap);
					BookController.getInstance().runBehavior(getEntity(),"BEHAVIOR_ON_MOUSE_UP");
					for (BehaviorEntity behavior : entity.behaviors) {
						//此处整理了分号分割的问题 ,为什么这么搞就是因为要兼容老版本  by zhaoq
						if(behavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_ITEM_CLICK")){
							BehaviorHelper.doBeheavorForList(behavior, i,entity.componentId);
						}
					}
					break;
				}
				}
				return true;
			}

		});

	}

	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub

	}



	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void recyle() {
	}
	
	private Bitmap getBitmap(String id) {
		Bitmap bitmap=BitmapManager.getBitmapFromCache(id);
		if(bitmap==null){
			bitmap=BitmapUtils.getBitMap(id, _con);
			BitmapManager.putBitmapCache(id, bitmap);
		}
		return bitmap;
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub

	}
	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (!isMove && !clickUp) {
				Bitmap bitmap = getBitmap(downIDList.get(currentIndex));
				currentButton.setImageBitmap(bitmap);
				cantMove=true;
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}
}
