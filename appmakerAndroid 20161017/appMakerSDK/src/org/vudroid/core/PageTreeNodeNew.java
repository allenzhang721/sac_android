package org.vudroid.core;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

class PageTreeNodeNew {
    private static final int SLICE_SIZE = 65535;
    private Bitmap bitmap;
    private SoftReference<Bitmap> bitmapWeakReference;
    private boolean decodingNow;
    private final RectF pageSliceBounds;
    private final PageNew page;
    private PageTreeNodeNew[] children;
    private final int treeNodeDepthLevel;
    private Matrix matrix = new Matrix();
    private final Paint bitmapPaint = new Paint();
    private DocumentViewNew documentView;
    private boolean invalidateFlag;
    private Rect targetRect;
    private RectF targetRectF;

    public PageTreeNodeNew(DocumentViewNew documentView, RectF localPageSliceBounds, PageNew page, int treeNodeDepthLevel, PageTreeNodeNew parent) {
        this.documentView = documentView;
        this.pageSliceBounds = evaluatePageSliceBounds(localPageSliceBounds, parent);
        this.page = page;
        this.treeNodeDepthLevel = treeNodeDepthLevel;
    }

    public void updateVisibility() {
        invalidateChildren();
        if (children != null) {
            for (PageTreeNodeNew child : children) {
                child.updateVisibility();
            }
        }
        if (isVisible()) {
            if (!thresholdHit()) {
                if (getBitmap() != null && !invalidateFlag) {
                    restoreBitmapReference();
                } else {
                    float zoom = documentView.zoomModel.getZoom();
                    if(zoom != 1.0f)
                        decodePageTreeNode();
                }
            }
        }
        if (!isVisibleAndNotHiddenByChildren()) {
            stopDecodingThisNode();
            setBitmap(null);
        }
    }

    public void invalidate() {
        invalidateChildren();
        invalidateRecursive();
        updateVisibility();
    }

    private void invalidateRecursive() {
        invalidateFlag = true;
        if (children != null) {
            for (PageTreeNodeNew child : children) {
                child.invalidateRecursive();
            }
        }
        stopDecodingThisNode();
    }

    void invalidateNodeBounds() {
        targetRect = null;
        targetRectF = null;
        if (children != null) {
            for (PageTreeNodeNew child : children) {
                child.invalidateNodeBounds();
            }
        }
    }


    void draw(Canvas canvas) {
        Rect tr = getTargetRect();
        Bitmap pbm = page.getBitmap();
        float zoom = documentView.zoomModel.getZoom();

        if (getBitmap() != null) {
            Rect sr = new Rect(0, 0, getBitmap().getWidth(), getBitmap().getHeight());
            canvas.drawBitmap(getBitmap(), sr, tr, bitmapPaint);
        }
        else if(pbm != null){
            Rect sr;
            if(zoom > 1.0f){
                sr = new Rect((int)(tr.left / zoom + 0.5),
                              (int)((tr.top - page.bounds.top) / zoom + 0.5),
                              (int)(tr.right / zoom + 0.5),
                              (int)((tr.bottom - page.bounds.top) / zoom + 0.5));
                
            }
            else
                sr = new Rect(tr.left, (int)(tr.top - page.bounds.top), tr.right, (int)(tr.bottom - page.bounds.top));
                canvas.drawBitmap(pbm, sr, tr, bitmapPaint);
        }
        if (children == null) {
            return;
        }
        for (PageTreeNodeNew child : children) {
            child.draw(canvas);
        }
    }

    private boolean isVisible() {
        boolean isVisible = page.isVisible();
        if(!isVisible)
            return false;
        return RectF.intersects(documentView.getViewRect(), getTargetRectF());
    }

    private RectF getTargetRectF() {
        if (targetRectF == null) {
            targetRectF = new RectF(getTargetRect());
        }
        return targetRectF;
    }

    private void invalidateChildren() {
        if (thresholdHit() && children == null && isVisible()) {
            final int newThreshold = treeNodeDepthLevel * 2;
            children = new PageTreeNodeNew[]
                    {
                            new PageTreeNodeNew(documentView, new RectF(0, 0, 0.5f, 0.5f), page, newThreshold, this),
                            new PageTreeNodeNew(documentView, new RectF(0.5f, 0, 1.0f, 0.5f), page, newThreshold, this),
                            new PageTreeNodeNew(documentView, new RectF(0, 0.5f, 0.5f, 1.0f), page, newThreshold, this),
                            new PageTreeNodeNew(documentView, new RectF(0.5f, 0.5f, 1.0f, 1.0f), page, newThreshold, this)
                    };
        }
        if (!thresholdHit() && getBitmap() != null || !isVisible()) {
            recycleChildren();
        }
    }

