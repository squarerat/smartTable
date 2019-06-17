package com.smarttable.data.format.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.smarttable.core.TableConfig;
import com.smarttable.data.CellInfo;
import com.smarttable.data.column.Column;
import com.smarttable.utils.DensityUtils;
import com.smarttable.utils.DrawUtils;


public class MultiLineDrawFormat<T> extends TextDrawFormat<T> {

    private int width;
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);


    public  MultiLineDrawFormat(int width) {
        this.width = width;
    }


    public  MultiLineDrawFormat(Context context,int dpWidth) {
        this.width = DensityUtils.dp2px(context,dpWidth);
    }

    @Override
    public int measureWidth(Column<T> column,int position, TableConfig config) {

        return width;
    }

    @Override
    public int measureHeight(Column<T> column, int position, TableConfig config) {
        config.getContentStyle().fillPaint(textPaint);
        StaticLayout sl = new StaticLayout(column.format(position), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        return sl.getHeight();
    }

    @Override
    public void draw(Canvas c, Rect rect, CellInfo<T> cellInfo ,TableConfig config) {
        setTextPaint(config, cellInfo, textPaint);
        if(cellInfo.column.getTextAlign() !=null) {
            textPaint.setTextAlign(cellInfo.column.getTextAlign());
        }
        int hPadding = (int) (config.getHorizontalPadding()*config.getZoom());
        int realWidth =rect.width() - 2*hPadding;
        StaticLayout staticLayout = new StaticLayout(cellInfo.column.format(cellInfo.row), textPaint, realWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        c.save();
        c.translate(DrawUtils.getTextCenterX(rect.left+hPadding,rect.right-hPadding,textPaint), rect.top+(rect.height()-staticLayout.getHeight())/2);
        staticLayout.draw(c);
        c.restore();
    }
}



