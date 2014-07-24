package de.kritzelbit.gt.logic;

/**
 *
 * @author boss
 */
public class Player {
    
    private String name;
    private long money;
    private String iconPath;
    
    
    public Player(String name, String iconPath){
        this.name = name;
        this.iconPath = iconPath;
        this.money = 3000000;
    }
    
    
    public String getName(){
        return name;
    }
    
    
    public String getIconPath(){
        return iconPath;
    }
    
    
    public long getMoney(){
        return money;
    }
    
    
    public void setMoney(long money){
        this.money = money;
    }
    
    
    public void reduceMoney(long by){
        this.money -= by;
    }
    
    
    public void addMoney(long by){
        this.money += by;
    }
    
}
