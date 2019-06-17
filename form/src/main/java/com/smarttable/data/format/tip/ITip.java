package com.smarttable.data.format.tip;

import android.graphics.Canvas;
import android.graphics.Rect;


public interface ITip<C,S> {

    void drawTip(Canvas canvas, float x, float y, Rect rect, C content, int position);

    boolean isShowTip(C c, int position);

    S format(C c, int position);
}
