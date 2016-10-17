package com.hl.android.view.pageflip;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;

/**
 * 竖向翻页
 * 
 * @author webcat
 * 
 */
public class PageFlipVerticleView extends AbstractPageFlipView {

	// Display _display;
	Context _context;
	int _index = 0;
	Animation an = null;

	private ActionOnEnd mAction;
	
	public PageFlipVerticleView(Context context) {
		super(context);
		// this._display = display;
		this._context = context;
	}
	public void show(){
		Drawable db = new BitmapDrawable(this.mCurPageBitmap);
		this.setBackgroundDrawable(db);
		this.setVisibility(View.VISIBLE);
		this.bringToFront();

		//保证公共页在翻页视图的上面
		BookController.getInstance().hlActivity.commonLayout.bringToFront();
	}
	
	public void hide() {
		this.setVisibility(View.GONE);
		setBackgroundColor(Color.TRANSPARENT);
	}
	
	@Override
	public void play(int pageIndex,int newPageIndex,ActionOnEnd action) {
		mAction = action;
		try {
			if (this.mCurPageBitmap == null) {
				// hide();

				BookController.getInstance().startPlay();
				return;
			}

			if (pageIndex<newPageIndex)
				an = new TranslateAnimation(0, 0, BookSetting.SCREEN_HEIGHT, 0);
			else
				an = new TranslateAnimation(0, 0, BookSetting.SCREEN_HEIGHT
						* (-1), 0);

			this.bookLayout.setAnimation(an);
			an.setDuration(HLSetting.FlipTime);
			an.setRepeatCount(0);
			an.setStartOffset(0);
			an.initialize(1, 1, 5, 5);

			Animation ans = null;
			if (pageIndex>newPageIndex)
				ans = new TranslateAnimation(0, 0, 0, BookSetting.SCREEN_HEIGHT);
			else
				ans = new TranslateAnimation(0, 0, 0, BookSetting.SCREEN_HEIGHT
						* (-1));
			ans.setDuration(HLSetting.FlipTime);
			ans.setRepeatCount(0);
			ans.setStartOffset(0);
			ans.initialize(1, 1, 5, 5);
			this.setAnimation(ans);
			if (this.mCurPageBitmap == null) {
				hide();
				BookController.getInstance().startPlay();
				return;
			}

			an.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					Log.d("PageFlipVIew.flip", "flipAnimation end");
					if(!_preload && mAction!= null){
						mAction.doAction();
					}
					hide();
					new CountDownTimer(500, 500){
						@Override
						public void onFinish() {
							BookController.getInstance().doFlipSubPage=false;
							BookController.getInstance().startPlay();
						}

						@Override
						public void onTick(long arg0) {
						}
						
					}.start();
				}
			});

			ans.startNow();
			an.startNow();

		} catch (Exception e) {
			BookController.getInstance().startPlay();
		}
	}

}
