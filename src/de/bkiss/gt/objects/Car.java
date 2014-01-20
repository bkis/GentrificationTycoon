package de.bkiss.gt.objects;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.bkiss.gt.controls.CarControl;
import de.bkiss.gt.utils.ModelLoader;

/**
 *
 * @author boss
 */
public class Car {
    
    private static final String C1A = "car_1a";
    private static final String C1B = "car_1b";
    private static final String C1C = "car_1c";
    
    private Node spatial;
    
    
    public Car(AssetManager assetManager,
               char key,
               int worldWidth,
               int worldHeight){
        
        //make car
        Spatial car;
        
        switch (key){
            case 'a': car = ModelLoader.loadSpatial(assetManager,
                            "Models/mobiles/", C1A); break;
            case 'b': car = ModelLoader.loadSpatial(assetManager,
                            "Models/mobiles/", C1B); break;
            case 'c': car = ModelLoader.loadSpatial(assetManager,
                            "Models/mobiles/", C1C); break;
            default:  car = ModelLoader.loadSpatial(assetManager,
                            "Models/mobiles/", C1A); break;
        }
        
        car.scale(0.3f, 0.4f, 0.6f);
        
        //make exhauster particle effect
        ParticleEmitter exhaustEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material fogMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        exhaustEffect.setMaterial(fogMat);
        exhaustEffect.setEndColor( new ColorRGBA(0.3f, 0.3f, 0.3f, 1f) );
        exhaustEffect.setStartColor( new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f) );
        exhaustEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0.2f, 0));
        exhaustEffect.setStartSize(0.01f);
        exhaustEffect.setEndSize(0.05f);
        exhaustEffect.setGravity(0f,0f,0f);
        exhaustEffect.setLowLife(0.1f);
        exhaustEffect.setHighLife(0.5f);
        exhaustEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        exhaustEffect.move(0.1f, -0.15f, 0.2f);
        
        //make car node
        spatial = new Node();
        spatial.attachChildAt(car, 0);
        spatial.attachChild(exhaustEffect);
        spatial.addControl(new CarControl(worldWidth, worldHeight));
    }
    
    
    public Spatial getSpatial(){
        return spatial;
    }
    
}
