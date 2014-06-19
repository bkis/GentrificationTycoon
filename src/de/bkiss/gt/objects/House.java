package de.bkiss.gt.objects;

import com.jme3.app.Application;
import de.bkiss.gt.logic.District;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author boss
 */
public class House extends GameObject {
    
    private int rent;
    //private int luxury;         // 0-100    type (+expansions bonuses)
    //private int neighborhood;   // 0-100    streets, infrastructure
    
    private Set<Expansion> expansions;
    
    public House(Application app, String type, String name, District district){
        super(app, type, name, district);
        expansions = new HashSet<Expansion>();
        rent = 0;
    }
    
    
    public int getLuxury() {
        int lux = 0;
        
        if (type.equals(GameObject.TYPE_HOUSE_1))
            lux += 5;
        else if (type.equals(GameObject.TYPE_HOUSE_2))
            lux += 20;
        else if (type.equals(GameObject.TYPE_HOUSE_3))
            lux += 50;
        
        for (Expansion e : expansions)
            lux += e.getEffect();
        
        return lux;
    }

    
    public void addExpansion(Expansion exp){
        for (Expansion e : expansions)
            if (e.getName().equals(exp.getName()))
                return;
        expansions.add(exp);
    }
    
    
    public int getRent(){
        return rent;
    }
    
    
    public void setRent(int rent){
        this.rent = rent;
    }
    
    
    public int calcDefaultRent(){
        return (int)(Math.pow(getLuxury()*2 + getNeighborhoodValue(), 1.5f)+400);
    }
    
    
    public void applyDefaultRent(){
        this.rent = calcDefaultRent();
    }
    
}
