package com.hl.android.view.gallary;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.hl.android.view.gallary.base.AbstractGalley;

public class GalleryAnimation implements AnimationListener {
	Animation an = null;
	AbstractGalley gallery;
	public void playGallery(boolean isDown, AbstractGalley gallery) {
		this.gallery  = gallery;
		if (!isDown) {

			an = new TranslateAnimation(0, 0, -300, 3000);
			gallery.showGalleryInfor();
		} else
			an = new TranslateAnimation(0, 0, 300, -300);

		
		an.setDuration(5500);
		an.setRepeatCount(0);
		an.setStartOffset(0);
		an.initialize(1, 1, 5, 5);
		an.setAnimationListener(this);
		gallery.startAnimation(an);

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		

	}

}
