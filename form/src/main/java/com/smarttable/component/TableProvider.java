package com.smarttable.component;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.data.CellInfo;
import com.smarttable.data.TableInfo;
import com.smarttable.data.column.Column;
import com.smarttable.data.column.ColumnInfo;
import com.smarttable.data.format.bg.ICellBackgroundFormat;
import com.smarttable.data.format.selected.IDrawOver;
import com.smarttable.data.format.selected.ISelectFormat;
import com.smarttable.data.format.tip.ITip;
import com.smarttable.data.table.TableData;
import com.smarttable.listener.OnColumnClickListener;
import com.smarttable.listener.TableClickObserver;
import com.smarttable.utils.DrawUtils;

import java.util.List;


public class TableProvider<T> implements TableClickObserver {


    private Rect scaleRect;
    private Rect showRect;
    private TableConfig config;
    private PointLF clickPoint;
    private ColumnInfo clickColumnInfo;
    private ClickType isClickPoint = ClickType.NONE;
    private OnColumnClickListener onColumnClickListener;

    private SelectionOperation operation;
    private TableData<T> tableData;
    private ITip<Column, ?> tip;
    private Rect clipRect;
    private Rect tempRect;
    private Column tipColumn;
    private int tipPosition;
    private GridDrawer<T> gridDrawer;
    private PointF tipPoint = new PointF();
    private IDrawOver drawOver;
    private CellInfo cellInfo = new CellInfo();

    public TableProvider() {

        clickPoint = new PointLF(-1, -1, false);
        clipRect = new Rect();
        tempRect = new Rect();
        operation = new SelectionOperation();
        gridDrawer = new GridDrawer<>();
    }


    public void onDraw(Canvas canvas, Rect scaleRect, Rect showRect,
                       TableData<T> tableData, TableConfig config) {
        setData(scaleRect, showRect, tableData, config);
        canvas.save();
        canvas.clipRect(this.showRect);
        drawColumnTitle(canvas, config);
        drawContent(canvas);
        drawCount(canvas);
        operation.draw(canvas, showRect, config);
        if (drawOver != null)
            drawOver.draw(canvas, scaleRect, showRect, config);
        canvas.restore();
        if (isClickPoint.isClick() && clickColumnInfo != null) {
            onColumnClickListener.onClick(clickColumnInfo);
        }
        if (tipColumn != null) {
            drawTip(canvas, tipPoint.x, tipPoint.y, tipColumn, tipPosition);
        }
    }


    private void setData(Rect scaleRect, Rect showRect, TableData<T> tableData, TableConfig config) {
        isClickPoint = ClickType.NONE;
        clickColumnInfo = null;
        tipColumn = null;
        operation.reset();
        this.scaleRect = scaleRect;
        this.showRect = showRect;
        this.config = config;
        this.tableData = tableData;
        gridDrawer.setTableData(tableData);
    }


    private void drawColumnTitle(Canvas canvas, TableConfig config) {
        if (config.isShowColumnTitle()) {
            if (config.isFixedTitle()) {
                drawTitle(canvas);
                canvas.restore();
                canvas.save();
                canvas.clipRect(this.showRect);
            } else {
                drawTitle(canvas);
            }
        }
    }


    private void drawCount(Canvas canvas) {
        if (tableData.isShowCount()) {
            float left = scaleRect.left;
            float bottom = config.isFixedCountRow() ? Math.min(scaleRect.bottom, showRect.bottom) : scaleRect.bottom;
            int countHeight = tableData.getTableInfo().getCountHeight();
            float top = bottom - countHeight;
            if (config.getCountBackground() != null) {
                tempRect.set((int) left, (int) top, showRect.right, (int) bottom);
                config.getCountBackground().drawBackground(canvas, tempRect, config.getPaint());
            }
            List<ColumnInfo> childColumnInfos = tableData.getChildColumnInfos();
            if (DrawUtils.isVerticalMixRect(showRect, (int) top, (int) bottom)) {
                List<Column> columns = tableData.getChildColumns();
                int columnSize = columns.size();
                boolean isPerColumnFixed = false;
                clipRect.set(showRect);
                int clipCount = 0;
                for (int i = 0; i < columnSize; i++) {
                    Column column = columns.get(i);
                    float tempLeft = left;
                    float width = column.getComputeWidth() * config.getZoom();
                    if (childColumnInfos.get(i).getTopParent().column.isFixed()) {
                        if (left < clipRect.left) {
                            left = clipRect.left;
                            clipRect.left += width;
                            isPerColumnFixed = true;
                        }
                    } else if (isPerColumnFixed) {
                        canvas.save();
                        clipCount++;
                        canvas.clipRect(clipRect.left, Math.min(bottom, showRect.bottom) - countHeight,
                                showRect.right, showRect.bottom);
                    }
                    tempRect.set((int) left, (int) top, (int) (left + width), (int) bottom);
                    drawCountText(canvas, column, i, tempRect, column.getTotalNumString(), config);
                    left = tempLeft;
                    left += width;
                }
                for (int i = 0; i < clipCount; i++) {
                    canvas.restore();
                }
            }
        }
    }


