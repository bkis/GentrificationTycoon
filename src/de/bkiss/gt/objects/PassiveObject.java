package de.bkiss.gt.objects;

import com.jme3.app.Application;
import com.jme3.scene.Spatial;

/**
 *
 * @author boss
 */
public class PassiveObject extends GameObject{
    
    public PassiveObject(Application app, String type, String name, Spatial spatial){
        super(app, GameObject.TYPE_PASSIVE, name);
        this.spatial = spatial;
    }
    
}
