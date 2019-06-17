package com.smarttable.listener;

public interface OnTableChangeListener {

    void onTableChanged(float scale, float translateX, float translateY);

    void onScrolled(float dx, float dy);

}
