package org.vudroid.core;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.View;

public interface DecodeService
{

    void setContainerView(View containerView);

    void open(String filePath);

    void decodePage(Object decodeKey, int pageNum, final DecodeCallback decodeCallback);
    
    void decodePage(Object decodeKey, int pageNum, DecodeCallback decodeCallback, float zoom, RectF pageSliceBounds);

    void stopDecoding(Object decodeKey);

    int getEffectivePagesWidth();

    int getEffectivePagesHeight();
    
    int getEffectivePagesWidth(int index);

    int getEffectivePagesHeight(int index);

    int getPageCount();

    int getPageWidth(int pageIndex);

    int getPageHeight(int pageIndex);
    
    Bitmap getBitmap(int pageIndex, int width, int height);

    void recycle();

    public interface DecodeCallback
    {
        void decodeComplete(Bitmap bitmap, float zoom);
    }
}
