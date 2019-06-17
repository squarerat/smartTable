package com.smarttable.utils;


public class LetterUtils {

    public static String ToNumberSystem26(int n) {
        StringBuilder s = new StringBuilder();
        while (n > 0) {
            int m = n % 26;
            if (m == 0) m = 26;
            s.insert(0, (char) (m + 64));
            n = (n - m) / 26;
        }
        return s.toString();
    }

    public static boolean isBasicType(Object data) {
        return data instanceof Number;
    }

    public static boolean isNumber(Object data) {
        return !(data instanceof Float || data instanceof Double);
    }

}
