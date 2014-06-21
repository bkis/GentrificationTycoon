package de.bkiss.gt.logic;

/**
 *
 * @author boss
 */
public class Player {
    
    private String name;
    private int money;
    private String iconPath;
    
    
    public Player(String name, String iconPath){
        this.name = name;
        this.iconPath = iconPath;
        this.money = 1000000;
    }
    
    
    public String getName(){
        return name;
    }
    
    
    public String getIconPath(){
        return iconPath;
    }
    
    
    public int getMoney(){
        return money;
    }
    
    
    public void setMoney(int money){
        this.money = money;
    }
    
    
    public void reduceMoney(int by){
        this.money -= by;
    }
    
}