    private void drawTitle(Canvas canvas) {
        int dis = showRect.top - scaleRect.top;
        float zoom = config.getZoom();
        TableInfo tableInfo = tableData.getTableInfo();
        int titleHeight = (int) (tableInfo.getTitleHeight() * tableInfo.getMaxLevel() * zoom);
        int clipHeight = (int) ((config.isFixedTitle() ? titleHeight : Math.max(0, titleHeight - dis)) * zoom);
        if (config.getColumnTitleBackground() != null) {
            tempRect.set(showRect.left, showRect.top, showRect.right, showRect.top + titleHeight);
            config.getColumnTitleBackground().drawBackground(canvas, tempRect, config.getPaint());
        }
        clipRect.set(showRect);
        List<ColumnInfo> columnInfoList = tableData.getColumnInfos();
        boolean isPerColumnFixed = false;
        int clipCount = 0;
        ColumnInfo parentColumnInfo = null;
        for (ColumnInfo info : columnInfoList) {
            int left = (int) (info.left * zoom + scaleRect.left);

            if (info.top == 0 && info.column.isFixed()) {
                if (left < clipRect.left) {
                    parentColumnInfo = info;
                    left = clipRect.left;
                    fillColumnTitle(canvas, info, left);
                    clipRect.left += info.width * zoom;
                    isPerColumnFixed = true;
                    continue;
                }

            } else if (isPerColumnFixed && info.top != 0) {
                left = (int) (clipRect.left - info.width * zoom);
                left += (info.left - parentColumnInfo.left);
            } else if (isPerColumnFixed) {
                canvas.save();
                canvas.clipRect(clipRect.left, showRect.top, showRect.right, showRect.top + titleHeight);
                isPerColumnFixed = false;
                clipCount++;
            }
            fillColumnTitle(canvas, info, left);
        }
        for (int i = 0; i < clipCount; i++) {
            canvas.restore();
        }
        if (config.isFixedTitle()) {
            scaleRect.top += titleHeight;
            showRect.top += titleHeight;
        } else {
            showRect.top += clipHeight;
            scaleRect.top += titleHeight;
        }

    }


    private void fillColumnTitle(Canvas canvas, ColumnInfo info, int left) {

        int top = (int) (info.top * config.getZoom() + (config.isFixedTitle() ? showRect.top : scaleRect.top));
        int right = (int) (left + info.width * config.getZoom());
        int bottom = (int) (top + info.height * config.getZoom());
        if (DrawUtils.isMixRect(showRect, left, top, right, bottom)) {
            if (isClickPoint.isNone() && onColumnClickListener != null) {
                if (DrawUtils.isClick(left, top, right, bottom, clickPoint)) {
                    if (clickPoint.isLongClick) {
                        isClickPoint = ClickType.LONG_CLICK;
                    } else {
                        isClickPoint = ClickType.CLICK;
                    }
                    clickColumnInfo = info;
                    clickPoint.reset();
                }
            }
            Paint paint = config.getPaint();
            tempRect.set(left, top, right, bottom);
            if (config.getTableGridFormat() != null) {
                config.getColumnTitleGridStyle().fillPaint(paint);
                int position = tableData.getChildColumns().indexOf(info.column);
                config.getTableGridFormat().drawColumnTitleGrid(canvas, tempRect, info.column, position, paint);
            }
            tableData.getTitleDrawFormat().draw(canvas, info.column, tempRect, config);
        }
    }


