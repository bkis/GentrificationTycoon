package de.bkiss.gt.utils;

public class Format {
    
    
    public static String twoDecimals(float f){
        String s = f + "000";
        return s.substring(0, s.indexOf(".") + 2);
    }
    
    
}
