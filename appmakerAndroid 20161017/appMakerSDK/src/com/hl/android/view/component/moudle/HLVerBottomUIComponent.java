package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.inter.Component;

public class HLVerBottomUIComponent extends FrameLayout implements Component {
	MoudleComponentEntity componentEntity;
	LinearLayout contentLayout;
	LinearLayout btnLayout;

	private Context mContext;

	public HLVerBottomUIComponent(Context context) {
		super(context);
		mContext = context;
		 
		contentLayout = new LinearLayout(context);
		LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(btnLayout, blp);
		addView(contentLayout);

	}

	private boolean isLock = false;
	public void hideMenu() {
		if(isLock)return;
		isLock = true;
		if (contentLayout.getVisibility() != View.VISIBLE) {
			isLock = false;
			return;
		}
		int height = getLayoutParams().height;
		this.clearAnimation();
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,
				height);
		translateAnimation.setDuration(1000);
		translateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) { // TODO

			}

			@Override
			public void onAnimationRepeat(Animation animation) { //

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isLock = false;
				contentLayout.setVisibility(View.INVISIBLE);
				btnLayout.setVisibility(View.VISIBLE);
			}
		});
		startAnimation(translateAnimation);
	}

	public void showMenu() {
		int height = getLayoutParams().height;
		if(isLock)return;
		isLock = true;
		this.clearAnimation();
		TranslateAnimation translateAnimation = new TranslateAnimation(0,
				0, height, 0);
		translateAnimation.setDuration(1000);
		translateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) { // TODO
				btnLayout.setVisibility(View.INVISIBLE);
				contentLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) { //

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isLock = false;
			}
		});
		startAnimation(translateAnimation);

	}

	public HLVerBottomUIComponent(Context context, ComponentEntity entity) {
		super(context);
		setEntity(entity);
		
		entity.isPlayAnimationAtBegining = true;
		mContext = context;

		TextView directionBtn = new TextView(context);
		btnLayout = new LinearLayout(context);
		btnLayout.setVerticalGravity(Gravity.BOTTOM);
		btnLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
		
		btnLayout.addView(directionBtn);
		
		contentLayout = new LinearLayout(context);
		for (String sourceID : componentEntity.getSourceIDList()) {
			InputStream is = FileUtils.getInstance().getFileInputStream(
					getContext(), sourceID);
			upMapList.add(createBitmap(is));
		}
		for (String sourceID : componentEntity.getDownIDList()) {
			InputStream is = FileUtils.getInstance().getFileInputStream(
					getContext(), sourceID);
			downMapList.add(createBitmap(is));
		}

		StateListDrawable states = getButtonDrawable(0);
		directionBtn.setBackgroundDrawable(states);
		directionBtn.setTag(false);
		// contentLayout.setVisibility(View.GONE);
		directionBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMenu();
			}
		});
		LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);

		for (int i = 1; i < upMapList.size(); i++) {
			TextView btn = new Button(mContext);
			StateListDrawable btnDrawable = getButtonDrawable(i);
			btn.setBackgroundDrawable(btnDrawable);
			contentLayout.addView(btn, itemLp);
			btn.setTag(i);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					int i = (Integer) arg0.getTag();
					for (BehaviorEntity behavior : componentEntity.behaviors) {
						BehaviorHelper.doBeheavorForList(behavior, i,componentEntity.componentId);
						BookController.lastPageID = BookController.getInstance().getViewPage().getEntity().getID();
					}
				}
			});
		}
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		addView(btnLayout,lp);
		addView(contentLayout);
		contentLayout.setVisibility(View.INVISIBLE);
		hideMenu();
	}

	@Override
	public ComponentEntity getEntity() {
		return componentEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		componentEntity = (MoudleComponentEntity) entity;
	}

	private ArrayList<Bitmap> upMapList = new ArrayList<Bitmap>();
	private ArrayList<Bitmap> downMapList = new ArrayList<Bitmap>();

	@Override
	public void load() {

	}

	public StateListDrawable getButtonDrawable(int index) {
		  StateListDrawable bg = new StateListDrawable(); 
          Drawable normal = new BitmapDrawable(upMapList.get(index)); 
          Drawable selected = new BitmapDrawable(downMapList.get(index)); 
          Drawable pressed = new BitmapDrawable(downMapList.get(index)); 
          bg.addState(View.PRESSED_ENABLED_STATE_SET, pressed); 
          bg.addState(View.ENABLED_FOCUSED_STATE_SET, selected); 
          bg.addState(View.ENABLED_STATE_SET, normal); 
          bg.addState(View.FOCUSED_STATE_SET, selected); 
          bg.addState(View.EMPTY_STATE_SET, normal); 
          return bg;
	}

	private Bitmap createBitmap(InputStream is) {

		try {

			BitmapFactory.Options _option = new BitmapFactory.Options();
			_option.inDither = false; // Disable Dithering mode
			_option.inPurgeable = true; // Tell to gc that whether it needs free
										// memory, the Bitmap can be cleared
			_option.inInputShareable = true; // Which kind of reference will be
												// used
												// to recover the Bitmap data
												// after
												// being clear, when it will be
												// used
												// in the future
			_option.inTempStorage = new byte[32 * 1024];
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(is, null, _option);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				_option.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(is, null, _option);
			}

			return bitmap;
			// return resizeBmp;
		} catch (Exception e) {
			Log.e("hl", "load error",e);
		} catch (OutOfMemoryError e) {
			Log.e("hl", "load error",e);
		}
		return null;
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void play() {
		// TODO Auto-generated method stub
		// hideMenu();
		// directionBtn.setVisibility(View.VISIBLE);
		// contentLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void stop() {
		BitmapUtils.recycleBitmaps(downMapList);
		BitmapUtils.recycleBitmaps(upMapList);
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
		// TODO Auto-generated method stub

	}

}
