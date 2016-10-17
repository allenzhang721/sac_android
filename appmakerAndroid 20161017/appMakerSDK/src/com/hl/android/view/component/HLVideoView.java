package com.hl.android.view.component;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.VideoView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.VideoComponentEntity;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;

@SuppressLint("NewApi")
public class HLVideoView extends VideoView implements Component{

	public VideoComponentEntity entity;
	public HLVideoView(Context context) {
		super(context);
	}

	public HLVideoView(Context context, ComponentEntity entity) {
		super(context);
		this.entity = (VideoComponentEntity) entity;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(InputStream is) {
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

	/***************************下面都是属性动画使用相关代码*******************************/
	public ViewRecord initRecord;
	@SuppressLint("NewApi")
	public ViewRecord getCurrentRecord(){
		ViewRecord curRecord = new ViewRecord();
		curRecord.mHeight = getLayoutParams().width;
		curRecord.mWidth = getLayoutParams().height;
		
		curRecord.mX = getX();
		curRecord.mY = getY();
		curRecord.mRotation = getRotation();
//		curRecord.mAlpha = getAlpha();
		return curRecord;
	}
}
