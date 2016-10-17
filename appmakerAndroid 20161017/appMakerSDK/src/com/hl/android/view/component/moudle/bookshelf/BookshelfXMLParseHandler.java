package com.hl.android.view.component.moudle.bookshelf;

import java.io.File;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Environment;

import com.hl.android.controller.BookController;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.core.utils.WebUtils;
/**
 * 解析解析书架组件的xml处理器
 * @author zhaoq
 *
 */
public class BookshelfXMLParseHandler extends DefaultHandler {
	private List<ShelvesBook> books;
	private String val;
	private ShelvesBook book;
	public BookshelfXMLParseHandler(List<ShelvesBook> books) {
		this.books = books;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		val = null;
		if (localName.equalsIgnoreCase("BOOK")) {
			book = new ShelvesBook();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		val = new String(ch, start, length);
	}
	
	/**
	 * 设置书架中书籍的根路径
	 * 同时创建书籍的根路径
	 * 使用书籍的域名，同一个网络路径就用同一个本地路径
	 * @param componentID
	 * @param book
	 * @return
	 */
	private void setBookDataPath(ShelvesBook book){
		String valueUrl = book.mBookUrl;
		if(StringUtils.isEmpty(valueUrl))return;
		book.mLocalPath = parseURL2LocalData(valueUrl);
	}

	private String parseURL2LocalData(String valueUrl) {
		String shelvesRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"
				+ BookController.getInstance().hlActivity.getPackageName()
				+ "/shelves/";
		int startIndex = valueUrl.indexOf("//")+2;
		if(startIndex<0)startIndex = 0;
		int endIndex = valueUrl.lastIndexOf(".");
		if(endIndex < 0)endIndex =valueUrl.length(); 
		
		String foldPath =shelvesRootPath +  valueUrl.substring(startIndex, endIndex);
		foldPath +="/";
		File bookDir = new File(foldPath);
		if(!bookDir.exists()){
			bookDir.mkdirs();
		}
		return foldPath;
	}
	
	private String parseURL2LocalCover(String valueUrl) {
		String shelvesRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"
				+ BookController.getInstance().hlActivity.getPackageName()
				+ "/shelves/";
		int startIndex = valueUrl.indexOf("//")+2;
		if(startIndex<0)startIndex = 0;
		int endIndex = valueUrl.lastIndexOf("/");
		if(endIndex < 0)endIndex =valueUrl.length(); 
		
		String foldPath =shelvesRootPath +  valueUrl.substring(startIndex, endIndex);
		foldPath +="/";
		File bookDir = new File(foldPath);
		if(!bookDir.exists()){
			bookDir.mkdirs();
		}
		return shelvesRootPath + valueUrl.substring(startIndex);
	}
	/**
	 * 设置书架中书皮的本地路径
	 * 路径有一个统一的前缀 包名 + shelves
	 * 结尾时网络路径去掉http://
	 * @param book
	 * @return
	 */
	private void setBookCoverath(ShelvesBook book){
		String valueUrl = book.mCoverUrl;
		if(StringUtils.isEmpty(valueUrl))return;
		book.mCoverPath = parseURL2LocalCover(valueUrl);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase("BOOKID")) {
			book.mBookID = val;
		} else if (localName.equalsIgnoreCase("COVERURL")) {
			book.mCoverUrl = val;
		} else if (localName.equalsIgnoreCase("BOOKURL")) {
			book.mBookUrl = val;
		} else if (localName.equalsIgnoreCase("VERSION")) {
			book.version = val;
		} else if (localName.equalsIgnoreCase("BOOK")) {
			books.add(book);
			setBookDataPath(book);
			setBookCoverath(book);
			downloadBitmap(book);
		}
	}
	
	private void downloadBitmap(ShelvesBook book) {
		
		if(new File(book.mCoverPath).exists()){
		}else{
			if(WebUtils.isConnectingToInternet(BookController.getInstance().hlActivity))
				WebUtils.downLoadResource(book.mCoverUrl, book.mCoverPath);
		}
	}
}
