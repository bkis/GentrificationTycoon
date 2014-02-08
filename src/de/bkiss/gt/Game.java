package de.bkiss.gt;

/**
 *
 * @author boss
 */
public class Game {
    
    private Player player;
    private Bank bank;
    private District district;
    private GameTimer timer;
    
    
    public Game(String playerName, String playerIconPath){
        player = new Player(playerName, playerIconPath);
        bank = new Bank();
        district = new District();
        timer = new GameTimer();
    }
    
}
