package com.smarttable.core;

import android.graphics.Paint;

import com.smarttable.data.CellInfo;
import com.smarttable.data.column.Column;
import com.smarttable.data.format.bg.IBackgroundFormat;
import com.smarttable.data.format.bg.ICellBackgroundFormat;
import com.smarttable.data.format.draw.LeftTopDrawFormat;
import com.smarttable.data.format.grid.IGridFormat;
import com.smarttable.data.format.grid.SimpleGridFormat;
import com.smarttable.data.style.FontStyle;
import com.smarttable.data.style.LineStyle;

public class TableConfig {

    private static final FontStyle defaultFontStyle = new FontStyle();

    private static final LineStyle defaultGridStyle = new LineStyle();

    public static final int INVALID_COLOR = 0;

    public int dp10;

    private FontStyle contentStyle;

    private FontStyle YSequenceStyle;

    private FontStyle XSequenceStyle;

    private FontStyle columnTitleStyle;

    private FontStyle tableTitleStyle;

    private FontStyle countStyle;

    private LineStyle columnTitleGridStyle;

    private LineStyle SequenceGridStyle;

    private LineStyle contentGridStyle;

    private int verticalPadding = 10;

    private int sequenceVerticalPadding = 10;

    private int textLeftOffset = 0;

    private int sequenceHorizontalPadding = 40;

    private int columnTitleVerticalPadding = 10;

    private int columnTitleHorizontalPadding = 40;

    private int horizontalPadding = 40;

    private IBackgroundFormat columnTitleBackground;

    private IBackgroundFormat contentBackground;

    private IBackgroundFormat countBackground;

    private IBackgroundFormat YSequenceBackground;

    private IBackgroundFormat XSequenceBackground;

    private IGridFormat tableGridFormat = new SimpleGridFormat();

    private boolean isShowXSequence = true;

    private boolean isShowYSequence = true;

    private boolean isShowTableTitle = true;

    private boolean isShowColumnTitle = true;

    private ICellBackgroundFormat<CellInfo> contentCellBackgroundFormat;

    private ICellBackgroundFormat<Column> columnCellBackgroundFormat;

    private ICellBackgroundFormat<Integer> XSequenceCellBgFormat;

    private ICellBackgroundFormat<Integer> YSequenceCellBgFormat;

    private ICellBackgroundFormat<Column> countBgCellFormat;

    private boolean fixedYSequence = false;

    private boolean fixedXSequence = false;

    private boolean fixedTitle = true;

    private boolean fixedFirstColumn = true;

    private boolean fixedCountRow = true;

    private int leftAndTopBackgroundColor;

    private LeftTopDrawFormat leftTopDrawFormat;

    private int minTableWidth = -1;

    private Paint paint;

    private float zoom = 1;

    public FontStyle getContentStyle() {
        if (contentStyle == null) {
            return defaultFontStyle;
        }
        return contentStyle;
    }

    public TableConfig setContentStyle(FontStyle contentStyle) {
        this.contentStyle = contentStyle;
        return this;
    }

    public FontStyle getYSequenceStyle() {
        if (YSequenceStyle == null) {
            return defaultFontStyle;
        }
        return YSequenceStyle;
    }

    public TableConfig setYSequenceStyle(FontStyle YSequenceStyle) {
        this.YSequenceStyle = YSequenceStyle;
        return this;
    }

    public FontStyle getXSequenceStyle() {
        if (XSequenceStyle == null) {
            return defaultFontStyle;
        }
        return XSequenceStyle;
    }

    public TableConfig setXSequenceStyle(FontStyle XSequenceStyle) {
        this.XSequenceStyle = XSequenceStyle;
        return this;
    }

    public FontStyle getColumnTitleStyle() {
        if (columnTitleStyle == null) {
            return defaultFontStyle;
        }
        return columnTitleStyle;
    }

    public TableConfig setColumnTitleStyle(FontStyle columnTitleStyle) {
        this.columnTitleStyle = columnTitleStyle;
        return this;
    }

    public int getVerticalPadding() {
        return verticalPadding;
    }

