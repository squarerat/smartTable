package com.smarttable.component;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.smarttable.core.TableConfig;
import com.smarttable.data.format.selected.ISelectFormat;
import com.smarttable.matrix.MatrixHelper;

public class SelectionOperation implements MatrixHelper.OnInterceptListener {

    private static final int INVALID = -1;
    private Rect selectionRect;
    private ISelectFormat selectFormat;
    private int selectRow = INVALID;
    private int selectColumn = INVALID;
    private boolean isShow;

    void reset(){
        isShow = false;
    }

    SelectionOperation() {
        this.selectionRect = new Rect();
    }

    void setSelectionRect(int selectColumn,int selectRow, Rect rect){
        this.selectRow = selectRow;
        this.selectColumn = selectColumn;
        selectionRect.set(rect);
        isShow = true;
    }

    boolean isSelectedPoint( int selectColumn,int selectRow){

       return  selectRow == this.selectRow  && selectColumn == this.selectColumn;
    }

    void checkSelectedPoint(int selectColumn,int selectRow, Rect rect){

         if(isSelectedPoint(selectColumn,selectRow)){

             selectionRect.set(rect);
             isShow = true;
         }
    }

    public void draw(Canvas canvas, Rect showRect, TableConfig config){

        if(selectFormat !=null && isShow){
          selectFormat.draw(canvas,selectionRect,showRect,config);
        }
    }

    public ISelectFormat getSelectFormat() {
        return selectFormat;
    }

    void setSelectFormat(ISelectFormat selectFormat) {
        this.selectFormat = selectFormat;
    }

    @Override
    public boolean isIntercept(MotionEvent e1, float distanceX, float distanceY) {
        return false;
    }

}
