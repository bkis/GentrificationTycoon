package de.bkiss.gt.logic;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author boss
 */
public class GameTimer extends Timer {

    private int millisEqualOneMonth;
    private Game game;
    
    public GameTimer(int millisEqualOneMonth, Game game){
        super();
        this.millisEqualOneMonth = millisEqualOneMonth;
        this.game = game;
    }
    
    
    public void addDayTimeTask(){
        this.schedule(new DayTimeTask(), millisEqualOneMonth);
    }
    
    
    private class DayTimeTask extends TimerTask {
        @Override
        public void run() {
            game.nextMonth();
            schedule(new DayTimeTask() , millisEqualOneMonth);
        }
    }
    
    
}
