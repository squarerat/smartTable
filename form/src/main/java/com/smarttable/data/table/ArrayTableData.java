package com.smarttable.data.table;


import com.smarttable.core.SmartTable;
import com.smarttable.data.column.Column;
import com.smarttable.data.format.IFormat;
import com.smarttable.data.format.draw.IDrawFormat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ArrayTableData<T> extends TableData<T> {

    private  T[][] data;
    private List<Column<T>> arrayColumns;


    public static<T> T[][]  transformColumnArray(T[][] rowArray){
        T[][] newData = null;
        T[] row= null;
        if(rowArray != null){
            int maxLength = 0;
            for(T[] t :rowArray){
                if(t !=null && t.length > maxLength){
                    maxLength = t.length;
                    row= t;
                }
            }
            if(row !=null) {
                newData = (T[][]) Array.newInstance(rowArray.getClass().getComponentType(),maxLength);
                for (int i = 0; i < rowArray.length; i++) {
                    for (int j = 0; j < rowArray[i].length; j++) {
                        if(newData[j] == null) {
                            newData[j] = (T[]) Array.newInstance(row.getClass().getComponentType(),rowArray.length);
                        }
                        newData[j][i] = rowArray[i][j];
                    }
                }
            }

        }
        return newData;
    }


    public static<T> ArrayTableData<T> create(String tableName,String[] titleNames, T[][] data, IDrawFormat<T> drawFormat){
        List<Column<T>> columns = new ArrayList<>();
        for(int i = 0;i <data.length;i++){
            T[] dataArray = data[i];
            Column<T> column = new Column<>(titleNames == null?"":titleNames[i], null,drawFormat);
            column.setDatas(Arrays.asList(dataArray));
            columns.add(column);
        }
        ArrayList<T> arrayList = new ArrayList<>(Arrays.asList(data[0]));
        ArrayTableData<T> tableData =  new ArrayTableData<>(tableName,arrayList,columns);
        tableData.setData(data);
        return tableData;
    }


    public static<T> ArrayTableData<T> create(SmartTable table,String tableName, T[][] data, IDrawFormat<T> drawFormat){
        table.getConfig().setShowColumnTitle(false);
        return create(tableName,null,data,drawFormat);
    }


    public void setFormat(IFormat<T> format){
        for(Column<T> column:arrayColumns){


            column.setFormat(format);
        }
    }

    public void setDrawFormat(IDrawFormat<T> format){
        for(Column<T> column:arrayColumns){
            column.setDrawFormat(format);
        }
    }


    public void setMinWidth(int minWidth){
        for(Column<T> column:arrayColumns){
            column.setMinWidth(minWidth);
        }
    }


    public void setMinHeight(int minHeight){
        for(Column<T> column:arrayColumns){
            column.setMinHeight(minHeight);
        }
    }


    protected ArrayTableData(String tableName, List<T> t, List<Column<T>> columns) {
        super(tableName, t, new ArrayList<Column>(columns));
        this.arrayColumns = columns;
    }

    public List<Column<T>> getArrayColumns() {
        return arrayColumns;
    }


    public T[][] getData() {
        return data;
    }


    public void setData(T[][] data) {
        this.data = data;
    }





}
