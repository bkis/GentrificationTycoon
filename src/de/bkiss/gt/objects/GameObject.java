package de.bkiss.gt.objects;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.bkiss.gt.logic.District;
import de.bkiss.gt.utils.ModelLoader;

/**
 *
 * @author boss
 */
public abstract class GameObject {
    
    public static final String TYPE_HOUSE_1      = "house_1";
    public static final String TYPE_HOUSE_2      = "house_2";
    public static final String TYPE_HOUSE_3      = "house_3";
    
    public static final String TYPE_CONSTRUCTION = "construction";
    public static final String TYPE_LAND         = "land";
    
    public static final String TYPE_PASSIVE      = "passive";
    
    
    protected String type;
    private String name;
    private boolean ownedByPlayer;
    private boolean occupied;
    
    protected Node spatial;
    
    private Geometry ownerMarker;
    private Geometry occupiedMarker;
    
    private SimpleApplication app;
    private String imagePath;
    protected District district;
    
    
    public GameObject(Application app, String type, String name, District district){
        this.type = type;
        this.name = name;
        this.district = district;
        this.app = (SimpleApplication) app;
        this.imagePath = "Interface/hud/" + type + ".png";
        
        if (!type.equals(TYPE_PASSIVE)){
            loadSpatial();
            
            if (spatial != null){
                System.out.println("Created GameObject: " + this);
            } else {
                System.out.println("ERROR LOADING SPATIAL: " + this);
                app.stop();
            }
        }
    }
    
    
    public Spatial getSpatial(){
        return spatial;
    }

    
    public boolean isOwnedByPlayer() {
        return ownedByPlayer;
    }

    public boolean isOccupied() {
        return occupied;
    }
    
    public boolean isPassive(){
        return type.equals(GameObject.TYPE_PASSIVE);
    }

    
    @Override
    public String toString(){
        return name;
    }
    
    
    private void loadSpatial(){
        spatial = new Node(name);
        Spatial objSpatial = null;
        
        if (type.contains("house")){
            objSpatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type
                                + randomABC());
            if (type.equals(TYPE_HOUSE_2) || type.equals(TYPE_HOUSE_3))
                objSpatial.rotate(0, FastMath.DEG_TO_RAD*180, 0);
        } else if (type.equals(TYPE_CONSTRUCTION)){
            objSpatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type);
        } else if (type.equals(TYPE_LAND)){
            objSpatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type);
        }
        
        if (objSpatial == null) return;
        
        objSpatial.setName("building-"+name);
        spatial.attachChild(objSpatial);
        createOwnerMarker();
        createOccupiedMarker();
        setOwned(false);
        setOccupied(false);
        setMarkers(false);
        setMarkers(district.isObjectMarkersOn());
    }
    
    
    private void createOwnerMarker(){
        if (type.equals(GameObject.TYPE_PASSIVE)) return;
        ownerMarker = ModelLoader.createAbstractMarkerGeometry(app.getAssetManager());
        ownerMarker.setName("ownerMarker");
        spatial.attachChild(ownerMarker);
        ownerMarker.setLocalTranslation(0f, 0.45f, 0.51f);
    }
    
    
    private void createOccupiedMarker(){
        if (type.equals(GameObject.TYPE_PASSIVE)) return;
        occupiedMarker = ModelLoader.createAbstractMarkerGeometry(app.getAssetManager());
        occupiedMarker.setName("occupiedMarker");
        occupiedMarker.scale(0.5f);
        spatial.attachChild(occupiedMarker);
        occupiedMarker.setLocalTranslation(0.05f, 0.5f, 0.52f);
    }
    
    
    private char randomABC(){
        int i = FastMath.rand.nextInt(9);
        if (i < 3) return 'a';
        if (i < 6) return 'b';
        if (i < 9) return 'c';
        return 'a';
    }
    
    
    public float getXPos(){
        return spatial.getLocalTranslation().x;
    }
    
    
    public float getZPos(){
        return spatial.getLocalTranslation().z;
    }
    
    
    public String getName(){
        return name;
    }

    
    public String getImagePath() {
        return imagePath;
    }
    
    
    public void setOccupied(boolean state){
        if (type.equals(GameObject.TYPE_PASSIVE)) return;
        occupied = state;
        if (state){
            occupiedMarker.getMaterial().setColor("Color", ColorRGBA.Black);
        } else {
            occupiedMarker.setMaterial(ownerMarker.getMaterial());
        }
    }

    
    public void setOwned(boolean state){
        if (type.equals(GameObject.TYPE_PASSIVE)) return;
        ownedByPlayer = state;
        if (state){
            ownerMarker.getMaterial().setColor("Color", ColorRGBA.Green);
        } else {
            ownerMarker.getMaterial().setColor("Color", ColorRGBA.Red);
        }
    }
 
    
    public void setMarkers(boolean on){
        if (type.equals(GameObject.TYPE_PASSIVE)) return;
        if (on){
            if (!spatial.hasChild(ownerMarker)){
                spatial.attachChild(ownerMarker);
                spatial.attachChild(occupiedMarker);
                //ownerMarker.move(0, markerHideOffset, 0);
            } else {
                
            }
            //occupiedMarker.move(0, markerHideOffset, 0);
            //markerHideOffset *= -1;
        } else {
            spatial.detachChild(ownerMarker);
            spatial.detachChild(occupiedMarker);
        }
    }
    
    
    public int getNeighborhoodValue(){
        return district.getNeighborhoodValue(this);
    }
    
    
    public int getValue(){
        return (int) (Math.pow(
                (this instanceof House ? ((House)this).calcDefaultRent()
                + this.getNeighborhoodValue() :
                this.getNeighborhoodValue()*10)+50
                , 1.5f
                )*10);
    }
}
