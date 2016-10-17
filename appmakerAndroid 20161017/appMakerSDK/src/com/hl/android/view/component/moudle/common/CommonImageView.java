package com.hl.android.view.component.moudle.common;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

public class CommonImageView extends ImageView{

	public CommonImageView(Context context) {
		super(context);
	}
	
	class RecycleBin {
        private final SparseArray<View> mScrapHeap = new SparseArray<View>();

        public void put(int position, View v) {
            mScrapHeap.put(position, v);
        }
        
        View get(int position) {
            // System.out.print("Looking for " + position);
            View result = mScrapHeap.get(position);
            if (result != null) {
                // System.out.println(" HIT");
                mScrapHeap.delete(position);
            } else {
                // System.out.println(" MISS");
            }
            return result;
        }

        void clear() {
            final SparseArray<View> scrapHeap = mScrapHeap;
            final int count = scrapHeap.size();
            for (int i = 0; i < count; i++) {
                final View view = scrapHeap.valueAt(i);
                if (view != null) {
                    //removeDetachedView(view, true);
                }
            }
            scrapHeap.clear();
        }
    }

}
