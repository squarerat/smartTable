package com.smarttable.data.format.selected;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;


public interface ISelectFormat {

     void draw(Canvas canvas, Rect rect,Rect showRect, TableConfig config);
}
