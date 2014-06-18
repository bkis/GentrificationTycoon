package de.bkiss.gt.logic;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author boss
 */
public class GameTimer extends Timer {

    
    public GameTimer(){
        super();
    }
    
    
    public void addDayTimeTask(Game game, int millisEqualOneDay){
        DayTimeTask dtt = new DayTimeTask(game);
        this.scheduleAtFixedRate(dtt, 0, millisEqualOneDay);
    }
    
    
    private class DayTimeTask extends TimerTask {

        private Game game;
        
        public DayTimeTask(Game game){
            this.game = game;
        }
        
        @Override
        public void run() {
            game.nextDay();
        }
        
    }
    
    
}
