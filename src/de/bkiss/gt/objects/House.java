package de.bkiss.gt.objects;

import com.jme3.app.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author boss
 */
public class House extends GameObject {
    
    private int condition;      // 0-100    < w/ time
    //private int luxury;         // 0-100    type (+expansions bonuses)
    //private int neighborhood;   // 0-100    streets, infrastructure
    
    private Set<Expansion> expansions;
    
    public House(Application app, String type, String name){
        super(app, type, name);
        expansions = new HashSet<Expansion>();
    }
    
    
    public int getCondition() {
        return condition;
    }

    
    public void setCondition(int condition) {
        this.condition = condition;
    }

    
    public int getLuxury() {
        int lux = 0;
        
        if      (type.equals(GameObject.TYPE_HOUSE_1))
            lux += 10;
        else if (type.equals(GameObject.TYPE_HOUSE_2))
            lux += 30;
        else if (type.equals(GameObject.TYPE_HOUSE_3))
            lux += 60;
        
        for (Expansion e : expansions)
            lux += e.getEffect();
        
        return lux;
    }

    
    public void addExpansion(Expansion exp){
        if (expansions.contains(exp)){
            System.out.println("Could not add expansion '" + exp + "' to '"
                    + this);
        } else {
            expansions.add(exp);
            System.out.println("Added expansion '" + exp + "' to '" + this);
        }
    }
    
}
