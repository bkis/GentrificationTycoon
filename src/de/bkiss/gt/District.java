package de.bkiss.gt;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import de.bkiss.gt.objects.ConstructionSite;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.objects.House;
import de.bkiss.gt.objects.Land;
import de.bkiss.gt.objects.PassiveObject;
import de.bkiss.gt.utils.ModelLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author boss
 */
public class District {
    
    public static final String STH = "street_h";
    public static final String STV = "street_v";
    public static final String STX = "street_x";
    public static final String H01 = "house_01";
    public static final String H02 = "house_02";
    public static final String H03 = "house_03";
    public static final String CON = "construction";
    public static final String LAN = "land";
    
    private Map<String, GameObject> objects;
    private SimpleApplication app;
    
    
    
    public District(Application app){
        this.app = (SimpleApplication) app;
        this.objects = new HashMap<String, GameObject>();
        constructDistrict();
    }
    
    
    public void addGameObject(String id, GameObject object){
        objects.put(id, object);
        System.out.println("Added " + object.getName() + " to the District.");
    }
    
    
    public GameObject getGameObject(String id){
        return objects.get(id);
    }
    
    
//    public GameObject getGameObject(Spatial spatial){
//        for (Entry<String, GameObject> go : objects.entrySet()){
//            if (go.getValue().getSpatial().getLocalTranslation() == spatial.getLocalTranslation())
//                    return go.getValue();
//        }
//        return null;
//    }
    
    
    public GameObject getGameObject(Vector3f translation){
        GameObject toReturn = null;
        float lastDist = 9999999;
        float currDist = 0;
        
        for (Entry<String, GameObject> go : objects.entrySet()){
            currDist = go.getValue().getSpatial().getLocalTranslation().distance(translation);
            if (currDist < lastDist){
                toReturn = go.getValue();
                lastDist = currDist;
            }
        }
        return toReturn;
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
    
    
    private String[][] getBoardMatrix(){
        String[][] matrix = {
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX}
        };
        return matrix;
    }
    
    
    public int getBoardWidth(){
        return getBoardMatrix().length;
    }
    
    
     public int getBoardHeight(){
        return getBoardMatrix()[0].length;
    }
     
     
    private void constructDistrict(){
        String[][] matrix = getBoardMatrix();
        String curr;
        GameObject go = null;
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                
                curr = matrix[i][j];
                
                
                if        (curr.equals(STX)){
                    go = new PassiveObject(app,
                            GameObject.TYPE_PASSIVE,
                            "Straße Nr." + i + "" + j,
                            ModelLoader.createStreetTile(app.getAssetManager(),
                            app.getAssetManager().loadTexture(
                            "Textures/tiles/streets/street_fx.png")));
                } else if (curr.equals(STH)){
                    go = new PassiveObject(app,
                            GameObject.TYPE_PASSIVE,
                            "Straße Nr." + i + "" + j,
                            ModelLoader.createStreetTile(app.getAssetManager(),
                            app.getAssetManager().loadTexture(
                            "Textures/tiles/streets/street_fh.png")));
                } else if (curr.equals(STV)){
                    go = new PassiveObject(app,
                            GameObject.TYPE_PASSIVE,
                            "Straße Nr." + i + "" + j,
                            ModelLoader.createStreetTile(app.getAssetManager(),
                            app.getAssetManager().loadTexture(
                            "Textures/tiles/streets/street_fv.png")));
                } else if (curr.equals(H01)){
                    go = new House(app,
                            House.TYPE_HOUSE_1, "Haus Nr." + i + "" + j);
                } else if (curr.equals(H02)){
                    go = new House(app,
                            House.TYPE_HOUSE_2, "Haus Nr." + i + "" + j);
                    go.getSpatial().rotate(0, FastMath.DEG_TO_RAD*180, 0);
                } else if (curr.equals(H03)){
                    go = new House(app,
                            House.TYPE_HOUSE_3, "Haus Nr." + i + "" + j);
                    go.getSpatial().rotate(0, FastMath.DEG_TO_RAD*180, 0);
                } else if (curr.equals(CON)){
                    go = new ConstructionSite(app,
                            "ConstructionSite Nr." + i + "" + j);
                    go.getSpatial().rotate(0, FastMath.DEG_TO_RAD*90, 0);
                } else if (curr.equals(LAN)){
                    go = new Land(app,
                            "Land Nr." + i + "" + j);
                }
                
                if (go == null) return;
                go.getSpatial().move(i-(getBoardWidth()/2), 0, j-(getBoardHeight()/2));
                addGameObject(go.getSpatial().getName(), go);
            }
        }
    }
    
    
    public List<GameObject> getObjectList(){
        List<GameObject> list = new ArrayList<GameObject>();
        
        for (Entry<String, GameObject> e : objects.entrySet())
            list.add(e.getValue());
        
        return list;
    }
    
}
