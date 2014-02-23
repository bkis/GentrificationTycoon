package de.bkiss.gt.objects;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.cache.AssetCache;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
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
    
    protected Spatial spatial;
    private SimpleApplication app;
    private String imagePath;
    
    
    public GameObject(Application app, String type, String name){
        this.type = type;
        this.name = name;
        this.app = (SimpleApplication) app;
        this.imagePath = "Interface/hud/" + type;
        
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
        if (type.contains("house")){
            spatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type
                                + randomABC());
        } else if (type.equals(TYPE_CONSTRUCTION)){
            spatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type);
        } else if (type.equals(TYPE_LAND)){
            spatial = ModelLoader.loadSpatial(app.getAssetManager(),
                                "Models/GameObjects/", type);
        }
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
 
}
