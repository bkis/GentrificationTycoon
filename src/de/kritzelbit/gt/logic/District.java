package de.kritzelbit.gt.logic;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import de.kritzelbit.gt.objects.GameObject;
import de.kritzelbit.gt.objects.House;
import de.kritzelbit.gt.objects.Land;
import de.kritzelbit.gt.objects.PassiveObject;
import de.kritzelbit.gt.objects.PublicBuilding;
import de.kritzelbit.gt.tenants.TenantGenerator;
import de.kritzelbit.gt.utils.ModelLoader;
import de.kritzelbit.gt.utils.RandomContentGenerator;
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
    private RandomContentGenerator gen;
    private TenantGenerator tenGen;
    
    private boolean objectMarkers;
    
    private GameObject selected;
    
    
    public District(Application app, RandomContentGenerator gen, TenantGenerator tenGen){
        this.app = (SimpleApplication) app;
        this.objects = new HashMap<String, GameObject>();
        this.objectMarkers = false;
        this.gen = gen;
        this.tenGen = tenGen;
    }
    
    
    public void addGameObject(String id, GameObject object){
        objects.put(id, object);
        //System.out.println("Added " + object.getName() + " to the District.");
    }
    
    
    public GameObject buildHouse(String type, Land land){
        GameObject go;
        
        if (type.contains("house")){
            go = new House(app, type, nameForType(type) + Math.random()*100000, this);
        } else {
            go = new PublicBuilding(app, type, nameForType(type) + Math.random(), this);
        }
        
        //replace land with house
        app.getRootNode().attachChild(go.getSpatial());
        go.getSpatial().setLocalTranslation(land.getSpatial().getLocalTranslation());
        app.getRootNode().detachChild(land.getSpatial());
        
        if (go instanceof House)
            ((House)go).setRent(((House)go).calcDefaultRent());
        
        go.setOwned(true);
        
        objects.remove(land.getName());
        objects.put(go.getName(), go);
        selected = go;
        return go;
    }
    
    
    private String nameForType(String type){
        if      (type.equalsIgnoreCase(GameObject.TYPE_HOUSE_1)) return "Hovel";
        else if (type.equalsIgnoreCase(GameObject.TYPE_HOUSE_2)) return "Family House";
        else if (type.equalsIgnoreCase(GameObject.TYPE_HOUSE_3)) return "Mansion";
        else if (type.equalsIgnoreCase(GameObject.PUBLIC_CLUB)) return "Club";
        else if (type.equalsIgnoreCase(GameObject.PUBLIC_GALLERY)) return "Art Gallery";
        else if (type.equalsIgnoreCase(GameObject.PUBLIC_SCHOOL)) return "School";
        else return "Land";
    }
    
    
    public void destroyBuilding(GameObject go){
        Land land = new Land(app, "Land" + Math.random(), this);
        
        //replace building with land
        app.getRootNode().attachChild(land.getSpatial());
        land.getSpatial().setLocalTranslation(go.getSpatial().getLocalTranslation());
        app.getRootNode().detachChild(go.getSpatial());
        
        objects.remove(go.getName());
        objects.put(land.getName(), land);
        land.setOwned(true);
        selected = land;
    }
    
    
    public GameObject getGameObject(String id){
        return objects.get(id);
    }
    
    
    public GameObject getGameObject(Spatial spatial){
        for (Entry<String, GameObject> go : objects.entrySet()){
            if (go.getValue().getSpatial().getLocalTranslation() == spatial.getLocalTranslation())
                    return go.getValue();
        }
        return null;
    }
    
    
    public GameObject getGameObject(Vector3f translation){
        GameObject toReturn = null;
        float lastDist = 9999999;
        float currDist;
        
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
        float dist = extended ? 6f : 3f; //TODO distance values
        Set<GameObject> neighborhood = new HashSet<GameObject>();
        
        for (Entry<String, GameObject> go : objects.entrySet())
            if (object.getSpatial().getLocalTranslation().distance(
                    go.getValue().getSpatial().getLocalTranslation()) < dist
                    && go.getValue() != object)
                neighborhood.add(go.getValue());
        
        return neighborhood;
    }
    
    
    public int getNeighborhoodValue(GameObject object){
        Set<GameObject> neighborhood = getNeighborhood(object, false);
        int value = 50;
        int count = 1;
        
        for (GameObject go : neighborhood)
            if (go instanceof House && go != object){
                value += ((House) go).getOwnLuxury();
                count++;
            }
        
        return (value / count) + 1;
    }
    
    
    private String[][] getBoardMatrix(){
        String[][] matrix = {
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,LAN,STV,H01,STV,H01,STV,H03,STV,H03,STV,LAN,STV,H01,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,LAN,STV,LAN,STV,H01,STV,H03,STV,H01,STV,LAN,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV,H01,STV,LAN,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,H01,STV,LAN,STV,H02,STV,LAN,STV,H01,STV,H01,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,LAN,STV,LAN,STV,H01,STV,H02,STV,H02,STV,LAN,STV,LAN,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,LAN,STV,H01,STV,H01,STV,H03,STV,H03,STV,LAN,STV,H01,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,LAN,STV,LAN,STV,H01,STV,H03,STV,H01,STV,LAN,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX}
        };
        return matrix;
    }
    
    
