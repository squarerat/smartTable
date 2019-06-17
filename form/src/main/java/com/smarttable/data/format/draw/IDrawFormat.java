package com.smarttable.data.format.draw;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.data.CellInfo;
import com.smarttable.data.column.Column;


public interface IDrawFormat<T>  {


    int measureWidth(Column<T> column, int position, TableConfig config);


    int measureHeight(Column<T> column,int position, TableConfig config);


    void draw(Canvas c, Rect rect, CellInfo<T> cellInfo, TableConfig config);




}