    public TableConfig setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
        return this;
    }

    public int getHorizontalPadding() {
        return horizontalPadding;
    }

    public TableConfig setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
        return this;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public LineStyle getContentGridStyle() {
        if (contentGridStyle == null) {
            return defaultGridStyle;
        }
        return contentGridStyle;
    }

    public LineStyle getColumnTitleGridStyle() {
        if (columnTitleGridStyle == null) {
            return defaultGridStyle;
        }
        return columnTitleGridStyle;
    }

    public TableConfig setColumnTitleGridStyle(LineStyle columnTitleGridStyle) {
        this.columnTitleGridStyle = columnTitleGridStyle;
        return this;
    }

    public boolean isFixedYSequence() {
        return fixedYSequence;
    }

    public TableConfig setFixedYSequence(boolean fixedYSequence) {
        this.fixedYSequence = fixedYSequence;
        return this;
    }

    public boolean isFixedXSequence() {
        return fixedXSequence;
    }

    public TableConfig setFixedXSequence(boolean fixedXSequence) {
        this.fixedXSequence = fixedXSequence;
        return this;
    }

    public boolean isFixedTitle() {
        return fixedTitle;
    }

    public TableConfig setFixedTitle(boolean fixedTitle) {
        this.fixedTitle = fixedTitle;
        return this;
    }

    public boolean isFixedFirstColumn() {
        return fixedFirstColumn;
    }

    @Deprecated
    public TableConfig setFixedFirstColumn(boolean fixedFirstColumn) {
        this.fixedFirstColumn = fixedFirstColumn;
        return this;
    }

    public FontStyle getCountStyle() {
        if (countStyle == null) {
            return defaultFontStyle;
        }
        return countStyle;
    }

    public TableConfig setCountStyle(FontStyle countStyle) {
        this.countStyle = countStyle;
        return this;
    }

    public TableConfig setContentGridStyle(LineStyle contentGridStyle) {
        this.contentGridStyle = contentGridStyle;
        return this;
    }

    public boolean isFixedCountRow() {
        return fixedCountRow;
    }

    public TableConfig setFixedCountRow(boolean fixedCountRow) {
        this.fixedCountRow = fixedCountRow;
        return this;
    }

    public FontStyle getTableTitleStyle() {
        if (tableTitleStyle == null) {
            return defaultFontStyle;
        }
        return tableTitleStyle;
    }

    public TableConfig setTableTitleStyle(FontStyle tableTitleStyle) {
        this.tableTitleStyle = tableTitleStyle;
        return this;
    }

    public boolean isShowXSequence() {
        return isShowXSequence;
    }

    public TableConfig setShowXSequence(boolean showXSequence) {
        isShowXSequence = showXSequence;
        return this;
    }

    public boolean isShowYSequence() {
        return isShowYSequence;
    }

    public TableConfig setShowYSequence(boolean showYSequence) {
        isShowYSequence = showYSequence;
        return this;
    }

    public ICellBackgroundFormat<CellInfo> getContentCellBackgroundFormat() {
        return contentCellBackgroundFormat;
    }

    public TableConfig setContentCellBackgroundFormat(ICellBackgroundFormat<CellInfo> contentCellBackgroundFormat) {
        this.contentCellBackgroundFormat = contentCellBackgroundFormat;
        return this;
    }

    public ICellBackgroundFormat<Column> getColumnCellBackgroundFormat() {
        return columnCellBackgroundFormat;
    }

    public TableConfig setColumnCellBackgroundFormat(ICellBackgroundFormat<Column> columnCellBackgroundFormat) {
        this.columnCellBackgroundFormat = columnCellBackgroundFormat;
        return this;
    }

    public ICellBackgroundFormat<Integer> getXSequenceCellBgFormat() {
        return XSequenceCellBgFormat;
    }

    public TableConfig setXSequenceCellBgFormat(ICellBackgroundFormat<Integer> XSequenceCellBgFormat) {
        this.XSequenceCellBgFormat = XSequenceCellBgFormat;
        return this;
    }

    public ICellBackgroundFormat<Integer> getYSequenceCellBgFormat() {
        return YSequenceCellBgFormat;
    }

    public TableConfig setYSequenceCellBgFormat(ICellBackgroundFormat<Integer> YSequenceCellBgFormat) {
        this.YSequenceCellBgFormat = YSequenceCellBgFormat;
        return this;
    }

    public ICellBackgroundFormat<Column> getCountBgCellFormat() {
        return countBgCellFormat;
    }

    public TableConfig setCountBgCellFormat(ICellBackgroundFormat<Column> countBgCellFormat) {
        this.countBgCellFormat = countBgCellFormat;
        return this;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public int getColumnTitleHorizontalPadding() {
        return columnTitleHorizontalPadding;
    }

    public TableConfig setColumnTitleHorizontalPadding(int columnTitleHorizontalPadding) {
        this.columnTitleHorizontalPadding = columnTitleHorizontalPadding;
        return this;
    }

    public boolean isShowTableTitle() {
        return isShowTableTitle;
    }

    public TableConfig setShowTableTitle(boolean showTableTitle) {
        isShowTableTitle = showTableTitle;
        return this;
    }

    public boolean isShowColumnTitle() {

        return isShowColumnTitle;
    }

    public int getLeftAndTopBackgroundColor() {
        return leftAndTopBackgroundColor;
    }

    public TableConfig setLeftAndTopBackgroundColor(int leftAndTopBackgroundColor) {
        this.leftAndTopBackgroundColor = leftAndTopBackgroundColor;
        return this;
    }

    public LeftTopDrawFormat getLeftTopDrawFormat() {
        return leftTopDrawFormat;
    }

    public void setLeftTopDrawFormat(LeftTopDrawFormat leftTopDrawFormat) {
        this.leftTopDrawFormat = leftTopDrawFormat;
    }

    public TableConfig setShowColumnTitle(boolean showColumnTitle) {
        isShowColumnTitle = showColumnTitle;
        return this;
    }

    public LineStyle getSequenceGridStyle() {
        if (SequenceGridStyle == null) {
            return defaultGridStyle;
        }
        return SequenceGridStyle;
    }

    public TableConfig setSequenceGridStyle(LineStyle sequenceGridStyle) {
        SequenceGridStyle = sequenceGridStyle;
        return this;
    }


    public TableConfig setMinTableWidth(int minTableWidth) {
        this.minTableWidth = minTableWidth;
        return this;
    }

    public int getMinTableWidth() {
        return minTableWidth;
    }

    public IBackgroundFormat getYSequenceBackground() {
        return YSequenceBackground;
    }

    public TableConfig setYSequenceBackground(IBackgroundFormat YSequenceBackground) {
        this.YSequenceBackground = YSequenceBackground;
        return this;
    }

    public int getColumnTitleVerticalPadding() {
        return columnTitleVerticalPadding;
    }

    public TableConfig setColumnTitleVerticalPadding(int columnTitleVerticalPadding) {
        this.columnTitleVerticalPadding = columnTitleVerticalPadding;
        return this;
    }

    public IBackgroundFormat getColumnTitleBackground() {
        return columnTitleBackground;
    }

    public TableConfig setColumnTitleBackground(IBackgroundFormat columnTitleBackground) {
        this.columnTitleBackground = columnTitleBackground;
        return this;

    }

    public IBackgroundFormat getContentBackground() {
        return contentBackground;
    }

    public TableConfig setContentBackground(IBackgroundFormat contentBackground) {
        this.contentBackground = contentBackground;
        return this;
    }

    public IBackgroundFormat getCountBackground() {
        return countBackground;
    }

    public TableConfig setCountBackground(IBackgroundFormat countBackground) {
        this.countBackground = countBackground;
        return this;
    }

    public IBackgroundFormat getXSequenceBackground() {
        return XSequenceBackground;
    }

    public TableConfig setXSequenceBackground(IBackgroundFormat XSequenceBackground) {
        this.XSequenceBackground = XSequenceBackground;
        return this;
    }

    public IGridFormat getTableGridFormat() {
        return tableGridFormat;
    }

    public TableConfig setTableGridFormat(IGridFormat tableGridFormat) {
        this.tableGridFormat = tableGridFormat;
        return this;
    }

    public int getSequenceVerticalPadding() {
        return sequenceVerticalPadding;
    }

    public TableConfig setSequenceVerticalPadding(int sequenceVerticalPadding) {
        this.sequenceVerticalPadding = sequenceVerticalPadding;
        return this;
    }

    public int getSequenceHorizontalPadding() {
        return sequenceHorizontalPadding;
    }

    public TableConfig setSequenceHorizontalPadding(int sequenceHorizontalPadding) {
        this.sequenceHorizontalPadding = sequenceHorizontalPadding;
        return this;
    }

    public int getTextLeftOffset() {
        return textLeftOffset;
    }

    public TableConfig setTextLeftOffset(int textLeftOffset) {
        this.textLeftOffset = textLeftOffset;
        return this;
    }
}
