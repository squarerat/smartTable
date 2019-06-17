package com.smarttable.data.column;


public class ColumnInfo {

    public int width;

    public int height;

    public int left;

    public int top;

    public String value;

    public Column column;

    private ColumnInfo parent;

    public ColumnInfo() {

    }


    public ColumnInfo getTopParent(){

        return getParent(this);
    }


    private ColumnInfo getParent(ColumnInfo column){
        if(column.getParent() != null){
            return getParent(column.getParent());
        }
        return column;
    }


    public ColumnInfo getParent() {
        return parent;
    }

    public void setParent(ColumnInfo parent) {
        this.parent = parent;
    }
}
