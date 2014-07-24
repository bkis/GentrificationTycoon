package de.kritzelbit.gt.logic;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import de.kritzelbit.gt.IngameState;
import de.kritzelbit.gt.gui.GUIController;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 *
 * @author boss
 */
public class GameTimer extends Timer {

    private int millisEqualOneMonth;
    private Game game;
    private SimpleApplication app;
    
    public GameTimer(Application app, int millisEqualOneMonth, Game game){
        super();
        this.app = (SimpleApplication) app;
        this.millisEqualOneMonth = millisEqualOneMonth;
        this.game = game;
    }
    
    
    public void addDayTimeTask(){
        this.schedule(new DayTimeTask(), millisEqualOneMonth);
    }
    
    
    private class DayTimeTask extends TimerTask {
        @Override
        public void run() {
            app.enqueue(callNextDay);
            schedule(new DayTimeTask() , millisEqualOneMonth);
        }
    }
    
    
    private Callable<Boolean> callNextDay = new Callable<Boolean>() {
        public Boolean call(){
            game.nextMonth();
            return true;
        }
    };
    
    
}
