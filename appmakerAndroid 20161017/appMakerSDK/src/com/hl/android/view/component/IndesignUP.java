package com.hl.android.view.component;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.hl.android.R;

public class IndesignUP extends RelativeLayout implements OnClickListener{

	private Context mContext;
	private static final int BTN_NAV_ITEM1_ID=0x10010;
	private static final int BTN_NAV_ITEM2_ID=0x10011;
	private static final int BTN_NAV_ITEM3_ID=0x10012;
	private static final int BTN_NAV_ITEM4_ID=0x10013;
//	private static final int BTN_NAV_ITEM5_ID=0x10014;
	private static final int BTN_NAV_ITEM6_ID=0x10015;
	
	private ImageButton btnItem1;
	private ImageButton btnItem2;
	private ImageButton btnItem3;
	private ImageButton btnItem4;
//	private ImageButton btnItem5;
	private ImageButton btnItem6;
	
	private NavMenuListenner mListenner=null;
	private Animation anim4dismiss,anim4show;

	public IndesignUP(Context context) {
		super(context);
		mContext=context;
		init();
	}

	private void init() {
		setBackgroundResource(R.drawable.indesign_topnavbgimg);
		btnItem1=new ImageButton(mContext);
		btnItem1.setScaleType(ScaleType.FIT_XY);
		btnItem1.setBackgroundResource(R.drawable.btn_gohome_selector);
		btnItem1.setId(BTN_NAV_ITEM1_ID);
		RelativeLayout.LayoutParams params4btnGoHome=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(btnItem1,params4btnGoHome);
		
		btnItem2=new ImageButton(mContext);
		btnItem2.setScaleType(ScaleType.FIT_XY);
		btnItem2.setBackgroundResource(R.drawable.btn_golastpage_selector);
		btnItem2.setId(BTN_NAV_ITEM2_ID);
		RelativeLayout.LayoutParams params4btnGoLastPage=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params4btnGoLastPage.addRule(RelativeLayout.RIGHT_OF, BTN_NAV_ITEM1_ID);
		addView(btnItem2, params4btnGoLastPage);
		
		btnItem3=new ImageButton(mContext);
		btnItem3.setScaleType(ScaleType.FIT_XY);
		btnItem3.setBackgroundResource(R.drawable.btn_cata_selector);
		btnItem3.setId(BTN_NAV_ITEM3_ID);
		RelativeLayout.LayoutParams params4btnShowCata=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params4btnShowCata.addRule(RelativeLayout.RIGHT_OF, BTN_NAV_ITEM2_ID);
		addView(btnItem3, params4btnShowCata);
		
		btnItem6=new ImageButton(mContext);
		btnItem6.setScaleType(ScaleType.FIT_XY);
		btnItem6.setBackgroundResource(R.drawable.btn_showconor_selecor);
		btnItem6.setId(BTN_NAV_ITEM6_ID);
		RelativeLayout.LayoutParams params4btnShowConor=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params4btnShowConor.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(btnItem6, params4btnShowConor);
		
//		btnItem5=new ImageButton(mContext);
//		btnItem5.setScaleType(ScaleType.FIT_XY);
//		btnItem5.setBackgroundResource(R.drawable.btn_search_selector);
//		btnItem5.setId(BTN_NAV_ITEM5_ID);
//		RelativeLayout.LayoutParams params4btnSearch=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params4btnSearch.addRule(RelativeLayout.LEFT_OF,BTN_NAV_ITEM6_ID);
//		addView(btnItem5, params4btnSearch);
		
		btnItem4=new ImageButton(mContext);
		btnItem4.setScaleType(ScaleType.FIT_XY);
		btnItem4.setBackgroundResource(R.drawable.btn_goback_selector);
		btnItem4.setId(BTN_NAV_ITEM4_ID);
		RelativeLayout.LayoutParams params4btnGoback=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params4btnGoback.addRule(RelativeLayout.LEFT_OF,BTN_NAV_ITEM6_ID);
		addView(btnItem4, params4btnGoback);
		btnItem1.setOnClickListener(this);
		btnItem2.setOnClickListener(this);
		btnItem3.setOnClickListener(this);
		btnItem4.setOnClickListener(this);
//		btnItem5.setOnClickListener(this);
		btnItem6.setOnClickListener(this);
		
		anim4dismiss=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
		anim4dismiss.setDuration(200);
		anim4dismiss.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
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
		
		anim4show=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
		anim4show.setDuration(200);
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	public void setNavMenuListenner(NavMenuListenner listenner){
		mListenner=listenner;
	}
	
	public boolean isShowing(){
		return getVisibility()==View.VISIBLE;
	}
	
	public void show(){
		if(!isShowing()){
			setVisibility(View.VISIBLE);
			doAnim4Show();
		}
	}
	
	public void dismiss(){
		bringToFront();
		if(isShowing()){
			doAnim4Dismiss();
		}
	}
	
	public View getItem(int position) {
		View result = null;
		switch (position) {
		case 0:
			result=btnItem1;
			break;
		case 1:
			result=btnItem2;
			break;
		case 2:
			result=btnItem3;
			break;
		case 3:
			result=btnItem4;
			break;
//		case 4:
//			result=btnItem5;
//			break;
		case 5:
			result=btnItem6;
			break;

		default:
			break;
		}
		return result;
	}
	
	private void doAnim4Dismiss() {
		if(mListenner!=null){
			mListenner.onDismiss();
		}
		startAnimation(anim4dismiss);
		btnItem3.setBackgroundResource(R.drawable.btn_cata_selector);
		btnItem6.setBackgroundResource(R.drawable.btn_showconor_selecor);
//		btnItem5.setBackgroundResource(R.drawable.btn_search_selector);
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(getWindowToken(), 0); 
	}

	private void doAnim4Show() {
		if(mListenner!=null){
			mListenner.onShow();
		}
		startAnimation(anim4show);
	}

	public interface NavMenuListenner{
		public void onItem1Click(View itemView);
		public void onItem2Click(View itemView);
		public void onItem3Click(View itemView);
		public void onItem4Click(View itemView);
		public void onItem5Click(View itemView);
		public void onItem6Click(View itemView);
		public void onShow();
		public void onDismiss();
	}

	@Override
	public void onClick(View v) {
		if(mListenner==null){
			return;
		}
		switch (v.getId()) {
		case BTN_NAV_ITEM1_ID:
			mListenner.onItem1Click(btnItem1);
			break;
		case BTN_NAV_ITEM2_ID:
			mListenner.onItem2Click(btnItem2);
			break;
		case BTN_NAV_ITEM3_ID:
			mListenner.onItem3Click(btnItem3);
			break;
		case BTN_NAV_ITEM4_ID:
			mListenner.onItem4Click(btnItem4);
			break;
//		case BTN_NAV_ITEM5_ID:
//			mListenner.onItem5Click(btnItem5);
//			break;
		case BTN_NAV_ITEM6_ID:
			mListenner.onItem6Click(btnItem6);
			break;

		default:
			break;
		}
	}
}
