package com.smarttable.listener;

import com.smarttable.data.column.Column;

public interface OnColumnItemClickListener<T> {
    void onClick(Column<T> column,String value, T t, int position);
}
