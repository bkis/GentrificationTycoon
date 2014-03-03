package de.bkiss.gt.objects;

import com.jme3.app.Application;
import de.bkiss.gt.District;

/**
 *
 * @author boss
 */
public class Land extends GameObject{
    
    public Land(Application app, String name, District district){
        super(app, GameObject.TYPE_LAND, name, district);
    }
    
}
