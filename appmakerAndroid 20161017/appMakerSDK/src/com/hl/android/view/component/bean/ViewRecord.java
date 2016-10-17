package com.hl.android.view.component.bean;
/**
 * 记录视图当前状态的java bean
 * 包含坐标，大小，透明度、旋转度
 * @author zhaoq
 *
 */
public class ViewRecord  implements Cloneable{
	public float mX = 0f;
	public float mY = 0f;
	public int mWidth = 0;
	public int mHeight = 0;
//	public float mAlpha = 1.0f;
	public float mRotation = 0f;
	
	public float mScaleX = 1f;
	public float mScaleY = 1f;
	
	public long usetime = 0l;
	public int animationIndex = 0;
	
	public ViewRecord getClone(){
		try {
			return (ViewRecord) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
