package de.bkiss.gt.utils;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author boss
 */
public class ModelLoader {
    
    public static Spatial loadSpatial(AssetManager assetManager,
                                      String path,
                                      String key){
        Spatial s = null;
        
        try {
            s = assetManager.loadModel(
                path + key + ".j3o");
        } catch (Exception e){
            System.out.println("[ERROR] model not found!");
            System.exit(1);
        }
        
        if (s == null)
            return null;
        else
            s.scale(0.5f);
        
        return s;
    }
    
}
