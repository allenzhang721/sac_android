package com.hl.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.util.DisplayMetrics;

/**
 * 工具类
 * 
 * @author zhaoq
 * 
 */
public class Utill {

	private static final String Mc_String_DataPath = "HLData";

	private static String appPath = "";

	/**
	 * 获得当前应用的数据文件存放位置
	 * 
	 * @param activity
	 * @return
	 */
	public static String getAppPath(Activity activity) {
		if (StringUtils.isEmpty(appPath)) {
			appPath = activity
					.getApplication()
					.getDir(Mc_String_DataPath,
							Application.MODE_WORLD_WRITEABLE).getAbsolutePath();
		}
		return appPath;
	}

	/**
	 * 获得屏幕的大小
	 * 
	 * @param activity
	 * @return
	 */
	public static int[] getScreenSize(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		int[] size = new int[2];
		size[0] = dm.widthPixels;
		size[1] = dm.heightPixels;
		return size;
	}

	private static String appSdPath;

	/**
	 * 取得本应用在系统中SD卡的的文件夹路径。以“/”结尾。<br>
	 * 如果没有Sd卡，且默认内置的sd卡（/mnt/sdcard）不存在，返回空。<br>
	 * 例如：/mnt/sdcard/
	 * */
	public static String getAppSdPath() {

		if (appSdPath != null)
			return appSdPath;
		String strPath = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			strPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			appSdPath = strPath.endsWith(File.separator) ? strPath : strPath
					+ File.separator;
		}
		if (strPath == null) {
			strPath = "/mnt/sdcard/";
		}
		if (new File(strPath).exists()) {
			appSdPath = strPath;
			return appSdPath;
		} else {
			return null;
		}
	}

	public static String getAppDataPath(Activity activity) {
		String appDataPath = "";
		String sdPath = getAppSdPath();

		if (sdPath != null) {
			appDataPath = StringUtils.contactForPath(sdPath,
					activity.getPackageName(), Mc_String_DataPath);
		} else {
			appDataPath = getAppPath(activity);
		}

		return appDataPath;
	}

	/**
	 * 检查输入参数是否符合正则表达式
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isUri(String content) {
		// String regix =
		// "^(http|www|ftp|)?(://)?(//w+(-//w+)*)(//.(//w+(-//w+)*))*((://d+)?)(/(//w+(-//w+)*))*(//.?(//w)*)(//?)?(((//w*%)*(//w*//?)*(//w*:)*(//w*//+)*(//w*//.)*(//w*&)*(//w*-)*(//w*=)*(//w*%)*(//w*//?)*(//w*:)*(//w*//+)*(//w*//.)*(//w*&)*(//w*-)*(//w*=)*)*(//w*)*)$";
		// return Pattern.matches(regix, content);
		return !StringUtils.isEmpty(content);
	}

	
	
	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();

	public static String encode(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 0x0ff) << 8)
					| (((int) data[i + 2]) & 0x0ff);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);

			i += 3;

			if (n++ >= 14) {
				n = 0;
				buf.append(" ");
			}
		}

		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 255) << 8);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}

		return buf.toString();
	}

	private static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	public static byte[] decode(String s) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(s, bos);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		byte[] decodedBytes = bos.toByteArray();
		try {
			bos.close();
			bos = null;
		} catch (IOException ex) {
			System.err.println("Error while decoding BASE64: " + ex.toString());
		}
		return decodedBytes;
	}

	private static void decode(String s, OutputStream os) throws IOException {
		int i = 0;

		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;

			int tri = (decode(s.charAt(i)) << 18)
					+ (decode(s.charAt(i + 1)) << 12)
					+ (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));

			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);

			i += 4;
		}
	}
}
