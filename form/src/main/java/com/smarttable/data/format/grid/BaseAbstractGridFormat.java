package com.smarttable.data.format.grid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.smarttable.data.CellInfo;
import com.smarttable.data.column.Column;


public abstract class BaseAbstractGridFormat implements IGridFormat {

    private Path path = new Path();


    protected abstract boolean isShowVerticalLine(int col, int row,CellInfo cellInfo);

    protected abstract boolean isShowHorizontalLine(int col, int row,CellInfo cellInfo);


    protected  boolean isShowCountVerticalLine(int col,Column column){
        return true;
    }


    protected  boolean isShowCountHorizontalLine(int col,Column column){
        return true;
    }


    protected  boolean isShowColumnTitleVerticalLine(int col,Column column){
        return true;
    }


    protected  boolean isShowColumnTitleHorizontalLine(int col,Column column){
        return true;
    }


    protected  boolean isShowXSequenceVerticalLine(int col){
        return true;
    }

    protected  boolean isShowXSequenceHorizontalLine(int col){
        return true;
    }


    protected  boolean isShowYSequenceVerticalLine(int row){
        return true;
    }

    protected  boolean isShowYSequenceHorizontalLine(int row){
        return true;
    }


    @Override
    public void drawContentGrid(Canvas canvas, int col, int row, Rect rect, CellInfo cellInfo, Paint paint) {
       drawGridPath(canvas,rect,paint,
               isShowHorizontalLine(col,row,cellInfo),
               isShowVerticalLine(col,row,cellInfo));
    }


    @Override
    public void drawXSequenceGrid(Canvas canvas, int col, Rect rect, Paint paint) {
        drawGridPath(canvas,rect,paint,
                isShowXSequenceHorizontalLine(col),
                isShowXSequenceVerticalLine(col));
    }


    @Override
    public void drawYSequenceGrid(Canvas canvas, int row, Rect rect, Paint paint) {
        drawGridPath(canvas,rect,paint,
                isShowYSequenceHorizontalLine(row),
                isShowYSequenceVerticalLine(row));
    }


    @Override
    public void drawCountGrid(Canvas canvas, int col, Rect rect, Column column, Paint paint) {
        drawGridPath(canvas,rect,paint,
                isShowCountHorizontalLine(col,column),
                isShowCountVerticalLine(col,column));
    }


    @Override
    public void drawColumnTitleGrid(Canvas canvas, Rect rect, Column column, int col,Paint paint) {
        drawGridPath(canvas,rect,paint,
                isShowColumnTitleHorizontalLine(col,column),
                isShowColumnTitleVerticalLine(col,column));
    }

    @Override
    public void drawTableBorderGrid(Canvas canvas, int left, int top, int right, int bottom, Paint paint) {
        canvas.drawRect(left,top,right,bottom,paint);
    }


    public void drawLeftAndTopGrid(Canvas canvas, Rect rect,Paint paint){
        canvas.drawRect(rect,paint);
    }


    protected void drawGridPath(Canvas canvas, Rect rect, Paint paint,boolean isShowHorizontal,
                                boolean isShowVertical) {
        path.rewind();
        if(isShowHorizontal) {
            path.moveTo(rect.left, rect.top);
            path.lineTo(rect.right, rect.top);
        }
        if(isShowVertical) {
            if(!isShowHorizontal){
                path.moveTo(rect.right, rect.top);
            }
            path.lineTo(rect.right, rect.bottom);
        }
        if(isShowHorizontal || isShowVertical)
            canvas.drawPath(path,paint);
    }
}
