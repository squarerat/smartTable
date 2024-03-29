package com.smarttable.data.format.count;


public class DecimalCountFormat<T> implements ICountFormat<T,Double> {

    private double  totalDoubleCount =0;


    @Override
    public void count(T t) {
         Number number = (Number) t;
         if(number instanceof Double){
            totalDoubleCount+=number.doubleValue();
        }else if(number instanceof Float){
            totalDoubleCount+=number.floatValue();
        }
    }

    @Override
    public Double getCount() {

        return totalDoubleCount;
    }


    @Override
    public String getCountString() {
        return String.valueOf(totalDoubleCount);
    }

    @Override
    public void clearCount() {
        totalDoubleCount = 0;
    }
}
