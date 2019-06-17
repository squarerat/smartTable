package com.smarttable.data.format.count;


public interface ICountFormat<T,N extends Number> {

    void count(T t);

    N getCount();

    String getCountString();

    void clearCount();
}
