package com.smarttable.data.column;

import android.graphics.Paint;

import com.smarttable.data.TableInfo;
import com.smarttable.data.format.IFormat;
import com.smarttable.data.format.count.DecimalCountFormat;
import com.smarttable.data.format.count.ICountFormat;
import com.smarttable.data.format.count.NumberCountFormat;
import com.smarttable.data.format.count.StringCountFormat;
import com.smarttable.data.format.draw.FastTextDrawFormat;
import com.smarttable.data.format.draw.IDrawFormat;
import com.smarttable.data.format.draw.MultiLineDrawFormat;
import com.smarttable.data.format.draw.TextDrawFormat;
import com.smarttable.listener.OnColumnItemClickListener;
import com.smarttable.listener.OnColumnItemLongClickListener;
import com.smarttable.utils.LetterUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Column<T> implements Comparable<Column> {

    public static final String INVAL_VALUE = "";

    private String columnName;

    private List<Column> children;

    private IFormat<T> format;
    private IDrawFormat<T> drawFormat;
    private String fieldName;
    private final List<T> datas = new ArrayList<>();
    private boolean isFixed;
    private int computeWidth;
    private int level;
    private Comparator<T> comparator;
    private ICountFormat<T,? extends Number> countFormat;
    private boolean isReverseSort;
    private OnColumnItemClickListener<T> onColumnItemClickListener;
    private OnColumnItemLongClickListener<T> onColumnItemLongClickListener;
    private Paint.Align textAlign;
    private Paint.Align titleAlign;
    private boolean isAutoCount =false;
    private boolean isAutoMerge = false;
    private int maxMergeCount = Integer.MAX_VALUE;
    private int id;
    private boolean isParent;
    private List<int[]> ranges;
    private boolean isFast;
    private int minWidth;
    private int minHeight;
    private int width;


    public Column(String columnName, List<Column> children) {
        this.columnName = columnName;
        this.children = children;
        isParent = true;
    }

    public Column(String columnName, Column... children) {
        this(columnName, Arrays.asList(children));
    }

    public Column(String columnName, String fieldName) {
        this(columnName, fieldName, null, null);
    }


    public Column(String columnName, String fieldName, IFormat<T> format) {
        this(columnName, fieldName, format, null);
    }

    public Column(String columnName, String fieldName, IDrawFormat<T> drawFormat) {
        this(columnName, fieldName, null, drawFormat);
    }


    public Column(String columnName, String fieldName, IFormat<T> format, IDrawFormat<T> drawFormat) {
        this.columnName = columnName;
        this.format = format;
        this.fieldName = fieldName;
        this.drawFormat =drawFormat;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public IFormat<T> getFormat() {
        return format;
    }

    public void setFormat(IFormat<T> format) {
        this.format = format;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setChildren(List<Column> children) {
        this.children = children;
    }

    public IDrawFormat<T> getDrawFormat() {
        if(drawFormat== null){
            drawFormat = isFast ? new FastTextDrawFormat<T>() : new TextDrawFormat<T>();
        }
        return drawFormat;
    }

    public void setDrawFormat(IDrawFormat<T> drawFormat) {
        this.drawFormat = drawFormat;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public List<T> getDatas() {
        synchronized (datas) {
            return datas;
        }
    }

    public void setDatas(List<T> datas) {
        synchronized (this.datas) {
            this.datas.clear();
            this.datas.addAll(datas);
        }
    }

    public T getData(Object o) throws NoSuchFieldException, IllegalAccessException {
        String[] fieldNames = fieldName.split("\\.");
        if (fieldNames.length >0) {
            Object child = o;
            for (int i = 0; i < fieldNames.length; i++) {
                if (child == null) {
                    return null;
                }
                Class childClazz = child.getClass();
                Field childField = childClazz.getDeclaredField(fieldNames[i]);
                if (childField == null) {
                    return null;
                }
                childField.setAccessible(true);
                if (i == fieldNames.length - 1) {
                    return (T) childField.get(child);

                } else {
                    child = childField.get(child);
                }
            }

        }
        return  null;
    }

    public void fillData(List<Object> objects) throws NoSuchFieldException, IllegalAccessException {
        synchronized (datas) {
            datas.clear();
            if (countFormat != null) {
                countFormat.clearCount();
            }
            if (objects.size() > 0) {
                String[] fieldNames = fieldName.split("\\.");
                if (fieldNames.length > 0) {
                    Field[] fields = new Field[fieldNames.length];
                    int size = objects.size();
                    for (int k = 0; k < size; k++) {
                        Object child = objects.get(k);
                        for (int i = 0; i < fieldNames.length; i++) {
                            if (child == null) {
                                addData(null, true);
                                countColumnValue(null);
                                break;
                            }
                            Field childField;
                            if (fields[i] != null) {
                                childField = fields[i];
                            } else {
                                Class childClazz = child.getClass();
                                childField = childClazz.getDeclaredField(fieldNames[i]);
                                childField.setAccessible(true);
                                fields[i] = childField;
                            }
                            if (childField == null) {
                                addData(null, true);
                                countColumnValue(null);
                                break;
                            }
                            if (i == fieldNames.length - 1) {
                                T t = (T) childField.get(child);
                                addData(t, true);
                                countColumnValue(t);
                            } else {
                                child = childField.get(child);
                            }
                        }

                    }
                }
            }
        }
    }

    public void addData(List<Object> objects, int startPosition,boolean isFoot) throws NoSuchFieldException, IllegalAccessException {
        if(objects.size()+ startPosition == datas.size()){
            return;
        }
        if (objects.size() > 0) {
            String[] fieldNames = fieldName.split("\\.");
            if (fieldNames.length >0) {
                int size = objects.size();
                for (int k = 0; k < size; k++) {
                    Object child= objects.get(isFoot ? k:(size-1-k));
                    for (int i = 0; i < fieldNames.length; i++) {
                        if (child == null) {
                            addData(null,isFoot);
                            countColumnValue(null);
                            break;
                        }
                        Class childClazz = child.getClass();
                        Field childField = childClazz.getDeclaredField(fieldNames[i]);
                        if (childField == null) {
                            addData(null,isFoot);
                            countColumnValue(null);
                            break;
                        }
                        childField.setAccessible(true);
                        if (i == fieldNames.length - 1) {
                            T t = (T) childField.get(child);
                            addData(t, isFoot);
                            countColumnValue(t);
                        } else {
                            child = childField.get(child);
                        }
                    }

                }
            }
        }
    }

    public String format(int position){
       if(position >=0 && position< datas.size()){
          return format(datas.get(position));
       }
       return INVAL_VALUE;
    }

    public List<int[]> parseRanges(){
        if(isAutoMerge && maxMergeCount> 1) {
            if(ranges != null){
                ranges.clear();
            }else{
                ranges = new ArrayList<>();
            }
            int size = datas.size();
            String perVal = null;
            int rangeStartPosition= -1;
            int rangeCount = 1;
            for (int i = 0; i < size; i++) {
                String val = format(datas.get(i));
                if(rangeCount < maxMergeCount && perVal !=null && val !=null
                        && val.length() != 0 && val.equals(perVal)){
                    if(rangeStartPosition ==-1){
                        rangeStartPosition = i-1;
                    }
                    rangeCount++;

                    if(i == size-1){
                        int[] range = {rangeStartPosition, i};
                        ranges.add(range);
                        rangeStartPosition =-1;
                        rangeCount =1;
                    }
                }else{
                    if(rangeStartPosition !=-1){
                        int[] range = {rangeStartPosition, i-1};
                        ranges.add(range);
                        rangeStartPosition =-1;
                        rangeCount =1;
                    }
                }
                perVal = val;
            }
        }
        return ranges;
    }

    public String format(T t){
        String value;
        if (format != null) {
            value = format.format(t);
        } else {
            value = t == null ? INVAL_VALUE : t.toString();
        }
        return value;
    }

    protected void countColumnValue(T t) {
        if(t != null && isAutoCount && countFormat ==null){
            if(LetterUtils.isBasicType(t)){
                if(LetterUtils.isNumber(t)) {
                    countFormat = new NumberCountFormat<>();
                }else{
                    countFormat = new DecimalCountFormat<>();
                }
            }else{
                countFormat = new StringCountFormat<>(this);
            }
        }
        if(countFormat != null){
            countFormat.count(t);
        }
    }

    protected void addData(T t,boolean isFoot){
        synchronized (datas) {
            if (isFoot) {
                datas.add(t);
            } else {
                datas.add(0, t);
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getComputeWidth() {
        return computeWidth;
    }

    public void setComputeWidth(int computeWidth) {
        this.computeWidth = computeWidth;
    }

    public  String getTotalNumString(){
        if(countFormat != null){
            return countFormat.getCountString();
        }
        return "";
    }

    public List<Column> getChildren() {
        return children;
    }

    public void addChildren(Column column) {
        children.add(column);
    }

    public Comparator<T> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public ICountFormat<T, ? extends Number> getCountFormat() {
        return countFormat;
    }

    public void setCountFormat(ICountFormat<T, ? extends Number> countFormat) {
        this.countFormat = countFormat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Column o) {
        return  this.id - o.getId();
    }

    public boolean isAutoCount() {
        return isAutoCount;
    }

    public void setAutoCount(boolean autoCount) {
        isAutoCount = autoCount;
    }

    public boolean isReverseSort() {
        return isReverseSort;
    }

    public void setReverseSort(boolean reverseSort) {
        isReverseSort = reverseSort;
    }

    public OnColumnItemClickListener<T> getOnColumnItemClickListener() {
        return onColumnItemClickListener;
    }

    public void setOnColumnItemClickListener(OnColumnItemClickListener<T> onColumnItemClickListener) {
        this.onColumnItemClickListener = onColumnItemClickListener;
    }

    public void setOnColumnItemLongClickListener(OnColumnItemLongClickListener<T> onColumnItemLongClickListener) {
        this.onColumnItemLongClickListener = onColumnItemLongClickListener;
    }

    public OnColumnItemLongClickListener<T> getOnColumnItemLongClickListener() {
        return onColumnItemLongClickListener;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    public Paint.Align getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(Paint.Align textAlign) {
        this.textAlign = textAlign;
    }

    public boolean isAutoMerge() {
        return isAutoMerge;
    }

    public void setAutoMerge(boolean autoMerge) {
        isAutoMerge = autoMerge;
    }

    public int getMaxMergeCount() {
        return maxMergeCount;
    }

    public void setMaxMergeCount(int maxMergeCount) {
        this.maxMergeCount = maxMergeCount;
    }

    public boolean isFast() {
        return isFast;
    }

    public void setFast(boolean fast) {
        isFast = fast;
        drawFormat = isFast ? new FastTextDrawFormat<T>() : new TextDrawFormat<T>();
    }

    public int getSeizeCellSize(TableInfo tableInfo,int position){
        return tableInfo.getArrayLineSize()[position];
    }

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public Paint.Align getTitleAlign() {
        return titleAlign;
    }

    public void setTitleAlign(Paint.Align titleAlign) {
        this.titleAlign = titleAlign;
    }

    public void setWidth(int width) {
        if(width >0) {
            this.width = width;
            this.setDrawFormat(new MultiLineDrawFormat<T>(width));
        }
    }

    public int getWidth() {
        if(width == 0){
            return computeWidth;
        }
        return width;
    }

    public List<int[]> getRanges() {
        return ranges;
    }

    public void setRanges(List<int[]> ranges) {
        this.ranges = ranges;
    }
}
