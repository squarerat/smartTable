package com.smarttable.core;

import com.smarttable.data.ArrayStructure;
import com.smarttable.data.CellRange;
import com.smarttable.data.TableInfo;
import com.smarttable.data.column.ArrayColumn;
import com.smarttable.data.column.Column;
import com.smarttable.data.column.ColumnNode;
import com.smarttable.data.table.ArrayTableData;
import com.smarttable.data.table.TableData;
import com.smarttable.exception.TableException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableParser<T> {

    private ArrayColumn bottomColumn;

    public List<Column> parse(TableData<T> tableData) {
        tableData.getChildColumns().clear();
        tableData.getColumnInfos().clear();
        int maxLevel = getChildColumn(tableData);
        TableInfo tableInfo = tableData.getTableInfo();
        tableInfo.setColumnSize(tableData.getChildColumns().size());
        tableInfo.setMaxLevel(maxLevel);
        tableData.clearCellRangeAddresses();
        addArrayNode(tableInfo, tableData.getChildColumns());
        if (!(tableData instanceof ArrayTableData)) {
            sort(tableData);
            try {
                List<T> dataList = tableData.getT();
                int i = 0;
                for (Column column : tableData.getChildColumns()) {
                    column.fillData(dataList);
                    List<int[]> ranges = column.parseRanges();
                    if (ranges != null && ranges.size() > 0) {
                        for (int[] range : ranges) {
                            tableData.addCellRange(new CellRange(range[0], range[1], i, i));
                        }
                    }
                    i++;
                }
                calculateArrayCellSize(tableInfo, tableData.getChildColumns());
            } catch (NoSuchFieldException e) {
                throw new TableException(
                        "NoSuchFieldException :Please check whether field name is correct!");
            } catch (IllegalAccessException e) {
                throw new TableException(
                        "IllegalAccessException :Please make sure that access objects are allowed!");
            }
        }
        return tableData.getColumns();
    }

    private void addArrayNode(TableInfo tableInfo, List<Column> childColumns) {
        for (Column child : childColumns) {
            if (child instanceof ArrayColumn) {
                ArrayColumn column = (ArrayColumn) child;
                ColumnNode topNode = tableInfo.getTopNode();
                if (topNode == null) {
                    topNode = new ColumnNode("top", null);
                    tableInfo.setTopNode(topNode);
                }
                String[] nodeNames = column.getFieldName().split("\\.");
                loop1:
                for (int i = 0; i < nodeNames.length; i++) {
                    String nodeName = nodeNames[i];
                    for (ColumnNode node : topNode.getChildren()) {
                        if (node.getName().equals(nodeName)) {
                            topNode = node;
                            continue loop1;
                        }
                    }
                    ColumnNode childNode;
                    if (i == nodeNames.length - 1) {
                        childNode = new ColumnNode(nodeName, topNode, column);
                        column.setNode(childNode);
                    } else {
                        childNode = new ColumnNode(nodeName, topNode);
                    }
                    topNode.getChildren().add(childNode);
                    topNode = childNode;
                }
            }
        }
        int maxLevel = 0;
        for (Column child : childColumns) {
            if (child instanceof ArrayColumn) {
                ArrayColumn column = (ArrayColumn) child;
                int level = column.getLevel();
                if (maxLevel <= level) {
                    maxLevel = level;
                    bottomColumn = column;
                    bottomColumn.getStructure().setEffective(true);
                }
            }

        }
    }

    private void calculateArrayCellSize(TableInfo tableInfo, List<Column> childColumns) {
        if (bottomColumn != null) {
            ArrayStructure bottomStructure = bottomColumn.getStructure();
            for (Column child : childColumns) {
                if (child instanceof ArrayColumn) {
                    ArrayColumn column = (ArrayColumn) child;
                    ArrayStructure structure = column.getStructure();
                    int level = column.getStructure().getMaxLevel();
                    if (structure.getCellSizes() == null) {
                        structure.setCellSizes(new ArrayList<Integer>());
                    } else {
                        structure.getCellSizes().clear();
                    }
                    int size = column.getDatas().size();
                    for (int i = 0; i < size; i++) {
                        int cellSize = bottomStructure.getLevelCellSize(level, i);
                        column.getStructure().getCellSizes().add(cellSize);
                    }
                }
            }
            tableInfo.countTotalLineSize(bottomColumn);
        }
    }

    public void addData(TableData<T> tableData, List<T> addData, boolean isFoot) {

        try {

            int size = tableData.getLineSize();
            if (isFoot) {
                tableData.getT().addAll(addData);
            } else {
                tableData.getT().addAll(0, addData);
            }
            TableInfo tableInfo = tableData.getTableInfo();
            tableInfo.addLine(addData.size(), isFoot);
            tableData.clearCellRangeAddresses();
            int i = 0;
            for (Column column : tableData.getChildColumns()) {
                column.addData(addData, size, isFoot);
                List<int[]> ranges = column.parseRanges();
                if (ranges != null && ranges.size() > 0) {
                    for (int[] range : ranges) {
                        tableData.addCellRange(new CellRange(range[0], range[1], i, i));
                    }
                }
                i++;
            }
            calculateArrayCellSize(tableInfo, tableData.getChildColumns());

        } catch (NoSuchFieldException e) {
            throw new TableException(
                    "NoSuchFieldException :Please check whether field name is correct!");
        } catch (IllegalAccessException e) {
            throw new TableException(
                    "IllegalAccessException :Please make sure that access objects are allowed!");
        }
    }

    public List<Column> sort(TableData<T> tableData) {
        final Column sortColumn = tableData.getSortColumn();
        if (sortColumn != null) {
            List<T> dataList = new ArrayList<>(tableData.getT());
            Collections.sort(dataList, new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    try {
                        if (o1 == null && o2 == null) {
                            return 0;
                        }
                        if (o1 == null) {
                            return sortColumn.isReverseSort() ? 1 : -1;
                        }
                        if (o2 == null) {
                            return sortColumn.isReverseSort() ? -1 : 1;
                        }
                        Object data = sortColumn.getData(o1);
                        Object compareData = sortColumn.getData(o2);
                        if (data == null && compareData == null) {
                            return 0;
                        }
                        if (data == null) {
                            return sortColumn.isReverseSort() ? 1 : -1;
                        }
                        if (compareData == null) {
                            return sortColumn.isReverseSort() ? -1 : 1;
                        }
                        int compare;
                        if (sortColumn.getComparator() != null) {
                            compare = sortColumn.getComparator().compare(data, compareData);
                            return sortColumn.isReverseSort() ? -compare : compare;
                        } else {
                            if (data instanceof Comparable) {
                                compare = ((Comparable) data).compareTo(compareData);
                                return sortColumn.isReverseSort() ? -compare : compare;
                            }
                            return 0;
                        }
                    } catch (NoSuchFieldException e) {
                        throw new TableException(
                                "NoSuchFieldException :Please check whether field name is correct!");
                    } catch (IllegalAccessException e) {
                        throw new TableException(
                                "IllegalAccessException :Please make sure that access objects are allowed!");
                    }
                }
            });

            tableData.setT(dataList);
        }
        return tableData.getColumns();
    }

    private int getChildColumn(TableData<T> tableData) {
        int maxLevel = 0;
        for (Column column : tableData.getColumns()) {
            int level = getColumnLevel(tableData, column, 0);
            if (level > maxLevel) {
                maxLevel = level;
            }
        }
        return maxLevel;
    }

    private int getColumnLevel(TableData<T> tableData, Column column, int level) {
        level++;
        if (column.isParent()) {
            List<Column> children = column.getChildren();
            int maxLevel = 0;
            for (Column child : children) {
                int childLevel = getColumnLevel(tableData, child, level);
                if (maxLevel < childLevel) {
                    maxLevel = childLevel;
                    column.setLevel(maxLevel);
                }
            }
            level = maxLevel;
            return level;
        } else {
            tableData.getChildColumns().add(column);
            return level;
        }
    }

}
