package de.bkiss.gt.objects;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;
import de.bkiss.gt.utils.ModelLoader;

/**
 *
 * @author boss
 */
public abstract class GameObject {
    
    public static final String TYPE_HOUSE_1      = "house_1";
    public static final String TYPE_HOUSE_2      = "house_2";
    public static final String TYPE_HOUSE_3      = "house_3";
    
    private String type;
    private String name;
    private Spatial spatial;
    private SimpleApplication app;
    
    
    public GameObject(Application app, String type, String name){
        this.type = type;
        this.name = name;
        this.app = (SimpleApplication) app;
        
        spatial = ModelLoader.loadSpatial(app.getAssetManager(),
                            "Models/buildings/", type
                            + randomABC());
    }
    
    
    public Spatial getSpatial(){
        return spatial;
    }
    
    
    @Override
    public String toString(){
        return name;
    }
    
    
    private char randomABC(){
        int i = FastMath.rand.nextInt(9);
        if (i < 3) return 'a';
        if (i < 6) return 'b';
        if (i < 9) return 'c';
        return 'a';
    }
}
