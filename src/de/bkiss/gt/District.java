package de.bkiss.gt;

import com.jme3.scene.Spatial;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.objects.House;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author boss
 */
public class District {
    
    private Map<String, GameObject> objects;
    
    
    public void addGameObject(String id, GameObject object){
        objects.put(id, object);
        System.out.println("Added " + object + " to the District.");
    }
    
    
    public GameObject getGameObject(String id){
        return objects.get(id);
    }
    
    
    public GameObject getGameObject(Spatial spatial){
        for (Entry<String, GameObject> go : objects.entrySet())
            if (go.getValue().getSpatial() == spatial)
                    return go.getValue();
        return null;
    }
    
    
    public Set<GameObject> getNeighborhood(GameObject object, boolean extended){
        float dist = extended ? 2.0f : 1.0f; //TODO distance values
        Set<GameObject> neighborhood = new HashSet<GameObject>();
        
        for (Entry<String, GameObject> go : objects.entrySet())
            if (object.getSpatial().getLocalTranslation().distance(
                    go.getValue().getSpatial().getLocalTranslation()) < dist)
                neighborhood.add(go.getValue());
        
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
