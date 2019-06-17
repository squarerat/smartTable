package com.smarttable.data.format.title;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.data.column.Column;


public interface ITitleDrawFormat {


    int measureWidth(Column column, TableConfig config);


    int measureHeight(TableConfig config);


    void draw(Canvas c, Column column, Rect rect, TableConfig config);


}
