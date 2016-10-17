package com.hl.android.core.helper;

import android.util.Log;

public final class LogHelper {
	private static boolean mIsDebugMode = true;//获取堆栈信息会影响性能，发布应用时记得关闭DebugMode
	private static String mLogTag = "SunYongle";
	private static final String CLASS_METHOD_LINE_FORMAT = "方法：%s.%s()  所在行:%d  所在文件：%s";
	
	public static void setLogTag(String tag) {
		mLogTag=tag;
	}
	
	public static void trace(String tittle,String showContent) {
		if (mIsDebugMode) {
			StackTraceElement traceElement = Thread.currentThread()
					.getStackTrace()[3];//从堆栈信息中获取当前被调用的方法信息
			String logText = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getClassName(), traceElement.getMethodName(),
					traceElement.getLineNumber(), traceElement.getFileName());
			Log.d(mLogTag, logText+"\nTITTLE："+tittle+"   VALUE："+showContent);
		}
	}
	
	public static void trace(String tittle,String showContent,boolean showInError) {
		if(!showInError){
			trace(tittle, showContent);
			return;
		}
		if (mIsDebugMode) {
			StackTraceElement traceElement = Thread.currentThread()
					.getStackTrace()[3];//从堆栈信息中获取当前被调用的方法信息
			String logText = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getClassName(), traceElement.getMethodName(),
					traceElement.getLineNumber(), traceElement.getFileName());
			Log.e(mLogTag, logText+"\nTITTLE："+tittle+"   VALUE："+showContent);
		}
	}
}
