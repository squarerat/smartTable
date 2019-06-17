package com.smarttable.data.format.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.data.column.Column;
import com.smarttable.utils.DrawUtils;


public class FastTextDrawFormat<T> extends TextDrawFormat<T> {


    private int height;
    private int width;
    private int maxLengthValue;

    @Override
    public int measureWidth(Column<T> column, int position, TableConfig config) {
        String value = column.format(position);
        if (value.length() > maxLengthValue) {
            maxLengthValue = value.length();
            Paint paint = config.getPaint();
            config.getContentStyle().fillPaint(paint);
            width = (int) paint.measureText(value);
        }
        return width;
    }

    @Override
    public int measureHeight(Column<T> column, int position, TableConfig config) {
        if (height == 0) {
            Paint paint = config.getPaint();
            config.getContentStyle().fillPaint(paint);
            height = DrawUtils.getTextHeight(paint);
        }
        return height;
    }


    protected void drawText(Canvas c, String value, Rect rect, Paint paint) {
        DrawUtils.drawSingleText(c, paint, rect, value);
    }


}
