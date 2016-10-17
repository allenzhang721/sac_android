package com.hl.android.view.pageflip;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;

public class PageFlipView extends AbstractPageFlipView {
	// Display _display;
	Context _context;
	int _index = 0;
	ObjectAnimator an = null;
	ActionOnEnd mAction;
	public PageFlipView(Context context) {
		super(context);
		// this._display = display;
		this._context = context;
		setBackgroundColor(Color.TRANSPARENT);
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
	}
	View v;
	public void play(int pageIndex,int newPaheIndex,ActionOnEnd action) {
//		Log.d("wdy", "此处调用了removeNotShowViewPage");
		mAction = action;
		try {
			Log.d("zhaoq", "mCurPageBitmap-----"+mCurPageBitmap);
			if (this.mCurPageBitmap == null) {
				return;
			}
			View moveView = null;
			moveView =  bookLayout;
			
			if (pageIndex<newPaheIndex)
				an = ObjectAnimator.ofFloat(moveView, "x", BookSetting.BOOK_WIDTH, 0);
			else
				an = ObjectAnimator.ofFloat(moveView, "x", BookSetting.BOOK_WIDTH * (-1), 0);
			an.setDuration(HLSetting.FlipTime);
	 
			ObjectAnimator ans = null;
			if (pageIndex<newPaheIndex)
				ans = ObjectAnimator.ofFloat(this, "x", 0,BookSetting.BOOK_WIDTH * (-1));
			else
				ans = ObjectAnimator.ofFloat(this, "x", 0,BookSetting.BOOK_WIDTH );
			
			ans.setDuration(HLSetting.FlipTime);
			ans.setRepeatCount(0);
			// ans.setFillAfter(true);
			an.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					Log.d("PageFlipVIew.flip", "flipAnimation end");
//					v.setVisibility(View.GONE);
					hide();
					if(!_preload && mAction!= null){
						mAction.doAction();
					}
					BookController.getInstance().hlActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							BookController.getInstance().revokeCommonPage();
							BookController.getInstance().removeNotShowViewPage();
							BookController.getInstance().startPlay();
						}
					});
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}
			}) ;

			ans.start();
			an.start();

		} catch (Exception e) {
			e.printStackTrace();
			BookController.getInstance().startPlay();
		}
	}
}
