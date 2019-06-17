package com.smarttable.data.format.draw;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.smarttable.core.TableConfig;
import com.smarttable.data.CellInfo;


public abstract class LeftTopDrawFormat extends ImageResDrawFormat<String> {


    public LeftTopDrawFormat() {
        super(20, 20);
    }

    @Override
    protected int getResourceID(String s, String value, int position) {
        return getResourceID();
    }

    protected abstract int getResourceID();

   public void setImageSize(int w,int h){
       setImageWidth(w);
       setImageHeight(h);
   }


    @Override
    public void draw(Canvas c, Rect rect, CellInfo<String> cellInfo , TableConfig config) {

       float zoom = config.getZoom();
        config.setZoom(zoom>1?1:zoom);
        super.draw(c,  rect, cellInfo, config);
        config.setZoom(zoom);
    }
}
