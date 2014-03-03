package de.bkiss.gt.objects;

import com.jme3.app.Application;
import de.bkiss.gt.District;

/**
 *
 * @author boss
 */
public class ConstructionSite extends GameObject{
    
    public ConstructionSite(Application app, String name, District district){
        super(app, GameObject.TYPE_CONSTRUCTION, name, district);
    }
    
}
