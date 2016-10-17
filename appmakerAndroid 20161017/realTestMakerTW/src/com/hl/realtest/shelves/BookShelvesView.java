package com.hl.realtest.shelves;

import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hl.android.HLReader;
import com.hl.android.common.BookSetting;
import com.hl.android.core.utils.FileUtils;
import com.hl.common.AppContext;
import com.hl.common.BitmapCache;
import com.hl.realtest.ScreenAdapter;
import com.hl.realtest.data.Book;
import com.hl.realtest.view.TasksCompletedView;
import com.hl.realtestTW2.R;

/**
 * 书架专用的scrollview
 * 
 * @author zhaoq
 * 
 */
public class BookShelvesView extends ScrollView{
	private ShelvesActivity mActivity;
	// 存放书架的容器视图
	private RelativeLayout shelvesContainer;

	private int mCoverWidth = 80;
	private int mCoverHeight = 80;
	
	private int mBookWidth = 80;
	private int mBookHeight = 80;

	private int mNameWidth = 80;
	private int mNameHeight = 80;
	
	private int mCloseWidth = 80;
	private int mCloseHeight = 80;
	private int mCloseLeftMargin = 80;
	private int mCloseBottomMargin = 80;
	
	private int mShelvesWidth = 384;
	private int bookgap = 0;

	// 设置每排书的个数
	private int columns = 1;
	private int bookSize = 0;

//	private int rightMargin = 8;
//	private int leftMargin = 5;
//	private int topMargin = 8;
//	private int bottomMargin = 15;
	
	//书排与书本封皮的高度比
	private float shelvesRate = 1.2f;
	
	public BookShelvesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mActivity = (ShelvesActivity) context;
	}

	public BookShelvesView(Context context) {
		super(context);
		mActivity = (ShelvesActivity) context;
	}

	private boolean isTop = false;
	private MotionEvent oldEv;
	private int toppadding = 0;
	private int bottompadding = 0;

	private boolean isBottom = true;
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		isTop = t==0;
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(oldEv ==null){
			oldEv = MotionEvent.obtain(ev);
		}
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
			isBottom = getScrollY() + getHeight() >=  computeVerticalScrollRange();
			if(isTop){
				toppadding = (int) (ev.getY()-oldEv.getY());
				if(toppadding>0){
					Log.d("hl","toppadding " +toppadding);
					rootLay.setPadding(0, toppadding, 0, 0);
					return true;
				}
			}
			
			if(isBottom){
				bottompadding = (int) (ev.getY()-oldEv.getY());
				if(bottompadding<0){
					rootLay.setPadding(0, 0, 0, -bottompadding);
					return true;
				}
			}
			
		}else{
			oldEv = MotionEvent.obtain(ev);
//			if(isTop||isBottom){
//				rootLay.setPadding(0, 0, 0, 0);
//			}
			rootLay.setPadding(0, 0, 0, 0);
			oldEv = null;
			isTop = false;
			isBottom = false;
			toppadding = 0;
			bottompadding = 0;
		}
		boolean ret = super.onTouchEvent(ev);
		return ret;
	}
	
	/**
	 * 创建新挡板视图
	 */
	private void createShelvesRow(int topMargin){
		TextView  img = new TextView(mActivity);
		img.setBackgroundResource(R.drawable.shelfone);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				mShelvesWidth, (int) (mBookHeight*shelvesRate));
		lp.topMargin = topMargin;
		shelvesContainer.addView(img,lp);
	}
	
	private LinearLayout rootLay;
	/**
	 * 初始化书架视图
	 */
	public void initView() {
		rootLay = new LinearLayout(mActivity);
		
		shelvesContainer = new RelativeLayout(mActivity);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(rootLay, lp);
		rootLay.addView(shelvesContainer, lp);
		
		mCoverWidth = ScreenAdapter.calcWidth(240*ShelvesActivity.myRatio);
		mCoverHeight = ScreenAdapter.calcWidth(320*ShelvesActivity.myRatio);
		mNameWidth = ScreenAdapter.calcWidth(240*ShelvesActivity.myRatio);
		mNameHeight = ScreenAdapter.calcWidth(80*ShelvesActivity.myRatio);
		mCloseWidth = ScreenAdapter.calcWidth(66*ShelvesActivity.myRatio); 
		mCloseHeight = ScreenAdapter.calcWidth(66*ShelvesActivity.myRatio); 
		mCloseBottomMargin = -ScreenAdapter.calcWidth(33*ShelvesActivity.myRatio); 
		mCloseLeftMargin = -ScreenAdapter.calcWidth(33*ShelvesActivity.myRatio);
		
		mBookWidth = mCoverWidth + mCloseWidth +  mCloseLeftMargin + ScreenAdapter.calcWidth(20*ShelvesActivity.myRatio);
		mBookHeight = mCoverHeight + mNameHeight + mCloseHeight + mCloseBottomMargin  + ScreenAdapter.calcWidth(52*ShelvesActivity.myRatio);;
		
		// 计算书架的各个参数
		// 书架的列数，将书总宽度的1/3作为间隔评分
		columns = mShelvesWidth / mBookWidth;
		//columns = columns * 9 / 10;

		int leftWidth = mShelvesWidth - (mBookWidth * columns);
		bookgap = leftWidth / (columns + 1);
		requestLayout();

	};

	public void clearBookView() {
		shelvesContainer.removeAllViews();
		bookSize = 0;
		requestLayout();
	}

	/**
	 * 设置书架的size
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		mShelvesWidth = width;
	}

	/**
	 * 增加书籍
	 * 
	 * @param book
	 */
	public void addBookView(Book book) {
		// 1 设置书的视图
		BookView bookView = new BookView(mActivity,book);
		bookView.setEditable(EDITABLE);
		// 2 计算书的位置
		RelativeLayout.LayoutParams bookLp = new RelativeLayout.LayoutParams(mBookWidth,mBookHeight);
		bookLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		bookLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		int _x = (bookSize % columns) * (mBookWidth + bookgap);
		_x += bookgap;
		int rows = bookSize / columns;
				
		//int initShelvesCnt =mShelvesHeight/ mBookHeight;
		//initShelvesCnt++;
	
		for(int i=0;i<rows+1;i++){
			int topmargin = (int) (mBookHeight*i*shelvesRate);
			createShelvesRow(topmargin);
		}
		
		int _y = (int) (rows * (mBookHeight * shelvesRate));
		_y += mBookHeight * 0.2;
		
		bookLp.leftMargin = _x;
		bookLp.topMargin = _y;
		
		bookSize++;
		// 3 装入书架，增加点击事件
		shelvesContainer.addView(bookView, bookLp);
		bookView.setTag(book);
		bookView.setOnClickListener(bookClickListsner);
	}

	/**
	 * 设置删除相关的东西
	 * @param editable
	 */
	public void setEditable(boolean editable){
		EDITABLE = editable;
		for(int index=0;index<shelvesContainer.getChildCount();index++){
			View v = shelvesContainer.getChildAt(index);
			if(v instanceof BookView){
				BookView bookView = (BookView) v;
				bookView.setEditable(EDITABLE);
			}
		}
	}
	public LinearLayout bottomlay;


	// 点击书籍的事件
	OnClickListener bookClickListsner = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// 如果处于编辑状态不允许进入读书页面
			if (EDITABLE)
				return;
			Book b = (Book) arg0.getTag();
			if(b.state==0){
				return;
			}
			BookSetting.IS_SHELVES = true;
			//for fix size only
