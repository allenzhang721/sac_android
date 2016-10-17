package com.hl.android.book;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.hl.android.book.entity.Book;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.core.utils.StringUtils;

public class BookDecoder {
	public static BookDecoder bookDecoder;
	private HashMap<String, BookIndex> itemMap = new HashMap<String, BookIndex>();
//	private byte[] data;
//	private int lenD;

	public static BookDecoder getInstance() {
		if (null == bookDecoder) {
			bookDecoder = new BookDecoder();
		}

		return bookDecoder;
	}

	/**
	 * 加载书籍的book.xml的信息
	 * @param activity  
	 * @param xmlName  书籍的配置信息文件名字
	 * @return
	 */
	public Book decode(Activity activity,String xmlName){
		InputStream bookis = FileUtils.getInstance().getFileInputStream(activity,xmlName);
		return BookDecoder.getInstance().decode(bookis);
	}
	public Book decode(InputStream xmlStream) {
		if (null == xmlStream) {
			return null;
		}
		Book book = new Book();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			BookXmlHandler xmlHandler = new BookXmlHandler(book);
			reader.setContentHandler(xmlHandler);// set content hander
			reader.parse(new InputSource(xmlStream));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}finally{
			try {
				xmlStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return book;
	}

	private PageEntity decodePageEntity(InputStream xmlStream) {
		if (null == xmlStream) {
			return null;
		}
		PageEntity page = new PageEntity();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			PageXmlHandler xmlHandler = new PageXmlHandler(page);
			reader.setContentHandler(xmlHandler);// set content hander
			reader.parse(new InputSource(xmlStream));
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			if(xmlStream!=null){
				try {
					xmlStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return page;
	}

	/**
	 * parse page
	 * @param context
	 * @param fileID
	 * @return
	 */
	public PageEntity decodePageEntity(Context context, String fileID) {
		BookIndex bi = itemMap.get(fileID);
		if(bi == null)return null;
		FileUtils.getInstance();
		InputStream pageIS = FileUtils.readBookPage(BookSetting.fileName,bi.getStart(),bi.getEnd());
		 
		if(pageIS == null){
			BookSetting.fileName="book.dat";
			pageIS = FileUtils.readBookPage(BookSetting.fileName,bi.getStart(),bi.getEnd());
		}
//		if(pageIS == null){
//			BookSetting.fileName="book.jpg";
//			pageIS = FileUtils.readBookPage(BookSetting.fileName,bi.getStart(),bi.getEnd());
//		}
		if(pageIS == null){
			Log.i("hl","解析index字符串 返回为空");
			return null;
		}
		PageEntity pe =  decodePageEntity(pageIS);
		return pe;
	}
	/**
	 * 初始化book的index信息
	 */
	public void initBookItemList() {
//		String indexStr = FileUtils.readBookIndex("book.jpg");
//		if(StringUtils.isEmpty(indexStr)){
//			indexStr = FileUtils.readBookIndex("book.dat");
//		}
		String indexStr= FileUtils.readBookIndex("book.dat");
		if(StringUtils.isEmpty(indexStr)){
			Log.i("hl","解析index字符串 返回为空");
			return;
		}
		StringReader sr = new StringReader(indexStr);
		parseXmlForBookIndex(sr);
	}

	public static int fromArray(byte[] payload) {
		ByteBuffer buffer = ByteBuffer.wrap(payload);
		buffer.order(ByteOrder.BIG_ENDIAN);
		return buffer.getInt();
	}

//	public InputStream getInputStream(String fileID) {
//		BookIndex bi = itemMap.get(fileID);
//		if(bi == null)return null;
//		int len = bi.getEnd() - bi.getStart();
//
//		byte[] dd = null;
//		try {
//			dd = FileUtils.readFileToByteArray("book.dat",bi.getStart()+startPageIndex,bi.getEnd()+startPageIndex);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(dd == null){
//			try {
//				dd = FileUtils.readFileToByteArray("book.jpg",bi.getStart()+startPageIndex,bi.getEnd()+startPageIndex);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		//System.arraycopy(data, lenD + bi.getStart(), dd, 0, len);
//		
//		InputStream is = new ByteArrayInputStream(dd);
//		return is;
//	}

	/**
	 * 解析字符串
	 * @param inStream
	 */
	private void parseXmlForBookIndex(StringReader inStream) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(new InputSource(inStream));
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("xmlIndex");// 查找所有person节点
			for (int i = 0; i < items.getLength(); i++) {
				BookIndex bookIndex = new BookIndex();
				Element personNode = (Element) items.item(i);
				NodeList childsNodes = personNode.getChildNodes();
				for (int j = 0; j < childsNodes.getLength(); j++) {
					Node node = (Node) childsNodes.item(j); // 判断是否为元素类型
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element childNode = (Element) node;
						if ("id".equals(childNode.getNodeName())) {
							bookIndex.setItemID(childNode.getFirstChild()
									.getNodeValue());
						} else if ("startnumber"
								.equals(childNode.getNodeName())) {
							bookIndex.setStart(Integer.valueOf(childNode
									.getFirstChild().getNodeValue()));
						} else if ("endnumber".equals(childNode.getNodeName())) {
							bookIndex.setEnd(Integer.valueOf(childNode
									.getFirstChild().getNodeValue()));

						}
					}
				}
				itemMap.put(bookIndex.getItemID(), bookIndex);
			}

		} catch (Exception e) {
			Log.e("hl","初始化bookindex 信息出错，出错原因字符内容不正确",e);
		}finally{
			inStream.close();
		}

	}
}
