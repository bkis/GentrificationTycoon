package de.kritzelbit.gt.objects;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.kritzelbit.gt.logic.District;

/**
 *
 * @author boss
 */
public class PassiveObject extends GameObject{
    
    public PassiveObject(Application app, String type, String name, District district, Spatial spatial){
        super(app, GameObject.TYPE_PASSIVE, name, district);
        Node node = new Node();
        node.attachChild(spatial);
        this.spatial = node;
    }
    
}
