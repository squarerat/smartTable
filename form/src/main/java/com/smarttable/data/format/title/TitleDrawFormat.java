package com.smarttable.data.format.title;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.data.column.Column;
import com.smarttable.data.format.bg.ICellBackgroundFormat;
import com.smarttable.utils.DrawUtils;


public class TitleDrawFormat implements ITitleDrawFormat {

    private boolean isDrawBg;

    @Override
    public int measureWidth(Column column, TableConfig config) {
        Paint paint = config.getPaint();
        config.getColumnTitleStyle().fillPaint(paint);
        return (int) (paint.measureText(column.getColumnName()) + 2 * config.getColumnTitleHorizontalPadding());
    }


    @Override
    public int measureHeight(TableConfig config) {
        Paint paint = config.getPaint();
        config.getColumnTitleStyle().fillPaint(paint);
        return DrawUtils.getTextHeight(config.getColumnTitleStyle(), config.getPaint()) + 2 * config.getColumnTitleVerticalPadding();
    }

    @Override
    public void draw(Canvas c, Column column, Rect rect, TableConfig config) {
        Paint paint = config.getPaint();
        boolean isDrawBg = drawBackground(c, column, rect, config);
        config.getColumnTitleStyle().fillPaint(paint);
        ICellBackgroundFormat<Column> backgroundFormat = config.getColumnCellBackgroundFormat();

        paint.setTextSize(paint.getTextSize() * config.getZoom());
        if (isDrawBg && backgroundFormat.getTextColor(column) != TableConfig.INVALID_COLOR) {
            paint.setColor(backgroundFormat.getTextColor(column));
        }
        rect.set(rect.left + (int) (config.getColumnTitleHorizontalPadding() * config.getZoom()),
                rect.top + (int) (config.getColumnTitleVerticalPadding() * config.getZoom()),
                rect.right - (int) (config.getColumnTitleHorizontalPadding() * config.getZoom()),
                rect.bottom - (int) (config.getColumnTitleVerticalPadding() * config.getZoom()));
        drawText(c, column, rect, paint);
    }

    protected void drawText(Canvas c, Column column, Rect rect, Paint paint) {
        if (column.getTitleAlign() != null) {
            paint.setTextAlign(column.getTitleAlign());
        }
        c.drawText(column.getColumnName(), DrawUtils.getTextCenterX(rect.left, rect.right, paint), DrawUtils.getTextCenterY(rect.centerY(), paint), paint);
    }


    public boolean drawBackground(Canvas c, Column column, Rect rect, TableConfig config) {
        ICellBackgroundFormat<Column> backgroundFormat = config.getColumnCellBackgroundFormat();
        if (isDrawBg && backgroundFormat != null) {
            backgroundFormat.drawBackground(c, rect, column, config.getPaint());
            return true;
        }
        return false;
    }

    public boolean isDrawBg() {
        return isDrawBg;
    }

    public void setDrawBg(boolean drawBg) {
        isDrawBg = drawBg;
    }
}
