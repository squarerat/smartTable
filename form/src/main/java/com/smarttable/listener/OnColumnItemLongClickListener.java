package com.smarttable.listener;

import com.smarttable.data.column.Column;

public interface OnColumnItemLongClickListener<T> {
    void onLongClick(Column<T> column, String value, T t, int position);
}
