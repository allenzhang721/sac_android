package com.hl.android.common;

import android.view.Display;

public class HLSetting {
	public static boolean IsResourceSD = false;
	public static boolean INIT_IsResourceSD = false;

	public static boolean IS_ASSETS_ZIP = false;
	public static boolean IS_SD_ZIP = false;
	public static boolean IsAD = false;
	public static int FlipTime = 50;
	public static final int FLING_MIN_DISTANCE = 80;
	public static final int FLING_MIN_VELOCIT = 100;// x或者y轴上的移动速度(像素/秒)
	public static final int FLING_SUB_MIN_VELOCIT = 50;
	public static boolean isHoneyComb = false;
	public static Display display;
	public static boolean PlayBackGroundMusic = false;
	public static boolean FitScreen = true;// false 按比例缩小 true按屏幕拉伸
	public static String DomobADPID = "56OJyM1ouMGoaSnvCK";
	public static boolean isCacheEnabled = false; // 是否启用缓存机制，true是 false 否
	public static boolean isSettingTumb = false; // if auto minus menu title
	public static boolean isBookStore = false;
	// 是否需要单独弹出新的视频activity
	public static boolean isNewActivityForVideo = false;

	public static String LOCATION = "";

	public static boolean IsHaveBookMark = false;
	public static boolean IsShowBookMark = false;
	public static boolean IsShowBookMarkLabel = false;
	public static String BookMarkLablePositon = "";
	public static int BookMarkLabelHorGap = 0;
	public static int BookMarkLabelVerGap = 0;
	public static String BookMarkLabelText = "";
}
