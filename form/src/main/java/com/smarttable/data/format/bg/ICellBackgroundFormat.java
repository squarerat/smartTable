package com.smarttable.data.format.bg;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public interface ICellBackgroundFormat<T> {

    void drawBackground(Canvas canvas, Rect rect,T t, Paint paint);


    int getTextColor(T t);

}
