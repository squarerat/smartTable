package com.smarttable.data;

import com.smarttable.data.column.Column;

public class CellInfo<T> {

    public T data;

    public int row;

    public int col;

    public Column<T> column;

    public String value;

    public void set(Column<T> column,T t,String value,int col, int row){
        this.column = column;
        this.value= value;
        this.data = t;
        this.row = row;
        this.col = col;
    }

}
