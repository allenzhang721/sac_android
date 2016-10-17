package com.hl.android.view.component.moudle;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Gallery;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.gallary.GalleyCommonAdapter;



public class GalleryComponent extends Gallery implements Component{
	ComponentEntity entity;
	int height,width;

	public GalleryComponent(Context context) {
		super(context);
	}
	public GalleryComponent(Context context, ComponentEntity entity) {
		super(context);
		this.entity = entity;
		this.setBackgroundColor(Color.BLUE);
	}

	@Override
	public ComponentEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;
		
	}
	@Override
	public void load() {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) this.getLayoutParams();
		width=lp.width;
		height=lp.height;
		this.setAdapter((new GalleyCommonAdapter(this.getContext(), ((MoudleComponentEntity)entity).getSourceIDList(),
				width, height)));
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

		width=this.getLayoutParams().width;
	//	height=this.getLayoutPrams().height;
		//registerAdapter(((HorSliderImageUIComponentEntity)entity.getTemplate()).getImageList());

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
}
