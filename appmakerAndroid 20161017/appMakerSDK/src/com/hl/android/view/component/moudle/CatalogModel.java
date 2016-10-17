package com.hl.android.view.component.moudle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.inter.Component;

public class CatalogModel extends HorizontalScrollView implements Component {
	LinearLayout galleryrl;
	Context _con;

	BitmapFactory.Options _option;
	private ArrayList<String> imagelist;
	public static boolean CHANGEBUTTON = false;
	public static boolean ISHORIZONTAL = true;

	public CatalogModel(Context context, ComponentEntity entity) {
		super(context);
		_con = context;
		galleryrl = new LinearLayout(context);
		imagelist = new ArrayList<String>();
	}

	public CatalogModel(Context context) {
		super(context);
		_con = context;
		galleryrl = new LinearLayout(context);
	}

	@Override
	public ComponentEntity getEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		// TODO Auto-generated method stub

	}


	@Override
	public void load() {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		this.addView(galleryrl, lp);
		if (ISHORIZONTAL) {
			galleryrl.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			galleryrl.setOrientation(LinearLayout.VERTICAL);

		}

		for (int i = 0; i < imagelist.size(); i++) {
			this.post(new runview(imagelist.get(i), i));
		}
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
				Log.e("hl", "load error",e);
			}

		}

	}

	@Override
	public void load(InputStream is) {
		Bitmap bitmap = null, resizeBmp;

		try {
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 2;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		}

		int aa = this.getLayoutParams().width;
		int bb = this.getLayoutParams().height;

		resizeBmp = Bitmap.createScaledBitmap(bitmap, aa, bb, true);
		Drawable dbg = new BitmapDrawable(resizeBmp);
		ImageButton ib = new ImageButton(_con);
		ib.setBackgroundDrawable(dbg);
		galleryrl.addView(ib);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CHANGEBUTTON) {

				}

			}
		});

		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

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

	public void recyle() {
		if (null != galleryrl) {
			galleryrl.removeAllViews();
			galleryrl = null;
		}
		this.removeAllViews();
	}
}
