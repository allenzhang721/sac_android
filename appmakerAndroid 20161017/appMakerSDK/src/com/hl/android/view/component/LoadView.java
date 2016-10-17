package com.hl.android.view.component;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class LoadView extends LinearLayout {
	public ProgressBar loadBar;
	public LoadView(Context context,ViewGroup.LayoutParams param) {
		this(context);
		setLayoutParams(param);
	}
	
	public LoadView(Context context) {
		super(context);
		setBackgroundColor(Color.BLACK);
		getBackground().setAlpha(20);
		loadBar=new ProgressBar(context); 
		
		LinearLayout.LayoutParams loadLp = new LinearLayout.LayoutParams(37,37);
		//loadLp.setMargins(10, 10, 10, 10);
		addView(loadBar,loadLp);
		setGravity(Gravity.CENTER);
	}

}
