package com.pam.tmdbmovielist.ui.custom;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

import androidx.annotation.NonNull;

public abstract class TouchableSpan extends ClickableSpan {
    
    private boolean isPressed;
    private int pressedBackgroundColor;
    private int normalTextColor;
    private int pressedTextColor;
    
    public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
        this.normalTextColor = normalTextColor;
        this.pressedTextColor = pressedTextColor;
        this.pressedBackgroundColor = pressedBackgroundColor;
    }
    
    public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }
    
    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(isPressed ? pressedTextColor : normalTextColor);
        ds.bgColor = isPressed ? pressedBackgroundColor : Color.TRANSPARENT;
        ds.setUnderlineText(false);
    }
}