    private boolean thresholdHit() {
        float zoom = documentView.zoomModel.getZoom();
        int mainWidth = documentView.getWidth();
        float height = page.getPageHeight(mainWidth, zoom);
        return (mainWidth * zoom * height) / (treeNodeDepthLevel * treeNodeDepthLevel) > SLICE_SIZE;
    }

    public Bitmap getBitmap() {
        return bitmapWeakReference != null ? bitmapWeakReference.get() : null;
    }

    private void restoreBitmapReference() {
        setBitmap(getBitmap());
    }

    private void decodePageTreeNode() {
        if (isDecodingNow()) {
            return;
        }
        setDecodingNow(true);
        documentView.decodeService.decodePage(this, page.index, new DecodeService.DecodeCallback() {
            public void decodeComplete(final Bitmap bitmap, final float zoom) {
                documentView.post(new Runnable() {
                    public void run() {
                        if(documentView.zoomModel.getZoom() != zoom)
                            setBitmap(null);
                        else
                            setBitmap(bitmap);
                        invalidateFlag = false;
                        setDecodingNow(false);
                        page.setAspectRatio(documentView.decodeService.getPageWidth(page.index), documentView.decodeService.getPageHeight(page.index));
                        invalidateChildren();
                    }
                });
            }
        }, documentView.zoomModel.getZoom(), pageSliceBounds);
    }

    private RectF evaluatePageSliceBounds(RectF localPageSliceBounds, PageTreeNodeNew parent) {
        if (parent == null) {
            return localPageSliceBounds;
        }
        final Matrix matrix = new Matrix();
        matrix.postScale(parent.pageSliceBounds.width(), parent.pageSliceBounds.height());
        matrix.postTranslate(parent.pageSliceBounds.left, parent.pageSliceBounds.top);
        final RectF sliceBounds = new RectF();
        matrix.mapRect(sliceBounds, localPageSliceBounds);
        return sliceBounds;
    }

    private void setBitmap(Bitmap bitmap) {
        if(bitmap == null && isVisible()) return ;
        if (bitmap != null && bitmap.getWidth() == -1 && bitmap.getHeight() == -1) {
            return;
        }
        if (this.bitmap != bitmap) {
            if (bitmap != null) {
                if (this.bitmap != null) {
                    this.bitmap.recycle();
                }
                bitmapWeakReference = new SoftReference<Bitmap>(bitmap);
                documentView.postInvalidate();
            }
            this.bitmap = bitmap;
        }
    }

    private boolean isDecodingNow() {
        return decodingNow;
    }

    private void setDecodingNow(boolean decodingNow) {
        if (this.decodingNow != decodingNow) {
            this.decodingNow = decodingNow;
            if(documentView.progressModel != null)
            {
                if (decodingNow) {
                    documentView.progressModel.increase();
                } else {
                    documentView.progressModel.decrease();
                }
            }
        }
    }

    private Rect getTargetRect() {
        if (targetRect == null) {
            matrix.reset();
            matrix.postScale(page.bounds.width(), page.bounds.height());
            matrix.postTranslate(page.bounds.left, page.bounds.top);
            RectF r = new RectF();
            matrix.mapRect(r, pageSliceBounds);
            targetRect = new Rect((int) (r.left + 0.5), (int) (r.top + 0.5), (int) (r.right + 0.5), (int) (r.bottom + 0.5));
        }
        return targetRect;
    }

    private void stopDecodingThisNode() {
        if (!isDecodingNow()) {
            return;
        }
        documentView.decodeService.stopDecoding(this);
        setDecodingNow(false);
    }

    private boolean isHiddenByChildren() {
        if (children == null) {
            return false;
        }
        for (PageTreeNodeNew child : children) {
            if (child.getBitmap() == null) {
                return false;
            }
        }
        return true;
    }

    private void recycleChildren() {
        if (children == null) {
            return;
        }
        for (PageTreeNodeNew child : children) {
            child.recycle();
        }
        if (!childrenContainBitmaps()) {
            children = null;
        }
    }

    private boolean containsBitmaps() {
        return getBitmap() != null || childrenContainBitmaps();
    }

    private boolean childrenContainBitmaps() {
        if (children == null) {
            return false;
        }
        for (PageTreeNodeNew child : children) {
            if (child.containsBitmaps()) {
                return true;
            }
        }
        return false;
    }

    public void recycle() {
        stopDecodingThisNode();
        setBitmap(null);
        if (children != null) {
            for (PageTreeNodeNew child : children) {
                child.recycle();
            }
        }
    }

    private boolean isVisibleAndNotHiddenByChildren() {
        return isVisible() && !isHiddenByChildren();
    }

}