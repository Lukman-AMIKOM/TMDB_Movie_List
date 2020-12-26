package com.pam.tmdbmovielist.ui.custom;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

public class MySpinner extends androidx.appcompat.widget.AppCompatSpinner {
    public MySpinner(Context context) {
        super(context);
    }
    
    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter != null ? new WrapperSpinnerAdapter(adapter) : null);
    }
    
    public final class WrapperSpinnerAdapter implements SpinnerAdapter {
        
        private final SpinnerAdapter baseAdapter;
        
        public WrapperSpinnerAdapter(SpinnerAdapter baseAdapter) {
            this.baseAdapter = baseAdapter;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return baseAdapter.getView(getSelectedItemPosition(), convertView, parent);
        }
        
        public final SpinnerAdapter getBaseAdapter() {
            return baseAdapter;
        }
        
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return baseAdapter.getDropDownView(position, convertView, parent);
        }
        
        @Override
        public int getCount() {
            return baseAdapter.getCount();
        }
        
        @Override
        public Object getItem(int position) {
            return baseAdapter.getItem(position);
        }
        
        @Override
        public long getItemId(int position) {
            return baseAdapter.getItemId(position);
        }
        
        @Override
        public int getItemViewType(int position) {
            return baseAdapter.getItemViewType(position);
        }
        
        @Override
        public int getViewTypeCount() {
            return baseAdapter.getViewTypeCount();
        }
        
        @Override
        public boolean isEmpty() {
            return baseAdapter.isEmpty();
        }
        
        @Override
        public boolean hasStableIds() {
            return baseAdapter.hasStableIds();
        }
        
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            baseAdapter.registerDataSetObserver(observer);
        }
        
        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            baseAdapter.unregisterDataSetObserver(observer);
        }
    }
}