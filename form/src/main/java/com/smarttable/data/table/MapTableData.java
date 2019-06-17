package com.smarttable.data.table;


import com.smarttable.data.column.ArrayColumn;
import com.smarttable.data.column.Column;
import com.smarttable.data.column.MapColumn;
import com.smarttable.data.format.IFormat;
import com.smarttable.data.format.draw.IDrawFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapTableData extends TableData<Object> {

    private FilterColumnIntercept mIntercept;


    public static MapTableData create(String tableName, List<Object> mapList) {
        return create(tableName, mapList, null);
    }


    public static MapTableData create(String tableName, List<Object> mapList, IFormat<String> keyFormat) {
        if (mapList != null) {
            List<Column> columns = new ArrayList<>();
            getMapColumn(columns, Column.INVAL_VALUE, Column.INVAL_VALUE, mapList, keyFormat);
            return new MapTableData(tableName, mapList, columns);
        }
        return null;
    }


    private static void getMapColumn(List<Column> columns, String fieldName, String parentKey, List<Object> mapList, IFormat<String> keyFormat) {
        if (mapList != null && mapList.size() > 0) {
            Object o = mapList.get(0);
            if (o != null) {
                if (o instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) o;

                    boolean isOneArray = true;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        Object val = entry.getValue();
                        if (ArrayColumn.isList(val)) {
                            if (isOneArray) {
                                List<Object> list = ((List) val);
                                getMapColumn(columns, fieldName + key + ".", key, list, keyFormat);
                                isOneArray = false;
                            }
                        } else {
                            String columnName = keyFormat == null ? key : keyFormat.format(key);
                            MapColumn<Object> column = new MapColumn<>(columnName, fieldName + key);
                            columns.add(column);
                        }
                    }
                } else {
                    String columnName = keyFormat == null ? parentKey : keyFormat.format(parentKey);
                    MapColumn<Object> column = new MapColumn<>(columnName, fieldName, false);
                    columns.add(column);
                }
            }
        }
    }


    private MapTableData(String tableName, List t, List<Column> columns) {
        super(tableName, t, columns);
    }


    public void setDrawFormat(IDrawFormat drawFormat) {
        for (Column column : getColumns()) {
            column.setDrawFormat(drawFormat);
        }
    }


    public void setFormat(IFormat format) {
        for (Column column : getColumns()) {
            column.setFormat(format);
        }
    }


    public void setMinWidth(int minWidth){
        for (Column column : getColumns()) {
            column.setMinWidth(minWidth);
        }
    }


    public void setMinHeight(int minHeight){
        for (Column column : getColumns()) {
            column.setMinHeight(minHeight);
        }
    }


    public interface FilterColumnIntercept {

        boolean onIntercept(Column column, String columnName);
    }


    public FilterColumnIntercept getFilterColumnIntercept() {
        return mIntercept;
    }


    public void setFilterColumnIntercept(FilterColumnIntercept intercept) {
        this.mIntercept = intercept;
        if (mIntercept != null) {
            for (int i = getColumns().size() - 1; i >= 0; i--) {
                Column column = getColumns().get(i);
                if (mIntercept.onIntercept(column, column.getColumnName())) {
                    getColumns().remove(i);
                }
            }
        }
    }



}
