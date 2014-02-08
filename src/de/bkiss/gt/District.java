package de.bkiss.gt;

import com.jme3.scene.Spatial;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.objects.House;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author boss
 */
public class District {
    
    private Set<GameObject> objects;
    
    
    public void addGameObject(GameObject object){
        objects.add(object);
        System.out.println("Added " + object + " to the District.");
    }
    
    
    public GameObject getGameObject(String name){
        for (GameObject go : objects)
            if (go.getName().equals(name))
                    return go;
        return null;
    }
    
    
    public GameObject getGameObject(Spatial spatial){
        for (GameObject go : objects)
            if (go.getSpatial() == spatial)
                    return go;
        return null;
    }
    
    
    public Set<GameObject> getNeighborhood(GameObject object, boolean extended){
        float dist = extended ? 2.0f : 1.0f; //TODO distance values
        Set<GameObject> neighborhood = new HashSet<GameObject>();
        
        for (GameObject go : objects)
            if (object.getSpatial().getLocalTranslation().distance(
                    go.getSpatial().getLocalTranslation()) < dist)
                neighborhood.add(go);
        
        return neighborhood;
    }
    
    
    public int getNeighborhoodValue(GameObject object){
        Set<GameObject> neighborhood = getNeighborhood(object, false);
        int value = 0;
        int count = 0;
        
        for (GameObject go : neighborhood)
            if (go instanceof House){
                value += ((House) go).getLuxury();
                count++;
            }
        
        return (value / count) + 1;
    }
    
}
