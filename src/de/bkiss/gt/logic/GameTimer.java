package de.bkiss.gt.logic;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author boss
 */
public class GameTimer extends Timer {

    private int millisEqualOneDay;
    private Game game;
    
    public GameTimer(int millisEqualOneDay, Game game){
        super();
        this.millisEqualOneDay = millisEqualOneDay;
        this.game = game;
    }
    
    
    public void addDayTimeTask(){
        this.schedule(new DayTimeTask(), millisEqualOneDay);
    }
    
    
    private class DayTimeTask extends TimerTask {
        @Override
        public void run() {
            game.nextDay();
            schedule(new DayTimeTask() , millisEqualOneDay);
        }
    }
    
    
}
