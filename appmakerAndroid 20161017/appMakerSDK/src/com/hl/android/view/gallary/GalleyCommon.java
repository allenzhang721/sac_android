package com.hl.android.view.gallary;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hl.android.R;
import com.hl.android.common.BookSetting;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.gallary.base.AbstractGalley;

public class GalleyCommon extends AbstractGalley {

	public GalleyCommon(Context context) {
		super(context);
	}

	@Override
	protected Bitmap getBitmap(String resourceID,int width,int height) {
		return BitmapUtils.getBitMap(resourceID, mContext,width,(int)height);
	}

	@Override
	protected android.widget.RelativeLayout.LayoutParams getGalleryLp() {
		RelativeLayout.LayoutParams galleryLp = new RelativeLayout.LayoutParams(
				BookSetting.SCREEN_WIDTH,
				BookSetting.SNAPSHOTS_HEIGHT);
		galleryLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		galleryLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		return galleryLp;
	}
	@Override
	protected void setWaitLoad(ImageView img) {
		img.setImageResource(R.drawable.scene_ic_loading_invert);
	}

	@Override
	protected float getSizeRatio() {
		return 0.8f;
	}
}
