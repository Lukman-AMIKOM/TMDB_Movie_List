package com.pam.tmdbmovielist.ui.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.recyclerview.widget.GridLayoutManager;

public class ResponsiveGridLayoutManager extends GridLayoutManager {
    
    boolean hasSetupColumns;
    int columnWidthPx;
    
    public ResponsiveGridLayoutManager(Context context, int orientation,
                                       boolean reverseLayout, int columnWidthUnit, float columnWidth) {
        
        super(context, 1, orientation, reverseLayout);
        
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        
        float colWidthPx = TypedValue.applyDimension(columnWidthUnit,
                columnWidth, r.getDisplayMetrics());
        
        this.columnWidthPx = Math.round(colWidthPx);
    }
    
    public ResponsiveGridLayoutManager(Context context, AttributeSet attrs,
                                       int defStyleAttr, int defStyleRes) {
        
        super(context, attrs, defStyleAttr, defStyleRes);
        int[] requestedValues = {
                android.R.attr.columnWidth,
        };
        
        TypedArray a = context.obtainStyledAttributes(attrs, requestedValues);
        this.columnWidthPx = a.getDimensionPixelSize(0, -1);
        a.recycle();
    }
    
    @Override
    public int getWidth() {
        int width = super.getWidth();
        if (!hasSetupColumns && width > 0) {
            this.setSpanCount((int) Math.floor(width / this.columnWidthPx));
        }
        
        return width;
    }
}