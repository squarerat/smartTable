package com.smarttable.data.format.selected;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;


public class BaseSelectFormat implements ISelectFormat {


     @Override
     public void draw(Canvas canvas, Rect rect,Rect showRect, TableConfig config) {
          Paint paint = config.getPaint();
          paint.setColor(Color.parseColor("#3A5FCD"));
          paint.setStyle(Paint.Style.STROKE);
          paint.setStrokeWidth(3);
          canvas.drawRect(rect,paint);
          paint.setStyle(Paint.Style.FILL);
          canvas.drawRect(rect.left-10,rect.top-10,rect.left+10,rect.top+10,paint);
          canvas.drawRect(rect.right-10,rect.bottom-10,rect.right+10,rect.bottom+10,paint);
     }
}
