package com.smarttable.data.table;


import com.smarttable.data.Cell;
import com.smarttable.data.CellRange;
import com.smarttable.data.TableInfo;
import com.smarttable.data.column.Column;
import com.smarttable.data.column.ColumnInfo;
import com.smarttable.data.format.sequence.ISequenceFormat;
import com.smarttable.data.format.sequence.LetterSequenceFormat;
import com.smarttable.data.format.sequence.NumberSequenceFormat;
import com.smarttable.data.format.title.ITitleDrawFormat;
import com.smarttable.data.format.title.TitleDrawFormat;
import com.smarttable.listener.OnColumnItemClickListener;
import com.smarttable.listener.OnColumnItemLongClickListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TableData<T> {

    private String tableName;
    private TableInfo tableInfo = new TableInfo();
    private final CopyOnWriteArrayList<Column> columns = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<T> t = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Column> childColumns = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ColumnInfo> columnInfos = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ColumnInfo> childColumnInfos = new CopyOnWriteArrayList<>();
    private Column sortColumn;
    private boolean showCount;
    private ITitleDrawFormat titleDrawFormat;
    private ISequenceFormat XSequenceFormat;
    private ISequenceFormat YSequenceFormat;

    private List<CellRange> userSetRangeAddress;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnRowClickListener<T> onRowClickListener;
    private OnRowLongClickListener<T> onRowLongClickListener;
    private OnColumnClickListener<?> onColumnClickListener;

    public TableData(String tableName, List<T> t, List<Column> columns) {
        this(tableName, t, columns, null);

    }

    public TableData(String tableName, List<T> t, Column... columns) {
        this(tableName, t, Arrays.asList(columns));
    }

    public TableData(String tableName, List<T> t, List<Column> columns, ITitleDrawFormat titleDrawFormat) {
        this.tableName = tableName;
        this.columns.addAll(columns);
        this.t.addAll(t);
        tableInfo.setLineSize(t.size());

        this.titleDrawFormat = titleDrawFormat == null ? new TitleDrawFormat() : titleDrawFormat;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumns() {
        synchronized (columns) {
            return columns;
        }
    }

    public void setColumns(List<Column> columns) {
        synchronized (this.columns) {
            this.columns.clear();
            this.columns.addAllAbsent(columns);
        }
    }

    public List<T> getT() {
        synchronized (t) {
            return t;
        }
    }

    public void setT(List<T> t) {
        synchronized (this.t) {
            this.t.clear();
            this.t.addAllAbsent(t);
        }
    }

    protected void updateLineSize() {
        tableInfo.setLineSize(t.size());
    }

    public List<Column> getChildColumns() {
        synchronized (childColumns) {
            return childColumns;
        }
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public List<ColumnInfo> getColumnInfos() {
        synchronized (columnInfos) {
            return columnInfos;
        }
    }

    public List<ColumnInfo> getChildColumnInfos() {
        synchronized (this.childColumnInfos) {
            return childColumnInfos;
        }
    }

    public void setChildColumnInfos(List<ColumnInfo> childColumnInfos) {
        synchronized (this.childColumnInfos) {
            this.childColumnInfos.clear();
            this.childColumnInfos.addAllAbsent(childColumnInfos);
        }

    }

    public void setColumnInfos(List<ColumnInfo> columnInfos) {
        synchronized (this.columnInfos) {
            this.columnInfos.clear();
            this.columnInfos.addAllAbsent(columnInfos);
        }
    }

    public void setChildColumns(List<Column> childColumns) {
        synchronized (this.childColumns) {
            this.childColumns.clear();
            this.childColumns.addAllAbsent(childColumns);
        }
    }

    public Column getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(Column sortColumn) {
        this.sortColumn = sortColumn;
    }

    public boolean isShowCount() {
        return showCount;
    }

    public void setShowCount(boolean showCount) {
        this.showCount = showCount;
    }

    public ITitleDrawFormat getTitleDrawFormat() {
        return titleDrawFormat;
    }

    public void setTitleDrawFormat(ITitleDrawFormat titleDrawFormat) {
        this.titleDrawFormat = titleDrawFormat;
    }

    public ISequenceFormat getXSequenceFormat() {
        if (XSequenceFormat == null) {
            XSequenceFormat = new LetterSequenceFormat();
        }
        return XSequenceFormat;
    }

    public void setXSequenceFormat(ISequenceFormat XSequenceFormat) {
        this.XSequenceFormat = XSequenceFormat;
    }

    public ISequenceFormat getYSequenceFormat() {
        if (YSequenceFormat == null) {
            YSequenceFormat = new NumberSequenceFormat();
        }
        return YSequenceFormat;
    }

    public void setYSequenceFormat(ISequenceFormat YSequenceFormat) {
        this.YSequenceFormat = YSequenceFormat;
    }

    public Column getColumnByID(int id) {
        List<Column> columns = getChildColumns();
        for (Column column : columns) {
            if (column.getId() == id) {
                return column;
            }
        }
        return null;
    }

    public Column getColumnByFieldName(String fieldName) {
        List<Column> columns = getChildColumns();
        for (Column column : columns) {
            if (column.getFieldName().equals(fieldName)) {
                return column;
            }
        }
        return null;
    }

    public int getLineSize() {
        return tableInfo.getLineHeightArray().length;
    }

    private void addCellRange(int firstRow, int lastRow, int firstCol, int lastCol) {
        Cell[][] tableCells = tableInfo.getRangeCells();
        Cell realCell = null;
        if (tableCells != null) {
            for (int i = firstRow; i <= lastRow; i++) {
                if (i < tableCells.length)
                    for (int j = firstCol; j <= lastCol; j++) {
                        if (j < tableCells[i].length) {
                            if (i == firstRow && j == firstCol) {
                                int rowCount = Math.min(lastRow + 1, tableCells.length) - firstRow;
                                int colCount = Math.min(lastCol + 1, tableCells[i].length) - firstCol;
                                realCell = new Cell(colCount, rowCount);
                                tableCells[i][j] = realCell;
                                continue;
                            }
                            tableCells[i][j] = new Cell(realCell);
                        }
                    }
            }
        }
    }

    public void addCellRange(CellRange range) {
        addCellRange(range.getFirstRow(), range.getLastRow(),
                range.getFirstCol(), range.getLastCol());

    }

    public void clearCellRangeAddresses() {

        if (userSetRangeAddress != null) {
            for (CellRange range : userSetRangeAddress) {
                addCellRange(range);
            }

        }
    }

    public void setUserCellRange(List<CellRange> userCellRange) {
        this.userSetRangeAddress = userCellRange;
    }

    public List<CellRange> getUserCellRange() {
        return userSetRangeAddress;
    }

    public void clear() {
        t.clear();
        columns.clear();
        columnInfos.clear();
        childColumns.clear();
        childColumnInfos.clear();

        if (userSetRangeAddress != null) {
            userSetRangeAddress.clear();
            userSetRangeAddress = null;
        }
        if (tableInfo != null) {
            tableInfo.clear();
            tableInfo = null;
        }
        sortColumn = null;
        titleDrawFormat = null;
        XSequenceFormat = null;
        YSequenceFormat = null;

    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        for (Column column : columns) {
            setColumnClick(column, new OnColumnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object t, int position) {
                    if (onItemClickListener != null) {
                        int index = childColumns.indexOf(column);
                        TableData.this.onItemClickListener.onClick(column, value, t, index, position);
                    }
                }
            });
        }
    }

    private void setColumnClick(Column column, OnColumnItemClickListener listener) {
        if (column.isParent()) {
            for (Object child : column.getChildren()) {
                setColumnClick((Column) child, listener);
            }
        } else {
            column.setOnColumnItemClickListener(listener);
        }
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return this.onItemLongClickListener;
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        for (Column column : columns) {
            setColumnLongClick(column, new OnColumnItemLongClickListener() {
                @Override
                public void onLongClick(Column column, String value, Object t, int position) {
                    if (onItemLongClickListener != null) {
                        int index = childColumns.indexOf(column);
                        TableData.this.onItemLongClickListener.onLongClick(column, value, t, index, position);
                    }
                }
            });
        }
    }

    private void setColumnLongClick(Column column, OnColumnItemLongClickListener listener) {
        if (column.isParent()) {
            for (Object child : column.getChildren()) {
                setColumnLongClick((Column) child, listener);
            }
        } else {
            column.setOnColumnItemLongClickListener(listener);
        }
    }

    public void setOnRowClickListener(final OnRowClickListener<T> onRowClickListener) {
        this.onRowClickListener = onRowClickListener;
        if (this.onRowClickListener != null) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object o, int col, int row) {
                    TableData.this.onRowClickListener.onClick(column, getT().get(row), col, row);
                }
            });
        }
    }

    public void setOnRowLongClickListener(final OnRowLongClickListener<T> onRowLongClickListener) {
        this.onRowLongClickListener = onRowLongClickListener;
        if (this.onRowLongClickListener != null) {
            setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onLongClick(Column column, String value, Object o, int col, int row) {
                    TableData.this.onRowLongClickListener.onLongClick(column, getT().get(row), col, row);
                }
            });
        }
    }

    public void setOnColumnClickListener(final OnColumnClickListener onColumnClickListener) {
        this.onColumnClickListener = onColumnClickListener;
        if (this.onRowClickListener != null) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object o, int col, int row) {
                    TableData.this.onColumnClickListener.onClick(column, column.getDatas(), col, row);
                }
            });
        }
    }

    public OnRowClickListener getOnRowClickListener() {
        return onRowClickListener;
    }

    public interface OnItemClickListener<T> {
        void onClick(Column<T> column, String value, T t, int col, int row);
    }

    public interface OnRowClickListener<T> {
        void onClick(Column column, T t, int col, int row);
    }

    public interface OnRowLongClickListener<T> {
        void onLongClick(Column column, T t, int col, int row);
    }

    public interface OnColumnClickListener<T> {
        void onClick(Column column, List<T> t, int col, int row);
    }

    public interface OnItemLongClickListener<T> {
        void onLongClick(Column<T> column, String value, T t, int col, int row);
    }
}