//			BookSetting.FIX_SIZE = true;
//			BookSetting.INIT_SCREEN_HEIGHT = 1024;
//			BookSetting.INIT_SCREEN_WIDTH = 768;
			
			Intent intent = new Intent(mActivity,HLReader.class);
			
			if(b.isReadOnly){
				intent.putExtra("readtype", 0);
			}else{
				intent.putExtra("readtype", 1);
				intent.putExtra("readpath", b.mData + "/book/");
			}
			mActivity.startActivity(intent);
		}
	};
	

	//表示是否可以打开阅读书籍的变量
	public static boolean EDITABLE = false;
	public static int BOOKVIEW_COVER_ID = 1000001;
	public static int BOOKVIEW_CLOSE_ID = 1000002;
	public static int BOOKVIEW_NAME_ID = 1000003;
	public static int BOOKVIEW_PROGRESS_ID = 1000004;
	public static int BOOKVIEW_PROGRESS_VALUE_ID = 1000005;
	public static int BOOKVIEW_UNZIPPROGRESS_ID = 1000006;
	public static int BOOKVIEW_TEXTNAME_ID = 1000007;

	class BookView extends RelativeLayout{
		private ImageView imgClose;
		private Book mBook;
//		ProgressBar progress = null;
//		TextView textViewPrrogress=null;
		public void setEditable(boolean editable){
			if(editable){
				if(!mBook.isReadOnly){
					imgClose.setVisibility(View.VISIBLE);
				}
			}else{
				imgClose.setVisibility(View.INVISIBLE);
			}
		}
		public void setDownView(){
			imgClose.bringToFront();
			if (mBook.state != 1) {
//				unZipprogress.setVisibility(View.INVISIBLE);
//				progress.setVisibility(View.VISIBLE);
//				textViewPrrogress.setVisibility(View.VISIBLE);
				return;
			} else{
//				progress.setVisibility(View.INVISIBLE);
//				textViewPrrogress.setVisibility(View.INVISIBLE);
			}
		}
		
		public BookView(Context context,Book book) {
			super(context);
			mBook = book;
			setId(book.mOrder);
			// 删除按钮
			imgClose = new ImageView(mActivity);
			imgClose.setBackgroundResource(R.drawable.bookdel);
			RelativeLayout.LayoutParams closeLp = new RelativeLayout.LayoutParams(
					mCloseWidth, mCloseHeight);
//			closeLp.leftMargin = mCloseLeftMargin;
//			closeLp.bottomMargin = mCloseBottomMargin;
			closeLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			closeLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imgClose.setId(BOOKVIEW_CLOSE_ID);
			addView(imgClose, closeLp);
			imgClose.setVisibility(View.INVISIBLE);
			imgClose.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.obj = mBook;
					msg.what = ShelvesActivity.MSG_DELETE_INFOR;
					mActivity.downhandle.sendMessage(msg);
				}
			});
			
			// 书籍的封皮
			ImageView imgCover = new ImageView(context);
			imgCover.setScaleType(ScaleType.FIT_XY);
			if(book.isReadOnly){
				imgCover.setBackgroundResource(R.drawable.defaultbook);
			}else{
				Bitmap coverbitmap = BitmapCache.getSimpleBitmap(book.mIcon, book.mIcon);
				if(coverbitmap != null){
					imgCover.setImageBitmap(coverbitmap);
				}else{
					imgCover.setBackgroundResource(R.drawable.defaultbook);
				}
			}
			RelativeLayout.LayoutParams coverLp = new RelativeLayout.LayoutParams(
					mCoverWidth, mCoverHeight);
			coverLp.addRule(RelativeLayout.LEFT_OF,BOOKVIEW_CLOSE_ID);
			coverLp.addRule(RelativeLayout.BELOW,BOOKVIEW_CLOSE_ID);
			coverLp.topMargin = mCloseBottomMargin;
			coverLp.rightMargin = mCloseLeftMargin;
			imgCover.setId(BOOKVIEW_COVER_ID);
			addView(imgCover, coverLp);
				
			// 名字
			TextView textName = new TextView(mActivity);
			textName.setId(BOOKVIEW_TEXTNAME_ID);
			textName.setTextColor(Color.BLACK);
			textName.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenAdapter.calcWidth(31*ShelvesActivity.myRatio));
			String namePath=mBook.mName;
			if(FileUtils.isExist(namePath)){
				try {
					FileInputStream nameis=new FileInputStream(namePath);
					String name=FileUtils.inputStream2String(nameis);
					textName.setText(name);
				} catch (IOException e) {
					textName.setText(R.string.bookdefaultname);
				}
				
			}else{
				textName.setText(R.string.bookdefaultname);
			}
			textName.setGravity(Gravity.CENTER);
			textName.getPaint().setFakeBoldText(true);
			textName.setTextColor(Color.rgb(110, 110, 110));
			RelativeLayout.LayoutParams nameLp = new RelativeLayout.LayoutParams(
					mNameWidth, mNameHeight);
