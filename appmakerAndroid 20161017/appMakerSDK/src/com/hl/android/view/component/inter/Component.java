package com.hl.android.view.component.inter;

import java.io.InputStream;

import com.hl.android.book.entity.ComponentEntity;

public interface Component{
	//一定要实现entity方法
	public ComponentEntity getEntity();
	public void setEntity(ComponentEntity entity);
	/**
	 * 装载资源
	 */
	public abstract void load();
	public abstract void load(InputStream is);
	public void play();
	public void stop();
	public void hide();
	public void show();
	public void resume();
	public void pause();
	//获得属性动画的刷新监听器
}
