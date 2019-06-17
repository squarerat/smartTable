package com.smarttable.matrix;

import android.view.MotionEvent;
import android.view.View;

public interface ITouch {

    void onDisallowInterceptEvent(View view, MotionEvent event);

    boolean handlerTouchEvent(MotionEvent event);

}
