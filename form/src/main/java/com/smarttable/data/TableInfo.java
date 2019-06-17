package com.smarttable.data;

import android.graphics.Rect;

import com.smarttable.data.column.ArrayColumn;
import com.smarttable.data.column.Column;
import com.smarttable.data.column.ColumnNode;


public class TableInfo {

    private int topHeight;
    private int titleHeight;
    private int tableTitleSize;
    private int yAxisWidth;
    private int countHeight;
    private int titleDirection;
    private Rect tableRect;
    private int maxLevel = 1;
    private int columnSize;
    private int[] lineHeightArray;
    private float zoom = 1;
    private Cell[][] rangeCells;
    private int lineSize;
    private ColumnNode topNode;
    private int[] arrayLineSize;
    private boolean isHasArrayColumn;

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
        rangeCells = new Cell[lineSize][columnSize];

    }

    public int getTopHeight() {
        return topHeight;
    }

    public int getTopHeight(float zoom) {
        return (int) (topHeight * zoom);
    }

    public void setTopHeight(int topHeight) {
        this.topHeight = topHeight;
    }

    public int getTitleHeight() {
        return titleHeight;
    }

    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }

    public Rect getTableRect() {
        return tableRect;
    }

    public void setTableRect(Rect tableRect) {
        this.tableRect = tableRect;
    }

    public int getyAxisWidth() {
        return yAxisWidth;
    }

    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
        this.lineHeightArray = new int[lineSize];

    }

    public void addLine(int count, boolean isFoot) {
        lineSize += count;
        int size = lineHeightArray.length;
        int[] tempArray = new int[size + count];

        if (isFoot) {
            System.arraycopy(lineHeightArray, 0, tempArray, 0, size);
        } else {
            System.arraycopy(lineHeightArray, 0, tempArray, count, size);
        }
        lineHeightArray = tempArray;
        if (!isHasArrayColumn) {
            if (size == rangeCells.length) {
                Cell[][] tempRangeCells = new Cell[size + count][columnSize];
                for (int i = 0; i < size; i++) {
                    tempRangeCells[i + (isFoot ? 0 : count)] = rangeCells[i];
                }
                rangeCells = tempRangeCells;
            }
        }
    }

    public int getCountHeight() {
        return (int) (countHeight * zoom);
    }

    public void setCountHeight(int countHeight) {
        this.countHeight = countHeight;
    }

    public int[] getLineHeightArray() {
        return lineHeightArray;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void setyAxisWidth(int yAxisWidth) {
        this.yAxisWidth = yAxisWidth;
    }

    public int getTableTitleSize() {
        return tableTitleSize;
    }

    public void setTableTitleSize(int tableTitleSize) {
        this.tableTitleSize = tableTitleSize;
    }

    public int getTitleDirection() {
        return titleDirection;
    }

    public void setTitleDirection(int titleDirection) {
        this.titleDirection = titleDirection;
    }

    public Cell[][] getRangeCells() {
        return rangeCells;
    }

    public void clear() {
        rangeCells = null;
        lineHeightArray = null;
        tableRect = null;
        topNode = null;
    }

    public ColumnNode getTopNode() {
        return topNode;
    }

    public void setTopNode(ColumnNode topNode) {
        this.topNode = topNode;
        if (this.topNode != null) {
            isHasArrayColumn = true;
            rangeCells = null;
        }
    }

    public void countTotalLineSize(ArrayColumn bottomColumn) {
        if (topNode != null) {
            arrayLineSize = new int[lineSize];
            int totalSize = 0;
            for (int i = 0; i < lineSize; i++) {
                arrayLineSize[i] = bottomColumn.getStructure().getLevelCellSize(-1, i);
                totalSize += arrayLineSize[i];
            }
            lineHeightArray = new int[totalSize];
            rangeCells = null;
        }
    }

    public int getSeizeCellSize(Column column, int position) {
        if (topNode != null) {
            return column.getSeizeCellSize(this, position);
        }
        return 1;
    }

    public int[] getArrayLineSize() {
        return arrayLineSize;
    }

}
