package gtpreview;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author boss
 */
public class CarControl extends AbstractControl{
    
    private float SPEED;
    private static final float yOffset = -0.25f;
    
    private Vector3f MOVE_N;
    private Vector3f MOVE_E;
    private Vector3f MOVE_S;
    private Vector3f MOVE_W;
    
    public static final Quaternion YAW090 = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,1,0));
    public static final Quaternion YAW180 = new Quaternion().fromAngleAxis(FastMath.PI  ,   new Vector3f(0,1,0));
    public static final Quaternion YAW270 = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0));
    public static final Quaternion YAW000 = new Quaternion().fromAngleAxis(0, new Vector3f(0,1,0));
    
            
    private Vector3f move;
    private Direction dir;
    private Transform initTransform;
    int worldWidth;
    int worldHeight;
    boolean initialized;
    private float breakPower;
    
    
    public CarControl(int worldWidth, int worldHeight){
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.dir = Direction.N;
        this.initialized = false;
        this.breakPower = 0;
        setSpeedAndDirection();
        this.move = MOVE_N;
    }
    
    
    private void setSpeedAndDirection(){
        this.SPEED = (FastMath.rand.nextFloat()+0.85f)*2;
        this.MOVE_N = new Vector3f(0,0,-SPEED);
        this.MOVE_E = new Vector3f(SPEED,0,0);
        this.MOVE_S = new Vector3f(0,0,SPEED);
        this.MOVE_W = new Vector3f(-SPEED,0,0);
    }
    
    
    @Override
    protected void controlUpdate(float tpf) {
        if (!initialized) initialize();
        if (!isInsideBounds()) setNewPath();
        spatial.move(move.mult(tpf));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    private void setNewPath(){
        setSpeedAndDirection();
        spatial.setLocalTransform(initTransform);
        int rndXPos, rndZPos;
        while ((rndXPos = FastMath.rand.nextInt(worldWidth)-worldWidth/2) % 2 != 0){};
        while ((rndZPos = FastMath.rand.nextInt(worldHeight)-worldHeight/2) % 2 != 0){};
        dir = pickDirection();
        
        switch (dir){
            case N:
                spatial.setLocalTranslation(rndXPos, yOffset, worldHeight/2);
                spatial.setLocalRotation(YAW000);
                move = MOVE_N;
                break;
            case E:
                spatial.setLocalTranslation(-worldHeight/2, yOffset, rndZPos);
                spatial.setLocalRotation(YAW270);
                move = MOVE_E;
                break;
            case S:
                spatial.setLocalTranslation(rndXPos, yOffset, -worldWidth/2);
                spatial.setLocalRotation(YAW180);
                move = MOVE_S;
                break;
            case W:
                spatial.setLocalTranslation(worldHeight/2, yOffset, rndZPos);
                spatial.setLocalRotation(YAW090);
                move = MOVE_W;
                break;
        }
    }
    
    private Direction pickDirection(){
        float rnd = FastMath.rand.nextFloat();
        if      (rnd <= 0.25f) return Direction.N;
        else if (rnd <= 0.50f) return Direction.E;
        else if (rnd <= 0.75f) return Direction.S;
        else                   return Direction.W;
    }
    
    private static enum Direction {
        N, E, S, W;
    }
    
    private boolean isInsideBounds(){
        switch(dir){
            case N: if (spatial.getLocalTranslation().getZ() < -worldHeight/2) return false;
            case E: if (spatial.getLocalTranslation().getX() > worldWidth/2)   return false;
            case S: if (spatial.getLocalTranslation().getZ() > worldHeight/2)  return false;
            case W: if (spatial.getLocalTranslation().getX() < -worldWidth/2)  return false;
            default: return true;
        }
    }
    
    private void initialize(){
        this.initTransform = spatial.getLocalTransform();
        setNewPath();
        initialized = true;
    }
    
}
