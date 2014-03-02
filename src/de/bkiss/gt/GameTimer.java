package de.bkiss.gt;

/**
 *
 * @author boss
 */
public class GameTimer {
    
    private int day;
    private long initMillis;
    
    public GameTimer(){
        day = 0;
        initMillis = System.currentTimeMillis();
    }
    
    private void updateTimer(){
        long time = System.currentTimeMillis() - initMillis;
        day = (int) (time / 1000) / 30;
    }
    
    public int getDay(){
        updateTimer();
        return day;
    }
    
}
