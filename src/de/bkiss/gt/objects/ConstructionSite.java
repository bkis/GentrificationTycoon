package de.bkiss.gt.objects;

import com.jme3.app.Application;
import de.bkiss.gt.logic.District;
import de.bkiss.gt.logic.Game;
import de.bkiss.gt.logic.GameTimer;

/**
 *
 * @author boss
 */
public class ConstructionSite extends GameObject{
    
    private GameObject building;
    private GameTimer countdown;
    
    public ConstructionSite(Application app, String name,
            District district, Game game){
        super(app, GameObject.TYPE_CONSTRUCTION, name, district);
        startToBuild(building, game);
    }
    
    
    private void startToBuild(GameObject building, Game game){
        this.building = building;
        //game.getTimer().addConstructionTask(this, 30000);
    }
    
    
    public GameObject getBuilding(){
        return building;
    }
    
    
    public void finish(){
        district.finishConstruction(this);
    }
    
}
