package com.hl.android.common;

import java.util.ArrayList;

import com.hl.android.book.entity.ButtonEntity;

import android.os.Environment;

public class BookSetting {
	public static String LOCATION = null;
	public static String BOOK_RESOURCE_DIR = "book/";
	public static String BOOK_RESOURCESD_ROOT = Environment
			.getExternalStorageDirectory() + "/hl/";

	public static String BOOK_RESOURCE_ZIP_NAME = "book.zip";
	public static boolean FIX_SIZE = false;
	public static int SCREEN_WIDTH = 480;
	public static int SCREEN_HEIGHT = 800;

	public static int SNAPSHOTS_WIDTH = 320;
	public static int SNAPSHOTS_HEIGHT = 280;

	public static float RESIZE_WIDTH = 1;
	public static float RESIZE_HEIGHT = 1;
	public static float RESIZE_COUNT = 1;
	public static boolean IS_HOR = true;
	public static boolean IS_AUTOPAGE = false;
	public static int FLIPCODE = 1;// 0 corner, 1 move 2 对开
	public static int GALLEYCODE = 1;// 0 common, 1 3D
	public static boolean ISSUBPAGE_ENABLE = true; //s是否启用子页功能
	
	public static int INIT_SCREEN_WIDTH = 480;
	public static int INIT_SCREEN_HEIGHT = 800;
	public static String CURRENTBOOKID = "";
	public static boolean IS_REMEMBER_READPAGE = false;
	
	public static boolean IS_HOR_VER = false;
	public static String BOOK_PATH = "";

	public static boolean IS_SHELVES_COMPONENT = false;
	public static boolean IS_SHELVES = false;
	public static String INIT_BOOK_PATH = "";
	
	public static boolean IS_READER = false;
	
	public static boolean IS_SHOW_LOADINGBAR = false;
	public static boolean IS_TRY = false;

	public static boolean noBackGround = false;
	//标志是否进行内容滑动，用于标记使用新版本的滑动翻页
	public static boolean FLIP_CHANGE_PAGE=true;
//	public static String ScaleType = "fit";
	

	public static String BOOK_ID = "";
	

	public static ArrayList<ButtonEntity> buttons;
	
	public static String fileName = "book.dat";
	
	
	
	/**
	 * 书籍的像素大小范围
	 * 如果改造完毕绘制各种元素的时候使用的应该就是这两个变量
	 */
	public static int BOOK_WIDTH = 480;
	public static int BOOK_HEIGHT = 800;
	public static float BOOK_WIDTH4CALCULATE=BOOK_WIDTH;
	public static float BOOK_HEIGHT4CALCULATE=BOOK_HEIGHT;
	//page中元素的放大的比率
	public static float PAGE_RATIO = 1.0f;
	public static float PAGE_RATIOX = 1.0f;
	public static float PAGE_RATIOY = 1.0f;
	
	
	public static boolean IS_NO_NAVIGATION = false;
	//是否已经关闭的状态标识，在关闭以后需要设置成true，开始的时候需要设置成false
	//执行动作或者循环的时候需要使用这个标志位，如果是true则就需要做退出机制的处理
	public static boolean IS_CLOSED = false;
	//标志是否使用全屏拉伸
	public static boolean FITSCREEN_TENSILE=true;
	//随手指放大和缩小
	public static final float MIN_SCALE = 1.0f;
	public static final float MAX_SCALE = 5.0f;
}
                                       