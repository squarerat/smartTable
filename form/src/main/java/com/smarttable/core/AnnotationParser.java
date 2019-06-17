package com.smarttable.core;

import com.smarttable.annotation.ColumnType;
import com.smarttable.annotation.SmartColumn;
import com.smarttable.data.column.ArrayColumn;
import com.smarttable.data.column.Column;
import com.smarttable.data.table.PageTableData;
import com.smarttable.exception.TableException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationParser<T>  {

    private int dp10;

    public PageTableData<T> parse(List<T> dataList){
        if(dataList!= null && dataList.size() >0) {
            T firstData = dataList.get(0);
            if(firstData != null) {
                Class clazz = firstData.getClass();
                Annotation tableAnnotation = clazz.getAnnotation(com.smarttable.annotation.SmartTable.class);
                if(tableAnnotation != null){
                    com.smarttable.annotation.SmartTable table = (com.smarttable.annotation.SmartTable) tableAnnotation;
                    List<Column> columns = new ArrayList<>();
                    PageTableData<T> tableData = new PageTableData<>(table.name(),dataList,columns);
                    tableData.setCurrentPage(table.currentPage());
                    tableData.setPageSize(table.pageSize());
                    tableData.setShowCount(table.count());
                    Map<String,Column> parentMap = new HashMap<>();
                    getColumnAnnotation(clazz, null,columns, parentMap,false);
                    Collections.sort(columns);
                    return tableData;
                }

            }
        }
        return null;
    }

    private  void getColumnAnnotation(Class clazz, String parentFieldName, List<Column> columns, Map<String, Column> parentMap,boolean isArray) {
        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            Annotation fieldAnnotation = field.getAnnotation(SmartColumn.class);
           if(fieldAnnotation != null){
               SmartColumn smartColumn = (SmartColumn) fieldAnnotation;
               ColumnType type = smartColumn.type();
               if(type == ColumnType.Own) {
                   String fieldName =parentFieldName != null? (parentFieldName+field.getName()) :field.getName();
                   createColumn(fieldName,field, columns, parentMap, isArray, true,smartColumn);
               }else if(type == ColumnType.Child){
                   String fieldName = (parentFieldName != null ?parentFieldName:Column.INVAL_VALUE)
                           +field.getName()+".";
                   getColumnAnnotation(fieldClass,fieldName,columns,parentMap,isArray);
               }else if(type == ColumnType.ArrayChild || type == ColumnType.ArrayOwn){
                   fieldClass = getParameterizedType(field);
                   String fieldName = (parentFieldName != null ?parentFieldName:Column.INVAL_VALUE)
                           +field.getName();
                   if(type == ColumnType.ArrayOwn) {
                       createColumn(fieldName, field, columns, parentMap, true,false, smartColumn);
                   }else {
                       getColumnAnnotation(fieldClass, fieldName + ".", columns, parentMap, true);
                   }
               }
           }

        }
    }

    private void createColumn(String fieldName,Field field, List<Column> columns, Map<String, Column> parentMap, boolean isArray,boolean isThoroughArray, SmartColumn smartColumn) {
        String name = smartColumn.name();
        int id = smartColumn.id();
        String parent = smartColumn.parent();
        boolean isAutoCount = smartColumn.autoCount();
        boolean isFast = smartColumn.fast();
        if (name.equals(Column.INVAL_VALUE)) {
            name = field.getName();
        }
        Column<?> column = getGenericColumn(name, fieldName,isArray);
        if(column instanceof ArrayColumn){
            ((ArrayColumn)column).setThoroughArray(isThoroughArray);
        }
        column.setId(id);
        column.setFast(isFast);
        column.setTextAlign(smartColumn.align());
        column.setAutoMerge(smartColumn.autoMerge());
        column.setMinWidth(smartColumn.minWidth()*dp10/10);
        column.setMinHeight(smartColumn.minHeight()*dp10/10);
        column.setTitleAlign(smartColumn.titleAlign());
        column.setWidth(smartColumn.width()*dp10/10);
        if(smartColumn.maxMergeCount() !=-1) {
            column.setMaxMergeCount(smartColumn.maxMergeCount());
        }

        column.setAutoCount(isAutoCount);
        column.setFixed(smartColumn.fixed());
        if (!parent.equals(Column.INVAL_VALUE)) {
            Column parentColumn = parentMap.get(parent);
            if (parentColumn == null) {
                List<Column> childColumns = new ArrayList<>();
                childColumns.add(column);
                parentColumn = new Column(parent, childColumns);
                parentColumn.setId(id);
                columns.add(parentColumn);
                parentMap.put(parent, parentColumn);
            }else {
                parentColumn.addChildren(column);
            }
            if (id < parentColumn.getId()) {
                parentColumn.setId(id);
            }
        }else{
            columns.add(column);
        }
    }

    private Class<?> getParameterizedType(Field field){

        if(field.getType() == java.util.List.class) {
            Type genericType = field.getGenericType();
            if (genericType == null) {
                throw new TableException("ColumnType Array field List  must be with generics");
            }

            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
                return genericClazz;
            } else {
                throw new TableException("ColumnType Array field List  must be with generics");
            }
        }else if(field.getType().isArray()){
                return field.getType().getComponentType();
        }else{
            throw new TableException("ColumnType Array field  must be List or Array");
        }

    }

    private Column<?> getGenericColumn(String name, String fieldName, boolean isArray) {

        Column<?> column;
        if (isArray) {
            column = new ArrayColumn<>(name, fieldName);
        } else {
            column = new Column<>(name, fieldName);
        }
        return column;
    }

    public AnnotationParser(int dp10) {
        this.dp10 = dp10;
    }

}
