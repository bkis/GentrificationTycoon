package de.bkiss.gt.logic;

import de.bkiss.gt.gui.GUIController;
import de.bkiss.gt.tenants.Tenant;
import java.util.List;

/**
 *
 * @author boss
 */
public class Game {
    
    private Player player;
    private District district;
    private GameTimer timer;
    private GUIController guiController;
    
    private long day;
    
    public Game(String playerName,
            String playerIconPath,
            District district,
            GUIController guiController){
        
        this.day = 0;
        player = new Player(playerName, playerIconPath);
        timer = new GameTimer();
        this.district = district;
        this.guiController = guiController;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public GameTimer getTimer(){
        return timer;
    }
    
    public District getDistrict(){
        return district;
    }
    
    public void nextDay(){
        day++;
        guiController.displayGameTime(day);
        guiController.displayGentrificationState();
        /**
         * EXECUTE
         * EVERY
         * DAY !!!
         */
    }
    
}