    private void drawContent(Canvas canvas) {
        float top;
        float left = scaleRect.left;
        List<Column> columns = tableData.getChildColumns();
        clipRect.set(showRect);
        TableInfo info = tableData.getTableInfo();
        int columnSize = columns.size();
        int dis = config.isFixedCountRow() ? info.getCountHeight()
                : showRect.bottom + info.getCountHeight() - scaleRect.bottom;
        int fillBgBottom = showRect.bottom - Math.max(dis, 0);
        if (config.getContentBackground() != null) {
            tempRect.set(showRect.left, showRect.top, showRect.right, fillBgBottom);
            config.getContentBackground().drawBackground(canvas, tempRect, config.getPaint());
        }
        if (config.isFixedCountRow()) {
            canvas.save();
            canvas.clipRect(showRect.left, showRect.top, showRect.right, showRect.bottom - info.getCountHeight());
        }
        List<ColumnInfo> childColumnInfo = tableData.getChildColumnInfos();
        boolean isPerFixed = false;
        int clipCount = 0;
        Rect correctCellRect;
        TableInfo tableInfo = tableData.getTableInfo();

        int firstVisibleRow = 0;
        int lastVisibleRow = 0;

        if (!columns.isEmpty()) {
            for (int j = 0; j < columns.get(0).getDatas().size(); j++) {
                correctCellRect = gridDrawer.correctCellRect(j, 0, tempRect, config.getZoom());
                if (correctCellRect.top < showRect.bottom) lastVisibleRow = j;
                else if (correctCellRect.bottom < showRect.top) firstVisibleRow = j;
                else break;
            }
        }

        for (int i = 0; i < columnSize; i++) {
            top = scaleRect.top;
            Column column = columns.get(i);
            float width = column.getComputeWidth() * config.getZoom();
            float tempLeft = left;

            Column topColumn = childColumnInfo.get(i).getTopParent().column;
            if (topColumn.isFixed()) {
                isPerFixed = false;
                if (tempLeft < clipRect.left) {
                    left = clipRect.left;
                    clipRect.left += width;
                    isPerFixed = true;
                }
            } else if (isPerFixed) {
                canvas.save();
                canvas.clipRect(clipRect);
                isPerFixed = false;
                clipCount++;
            }
            float right = left + width;

            if (left < showRect.right) {
                int realPosition = 0;

                for (int j = firstVisibleRow; j <= lastVisibleRow; j++) {
                    String value = column.format(j);
                    int skip = tableInfo.getSeizeCellSize(column, j);
                    int totalLineHeight = 0;
                    for (int k = realPosition; k < realPosition + skip; k++) {
                        totalLineHeight += info.getLineHeightArray()[k];
                    }
                    realPosition += skip;
                    float bottom = top + totalLineHeight * config.getZoom();
                    tempRect.set((int) left, (int) top, (int) right, (int) bottom);
                    correctCellRect = gridDrawer.correctCellRect(j, i, tempRect, config.getZoom());
                    if (correctCellRect != null) {
                        if (correctCellRect.top < showRect.bottom) {
                            if (correctCellRect.right > showRect.left && correctCellRect.bottom > showRect.top) {
                                Object data = column.getDatas().get(j);
                                if (DrawUtils.isClick(correctCellRect, clickPoint)) {
                                    operation.setSelectionRect(i, j, correctCellRect);
                                    tipPoint.x = (left + right) / 2;
                                    tipPoint.y = (top + bottom) / 2;
                                    tipColumn = column;
                                    tipPosition = j;
                                    if (clickPoint.isLongClick) {
                                        longClickColumn(column, j, value, data);
                                        isClickPoint = ClickType.LONG_CLICK;
                                    } else {
                                        clickColumn(column, j, value, data);
                                        isClickPoint = ClickType.CLICK;
                                    }
                                    clickPoint.reset();
                                }
                                operation.checkSelectedPoint(i, j, correctCellRect);
                                cellInfo.set(column, data, value, i, j);
                                drawContentCell(canvas, cellInfo, correctCellRect, config);

                            }
                        } else {
                            break;
                        }
                    }
                    top = bottom;
                }
                left = tempLeft + width;
            } else {
                break;
            }
        }
        for (int i = 0; i < clipCount; i++) {
            canvas.restore();
        }
        if (config.isFixedCountRow()) {
            canvas.restore();
        }
    }


    protected void drawContentCell(Canvas c, CellInfo<T> cellInfo, Rect rect, TableConfig config) {

        if (config.getContentCellBackgroundFormat() != null) {
            config.getContentCellBackgroundFormat().drawBackground(c, rect, cellInfo, config.getPaint());
        }
        if (config.getTableGridFormat() != null) {
            config.getContentGridStyle().fillPaint(config.getPaint());
            config.getTableGridFormat().drawContentGrid(c, cellInfo.col, cellInfo.row, rect, cellInfo, config.getPaint());
        }
        rect.left += config.getTextLeftOffset();
        cellInfo.column.getDrawFormat().draw(c, rect, cellInfo, config);
    }


    private void clickColumn(Column column, int position, String value, Object data) {
        if (isClickPoint.isNone() && column.getOnColumnItemClickListener() != null) {
            column.getOnColumnItemClickListener().onClick(column, value, data, position);
        }
    }

    public void longClickColumn(Column column, int position, String value, Object data) {
        if (isClickPoint.isNone() && column.getOnColumnItemLongClickListener() != null) {
            column.getOnColumnItemLongClickListener().onLongClick(column, value, data, position);
        }
    }


    private void drawTip(Canvas canvas, float x, float y, Column c, int position) {
        if (tip != null) {
            tip.drawTip(canvas, x, y, showRect, c, position);
        }
    }

