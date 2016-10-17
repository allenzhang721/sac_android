package com.hl.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

/**
 * 中文名称 <br>
 * <br>
 * 简略说明功能作用 <br>
 * <br>
 * 详细说明使用方法以及功能描述。<br>
 * <br>
 * 
 * @author zhaoq
 * @LastModified 2011-10-21
 */
public class FileUtils {

	/**
	 * 读取指定文件的内容，以字符串形式返回。
	 * 
	 * @param in
	 *            指定要读取的文件
	 * */
	public static String getFileContent(File in) {
		try {
			return getStreamContent(new FileInputStream(in));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * 读取指定文件流的内容，以字符串形式返回。
	 * 
	 * @param stream
	 *            指定要读取的文件流
	 * */
	public static String getStreamContent(InputStream stream) {
		StringBuffer sbContent = new StringBuffer();
		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					stream, "UTF-8"));
			String strContentLine = bfr.readLine();
			while (strContentLine != null) {
				sbContent.append(strContentLine);
				strContentLine = bfr.readLine();
			}
		} catch (Exception e) {
			Log.e("hl", "读取文件内容时出错！", e);
			return null;
		}
		return sbContent.toString();
	}

	/**
	 * 复制单个文件
	 * 
	 * @param inStream
	 *            准备复制的文件来源
	 * @param newPathFile
	 *            拷贝到新绝对路径带文件名
	 */
	public static boolean copyFile(InputStream inStream, String newPathFile) {
		if (inStream == null) {
			return false;
		}
		boolean result = false;
		try {
			int byteread = 0;
			// 检查新文件是否存在
			File outFile = new File(newPathFile);
			if (!outFile.exists()) {
				if (outFile.getParentFile() != null
						&& !outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}
				outFile.createNewFile();
			}
			FileOutputStream fs = new FileOutputStream(outFile, false);
			byte[] buffer = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.flush();
			fs.close();
			result = true;
		} catch (Exception e) {
			Log.e("MarsorErroring", "复制单个文件时出错！文件：" + inStream + ",新路径："
					+ newPathFile, e);
		}
		return result;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPathFile
	 *            准备复制的文件源
	 * @param newPathFile
	 *            拷贝到新绝对路径带文件名
	 */
	public static boolean copyFile(String oldPathFile, String newPathFile)
			throws Exception {
		boolean result = false;
		try {
			File oldfile = new File(oldPathFile);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
				result = copyFile(inStream, newPathFile);
			} else {
				result = false;
			}
		} catch (Exception e) {
			Log.e("MarsorErroring", "复制单个文件时出错！文件名：" + oldPathFile + ",新路径："
					+ newPathFile, e);
			throw e;
		}
		return result;
	}

	/**
	 * 复制整个文件夹的内容
	 * 
	 * @param oldPath
	 *            准备拷贝的目录
	 * @param newPath
	 *            指定绝对路径的新目录
	 */
	public static boolean copyFolder(String oldPath, String newPath)
			throws Exception {
		boolean result = false;
		try {
			new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 2];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
			result = true;
		} catch (Exception e) {
			Log.e("hl", "复制整个文件夹内容时出错！原文件夹：" + oldPath + ",新文件夹：" + newPath,
					e);
			throw e;
		}
		return result;
	}

	/**
	 * 设置文件权限。如果是文件，设置文件本身的权限，如果是文件夹针对文件夹下的所有文件（包括子文件夹和该文件夹本身），设置权限。<br>
	 * 
	 * @param file
	 *            需要设置权限的文件或者文件夹
	 * @param permission
	 *            待设置的文件或者文件夹的权限描述（Linux 777，等，如果文件系统不支持，不会抛出错误）
	 * @count 每次设置权限的文件数量，越多，速度越快，但是如果任何一个文件没有权限，可能后面的文件就不会被设置。
	 * */
	public static void setupFilePermission(File file, String permission,
			int... count) {
		// 如果文件不存在，返回
		if (!file.exists()) {
			return;
		}
		// 如果文件权限没有设置，不处理
		if (permission == null || permission.trim().length() == 0) {
			return;
		}
		int originSize = 2;
		if (count != null && count.length > 0) {
			originSize = count[0];
		}
		int size = originSize;
		ArrayList<String> list = new ArrayList<String>();
		getAllFileNames(file, list);

		String params = "";
		for (String str : list) {
			params = params + " " + str;
			size--;
			if (size == 0) {
				runCommand("chmod " + permission + " " + params, false);
				size = originSize;
				params = "";
			}
		}
		if (params.trim().length() != 0) {
			runCommand("chmod " + permission + " " + params, false);
		}
	}

	/**
	 * 执行命令（android系统环境下）
	 * */
	public static void runCommand(String command, boolean wait) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			if (wait) {
				process.waitFor();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 取得指定文件夹下的所有文件名称。（包括文件夹）
	 * 
	 * @param file
	 *            指定的文件或者文件夹
	 * @param arrayList
	 *            结果集
	 * */
	public static void getAllFileNames(File file, ArrayList<String> arrayList) {
		// 如果文件不存在，返回
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File subFile : files) {
					if (subFile.isDirectory()) {
						getAllFileNames(subFile, arrayList);
					} else {
						arrayList.add(subFile.getAbsolutePath());
					}
				}
			}
		} else {
			arrayList.add(file.getAbsolutePath());
		}
	}

	/**
	 * 获取子文件夹列表
	 * 
	 * @param path
	 *            文件目录
	 * @param suffix
	 *            文件类型，如果不传则默认查询全部
	 * @return
	 */
	public static ArrayList<File> getMyChildFileNames(String path,
			String... suffix) {
		ArrayList<File> fileList = new ArrayList<File>();
		String type = "";
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (suffix.length > 0) {
				type = suffix[0];
			}
			for (File item : files) {
				if (item.getName().endsWith(type)) {
					fileList.add(item);
				}
			}
		} else {
			return null;
		}
		return fileList;
	};

	/**
	 * 根据指定的路径（相对于assets文件夹），获取对应文件的输入流。
	 * 
	 * @param 文件的路径
	 *            ，例如：book/keywords/surprise.xml
	 * @return 对应文件的输入流,文件不存在，返回空值
	 */
	public static InputStream getAssetsInputStream(Activity activity,
			String fileName) {
		try {
			if (AlgorithmicUtils.isEmpty(fileName)
					|| AlgorithmicUtils.isEmpty(activity)) {
				return null;
			}
			InputStream in = activity.getResources().getAssets().open(fileName);
			return in;

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据指定的路径（相对于assets文件夹），获取该路径下的所有文件名称（短文件名）。
	 * 
	 * @param 文件的路径
	 *            ，例如：book/keywords/
	 * @return 文件名列表，例如：[surprise.xml,keywords.xml]
	 */
	public static String[] getAssetsFileNames(Activity activity, String dirPath) {
		try {
			if (AlgorithmicUtils.isEmpty(dirPath)
					|| AlgorithmicUtils.isEmpty(activity)) {
				return null;
			}
			// android文件夹路径不能以 ”/“结尾，否则无法列出东西来
			String[] result = activity.getResources().getAssets()
					.list(dirPath.replaceAll("/$", ""));
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            文件夹完整绝对路径
	 */
	public static boolean delFolder(String folderPath) {
		boolean result = false;
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
			result = true;
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 删除指定文件夹下所有文件
	 * 
	 * @param path
	 *            文件夹完整绝对路径
	 * @return boolean 是否删除成功
	 */
	public static boolean delAllFile(String path) {

		boolean bea = false;
		File file = new File(path);
		if (!file.exists()) {
			return bea;
		}
		if (!file.isDirectory()) {
			return bea;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				bea = true;
			}
		}
		return bea;
	}

	/**
	 * 写文件
	 * 
	 * @param txtFile
	 * @param message
	 * @return
	 */
	public static void writeTxtFile(String txtFile, String message) {
		try {
			File f = new File(txtFile);
			if (!f.exists()) {
				f.createNewFile();// 不存在则创建
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(message);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
