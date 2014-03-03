package de.bkiss.gt.objects;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.bkiss.gt.District;
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
    private int moneyValue;
    
    protected Node spatial;
    
    private Geometry ownerMarker;
    private Geometry occupiedMarker;
    private float markerHideOffset;
    
    private SimpleApplication app;
    private String imagePath;
    private District district;
    
    
    public GameObject(Application app, String type, String name, District district){
        this.type = type;
        this.name = name;
        this.district = district;
        this.app = (SimpleApplication) app;
        this.imagePath = "Interface/hud/" + type + ".png";
        this.markerHideOffset = 100;
        
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
        } else if (type.equals(TYPE_CONSTRUCTION)){
            objSpatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type);
        } else if (type.equals(TYPE_LAND)){
            objSpatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type);
        }
        
        spatial.attachChild(objSpatial);
        createOwnerMarker();
        createOccupiedMarker();
        toggleMarkers();
    }
    
    
    private void createOwnerMarker(){
        ownerMarker = ModelLoader.createAbstractMarkerGeometry(app.getAssetManager());
        ownerMarker.setName("ownerMarker");
        ownerMarker.getMaterial().setColor("Color", ColorRGBA.Green);
        spatial.attachChild(ownerMarker);
        ownerMarker.setLocalTranslation(0f, 0.45f, 0.51f);
    }
    
    
    private void createOccupiedMarker(){
        occupiedMarker = ModelLoader.createAbstractMarkerGeometry(app.getAssetManager());
        occupiedMarker.setName("occupiedMarker");
        occupiedMarker.getMaterial().setColor("Color", ColorRGBA.Black);
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
    
    
    public int getPrice(){
        return moneyValue;
    }
    
    
    public void setPrice(int price){
        this.moneyValue = price;
    }
    
    
    public String getName(){
        return name;
    }

    
    public String getImagePath() {
        return imagePath;
    }
    
    
    public void setOccupiedMarker(boolean state){
        if (state && spatial.getChild("occupiedMarker") == null){
            occupiedMarker.getMaterial().setColor("Color", ColorRGBA.Black);
        } else if (!state && spatial.getChild("occupiedMarker") != null) {
            occupiedMarker.setMaterial(ownerMarker.getMaterial());
        }
    }

    
    public void setOwnerMarker(boolean state){
        if (state && spatial.getChild("ownerMarker") == null){
            ownerMarker.getMaterial().setColor("Color", ColorRGBA.Green);
        } else if (!state && spatial.getChild("ownerMarker") != null) {
            ownerMarker.getMaterial().setColor("Color", ColorRGBA.Gray);
        }
    }
 
    
    public void toggleMarkers(){
        if (type.equals(GameObject.TYPE_PASSIVE)) return;
        ownerMarker.move(0, markerHideOffset, 0);
        occupiedMarker.move(0, markerHideOffset, 0);
        markerHideOffset *= -1;
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
