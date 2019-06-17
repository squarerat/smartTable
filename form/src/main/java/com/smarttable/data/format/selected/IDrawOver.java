package com.smarttable.data.format.selected;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;


public interface IDrawOver {

     void draw(Canvas canvas,Rect scaleRect, Rect showRect, TableConfig config);
}
