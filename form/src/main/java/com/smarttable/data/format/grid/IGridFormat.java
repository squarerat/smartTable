package com.smarttable.data.format.grid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.smarttable.data.CellInfo;
import com.smarttable.data.column.Column;


public interface IGridFormat {


    void drawContentGrid(Canvas canvas, int col, int row, Rect rect, CellInfo cellInfo, Paint paint);


    void drawXSequenceGrid(Canvas canvas, int col, Rect rect, Paint paint);


    void drawYSequenceGrid(Canvas canvas, int row, Rect rect, Paint paint);


    void drawCountGrid(Canvas canvas, int col, Rect rect, Column column, Paint paint);


    void drawColumnTitleGrid(Canvas canvas, Rect rect, Column column, int col, Paint paint);


    void drawTableBorderGrid(Canvas canvas, int left, int top, int right, int bottom, Paint paint);


    void drawLeftAndTopGrid(Canvas canvas, Rect rect, Paint paint);
}