//			nameLp.setMargins(leftMargin, 0, rightMargin, bottomMargin);
			nameLp.addRule(RelativeLayout.BELOW, BOOKVIEW_COVER_ID);
			nameLp.addRule(RelativeLayout.ALIGN_LEFT, BOOKVIEW_COVER_ID);
			addView(textName, nameLp);
			
			//进度条和下载百分比
			
			TasksCompletedView completedView=new TasksCompletedView(context);
			completedView.setId(BOOKVIEW_PROGRESS_ID);
			completedView.setVisibility(View.GONE);
			addView(completedView,coverLp);
			
			RelativeLayout.LayoutParams unZipprogressLp = new RelativeLayout.LayoutParams(dip2px(mActivity, 40), dip2px(mActivity, 40));
			unZipprogressLp.addRule(RelativeLayout.CENTER_IN_PARENT);
			ProgressBar unZipprogress = new ProgressBar(mActivity, null,
					android.R.attr.progressBarStyleLarge);
			unZipprogress.setId(BOOKVIEW_UNZIPPROGRESS_ID);
			unZipprogress.setVisibility(View.GONE);
			addView(unZipprogress, unZipprogressLp);
			setDownView();
		}
	}
	
	
 
	public static void formatSmallTextViewSize(Activity mActivity,TextView textName) {
		float aa=AppContext.getScreenWidth(mActivity);
		float bb=AppContext.getScreenHeight(mActivity);
		float width=Math.min(aa, bb);
		if(width<=800){
			textName.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
		}else{
			textName.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
		}
	}
	public static void formatMidTextViewSize(Activity mActivity,TextView textName) {
		float aa=AppContext.getScreenWidth(mActivity);
		float bb=AppContext.getScreenHeight(mActivity);
		float width=Math.min(aa, bb);
		if(width<=800){
			textName.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
		}else{
			textName.setTextSize(TypedValue.COMPLEX_UNIT_PT, 9);
		}
	}
	public static void formatAddTextViewSize(Activity mActivity,TextView textName) {
		float aa=AppContext.getScreenWidth(mActivity);
		float bb=AppContext.getScreenHeight(mActivity);
		float width=Math.min(aa, bb);
		if(width<=800){
			textName.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
		}else{
			textName.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
		}
	}
	

    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
