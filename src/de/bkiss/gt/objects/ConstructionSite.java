package de.bkiss.gt.objects;

import com.jme3.app.Application;
import de.bkiss.gt.logic.District;
import de.bkiss.gt.logic.GameTimer;

/**
 *
 * @author boss
 */
public class ConstructionSite extends GameObject{
    
    private GameObject building;
    private GameTimer countdown;
    
    public ConstructionSite(Application app, String name,
            District district, GameObject building){
        super(app, GameObject.TYPE_CONSTRUCTION, name, district);
        this.building = building;
        startToBuild(building);
    }
    
    
    private void startToBuild(GameObject building){
        
    }
    
    
    public void finish(){
        
    }
    
}
