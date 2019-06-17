package com.smarttable.data.format.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.data.CellInfo;
import com.smarttable.data.column.Column;
import com.smarttable.data.format.bg.ICellBackgroundFormat;
import com.smarttable.utils.DrawUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;


public class TextDrawFormat<T> implements IDrawFormat<T> {

    private Map<String, SoftReference<String[]>> valueMap;

    public TextDrawFormat() {
        valueMap = new HashMap<>();
    }

    @Override
    public int measureWidth(Column<T> column, int position, TableConfig config) {
        Paint paint = config.getPaint();
        config.getContentStyle().fillPaint(paint);
        return DrawUtils.getMultiTextWidth(paint, getSplitString(column.format(position))) + 2 * config.getHorizontalPadding();
    }

    @Override
    public int measureHeight(Column<T> column, int position, TableConfig config) {
        Paint paint = config.getPaint();
        config.getContentStyle().fillPaint(paint);
        return DrawUtils.getMultiTextHeight(paint, getSplitString(column.format(position))) + 2 * config.getVerticalPadding();
    }

    @Override
    public void draw(Canvas c, Rect rect, CellInfo<T> cellInfo, TableConfig config) {
        Paint paint = config.getPaint();
        setTextPaint(config, cellInfo, paint);
        if (cellInfo.column.getTextAlign() != null) {
            paint.setTextAlign(cellInfo.column.getTextAlign());
        }
        rect.set(rect.left + (int) (config.getHorizontalPadding() * config.getZoom()),
                rect.top + (int) (config.getVerticalPadding() * config.getZoom()),
                rect.right - (int) (config.getHorizontalPadding() * config.getZoom()),
                rect.bottom - (int) (config.getVerticalPadding() * config.getZoom()));
        drawText(c, cellInfo.value, rect, paint);
    }

    protected void drawText(Canvas c, String value, Rect rect, Paint paint) {
        DrawUtils.drawMultiText(c, paint, rect, getSplitString(value));
    }

    public void setTextPaint(TableConfig config, CellInfo<T> cellInfo, Paint paint) {
        config.getContentStyle().fillPaint(paint);
        ICellBackgroundFormat<CellInfo> backgroundFormat = config.getContentCellBackgroundFormat();
        if (backgroundFormat != null && backgroundFormat.getTextColor(cellInfo) != TableConfig.INVALID_COLOR) {
            paint.setColor(backgroundFormat.getTextColor(cellInfo));
        }
        paint.setTextSize(paint.getTextSize() * config.getZoom());
    }

    protected String[] getSplitString(String val) {
        String[] values = null;
        SoftReference<String[]> ref = valueMap.get(val);
        if (ref != null) {
            values = ref.get();
        }
        if (values == null) {
            values = val.split("\n");

            valueMap.put(val, new SoftReference<>(values));
        }
        return values;
    }
}
