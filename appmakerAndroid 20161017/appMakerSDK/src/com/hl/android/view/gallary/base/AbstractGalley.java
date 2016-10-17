package com.hl.android.view.gallary.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.android.HLActivity;
import com.hl.android.R;
import com.hl.android.common.BookSetting;
import com.hl.android.controller.BookController;
import com.hl.android.controller.BookState;
import com.hl.android.core.utils.ScreenUtils;
/**
 * 让公用的gallery做的更多一些吧
 * @author zhaoq
 *
 */
public abstract class AbstractGalley extends Gallery {
	private TextView mPageTextView;

	private ImageButton mHideImgButton;
	private int currentSectionIndex = -1;
	private ArrayList<String> mSnapshots;
	
	int width = 320;
	int height = 280;
	
	protected List<ImageMessage> imageList = new ArrayList<ImageMessage>();
	private int showingIndex = -1;
	private static final int TIME_OUT_DISPLAY = 500;
	private int toShowIndex = 0;
	ImageAdapter adpter;
	public static String ISFALSE="ISFALSE";
	public static String ISTRUE="ISTRUE";
	
	protected HLActivity mContext;

	
	private int showSize = 3;
	public AbstractGalley(Context context) {
		super(context);
		
		this.setSpacing(5);
		mContext = (HLActivity) context;
		
		height = (int) (BookSetting.SNAPSHOTS_HEIGHT*getSizeRatio());
		width = (int) (BookSetting.SNAPSHOTS_WIDTH*getSizeRatio());
		showSize = ScreenUtils.getScreenWidth((Activity) context)/width;
		if(showSize<3)showSize=3;
		adpter = new ImageAdapter(context);
		setAdapter(adpter);
		setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				mPageTextView.setText((getSelectedItemPosition() + 1)+"/"+imageList.size());
				toShowIndex = position;
				showItem();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		mPageTextView = new TextView(context);
		String aa = "1/5";
		mPageTextView.setText(aa);
		mPageTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		mPageTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		mPageTextView.getPaint().setFakeBoldText(true);
		mPageTextView.setTextColor(Color.WHITE);
		mPageTextView.setBackgroundColor(Color.TRANSPARENT);
		mPageTextView.setVisibility(View.GONE);
		
		RelativeLayout.LayoutParams textViewLp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textViewLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		textViewLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mPageTextView.setLayoutParams(textViewLp);
		
		
		mHideImgButton = new ImageButton(context);
		mHideImgButton.setBackgroundResource(R.drawable.downgallerybtn);
		RelativeLayout.LayoutParams hideBtnLp = new RelativeLayout.LayoutParams(
				120,
				48);
		hideBtnLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		hideBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		hideBtnLp.bottomMargin = 700;
		mHideImgButton.setLayoutParams(hideBtnLp);
		mHideImgButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideGalleryInfor();
				//viewPage.resume();
				BookController.getInstance().getViewPage().playVideo();
				//startPlay();

				BookState.getInstance().setPlayViewPage();
			
				
			}
		});
		setBackgroundResource(R.drawable.tbp);
		setLayoutParams(getGalleryLp());
	}
	
	protected abstract RelativeLayout.LayoutParams getGalleryLp();
	public TextView getPageTextView(){
		return mPageTextView;
	}
	public ImageButton getHideImgButton(){
		return mHideImgButton;
	}
	
 	private void showItem() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (showingIndex != toShowIndex) {
					showingIndex = toShowIndex;
					// 业务逻辑处理
					if (toShowIndex < imageList.size()) {
						addImage(toShowIndex);
					}
				}
			}
		};
		Thread checkChange = new Thread() {
			@Override
			public void run() {
				int myIndex = toShowIndex;
				try {
					sleep(TIME_OUT_DISPLAY);
					if (myIndex == toShowIndex) {
						handler.sendEmptyMessage(0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		checkChange.start();
	}
	
	
	public void showGalleryInfor() {
		if (mPageTextView != null) {
			this.mPageTextView.setVisibility(View.VISIBLE);
			this.mPageTextView.bringToFront();
		}
		if (mHideImgButton != null) {
			this.mHideImgButton.setVisibility(View.VISIBLE);
			this.mHideImgButton.bringToFront();
		}
		setVisibility(View.VISIBLE);

		if(mContext.adViewLayout!=null)mContext.adViewLayout.setVisibility(View.GONE);
	}

	public void hideGalleryInfor() {
		if (mPageTextView != null)
			this.mPageTextView.setVisibility(View.INVISIBLE);

		if (mHideImgButton != null)
			this.mHideImgButton.setVisibility(View.INVISIBLE);

		setVisibility(View.INVISIBLE);
		if(mContext.adViewLayout!=null)mContext.adViewLayout.setVisibility(View.VISIBLE);
		//BookController.getInstance().startPlay();
	}
//	private int mSelect = -1;
//	public void setSelection(int select){
//		if(mSelect==select){
//			return;
//		}else{
//			super.setSelection(select);
//			mSelect = select;
//		}
//	}
	public int getCurrentSectionIndex() {
		return currentSectionIndex;
	}

	public void setCurrentSectionIndex(int currentSectionIndex) {
		this.currentSectionIndex = currentSectionIndex;
	}
	
	/**
	 * 获得数据源
	 * @return
	 */
	protected ArrayList<String> getSnashots(){
		return mSnapshots;
	}
	/**
	 * 设置数据源
	 */
	public void setSnapshots(ArrayList<String> snapshots){
		mSnapshots = snapshots;
		recycle();
		for(String snapshot:snapshots){
			ImageMessage im = new ImageMessage();
			im.setPath(snapshot);
			im.setIsNull(ISTRUE);
			imageList.add(im);
		}
		Log.d("hl","show index is " + showingIndex);
		showItem();
		adpter.notifyDataSetChanged();
	}
	


//	public RelativeLayout.LayoutParams getLayoutPrams() {
//		RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
//				BookSetting.SCREEN_WIDTH,
//				BookSetting.SNAPSHOTS_HEIGHT);
//		
//		return layoutParams1;
//	}
	public void playAnimation(){
		
	}
	
	public void recycle(){;
		ImageManager.clearImage(imageList, 0, imageList.size());
		imageList.clear();
	}
	public void refreshAdapter() {
		BookController.getInstance().hlActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adpter.notifyDataSetChanged();
			}
		});
	}
	
	public void addImage(int args) {
		int start = toShowIndex - (showSize/2 + showSize%2);
		int end = toShowIndex +(showSize/2 + showSize%2);
		if (start < 0) {
			start = 0;
		}

		if (end >= imageList.size()) {
			end = imageList.size() - 1;
		}
		imageList = ImageManager.clearImage(imageList,start,end);
	   	 ExecutorService pool = Executors.newFixedThreadPool(4); 
	   	 for(int i=start;i<=end;i++){
	   		 Log.d("hl","hl requeir index paths is " + imageList.get(i).getPath());
			 Thread t1 = new MyThread(i); 
	   		 pool.execute(t1); 
	   	 }
	   	 pool.shutdown(); 
	}

	class MyThread extends Thread {
		int index;

		public MyThread(int index) {
			super();
			this.index = index;
		}

		@Override
		public void run() {

			if (imageList.get(index).getIsNull().equals(ISTRUE)) {
				Log.d("hl","hl show index paths is " + imageList.get(index).getPath());
				Bitmap b = getBitmap(imageList.get(index).getPath(),width,height);
				if(b!=null){
					imageList.get(index).setImage(b);
					imageList.get(index).setIsNull(ISFALSE);
				}
			}
			refreshAdapter();
		}
	}
	
	public class ImageAdapter extends BaseAdapter {
		/* 变量声明 */
		private Context mContext;// 上下文

		/* 自定义的构造方法 */
		public ImageAdapter(Context c) {

			this.mContext = c;

		}

		@Override
		/* 重写的方法getCount,返回图片数目 */
		public int getCount() {

			if (imageList != null) {
				return imageList.size();
			} else {
				return 0;
			}
		}

		@Override
		/* 重写的方法getItemId,返回图像的数组id */
		public Object getItem(int position) {
			// TODO Auto-generated constructor stub
			if (imageList != null) {
				return imageList.get(position);
			}
			return position;// 获取图片在库中的位置
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated constructor stub
			return position;// 获取图片在库中的位置
		}

		@Override
		/* 重写的方法getView,返回一View 对象 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated constructor stub
			/* 产生ImageView 对象 */
			ImageView imageView = new ImageView(mContext);

			// 填充ImageView
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			/* 设置背景资源 */
			int imageHeight=height;
			if(BookSetting.GALLEYCODE == 1){
				imageHeight=(int) (1.2f*height);
			}
			imageView.setLayoutParams(new Gallery.LayoutParams(width, imageHeight));
			if (imageList.get(position).getIsNull().equals(ISTRUE)) {
				setWaitLoad(imageView);
			} else {
				imageView.setImageBitmap(imageList.get(position).getImage());
			}
			return imageView;
		}

		public float getScale(boolean focused, int offset) {
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}
	}

	public void setHideBtn(ImageButton hideBtn) {
		mHideImgButton = hideBtn;
		mHideImgButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setVisibility(View.GONE);
				hideGalleryInfor();
			}
		});
	}
	
	
	protected abstract Bitmap getBitmap(String resourceID,int width,int height);

	protected abstract void setWaitLoad(ImageView img);

	protected abstract float getSizeRatio();
}
