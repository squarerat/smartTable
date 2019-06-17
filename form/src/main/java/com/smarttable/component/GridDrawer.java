package com.smarttable.component;

import android.graphics.Rect;

import com.smarttable.data.Cell;
import com.smarttable.data.column.Column;
import com.smarttable.data.table.TableData;

import java.util.List;

public class GridDrawer<T> {

    private TableData<T> tableData;
    private Cell[][] rangePoints;

    public GridDrawer() {

    }

    public void setTableData(TableData<T> tableData) {
        this.tableData = tableData;
        this.rangePoints = tableData.getTableInfo().getRangeCells();

    }

    public Rect correctCellRect(int row, int col, Rect rect, float zoom) {
        if (rangePoints != null && rangePoints.length > row) {
            Cell point = rangePoints[row][col];
            if (point != null) {
                if (point.col != Cell.INVALID && point.row != Cell.INVALID) {
                    List<Column> childColumns = tableData.getChildColumns();
                    int[] lineHeights = tableData.getTableInfo().getLineHeightArray();
                    int width = 0, height = 0;
                    for (int i = col; i < Math.min(childColumns.size(), col + point.col); i++) {
                        width += childColumns.get(i).getComputeWidth();
                    }
                    for (int i = row; i < Math.min(lineHeights.length, row + point.row); i++) {
                        height += lineHeights[i];
                    }
                    rect.right = (int) (rect.left + width * zoom);
                    rect.bottom = (int) (rect.top + height * zoom);
                    return rect;
                }
                return null;
            }
        }
        return rect;
    }

}
