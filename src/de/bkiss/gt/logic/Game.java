package de.bkiss.gt.logic;

import de.bkiss.gt.gui.GUIController;

/**
 *
 * @author boss
 */
public class Game {
    
    private Player player;
    private District district;
    private GameTimer timer;
    private GUIController guiController;
    
    public Game(String playerName,
            String playerIconPath,
            District district,
            GUIController guiController){
        
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
        System.out.println("NEXT DAY!!!!!");
        /**
         * EXECUTE
         * EVERY
         * DAY !!!
         */
    }
    
}
