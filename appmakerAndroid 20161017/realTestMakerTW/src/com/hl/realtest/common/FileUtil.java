package com.hl.realtest.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileUtil {
	public static FileUtil fileUtil;
	// private Context mContext;
	private String bookStorePath;

	public FileUtil() {

	}

	public void init(Context context) {
		// mContext = context;
	}

	public static FileUtil getInstance() {
		if (fileUtil == null)
			fileUtil = new FileUtil();
		return fileUtil;
	}

	public AssetFileDescriptor getFileFD(Context con, String fileName) {
		AssetFileDescriptor assFD = null;
		try {
			assFD = con.getAssets().openFd(fileName);
		} catch (IOException e) {

		}
		return assFD;
	}

	public InputStream getFileInputStream(Context con, String fileName) {
		InputStream in = null;
		try {
			in = con.getAssets().open(fileName);

		} catch (IOException e) {

		}
		return in;
	}

	public InputStream getFileInputStream(String fileName) {
		File file = new File(fileName);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (Exception e) {
			return null;
		}
		return in;
	}

	public InputStream getFileInputStreamFilePath(String fileName) {
		File file = new File(fileName);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (Exception e) {
			return null;
		}
		return in;
	}

	public byte[] readFileToByteArray(Context con, String fileName)
			throws IOException {

		InputStream in = null;
		try {
			in = con.getAssets().open(fileName);
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			return buffer;
		} catch (Exception e) {
			// e.printStackTrace();
			Log.e("hl", fileName + " not exists");
			return null;
		}
	}

	public static String inputStream2String(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	public static byte[] readFileToByteArray(String fileName)
			throws IOException {
		File file = new File(fileName);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (Exception e) {
			return null;
		}

		ByteArrayOutputStream bouts = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
			bouts.write(buf, 0, len);
		}
		byte[] data = bouts.toByteArray();
		in.close();
		bouts.close();
		return data;
	}

	public Bitmap load(String localSourceID, int width, int height,
			Context context) {
		Bitmap bitmap = null;
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inTempStorage = new byte[32 * 1024];

		InputStream is = null;

		try {

			try {

				is = FileUtil.getInstance().getFileInputStream(context,
						localSourceID);
			} catch (OutOfMemoryError e) {

			}

			bitmap = BitmapFactory.decodeStream(is, null, _option);
		} catch (Exception e) {
			return null;
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 2;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
			return bitmap;
		}

		Bitmap resizeBmp = Bitmap.createScaledBitmap(bitmap, width, height,
				true);
		return resizeBmp;
	}

	/**
	 * 拷贝资源文件到sdcard 根目录
	 * 
	 * @param con
	 * @param fileName
	 */
	public String getDataFilePath(Context con, String fileName) {
		for (String name : con.getFilesDir().list()) {
			if (name.contains(fileName)) {
				return con.getFilesDir().getAbsolutePath() + "/" + fileName;
			}

		}
		return null;
	}

	public File getDataFile(Context con, String fileName) {
		for (String name : con.getFilesDir().list()) {
			if (name.contains(fileName)) {
				return new File(con.getFilesDir().getAbsolutePath() + "/"
						+ fileName);
			}

		}
		return null;
	}

	public void copyFileToData(Context con, String fileName) {
		InputStream in;
		try {
			for (String name : con.getFilesDir().list()) {
				if (name.contains(fileName)) {
					return;
				}
			}

			in = FileUtil.getInstance().getFileInputStream(con, fileName);
			FileOutputStream outputStream = con.openFileOutput(fileName,
					Activity.MODE_PRIVATE);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			in.close();
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static boolean isExist(String filePath) {

		try {

			File file = new File(filePath);
			return file.exists();

		} catch (OutOfMemoryError e) {

		}
		return false;
	}

	public static void deleteFile(String filePath) {
		try {
			del(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getBookStorePath() {
		return bookStorePath;
	}

	public void setBookStorePath(String bookStorePath) {
		this.bookStorePath = bookStorePath;
	}

	public static void del(String filepath) throws IOException {
		File f = new File(filepath);
		if (f.exists() && f.isDirectory()) {
			if (f.listFiles().length == 0) {
				f.delete();
			} else {
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						del(delFile[j].getAbsolutePath());
					}
					delFile[j].delete();
				}
			}
		}
	}
}
