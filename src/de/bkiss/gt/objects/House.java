package de.bkiss.gt.objects;

import com.jme3.app.Application;

/**
 *
 * @author boss
 */
public class House extends GameObject {
    
    public House(Application app, String type, String name){
        super(app, type, name);
        
        System.out.println("Haus erstellt: " + this);
        //TODO
    }
    
}
