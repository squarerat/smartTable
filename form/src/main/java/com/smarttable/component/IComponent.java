package com.smarttable.component;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;

public interface IComponent<T> {

    int LEFT = 0;
    int TOP = 1;
    int RIGHT = 2;
    int BOTTOM = 3;

    void onMeasure(Rect scaleRect, Rect showRect, TableConfig config);

    void onDraw(Canvas canvas, Rect showRect, T t, TableConfig config);

}
