package de.bkiss.gt.objects;

import com.jme3.app.Application;
import de.bkiss.gt.logic.District;

/**
 *
 * @author boss
 */
public class PublicBuilding extends GameObject{
    
    
    public PublicBuilding(Application app,
            String type,
            String name,
            District district){
        
        super(app, type, name, district);
    }
    
}
