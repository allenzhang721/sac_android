package com.hl.android.book;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.hl.android.book.entity.Book;
import com.hl.android.book.entity.BookMarkEntity;
import com.hl.android.book.entity.ButtonEntity;
import com.hl.android.book.entity.SectionEntity;
import com.hl.android.book.entity.SnapshotEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;

public class BookXmlHandler implements ContentHandler {
	private String val = "";
	private String tagName = null;
	private boolean Ispages = false;
	private boolean IsSection = false;
	private boolean IsSnapshot = false;
	private boolean IsBookInfo = true;
	private boolean IsButton = false;
	private Book book;
	SectionEntity section = null;
	SnapshotEntity snaps = null;
	ButtonEntity button = null;
	BookMarkEntity mark = null;

	// private ArrayList<Button> buttons;

	BookXmlHandler(Book book) {
		this.book = book;
	}

	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		String temp = new String(arg0, arg1, arg2);
		val += temp;
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		this.tagName = arg1;
		val = val.replace(" ", "");
		if (IsBookInfo) {
			if (tagName.equals("ID")) {
				book.getBookInfo().setId(val);
			} else if (tagName.equals("Name")) {
				book.getBookInfo().setName(val);
			} else if (tagName.equals("BackgroundMusicId")) {
				book.getBookInfo().setBackgroundMusicId(val);
			} else if (tagName.equals("Description")) {
				book.getBookInfo().setDescription(val);
			} else if (tagName.equals("BookType")) {
				book.getBookInfo().setBookType(val);
			} else if (tagName.equals("DeviceType")) {
				book.getBookInfo().setDeviceType(val);
			} else if (tagName.equals("BookIconId")) {
				book.getBookInfo().setBookIconId(val);
			} else if (tagName.equals("ADType")) {
				book.getBookInfo().adType = Integer.parseInt(val);
			} else if (tagName.equals("ADPosition")) {
				book.getBookInfo().position = val;
			} else if (tagName.equals("BookFlipType")) {
				book.getBookInfo().bookFlipType = val;
			} else if (tagName.equals("BookNavType")) {
				book.getBookInfo().setBookNavType(val);
			} else if (tagName.equals("HomePageID")) {
				book.getBookInfo().homePageID = val;
			} else if (tagName.equals("BookWidth")) {
				book.getBookInfo().bookWidth = Integer.parseInt(val);
			} else if (tagName.equals("BookHeight")) {
				book.getBookInfo().bookHeight = Integer.parseInt(val);
			} else if (tagName.equals("StartPageTime")) {
				if (!val.equals("")) {
					book.getBookInfo().setStartPageTime(Double.valueOf(val));
				} else {
					book.getBookInfo().setStartPageTime(0);
				}
			} else if (tagName.equals("AndroidAdapterType")) {
				if ("KEEPSIZE_TRACTION".equals(val)) {
					// book.getBookInfo().setStartPageTime(0);
					HLSetting.FitScreen = true;
					BookSetting.FITSCREEN_TENSILE = false;
				} else if ("KEEPSIZE_SCALE".equals(val)) {
					HLSetting.FitScreen = false;
					BookSetting.FITSCREEN_TENSILE = false;
				} else if ("CHANGESIZE_TRACTION".equals(val)) {
					HLSetting.FitScreen = true;
					BookSetting.FITSCREEN_TENSILE = true;
				} else {
					HLSetting.FitScreen = true;
					BookSetting.FITSCREEN_TENSILE = true;
				}
			} else if (tagName.equals("IsLoadNavigation")) {
				if ("true".equals(val)) {
					BookSetting.IS_NO_NAVIGATION = false;
				} else {
					BookSetting.IS_NO_NAVIGATION = true;
				}
			}
			if (tagName.equals("IsFree")) {
				book.getBookInfo().isFree = Boolean.parseBoolean(val);
			}

		}
		if (Ispages) {
			if (tagName.equals("ID")) {
				book.getPages().add(val);
			}
		}
		if (IsSnapshot) {
			if (tagName.equals("SnapshotId")) {
				snaps.setId(val);
			} else if (tagName.equals("PageId")) {
				snaps.setPageID(val);
			} else if (tagName.equals("SnapshotWidth")) {
				snaps.setWidth(val);
			} else if (tagName.equals("SnapshotHeight")) {
				snaps.setHeight(val);
			}

		}
		if (tagName.equals("StartPageID")) {
			book.setStartPageID(val);
		}
		if (IsSection) {
			if (tagName.equals("ID")) {
				section.setID(val);
				section.bookID = book.getBookInfo().getId();

			} else if (tagName.equals("Name")) {
				section.setName(val);
			}
			if (tagName.equals("Page")) {
				if (tagName.equals("Page")) {
					section.getPages().add(val);
				}

			}
		}
		if (IsButton) {
			if (tagName.equals("X")) {
				button.setX(Float.valueOf(val));
			} else if (tagName.equals("Y")) {
				button.setY(Float.valueOf(val));
			} else if (tagName.equals("Width")) {
				button.setWidth(Float.valueOf(val).intValue());
			} else if (tagName.equals("Height")) {
				button.setHeight(Float.valueOf(val).intValue());
			} else if (tagName.equals("Type")) {
				button.setType(val);
			} else if (tagName.equals("isVisible")) {
				button.setVisible(Boolean.valueOf(val));
			} else if (tagName.equals("Source")) {
				button.setSource(val);
			} else if (tagName.equals("SelectedSource")) {
				button.setSelectedSource(val);
			}
		}
		if (HLSetting.IsHaveBookMark) {
			if (tagName.equals("IsShowBookMark")) {
				mark.setIsShowBookMark(val);
				if (val.equals("true")) {
					HLSetting.IsShowBookMark = true;
				} else {
					HLSetting.IsShowBookMark = false;
				}
			}
			if (tagName.equals("IsShowBookMarkLabel")) {
				if (val.equals("true")) {
					HLSetting.IsShowBookMarkLabel = true;
				} else {
					HLSetting.IsShowBookMarkLabel = false;

				}
				mark.setIsShowBookMarkLabel(val);
			}
			if (tagName.equals("BookMarkLablePositon")) {
				mark.setBookMarkLablePositon(val);
				HLSetting.BookMarkLablePositon = val;
			}
			if (tagName.equals("BookMarkLabelHorGap")) {
				mark.setBookMarkLabelHorGap(val);
				HLSetting.BookMarkLabelHorGap = Integer.valueOf(val);
			}
			if (tagName.equals("BookMarkLabelVerGap")) {
				mark.setBookMarkLabelVerGap(val);
				HLSetting.BookMarkLabelVerGap = Integer.valueOf(val);
			}
			if (tagName.equals("BookMarkLabelText")) {
				mark.setBookMarkLabelText(val);
				HLSetting.BookMarkLabelText = val;
			}
		} else {
			// HLSetting.IsShowBookMarkLabel = false;
			// HLSetting.BookMarkLablePositon = "";
			// HLSetting.BookMarkLabelHorGap = 0;
			// HLSetting.BookMarkLabelVerGap = 0;
			// HLSetting.BookMarkLabelText = "";
		}