//    private String[][] getBoardMatrix(){
//        String[][] matrix = {
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,LAN,STV,H01,STV,H01,STV,H03,STV,H03,STV,LAN,STV,H01,STV,H01,STV,H03,STV,H03,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,H01,STV,LAN,STV,LAN,STV,H01,STV,H03,STV,H01,STV,LAN,STV,LAN,STV,H01,STV,H03,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,H01,STV,H01,STV,LAN,STV,H02,STV,LAN,STV,H01,STV,H01,STV,LAN,STV,H02,STV,LAN,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,LAN,STV,LAN,STV,H01,STV,H02,STV,H02,STV,LAN,STV,LAN,STV,H01,STV,H02,STV,H02,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,LAN,STV,H01,STV,H01,STV,H03,STV,H03,STV,LAN,STV,H01,STV,H01,STV,H03,STV,H03,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,H01,STV,LAN,STV,LAN,STV,H01,STV,H03,STV,H01,STV,LAN,STV,LAN,STV,H01,STV,H03,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,H01,STV,H01,STV,LAN,STV,H02,STV,LAN,STV,H01,STV,H01,STV,LAN,STV,H02,STV,LAN,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
//            {STV,LAN,STV,LAN,STV,H01,STV,H02,STV,H02,STV,LAN,STV,LAN,STV,H01,STV,H02,STV,H02,STV},
//            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX}
//        };
//        return matrix;
//    }
    
    
    public int getNrOfBuildings(String type){
        int count = 0;
        for (Entry<String,GameObject> e : objects.entrySet())
            if (e.getValue().getType().equals(type))
                count++;
        return count;
    }
    
    
    public float getOwnedRatio(){
        float count = 0;
        float owned = 0;
        
        for (Entry<String,GameObject> e : objects.entrySet()){
            count++;
            if (e.getValue().isOwnedByPlayer()){
                if (e.getValue() instanceof Land) owned += 0.5f;
                else owned++;
            }
        }
        //System.out.println("OWNED: " + owned/count);
        return owned/count;
    }
    
    
    public float getStudentsRatio(){
        float count = 0;
        float hit = 0;
        
        for (Entry<String,GameObject> e : objects.entrySet()){
            if (e.getValue() instanceof House && ((House)e.getValue()).isOccupied()){
                count++;
                if (((House)e.getValue()).getTenant().getProfession()
                        .equalsIgnoreCase("student")){
                    hit++;
                }
            }
        }
        return (hit/count) * 100;
    }
    
    
    public int getAverageBudget(){
        int count = 0;
        long budget = 0;
        
        for (Entry<String,GameObject> e : objects.entrySet()){
            if (e.getValue() instanceof House && ((House)e.getValue()).isOccupied()){
                count++;
                budget += ((House)e.getValue()).getTenant().getBudget();
            }
        }
            
        return (int)budget/count;
    }
    
    
    public int getBoardWidth(){
        return getBoardMatrix().length;
    }
    
    
     public int getBoardHeight(){
        return getBoardMatrix()[0].length;
    }
     
     
    public void construct(){
        String[][] matrix = getBoardMatrix();
        String curr;
        GameObject go;
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                
                curr = matrix[i][j];
                
                if        (curr.equals(STX)){
                    go = new PassiveObject(app,
                            GameObject.TYPE_PASSIVE,
                            "Straße Nr." + i + "" + j, this,
                            ModelLoader.createStreetTile(app.getAssetManager(),
                            app.getAssetManager().loadTexture(
                            "Textures/tiles/streets/street_fx.png")));
                } else if (curr.equals(STH)){
                    go = new PassiveObject(app,
                            GameObject.TYPE_PASSIVE,
                            "Straße Nr." + i + "" + j, this,
                            ModelLoader.createStreetTile(app.getAssetManager(),
                            app.getAssetManager().loadTexture(
                            "Textures/tiles/streets/street_fv.png")));
                } else if (curr.equals(STV)){
                    go = new PassiveObject(app,
                            GameObject.TYPE_PASSIVE,
                            "Straße Nr." + i + "" + j, this,
                            ModelLoader.createStreetTile(app.getAssetManager(),
                            app.getAssetManager().loadTexture(
                            "Textures/tiles/streets/street_fh.png")));
                } else {
                    double rnd = Math.random();
                    if (rnd < 0.5f){
                        go = new House(app,
                            House.TYPE_HOUSE_1, "Hovel" + i + "" + j, this);
                        ((House)go).addExpansion(gen.getRndExpansionFor(0));
                        if (Math.random() < 0.25f){
                            ((House)go).setTenant(tenGen.generateTenant(0));
                            ((House)go).setRent(((House)go).calcDefaultRent());
                            ((House)go).getTenant().setNeedsFor((House)go);
                        }
                    } else if (rnd < 0.55f){
                        go = new House(app,
                            House.TYPE_HOUSE_2, "Family House" + i + "" + j, this);
                        ((House)go).addExpansion(gen.getRndExpansionFor(1));
                        if (Math.random() < 0.25f){
                            ((House)go).setTenant(tenGen.generateTenant(1));
                            ((House)go).setRent(((House)go).calcDefaultRent());
                            ((House)go).getTenant().setNeedsFor((House)go);
                        }
                    } else {
                        go = new Land(app,
                            "Plot" + i + "" + j, this);
                    }
                }
                
                go.getSpatial().move(i-(getBoardWidth()/2), 0, j-(getBoardHeight()/2));
                go.getSpatial().setName(go.getSpatial().getName() + "_" + i + "." + j);
                addGameObject(go.getSpatial().getName(), go);
            }
        }
        
        for (Entry<String, GameObject> e : objects.entrySet())
            if (e.getValue() instanceof House && !((House)e.getValue()).isOccupied())
                ((House)e.getValue()).applyDefaultRent();
    }
    
    
    public int getTypeCount(String type){
        int count = 0;
        for (Entry<String, GameObject> e : objects.entrySet()){
            if (e.getValue().getType().equalsIgnoreCase(type))
                    count++;
        }
        //System.out.println("COUNT FOR TYPE " + type + ": " + count);
        return count;
    }
    
    
    public List<GameObject> getObjectList(){
        List<GameObject> list = new ArrayList<GameObject>();
        
        for (Entry<String, GameObject> e : objects.entrySet())
            list.add(e.getValue());
        
        return list;
    }

    
    public GameObject getSelected() {
        return selected;
    }

    
    public void setSelected(GameObject selected) {
        this.selected = selected;
    }

    
    public void toogleObjectMarkers() {
        objectMarkers = !objectMarkers;
        for (Entry<String, GameObject> e : objects.entrySet()){
            e.getValue().setMarkers(objectMarkers);
        }
    }
    
    
    public boolean isObjectMarkersOn(){
        return objectMarkers;
    }

    
    public void clearPassives() {
        Set<String> mark = new HashSet<String>();
        
        for (Entry<String, GameObject> e : objects.entrySet()){
            if (e.getValue() instanceof PassiveObject)
                mark.add(e.getKey());
        }
        
        for (String s : mark) objects.remove(s);
    }

    
    public long getTotalRent() {
        long r = 0;
        for (Entry<String, GameObject> e : objects.entrySet())
            if (e.getValue() instanceof House
                    && ((House)e.getValue()).isOwnedByPlayer()
                    && ((House)e.getValue()).isOccupied())
                r += ((House)e.getValue()).getRent();
        return r;
    }
    
    
}
