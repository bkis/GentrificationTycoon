package de.bkiss.gt.logic;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author boss
 */
public class GameTimer extends Timer {

    private int millisEqualOneDay;
    private DayTimeTask dtt;
    
    public GameTimer(int millisEqualOneDay, Game game){
        super();
        this.millisEqualOneDay = millisEqualOneDay;
        this.dtt = new DayTimeTask(game);
    }
    
    
    public void addDayTimeTask(){
        this.schedule(dtt, millisEqualOneDay);
    }
    
    
    private class DayTimeTask extends TimerTask {

        private Game game;
        
        public DayTimeTask(Game game){
            this.game = game;
        }
        
        @Override
        public void run() {
            game.nextDay();
            schedule(dtt , millisEqualOneDay);
        }
        
    }
    
    
}