		if (tagName.equals("Section")) {
			this.book.getSections().add(section);
		}
		if (tagName.equals("Snapshot")) {
			snaps.bookID = book.getBookInfo().getId();
			this.book.getSnapshots().add(snaps);
		}
		if (tagName.equals("Button")) {
			this.book.getButtons().add(button);
		}

		val = "";

	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(String arg0, String arg1, String arg2,
			Attributes arg3) throws SAXException {
		// TODO Auto-generated method stub
		this.tagName = arg1;
		if (tagName.equals("Snapshots")) {
			IsBookInfo = false;
			IsSnapshot = true;
			IsButton = false;
		}
		if (tagName.equals("Pages")) {
			IsSnapshot = false;
			Ispages = true;
			IsButton = false;
		}
		if (tagName.equals("Sections")) {
			Ispages = false;
			IsSection = true;
			IsButton = false;
		}

		if (tagName.equals("Section")) {
			section = new SectionEntity();
		}
		if (tagName.equals("Snapshot")) {
			snaps = new SnapshotEntity();
		}
		if (tagName.equals("Buttons")) {
			Ispages = false;
			IsSection = false;
			IsButton = true;
			// buttons = new ArrayList<Button>();
		}
		if (tagName.equals("Button")) {
			button = new ButtonEntity();
		}
		if (tagName.equals("BookMark")) {	
			mark = new BookMarkEntity();
			HLSetting.IsHaveBookMark = true;
		}
	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

}
