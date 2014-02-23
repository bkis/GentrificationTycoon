package de.bkiss.gt.utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

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
        
        s.scale(0.5f);
        
        return s;
    }
    
    
    public static Spatial createStreetTile(AssetManager assetManager, Texture tex){
        Quad tile = new Quad(1,1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        Geometry geom = new Geometry("street", tile);
        geom.setMaterial(mat);
        geom.rotate(FastMath.DEG_TO_RAD*270, 0, 0);
        geom.move(-0.5f, -0.5f, 0.5f);
        return geom;
    }
    
    
//    private static String getIdStamp(){
//        return "_" + System.nanoTime();
//    }
    
}
