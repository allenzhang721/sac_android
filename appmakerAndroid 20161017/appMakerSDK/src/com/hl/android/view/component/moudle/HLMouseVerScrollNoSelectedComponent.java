package com.hl.android.view.component.moudle;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.controller.BookState;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.component.inter.Component;

public class HLMouseVerScrollNoSelectedComponent extends ScrollView
		implements Component {
	private Context mContext;
	private MoudleComponentEntity mEntity;
	
	public HLMouseVerScrollNoSelectedComponent(Context context,ComponentEntity entity){
		this(context);
		setEntity(entity);
	}
	public HLMouseVerScrollNoSelectedComponent(Context context) {
		super(context);
		mContext = context;
		setVerticalScrollBarEnabled(false);
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity = (MoudleComponentEntity) entity;
	}

	@Override
	public void load() {
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);

		int width = mEntity.getItemWidth();
		int height = mEntity.getItemHeight();

		width = (int) ScreenUtils.getHorScreenValue(width);
		height = (int) ScreenUtils.getVerScreenValue(height);
		ScrollView.LayoutParams lp = new ScrollView.LayoutParams(width,height);
		
		//for(int i=0;i<mEntity.getSourceIDList().size();i++){
			String sourceID = mEntity.getSourceIDList().get(0);
			ImageView imageView = new ImageView(this.getContext());
			imageView.measure(MeasureSpec.makeMeasureSpec(lp.width,
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					lp.height, MeasureSpec.EXACTLY));
			Bitmap bitmap = BitmapUtils.getBitMap(sourceID,mContext,width,height);
			if(bitmap==null){
				Toast.makeText(mContext, "图片为空", Toast.LENGTH_LONG).show();
				return;
			}
			imageView.setImageBitmap(bitmap);
			layout.addView(imageView,lp);
		//}
		
		addView(layout);
		requestLayout();
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 

	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub

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
	//每个对象只能翻一次
	private boolean isFliped = false;
	MotionEvent oldMotion = null;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			oldMotion = MotionEvent.obtain(ev);
			super.onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			float disY = ev.getY() - oldMotion.getY();
			//disY = Math.abs(disY);
			float disX =  ev.getX() - oldMotion.getX();
			//disX = Math.abs(disX);
			//如果纵向移动距离小于10并且横向距离大于10，我们就左右移动一下
			if(!isFliped && Math.abs(disY)<100 && Math.abs(disX)>100){
				isFliped = true;
				if(disX>0){//上一页

					if (BookState.getInstance().isFliping == false){
						BookController.getInstance().flipPage(-1);//prePage();
						return true;
					}
					//BookController.getInstance().prePage();
				}else{//下一页
					//BookController.getInstance().nextPage();
					if (BookState.getInstance().isFliping == false){
						BookController.getInstance().flipPage(1);
						return true;
					}
				}
				
				return true;
			}else{
				super.onTouchEvent(ev);
			}
		}
		
		return true;
	}
}
