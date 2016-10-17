package com.hl.android.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.hl.android.core.utils.WebUtils.CallBack;

/**
 * 处理http请求的类
 * 
 * @author zhaoq
 * 
 */
public class WebUtils {

	public static StringBuffer errorMessage = new StringBuffer();
	// 不允许实例化
	private WebUtils() {
	}

	public static boolean isConnectingToInternet(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
		}
		return false;
	}

	public static String downLoadResource(String urlPath, String localPath) {
		if (new File(localPath).exists()) {
			return localPath;
		}
		// 是不是要自动重定向，一定要，因为我的图片可能不存在apache服务器上，不转发怎么行
		HttpURLConnection.setFollowRedirects(true);
		URL url;
		try {
			url = new URL(urlPath);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setReadTimeout(5000);
			connection.setRequestMethod("GET");
			// 有些网站不允许没有浏览器信息的请求，故添加浏览器信息
			connection.setRequestProperty("User-Agent",
					"Mozilla/4.0(compatible;MSIE7.0;windows NT 5)");
			// // 有些网站可能返回的是一个xml文件，这时候需要指定内容类型
			connection.setRequestProperty("Content-Type", "text/html");

			Log.i("hl", "正在下载资源：" + urlPath);
			// 如果网络地址上存在这个文件，直接下载，如果不存在，返回false，下载失败
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();

				File output = new File(localPath).getAbsoluteFile();
				if (!output.exists()) {
					File parent = output.getParentFile();
					if (parent != null) {
						parent.mkdirs();
					}
					output.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(output, false);
				byte[] data = new byte[1024];
				int read = 0;
				while ((read = in.read(data)) != -1) {
					out.write(data, 0, read);
				}
				out.flush();
				out.close();
				in.close();
				connection.disconnect();
				StringBuffer sb = new StringBuffer("下载完毕");
				sb.append("/n 下载的网址是");
				sb.append(urlPath);
				sb.append("/n 下载的文件路径是");
				sb.append(localPath);
				Log.i("hl",sb.toString());
				
				return localPath;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
		StringBuffer sb = new StringBuffer("下载失败");
		sb.append("/n 下载的网址是");
		sb.append(urlPath);
		sb.append("/n 下载的文件路径是");
		sb.append(localPath);
		Log.i("hl",sb.toString());
		return "";
	}

	public static String getUrlContent(String urlPath, String encoding) {
		
		HttpURLConnection.setFollowRedirects(true);
		// 网址
		URL url = null;
		// 连接
		HttpURLConnection con = null;

		String strEncoding = null;
		try {
			// 去掉URL中的空格
			int index = 0;
			while ((index = urlPath.indexOf(" ")) >= 0) {
				urlPath = urlPath.substring(0, index) + "%20"
						+ urlPath.substring(index + 1);
			}

			url = new URL(urlPath);

			// 使用指定的代理服务器打开连接
			con = (HttpURLConnection) url.openConnection();
			// 有些网站不允许没有浏览器信息的请求，故添加浏览器信息
			con.setRequestProperty("User-Agent",
					"Mozilla/4.0(compatible;MSIE7.0;windows NT 5)");
			// // 有些网站可能返回的是一个xml文件，这时候需要指定内容类型
			con.setRequestProperty("Content-Type", "text/html");
			// 设置超时时长。
			con.setConnectTimeout(500);
			con.connect();
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.i("hl", "获取网址页面内容失败时返回值为：" + con.getResponseCode()
						+ ",网址为：" + urlPath);
				con.disconnect();
				con = null;
				return null;
			}
			// 取得字符编码
			String strContentType = con.getContentType();
			if (null != strContentType && strContentType.trim().length() != 0
					&& strContentType.indexOf("charset=") > 0) {
				strEncoding = strContentType.substring(strContentType
						.indexOf("charset=") + 8);
			} else {
				strEncoding = con.getContentEncoding();
			}

			StringBuffer sbContent = new StringBuffer();
			// 按照字符编码读取数据（如果字符编码为空，尝试从内容中搜索字符编码）
			BufferedReader bf = null;
			if (strEncoding == null || strEncoding.trim().length() == 0) {
				strEncoding = encoding;
			}
			bf = new BufferedReader(new InputStreamReader(con.getInputStream(),
					strEncoding));

			String strContentLine = bf.readLine();
			while (strContentLine != null) {
				sbContent.append(strContentLine);
				strContentLine = bf.readLine();
			}
			String strResult = sbContent.toString();
			url = null;
			con = null;
			StringBuffer sb = new StringBuffer("下载");
			
			if(StringUtils.isEmpty(strResult)){
				sb.append("成功");
			}else{
				sb.append("失败");
			}
			sb.append("/n 下载的网址是");
			sb.append(urlPath);
			Log.i("hl",sb.toString());
			
			return strResult;

		} catch (Exception e) {
			Log.e("hl", "获取网页内容时发生错误,网页地址是:" + urlPath);
			return "";
		}
	}

	/**
	 * 获取指定URL的内容，get方式请求数据。
	 * 
	 * @param strUrl
	 *            要获取内容的URL
	 * @param encoding
	 *            获取内容时，解析字符串时使用的编码方式
	 * */
	public static void getUrlContent(String strUrl, String encoding, CallBack c) {
		new GetUrlContentTask(strUrl, encoding, c).execute();
	}

	/**
	 * 从网络地址下载指定的资源，并且保存到指定的本地路径
	 * 
	 * @param resUrl
	 *            资源的URL
	 * @param localPath
	 *            资源下载完毕后，保存的路径（带有文件名的全路径）
	 * */
	public static void downloadWebResource(String resUrl, String localPath,
			CallBack c) throws Exception {

		new DownAsyncTask(resUrl, localPath, c).execute();
	}

	public interface CallBack {
		public void successAction(String msg);

		public void failAction(String msg);
	}

	/**
	 * 使用HttpClinet向指定的URL发送一个Post请求，且传递过去一些参数。
	 * @param postUrl 指定的URL
	 * @param formData 参数信息
	 * */
	public static String postDataToUrl(String postUrl, HashMap<String, String> formData) {
		String result = "";
		try {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for(String strName : formData.keySet()) {
				BasicNameValuePair nameValue= new BasicNameValuePair(strName, formData.get(strName));
				list.add(nameValue);
			}
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"UTF-8"); 
			HttpPost post = new HttpPost(postUrl); 
			post.setEntity(entity); 
	        HttpParams httpParameters = new BasicHttpParams();
	        HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(post); 
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode == HttpStatus.SC_OK) {
				StringBuffer contentBuffer = new StringBuffer();
				InputStream in = response.getEntity().getContent();
				
				String encoding = response.getEntity().getContentEncoding()==null?"utf-8":response.getEntity().getContentEncoding().getValue();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
				String inputLine = null;
				while ((inputLine = reader.readLine()) != null) {
					contentBuffer.append(inputLine);
					contentBuffer.append("\r\n");
				}
				in.close();
				result = contentBuffer.toString();
			} else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					return getUrlContent(location, "utf-8");
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

class GetUrlContentTask extends AsyncTask<String, String, String> {
	private String mUrl;
	private CallBack mC;
	private String mEncoding;

	public GetUrlContentTask(String url, String encoding, CallBack c) {
		this.mUrl = url;
		this.mC = c;
		this.mEncoding = encoding;
	}

	@Override
	protected String doInBackground(String... params) {

		return WebUtils.getUrlContent(mUrl, mEncoding);
	}

	@Override
	protected void onPostExecute(String result) {
		if(mC == null)return;
		if (StringUtils.isEmpty(result)) {
			mC.failAction(result);
		} else {
			mC.successAction(result);
		}
	}
}

class DownAsyncTask extends AsyncTask<String, String, String> {
	private String mUrl;
	private String mLocalUrl;
	private CallBack mC;

	public DownAsyncTask(String url, String localUrl, CallBack c) {
		this.mUrl = url;
		this.mLocalUrl = localUrl;
		this.mC = c;
	}

	@Override
	protected String doInBackground(String... params) {
		return WebUtils.downLoadResource(mUrl, mLocalUrl);
	}

	@Override
	protected void onPostExecute(String result) {
		mC.successAction(result);
	}

}
