package com.hl.android.book.entity;

import java.util.ArrayList;
import android.graphics.PointF;

public class AnimationEntity {
public String ClassName;
public String CurrentAnimationIndex;
public String Repeat;
public String Delay;
public String Duration;
public String AnimationType;
public String AnimationEnterOrQuit;
public String AnimationTypeLabel;
public String CustomProperties;
public ArrayList<PointF> Points;
public String IsKeep = "true";
//false 是播放时重置，true 是播放时不重置。
public boolean isKeepEndStatus = true;
public String EaseType;
//=======================================
public ArrayList<SeniorAnimationEntity> hEntitys;//增加的路径动画entity
//=======================================
public AnimationEntity()
{
	this.Points=new ArrayList<PointF>();
}

/*
public float getSumDuration(){
	float sumDuration =Float.parseFloat(Delay);
	sumDuration = Float.parseFloat(Duration)*Long.parseLong(Repeat) + sumDuration;
	return sumDuration;
}*/
}
