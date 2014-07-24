package de.kritzelbit.gt.utils;

public class Format {
    
    
    public static String twoDecimals(float f){
        String s = f + "000";
        return s.substring(0, s.indexOf(".") + 2);
    }
    
    
    public static String money(long money){
        return String.format("%,d", money);
    }
    
    
}
