package de.kritzelbit.gt.objects;

import com.jme3.app.Application;
import de.kritzelbit.gt.logic.District;
import de.kritzelbit.gt.tenants.Tenant;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author boss
 */
public class House extends GameObject {
    
    private int rent;
    private Set<Expansion> expansions;
    
    private Tenant tenant;
    
    
    public House(Application app, String type, String name, District district){
        super(app, type, name, district);
        expansions = new HashSet<Expansion>();
        rent = 0;
    }
    
    
    public int getLuxury() {
        int lux = 0;
        
        if (type.equals(GameObject.TYPE_HOUSE_1))
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
        return (int)(Math.pow(getLuxury()*2 + getNeighborhoodValue(), 1.5f));
    }
    
    
    public void applyDefaultRent(){
        this.rent = calcDefaultRent();
    }
    
    
    public boolean hasExpansion(String expName){
        for(Expansion e : expansions)
            if (e.getName().equalsIgnoreCase(expName))
                return true;
        return false;
    }
    
    
    public void setTenant(Tenant tenant){
        this.tenant = tenant;
        setOccupied(true);
    }
    
    
    public void removeTenant(){
        tenant = null;
        setOccupied(false);
    }
    
    
    public Tenant getTenant(){
        return tenant;
    }
    
    
    public Set<Expansion> getExpansions(){
        return expansions;
    }
    
}
