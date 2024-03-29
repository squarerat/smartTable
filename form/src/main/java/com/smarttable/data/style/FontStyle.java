package com.smarttable.data.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.smarttable.utils.DensityUtils;


public class FontStyle implements IStyle {

    private static int defaultFontSize = 12;
    private static int defaultFontColor = Color.parseColor("#636363");
    private static Paint.Align defaultAlign = Paint.Align.CENTER;
    private int textSize;
    private int textColor;
    private Paint.Align align;
    private Typeface typeface = Typeface.DEFAULT;

    public static void setDefaultTextSize(int defaultTextSize) {
        defaultFontSize = defaultTextSize;
    }

    public static void setDefaultTextAlign(Paint.Align align) {
        defaultAlign = align;
    }

    public static void setDefaultTextSpSize(Context context, int defaultTextSpSize) {
        defaultFontSize = DensityUtils.sp2px(context, defaultTextSpSize);
    }

    public static void setDefaultTextColor(int defaultTextColor) {
        defaultFontColor = defaultTextColor;
    }

    public FontStyle() {
    }

    public FontStyle(int textSize, int textColor) {
        this.textSize = textSize;
        this.textColor = textColor;
    }

    public FontStyle(Context context, int sp, int textColor) {
        this.textSize = DensityUtils.sp2px(context, sp);
        this.textColor = textColor;
    }

    public Paint.Align getAlign() {
        if (align == null) {
            return defaultAlign;
        }
        return align;
    }

    public FontStyle setAlign(Paint.Align align) {
        this.align = align;
        return this;
    }

    public int getTextSize() {
        if (textSize == 0) {
            return defaultFontSize;
        }
        return textSize;
    }

    public FontStyle setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public void setTextSpSize(Context context, int sp) {
        this.setTextSize(DensityUtils.sp2px(context, sp));
    }

    public int getTextColor() {
        if (textColor == 0) {
            return defaultFontColor;
        }
        return textColor;
    }

    public FontStyle setTextColor(int textColor) {

        this.textColor = textColor;
        return this;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    @Override
    public void fillPaint(Paint paint) {
        paint.setColor(getTextColor());
        paint.setTextAlign(getAlign());
        paint.setTextSize(getTextSize());
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(typeface);
    }
}
