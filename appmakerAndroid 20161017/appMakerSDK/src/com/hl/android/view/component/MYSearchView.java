package com.hl.android.view.component;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.hl.android.controller.PageEntityController;
import com.hl.android.core.utils.BitmapUtils;

public class MYSearchView extends RelativeLayout{
	private Context mContext;
	private EditText editText4Search;
	private ListView listView4SearchResult;
	public BaseAdapter listViewAdapter;
	private static final int ID_LAYOUT_EDITTEXT=0x10010;
	private ArrayList<Description> descriptions;
	private ArrayList<String> showSnapshotPageIDs;
	
	public MYSearchView(Context context) {
		super(context);
		mContext=context;
		init();
	}

	private void init() {
		RelativeLayout layout=new RelativeLayout(mContext);
		layout.setId(ID_LAYOUT_EDITTEXT);
		editText4Search=new EditText(mContext);
		editText4Search.setSingleLine(true);
		editText4Search.setHint(R.string.search_hint);
		editText4Search.setBackgroundColor(Color.WHITE);
		layout.setBackgroundResource(R.drawable.indesign_colle_headbgimg);
		RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(layout,layoutParams1);
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		layoutParams.setMargins(5, 5, 5, 5);
		layout.addView(editText4Search,layoutParams);
		listView4SearchResult=new ListView(mContext);
		listView4SearchResult.setBackgroundResource(R.drawable.indesign_colle_bgimg);
		RelativeLayout.LayoutParams params4ShowMark=new RelativeLayout.LayoutParams(300, LayoutParams.MATCH_PARENT);
		params4ShowMark.addRule(RelativeLayout.BELOW, layout.getId());
		addView(listView4SearchResult, params4ShowMark);
		listView4SearchResult.setFadingEdgeLength(0);
		listView4SearchResult.setCacheColorHint(Color.TRANSPARENT);
		listView4SearchResult.setSelector(new ColorDrawable(Color.TRANSPARENT));
		BookController bookController=BookController.getInstance();
		int sectionCount=bookController.getBook().getSections().size();
		descriptions=new ArrayList<MYSearchView.Description>();
		showSnapshotPageIDs=new ArrayList<String>();
		for (int j = 0; j < sectionCount; j++) {
			for (int i = 0; i < bookController.getBook().getSections().get(j).getPages().size(); i++) {
				String pageID=bookController.getBook().getSections().get(j).getPages().get(i);
				PageEntity pageEntity = PageEntityController.getInstance().getPageEntityByPageId(mContext, pageID);
				String pageDescription=pageEntity.getDescription();
				String pageTittle=pageEntity.getTitle();
				if(pageDescription!=null||pageTittle!=null){
					descriptions.add(new Description(bookController.getBook().getSections().get(j).getID(), pageEntity.getID(), pageDescription,pageTittle));
				}
			}
		}
		listViewAdapter=new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				RelativeLayout layout=new RelativeLayout(mContext);
				AbsListView.LayoutParams params4layout=new AbsListView.LayoutParams(300, 100);
				layout.setLayoutParams(params4layout);
				if(showSnapshotPageIDs.size()==0){
					TextView textView=new TextView(mContext);
					textView.setText(R.string.no_search);
					textView.setTextSize(20);
					textView.setGravity(Gravity.CENTER);
					textView.setTextColor(Color.WHITE);
					RelativeLayout.LayoutParams params4text=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					params4text.addRule(RelativeLayout.CENTER_IN_PARENT);
					layout.addView(textView,params4text);
				}else{
					ImageView imageView=new ImageView(mContext);
					imageView.setId(0x30010);
					String snapshotID=BookController.getInstance().getSnapshotIdByPageId(showSnapshotPageIDs.get(position));
					Bitmap bitmap=BitmapUtils.getBitMap(snapshotID, mContext);
					imageView.setImageBitmap(bitmap);
					imageView.setScaleType(ScaleType.FIT_XY);
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
					PageEntity pageEntity = PageEntityController.getInstance().getPageEntityByPageId(mContext, showSnapshotPageIDs.get(position));
					String pageTittle=pageEntity.getTitle();
					String aa=pageTittle.replaceAll(editText4Search.getText().toString().trim(), "<font color=\"#ffff00\">"+editText4Search.getText().toString().trim()+"</font>");
					String pageDescription=pageEntity.getDescription();
					String bb=pageDescription.replaceAll(editText4Search.getText().toString().trim(), "<font color=\"#ffff00\">"+editText4Search.getText().toString().trim()+"</font>");
					textView4tittle.setText(Html.fromHtml(aa));
					textView4tittle.setTextColor(Color.WHITE);
					LinearLayout.LayoutParams params4textView=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params4textView.setMargins(20, 0, 20, 0);
					linearLayout.addView(textView4tittle,params4textView);
					TextView textView=new TextView(mContext);
					textView.setTextSize(12);
					textView.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
					textView.setSingleLine(true);
					textView.setText(Html.fromHtml(bb));
					textView.setTextColor(Color.WHITE);
					LinearLayout.LayoutParams params4text=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params4text.setMargins(20, 0, 20, 0);
					linearLayout.addView(textView,params4text);
					RelativeLayout.LayoutParams params4LinearLayout=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					params4LinearLayout.addRule(RelativeLayout.CENTER_VERTICAL);
					params4LinearLayout.addRule(RelativeLayout.RIGHT_OF,imageView.getId());
					layout.addView(linearLayout,params4LinearLayout);
				}
				return layout;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return showSnapshotPageIDs.get(position);
			}
			
			@Override
			public int getCount() {
				return showSnapshotPageIDs.size()==0?1:showSnapshotPageIDs.size();
			}
		};
		listView4SearchResult.setAdapter(listViewAdapter);
		listView4SearchResult.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(showSnapshotPageIDs.size()!=0){
					BookController bookController = BookController.getInstance();
					bookController.playPageById(showSnapshotPageIDs.get(position));
					((HLLayoutActivity)mContext).getUPNav().dismiss();
					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);  
					imm.hideSoftInputFromWindow(getWindowToken(), 0); 
//					showSnapshotPageIDs.clear();
				}
			}
		});
		editText4Search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				showSnapshotPageIDs.clear();
				for (MYSearchView.Description curDescription : descriptions) {
					if(editText4Search.getText().toString().isEmpty()){
						break;
					}
					String aa=curDescription.mDescription+curDescription.mTittle;
					if(aa.contains(editText4Search.getText().toString().trim())){
						showSnapshotPageIDs.add(curDescription.mpageID);
					}
				}
				listViewAdapter.notifyDataSetChanged();
			}
		});
	}
	
	class Description{
		public String mSectionID;
		public String mpageID;
		public String mDescription;
		public String mTittle;
		public Description(String mSectionID, String mpageID, String mDescription, String pageTittle) {
			super();
			this.mSectionID = mSectionID;
			this.mpageID = mpageID;
			this.mDescription = mDescription;
			this.mTittle=pageTittle;
		}
		
	}

}
