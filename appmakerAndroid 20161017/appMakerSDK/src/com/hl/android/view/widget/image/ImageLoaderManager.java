package com.hl.android.view.widget.image;

public class ImageLoaderManager {
	private static ImageLoaderManager imageLoaderManager;

	public static ImageLoaderManager getInstance() {
		if (null == imageLoaderManager) {
			imageLoaderManager = new ImageLoaderManager();
		}

		return imageLoaderManager;
	}
}
