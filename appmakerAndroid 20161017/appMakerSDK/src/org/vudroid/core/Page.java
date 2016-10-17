package org.vudroid.core;

import java.util.concurrent.locks.ReentrantLock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

class Page {
    final int index;
    RectF bounds;
    private Bitmap bitmap;
    private PageTreeNode node;
    private DocumentView documentView;
    //private final TextPaint textPaint = textPaint();
    private final Paint fillPaint = fillPaint();
    private final Paint strokePaint = strokePaint();
    
    private ReentrantLock lock = new ReentrantLock();

    Page(DocumentView documentView, int index) {
        this.documentView = documentView;
        this.index = index;
        node = new PageTreeNode(documentView, new RectF(0, 0, 1, 1), this, 1, null);
    }
    
    public void dispose()
    {
        lock.lock();
        try
        {
            if(bitmap == null)
                return;
            bitmap.recycle();
            bitmap = null;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public void setBitmap(Bitmap bitmap)
    {
        lock.lock();
        try
        {
            if(this.bitmap != null)
                this.bitmap.recycle();
            this.bitmap = bitmap;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public Bitmap getBitmap()
    {
        lock.lock();
        try
        {
            return bitmap;
        }
        finally
        {
            lock.unlock();
        }
    }

    private float aspectRatio;

    float getPageHeight(int mainWidth, float zoom) {
        return mainWidth / getAspectRatio() * zoom;
    }

    public int getTop() {
        return Math.round(bounds.top);
    }
    
    public int getBottom() {
        return Math.round(bounds.bottom);
    }

    public void draw(Canvas canvas) {
        if (!isVisible()) {
            return;
        }
        canvas.drawRect(bounds, fillPaint);

        //canvas.drawText("Page " + (index + 1), bounds.centerX(), bounds.centerY(), textPaint);
        node.draw(canvas);
        canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.top, strokePaint);
        canvas.drawLine(bounds.left, bounds.bottom, bounds.right, bounds.bottom, strokePaint);
    }

    private Paint strokePaint() {
        final Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        return strokePaint;
    }

    private Paint fillPaint() {
        final Paint fillPaint = new Paint();
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
        return fillPaint;
    }

    protected TextPaint textPaint() {
        final TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        if (this.aspectRatio != aspectRatio) {
            this.aspectRatio = aspectRatio;
            documentView.invalidatePageSizes();
        }
    }

    public boolean isVisible() {
        return documentView.pageIndex == index;
        //return RectF.intersects(documentView.getViewRect(), bounds);
    }

    public void setAspectRatio(int width, int height) {
        setAspectRatio(width * 1.0f / height);
    }

    void setBounds(RectF pageBounds) {
        bounds = pageBounds;
        node.invalidateNodeBounds();
    }

    public void updateVisibility() {
        node.updateVisibility();
    }

    public void invalidate() {
        node.invalidate();
    }
}
