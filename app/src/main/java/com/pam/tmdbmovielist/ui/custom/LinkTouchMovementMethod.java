package com.pam.tmdbmovielist.ui.custom;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkTouchMovementMethod extends LinkMovementMethod {
    
    private TouchableSpan pressedSpan;
    
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pressedSpan = getPressedSpan(widget, buffer, event);
            if (pressedSpan != null) {
                pressedSpan.setPressed(true);
                Selection.setSelection(buffer, buffer.getSpanStart(pressedSpan), buffer.getSpanEnd(pressedSpan));
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            TouchableSpan touchedSpan = getPressedSpan(widget, buffer, event);
            if (pressedSpan != null && touchedSpan != pressedSpan) {
                pressedSpan.setPressed(false);
                pressedSpan = null;
                Selection.removeSelection(buffer);
            }
        } else {
            if (pressedSpan != null) {
                pressedSpan.setPressed(false);
                super.onTouchEvent(widget, buffer, event);
            }
            pressedSpan = null;
            Selection.removeSelection(buffer);
        }
        
        return true;
    }
    
    private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        int x = (int) event.getX() - textView.getTotalPaddingStart() + textView.getScrollX();
        int y = (int) event.getY() - textView.getTotalPaddingTop() + textView.getScrollY();
        
        Layout layout = textView.getLayout();
        int position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x);
        
        TouchableSpan[] link = spannable.getSpans(position, position, TouchableSpan.class);
        TouchableSpan touchedSpan = null;
        if (link.length > 0 && positionWithinTag(position, spannable, link[0])) {
            touchedSpan = link[0];
        }
        
        return touchedSpan;
    }
    
    private boolean positionWithinTag(int position, Spannable spannable, Object tag) {
        return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag);
    }
}