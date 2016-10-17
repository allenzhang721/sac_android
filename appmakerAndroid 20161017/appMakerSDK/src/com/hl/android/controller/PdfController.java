package com.hl.android.controller;

import com.artifex.mupdf.MuPDFCore;

public class PdfController {
	private static PdfController pdfController;
	public String documentName;
	public MuPDFCore muPDFCore;

	public static PdfController getInstance() {
		if (null == pdfController) {
			pdfController = new PdfController();
		}

		return pdfController;
	}

	public void openFile(String path) {
		if (null != muPDFCore && null != documentName
				&& this.documentName.equals(path)) {
			return;
		} else {
			destroy();
		}
		this.documentName = path;

		try {
			muPDFCore = new MuPDFCore(path);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void destroy() {
		if (null != muPDFCore) {
			this.muPDFCore.onDestroy();
			this.muPDFCore = null;
		}

	}
}
