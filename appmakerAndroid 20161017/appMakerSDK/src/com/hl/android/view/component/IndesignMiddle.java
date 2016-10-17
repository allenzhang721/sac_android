package com.hl.android.view.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.android.HLLayoutActivity;
import com.hl.android.R;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.controller.BookController;
import com.hl.android.controller.PageEntityController;
import com.hl.android.core.utils.BitmapUtils;

public class IndesignMiddle extends RelativeLayout{

	private Context mContext;
	private RelativeLayout topView;
	private RelativeLayout bottomView;
	private Gallery pageSnapshots;
	private TextView textView;
	

	public IndesignMiddle(Context context) {
		super(context);
		mContext=context;
		init();
	}
	
	public void checkChangeSelection(float percent,boolean shouldMove){
		if(percent<=0||percent>=1){
			return;
		}
		if(!shouldMove){
			pageSnapshots.setSelection((int)(pageSnapshots.getAdapter().getCount()*percent),true);
			return;
		}
		while((int)(pageSnapshots.getAdapter().getCount()*percent)!=pageSnapshots.getSelectedItemPosition()){
			((HLLayoutActivity)mContext).getBottomNav().tagggg=true;
			if((int)(pageSnapshots.getAdapter().getCount()*percent)>pageSnapshots.getSelectedItemPosition()){
				pageSnapshots.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null); 
			}else{
				pageSnapshots.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null); 
			}
			pageSnapshots.setSelection((int)(pageSnapshots.getAdapter().getCount()*percent));
		}
	}
	
	public void changeSelection(float percent){
		pageSnapshots.setSelection((int)(pageSnapshots.getAdapter().getCount()*percent),true);
	}

	private void init() {
		setBackgroundColor(Color.argb(255, 81, 81, 81));
		topView=new RelativeLayout(mContext);
		topView.setBackgroundResource(R.drawable.indesign_titlebgimg);
		textView=new TextView(mContext);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(20);
		textView.setPadding(20, 0, 20, 0);
		textView.setLineSpacing(5.0f, 1.0f);
		textView.setMovementMethod(new ScrollingMovementMethod());
		topView.addView(textView,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(topView);
		bottomView=new RelativeLayout(mContext);
		pageSnapshots=new Gallery(mContext);
		pageSnapshots.setGravity(Gravity.TOP);
		pageSnapshots.setSpacing(BookSetting.BOOK_WIDTH/30);
		pageSnapshots.setAnimationDuration(500);
		pageSnapshots.setUnselectedAlpha(1.0f);
		pageSnapshots.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				int imageWidth=(BookSetting.BOOK_WIDTH-BookSetting.BOOK_WIDTH/15)/3;
				LinearLayout layout=new LinearLayout(mContext);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setLayoutParams(new Gallery.LayoutParams(imageWidth, LayoutParams.WRAP_CONTENT));
				BookController controller=BookController.getInstance();
				String pageID=controller.getBook().getSections().get(controller.currendsectionindex).getPages().get(position);
				PageEntity pageEntity = PageEntityController.getInstance().getPageEntityByPageId(controller.viewPage.getContext(), pageID);
				Bitmap bitmap=BitmapUtils.getBitMap(pageEntity.getSnapShotID(), mContext);
				ImageView imageView=new ImageView(mContext);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setImageBitmap(bitmap);
				layout.addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT,bitmap.getHeight()*imageWidth/bitmap.getWidth()));
				for (String navePageId : pageEntity.getNavePageIds()) {
					PageEntity pageEntity1 = PageEntityController.getInstance().getPageEntityByPageId(controller.viewPage.getContext(), navePageId);
					Bitmap bitmap1=BitmapUtils.getBitMap(pageEntity1.getSnapShotID(), mContext);
					ImageView imageView1=new ImageView(mContext);
					imageView1.setScaleType(ScaleType.FIT_XY);
					imageView1.setPadding(0, 1, 0, 0);
					imageView1.setImageBitmap(bitmap1);
					layout.addView(imageView1, new LayoutParams(LayoutParams.MATCH_PARENT,bitmap.getHeight()*imageWidth/bitmap.getWidth()));
				}
				return layout;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				BookController controller=BookController.getInstance();
				return controller.getBook().getSections().get(controller.currendsectionindex).getPages().size();
			}
		});
		RelativeLayout.LayoutParams layout4pageSnapshots=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		layout4pageSnapshots.setMargins(10, 10, 10, 10);
		bottomView.addView(pageSnapshots,layout4pageSnapshots);
		addView(bottomView);
		pageSnapshots.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				BookController controller=BookController.getInstance();
				String pageID=controller.getBook().getSections().get(controller.currendsectionindex).getPages().get(position);
				PageEntity pageEntity = PageEntityController.getInstance().getPageEntityByPageId(controller.viewPage.getContext(), pageID);
				setTopViewText(pageEntity.getTitle());
				if(((HLLayoutActivity)mContext).getBottomNav().tagggg){
					((HLLayoutActivity)mContext).getBottomNav().tagggg=false;
					return;
				}
				((HLLayoutActivity)mContext).getBottomNav().seekTo(position*1.0f/(pageSnapshots.getAdapter().getCount()-1));
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
			
		});
		pageSnapshots.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				BookController controller=BookController.getInstance();
				String pageID=controller.getBook().getSections().get(controller.currendsectionindex).getPages().get(position);
				controller.changePageById(pageID);
				((HLLayoutActivity)mContext).getUPNav().dismiss();
				((HLLayoutActivity)mContext).getBottomNav().dismiss();
				new CountDownTimer(300,300) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						
					}
					
					@Override
					public void onFinish() {
						dismiss();
					}
				}.start();
			}
		});
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	public boolean isShowing(){
		return getVisibility()==View.VISIBLE;
	}
	
	public void show(){
		Animation animation=new ScaleAnimation(3.0f, 1.0f, 3.0f, 1.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(200);
		animation.setInterpolator(new DecelerateInterpolator());
		setVisibility(View.VISIBLE);
		startAnimation(animation);
	}
	
	public void dismiss(){
		Animation animation=new ScaleAnimation(1.0f, 3.0f, 1.0f, 3.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(200);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.INVISIBLE);
			}
		});
		startAnimation(animation);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(b-t)/4);
		layoutParams.topMargin=1;
		topView.setLayoutParams(layoutParams);
		RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(b-t)*3/4);
		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bottomView.setLayoutParams(layoutParams1);
		if(textView.getLineCount()>1){
			textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
		}else{
			textView.setGravity(Gravity.CENTER);
		}
		
	}
	
	public void setTopViewText(String text){
		textView.setText(text);
	}
	
	
}
