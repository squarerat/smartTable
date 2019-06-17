package com.smarttable.data.format.count;


public class NumberCountFormat<T> implements ICountFormat<T,Long> {

    private long  totalLongCount =0;

    @Override
    public void count(T t) {
         Number number = (Number) t;
        if(number instanceof Integer){
            totalLongCount+=number.intValue();
        }else if(number instanceof Long){
            totalLongCount+=number.longValue();

        } else if(number instanceof  Byte){
            totalLongCount+=number.byteValue();

        }else if(number instanceof Short){
            totalLongCount+=number.shortValue();
        }
    }

    @Override
    public Long getCount() {
        return  totalLongCount;

    }


    @Override
    public String getCountString() {
        return String.valueOf(totalLongCount);
    }

    @Override
    public void clearCount() {
        totalLongCount = 0;
    }
}
