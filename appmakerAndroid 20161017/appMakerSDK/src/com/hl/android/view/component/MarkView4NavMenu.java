package com.hl.android.view.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.android.HLLayoutActivity;
import com.hl.android.R;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.controller.BookController;
import com.hl.android.view.component.bookmark.BookMarkManager;

public class MarkView4NavMenu extends RelativeLayout{
	private Context mContext;
	private ImageButton mBtn4addOrRemoveMark;
	private ListView listView4showMark;
	private BaseAdapter markItemsAdapter;
	private static final int ID_LAYOUT_ADDORREMOVE_MARK=0x10010;
	public MarkView4NavMenu(Context context) {
		super(context);
		mContext=context;
		init();
	}

	private void init() {
		RelativeLayout layout=new RelativeLayout(mContext);
		layout.setId(ID_LAYOUT_ADDORREMOVE_MARK);
		mBtn4addOrRemoveMark=new ImageButton(mContext);
		String curPageId = BookController.getInstance().mainViewPage.getEntity().getID();
		if (BookMarkManager.getMarkList((HLLayoutActivity) mContext).contains(curPageId)) {
			mBtn4addOrRemoveMark.setBackgroundResource(R.drawable.indesign_collectionbtndown);
		}else{
			mBtn4addOrRemoveMark.setBackgroundResource(R.drawable.indesign_collectionbtnup);
		}
		layout.setBackgroundResource(R.drawable.indesign_colle_headbgimg);
		RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(layout,layoutParams1);
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin=5;
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		layout.addView(mBtn4addOrRemoveMark,layoutParams);
		listView4showMark=new ListView(mContext);
		listView4showMark.setBackgroundResource(R.drawable.indesign_colle_bgimg);
		RelativeLayout.LayoutParams params4ShowMark=new RelativeLayout.LayoutParams(300, LayoutParams.MATCH_PARENT);
		params4ShowMark.addRule(RelativeLayout.BELOW, layout.getId());
		addView(listView4showMark, params4ShowMark);
		listView4showMark.setFadingEdgeLength(0);
		listView4showMark.setCacheColorHint(Color.TRANSPARENT);
		listView4showMark.setSelector(new ColorDrawable(Color.TRANSPARENT));
		markItemsAdapter=new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				RelativeLayout layout=new RelativeLayout(mContext);
				ImageView imageView=new ImageView(mContext);
				imageView.setId(0x40010);
				PageEntity page = BookController.getInstance().getPageEntityByID(BookMarkManager.getMarkList((HLLayoutActivity) mContext).get(getCount()-1-position));
				Bitmap bitmap = BookController.getInstance().getSmallSnapShotCashImage(page);
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ScaleType.FIT_XY);
				AbsListView.LayoutParams params4layout=new AbsListView.LayoutParams(300, 100);
				layout.setLayoutParams(params4layout);
				RelativeLayout.LayoutParams params4image=new RelativeLayout.LayoutParams(60*bitmap.getWidth()/bitmap.getHeight(), 60);
				params4image.addRule(RelativeLayout.CENTER_VERTICAL);
				params4image.leftMargin=20;
				layout.addView(imageView,params4image);
				
				LinearLayout linearLayout=new LinearLayout(mContext);
				linearLayout.setOrientation(LinearLayout.VERTICAL);
				TextView textView4tittle=new TextView(mContext);
				textView4tittle.setTextSize(15);
				textView4tittle.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
				textView4tittle.setSingleLine(true);
				textView4tittle.setText(page.getTitle());
				textView4tittle.setTextColor(Color.WHITE);
				
				LinearLayout.LayoutParams params4textView=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params4textView.setMargins(20, 0, 20, 0);
				
				linearLayout.addView(textView4tittle,params4textView);
				
				TextView textView=new TextView(mContext);
				textView.setTextSize(12);
				textView.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
				textView.setSingleLine(true);
				textView.setText(page.getDescription());
				textView.setTextColor(Color.WHITE);
				
				LinearLayout.LayoutParams params4text=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params4text.setMargins(20, 0, 20, 0);
				
				linearLayout.addView(textView,params4text);
				RelativeLayout.LayoutParams params4LinearLayout=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params4LinearLayout.addRule(RelativeLayout.CENTER_VERTICAL);
				params4LinearLayout.addRule(RelativeLayout.RIGHT_OF,imageView.getId());
				layout.addView(linearLayout,params4LinearLayout);
				return layout;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return BookMarkManager.getMarkList((HLLayoutActivity) mContext).get(position);
			}
			
			@Override
			public int getCount() {
				return BookMarkManager.getMarkList((HLLayoutActivity) mContext).size();
			}
		};
		listView4showMark.setAdapter(markItemsAdapter);
		listView4showMark.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				BookController bookController = BookController.getInstance();
				String pageID = BookMarkManager.getMarkList((HLLayoutActivity) mContext).get(markItemsAdapter.getCount()-1-position);
				if(!pageID.equals(BookController.getInstance().mainPageID)){
					bookController.playPageById(pageID);
				}
				((HLLayoutActivity)mContext).getUPNav().dismiss();
			}
		});
		mBtn4addOrRemoveMark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String curPageId = BookController.getInstance().mainViewPage.getEntity().getID();
				if (!BookMarkManager.getMarkList((HLLayoutActivity) mContext).contains(curPageId)) {
					BookMarkManager.addCurPage((HLLayoutActivity) mContext);
					mBtn4addOrRemoveMark.setBackgroundResource(R.drawable.indesign_collectionbtndown);
				}else{
					int pos=BookMarkManager.getMarkList((HLLayoutActivity) mContext).indexOf(curPageId);
					BookMarkManager.deleteMark((HLLayoutActivity) mContext, pos);
					mBtn4addOrRemoveMark.setBackgroundResource(R.drawable.indesign_collectionbtnup);
				}
			}
		});
	}
	
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		refresh();
	}

	public void refresh() {
		if(markItemsAdapter!=null){
			markItemsAdapter.notifyDataSetChanged();
			String curPageId = BookController.getInstance().mainViewPage.getEntity().getID();
			if (BookMarkManager.getMarkList((HLLayoutActivity) mContext).contains(curPageId)) {
				mBtn4addOrRemoveMark.setBackgroundResource(R.drawable.indesign_collectionbtndown);
			}else{
				mBtn4addOrRemoveMark.setBackgroundResource(R.drawable.indesign_collectionbtnup);
			}
		}
	}

}
