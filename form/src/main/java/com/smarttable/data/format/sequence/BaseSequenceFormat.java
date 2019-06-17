package com.smarttable.data.format.sequence;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.utils.DrawUtils;


public abstract class BaseSequenceFormat implements ISequenceFormat{
    @Override
    public void draw(Canvas canvas, int sequence, Rect rect, TableConfig config) {

        Paint paint  = config.getPaint();
        paint.setTextSize(paint.getTextSize()*(config.getZoom()>1?1:config.getZoom()));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(format(sequence+1),rect.centerX(), DrawUtils.getTextCenterY(rect.centerY(),paint) ,paint);
    }
}
