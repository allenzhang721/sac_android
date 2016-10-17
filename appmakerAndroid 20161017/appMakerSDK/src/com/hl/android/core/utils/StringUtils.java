package com.hl.android.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 字符串操作工具类  <br>
 * <br>
 * 提供常用的字符串操作。 <br>
 * <br>
 * 所有方法均为static方法，可直接使用。<br>
 * <br>
 * @author zhaoq
 * @LastModified 2012-7-3
 */
public class StringUtils {
	private static final String C_String_Date_Format = "yyyy-MM-dd";
	private static final String C_String_Date_Time_Format = "yyyy-MM-dd HH:mm:ss";
	//标准日期格式化工具
	private static final SimpleDateFormat dateFormater = new SimpleDateFormat(C_String_Date_Format);
	private static final SimpleDateFormat dateTimeFormater = new SimpleDateFormat(C_String_Date_Time_Format);
	
	/**
	 * 返回格式化好的当前日期，格式为：yyyy-MM-dd
	 * */
	public static String formatNowDate(String... format) {
		String result = null;
		synchronized (dateFormater) {
			if(format!=null&&format.length>0) {
				dateFormater.applyPattern(format[0]);
				result = dateFormater.format(new Date()); 
				dateFormater.applyPattern(C_String_Date_Format);
			}else {
				result = dateFormater.format(new Date()); 
			}
		}
		return result;
	}
	
	/**
	 * 返回格式化好的当前日期和时间，格式为：yyyy-MM-dd HH:mm:ss
	 * */
	public static String formatNowDateTime(String... format) {
		String result = null;
		synchronized (dateTimeFormater) {
			if(format!=null&&format.length>0) {
				dateTimeFormater.applyPattern(format[0]);
				result = dateTimeFormater.format(new Date()); 
				dateTimeFormater.applyPattern(C_String_Date_Time_Format);
			}else {
				result = dateTimeFormater.format(new Date()); 
			}
		}
		return result;
	}
	/**
	 * 返回格式化好的当前日期，格式为：yyyy-MM-dd
	 * @param date 要格式化的日期参数
	 * */
	public static String formatDate(Date date,String... format) {
		if(date==null) {
			return "";
		}
		String result = null;
		synchronized (dateFormater) {
			if(format!=null&&format.length>0) {
				dateFormater.applyPattern(format[0]);
				result = dateFormater.format(date); 
				dateFormater.applyPattern(C_String_Date_Format);
			}else {
				result = dateFormater.format(date); 
			}
		}
		return result;
	}
	/**
	 * 返回格式化好的当前日期和时间，格式为：yyyy-MM-dd HH:mm:ss
	 * @param date 要格式化的日期参数
	 * */
	public static String formatDateTime(Date date,String... format) {
		if(date==null) {
			return "";
		}
		String result = null;
		synchronized (dateTimeFormater) {
			if(format!=null&&format.length>0) {
				dateTimeFormater.applyPattern(format[0]);
				result = dateTimeFormater.format(date); 
				dateTimeFormater.applyPattern(C_String_Date_Time_Format);
			}else {
				result = dateTimeFormater.format(date); 
			}
		}
		return result;
	}
	/**
	 * 讲若干字符串拼接为一个可以作为路径的字符串。以”/“结尾
	 * 
	 * @param paths 作为路径一部分的字符串，按顺序放置
	 */
	public static String contactForPath(String... paths) {

		if (paths == null || paths.length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < paths.length; i++) {
			sb.append(paths[i]).append("/");
		}

		String result = sb.toString();
		int index = -1;
		while ((index = result.indexOf("//")) >= 0) {
			result = result.substring(0, index) + result.substring(index + 1);
		}
		return result;
	}

	/**
	 * 讲若干字符串拼接为一个可以作为文件名的字符串。（不以/结尾）
	 * 
	 * @param paths 作为路径一部分的字符串，按顺序放置，最后一个为文件名
	 */
	public static String contactForFile(String... paths) {

		String result = contactForPath(paths);
		if (result.endsWith("/")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	/**
	 * 检查字符串是否为空
	 * @param content
	 * @return
	 */
	public static boolean isEmpty(String content){
		return content==null||content.trim().length()==0;
	}
//	public static String getParameter(String ps,int index,String defValue){
//		String[] vs = ps.split(";");
//		if(vs.length <= index)return defValue;
//		String v = vs[index];
//		return v;
//	}
//	public static int getParameter(String ps,int index,int defValue){
//		String[] vs = ps.split(";");
//		if(vs.length <= index)return defValue;
//		int v = Integer.parseInt(vs[index]);
//		return v;
//	}
	
	
	public static String file2String(File file, String encoding) {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		try {
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(new FileInputStream(file),
						encoding);
			} else {
				reader = new InputStreamReader(new FileInputStream(file));
			}
			// 将输入流写入输出流
			char[] buffer = new char[1024];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return writer.toString();
	}
	
	

	/**
	 * 检查输入参数是否符合正则表达式
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isUri(String content) {
		return !StringUtils.isEmpty(content);
	}

	
}