    private void drawCountText(Canvas canvas, Column column, int position, Rect rect, String text, TableConfig config) {
        Paint paint = config.getPaint();
        ICellBackgroundFormat<Column> backgroundFormat = config.getCountBgCellFormat();
        if (backgroundFormat != null) {
            backgroundFormat.drawBackground(canvas, rect, column, config.getPaint());
        }
        if (config.getTableGridFormat() != null) {
            config.getContentGridStyle().fillPaint(paint);
            config.getTableGridFormat().drawCountGrid(canvas, position, rect, column, paint);
        }
        config.getCountStyle().fillPaint(paint);
        if (backgroundFormat != null && backgroundFormat.getTextColor(column) != TableConfig.INVALID_COLOR) {
            paint.setColor(backgroundFormat.getTextColor(column));
        }
        paint.setTextSize(paint.getTextSize() * config.getZoom());
        if (column.getTextAlign() != null) {
            paint.setTextAlign(column.getTextAlign());
        }
        rect.set(rect.left + config.getHorizontalPadding(),
                rect.top + config.getVerticalPadding(),
                rect.right - config.getHorizontalPadding(),
                rect.bottom - config.getVerticalPadding());
        canvas.drawText(text, DrawUtils.getTextCenterX(rect.left, rect.right, paint), DrawUtils.getTextCenterY(rect.centerY(), paint), paint);
    }


    @Override
    public void onClick(float x, float y) {
        clickPoint.x = x;
        clickPoint.y = y;
        clickPoint.isLongClick = false;
    }

    @Override
    public void onLongClick(float x, float y) {
        clickPoint.x = x;
        clickPoint.y = y;
        clickPoint.isLongClick = true;
    }

    public OnColumnClickListener getOnColumnClickListener() {
        return onColumnClickListener;
    }

    public void setOnColumnClickListener(OnColumnClickListener onColumnClickListener) {
        this.onColumnClickListener = onColumnClickListener;
    }

    public ITip<Column, ?> getTip() {
        return tip;
    }

    public void setTip(ITip<Column, ?> tip) {
        this.tip = tip;
    }


    public void setSelectFormat(ISelectFormat selectFormat) {
        this.operation.setSelectFormat(selectFormat);
    }

    public GridDrawer<T> getGridDrawer() {
        return gridDrawer;
    }

    public void setGridDrawer(GridDrawer<T> gridDrawer) {
        this.gridDrawer = gridDrawer;
    }


    public int[] getPointLocation(double row, double col) {
        List<Column> childColumns = tableData.getChildColumns();
        int[] lineHeights = tableData.getTableInfo().getLineHeightArray();
        int x = 0, y = 0;
        int columnSize = childColumns.size();
        for (int i = 0; i <= (columnSize > col + 1 ? col + 1 : columnSize - 1); i++) {
            int w = childColumns.get(i).getComputeWidth();
            if (i == (int) col + 1) {
                x += w * (col - (int) col);
            } else {
                x += w;
            }
        }
        for (int i = 0; i <= (lineHeights.length > row + 1 ? row + 1 : lineHeights.length - 1); i++) {
            int h = lineHeights[i];
            if (i == (int) row + 1) {
                y += h * (row - (int) row);
            } else {
                y += h;
            }
        }
        x *= config.getZoom();
        y *= config.getZoom();
        x += scaleRect.left;
        y += scaleRect.top;
        return new int[]{x, y};

    }

    public int[] getPointSize(int row, int col) {
        List<Column> childColumns = tableData.getChildColumns();
        int[] lineHeights = tableData.getTableInfo().getLineHeightArray();
        col = col < childColumns.size() ? col : childColumns.size() - 1;
        row = row < lineHeights.length ? row : lineHeights.length;
        col = col < 0 ? 0 : col;
        row = row < 0 ? 0 : row;
        return new int[]{(int) (childColumns.get(col).getComputeWidth() * config.getZoom()),
                (int) (lineHeights[row] * config.getZoom())};

    }

    public void setDrawOver(IDrawOver drawOver) {
        this.drawOver = drawOver;
    }

    public SelectionOperation getOperation() {
        return operation;
    }

    private class PointLF extends PointF {
        boolean isLongClick;

        public PointLF(float x, float y, boolean isLongClick) {
            super(x, y);
            this.isLongClick = isLongClick;
        }

        public final void reset() {
            set(-1, -1);
            isLongClick = false;
        }
    }

    private enum ClickType {
        NONE,
        CLICK,
        LONG_CLICK;

        public boolean isClick() {
            return this == CLICK;
        }

        public boolean isLongClick() {
            return this == LONG_CLICK;
        }

        public boolean isNone() {
            return this == NONE;
        }
    }

}
