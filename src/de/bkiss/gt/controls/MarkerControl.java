package de.bkiss.gt.controls;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author boss
 */
public class MarkerControl extends AbstractControl{
    
    private final static float rot = FastMath.DEG_TO_RAD*100;
    private double frames;
    
    
    public MarkerControl(){
        frames = 0;
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.rotate(0, rot*tpf, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
