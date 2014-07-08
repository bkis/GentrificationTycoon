package de.kritzelbit.gt.utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import de.kritzelbit.gt.controls.MarkerControl;

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
//        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("ColorMap", tex);
//        mat.setTexture("DiffuseMap", tex);
        Geometry geom = new Geometry("street", tile);
        geom.setMaterial(mat);
        geom.rotate(FastMath.DEG_TO_RAD*270, 0, 0);
        geom.move(-0.5f, -0.5f, 0.5f);
        return geom;
    }
    
    
    public static Spatial createSelectionMarker(AssetManager assetManager){
        Dome shape = new Dome(2, 4, 0.5f);
        Geometry geom = new Geometry("marker", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Green);
        mat.setColor("Ambient", ColorRGBA.Green);
        geom.setMaterial(mat);
        geom.setLocalScale(0.5f, 1.5f, 0.5f);
        geom.rotate(FastMath.DEG_TO_RAD*180, 0, 0);
        geom.addControl(new MarkerControl());
        geom.setLocalTranslation(0, -5, 0);
        return geom;
    }
    
    
    public static Geometry createAbstractMarkerGeometry(AssetManager assetManager){
        Quad shape = new Quad(0.2f, 0.2f);
        Geometry geom = new Geometry("marker", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        geom.setMaterial(mat);
        //geom.rotate(FastMath.DEG_TO_RAD*-90, 0, 0);
        return geom;
    }
    
}
