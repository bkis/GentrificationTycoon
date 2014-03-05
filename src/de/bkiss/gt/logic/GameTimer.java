package de.bkiss.gt.logic;

import de.bkiss.gt.logic.Game;
import de.bkiss.gt.objects.ConstructionSite;
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
    
    
    public void addConstructionTask(ConstructionSite cs, int milliDelay){
        ConstructionTask tt = new ConstructionTask(cs);
        this.schedule(tt, milliDelay);
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
    
    
    private class ConstructionTask extends TimerTask {
        
        private ConstructionSite cs;
        
        public ConstructionTask(ConstructionSite cs){
            this.cs = cs;
        }
        
        @Override
        public void run() {
            cs.finish();
        }
    }
    
    
}
