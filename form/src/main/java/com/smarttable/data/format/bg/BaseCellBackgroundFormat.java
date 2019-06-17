package com.smarttable.data.format.bg;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;


public abstract class BaseCellBackgroundFormat<T> implements ICellBackgroundFormat<T> {

    @Override
    public void drawBackground(Canvas canvas, Rect rect, T t,Paint paint) {
        int color = getBackGroundColor(t);
        if(color != TableConfig.INVALID_COLOR) {
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(rect, paint);
        }
    }


    public abstract   int getBackGroundColor(T t);


    @Override
    public int getTextColor(T t) {
        return TableConfig.INVALID_COLOR;
    }
}
