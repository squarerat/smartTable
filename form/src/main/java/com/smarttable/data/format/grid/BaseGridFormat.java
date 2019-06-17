package com.smarttable.data.format.grid;


import com.smarttable.data.CellInfo;


public  class BaseGridFormat extends BaseAbstractGridFormat {


    @Override
    protected boolean isShowVerticalLine(int col, int row, CellInfo cellInfo) {
        return true;
    }

    @Override
    protected boolean isShowHorizontalLine(int col, int row, CellInfo cellInfo) {
        return true;
    }
}
