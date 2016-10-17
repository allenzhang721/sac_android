package com.hl.android.view.gallary.base;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ImageMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String path;
	public Bitmap image;
	public String isNull;
	public String getIsNull() {
		return isNull;
	}
	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
	

}
