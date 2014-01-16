package gtpreview;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 *
 * @author boss
 */
public class InputMapper{
    
    public final static int INPUT_MODE_INGAME = 0;
    public final static int INPUT_MODE_MAINMENU = 1;
    public final static int INPUT_MODE_PAUSED = 2;
    
    private SimpleApplication app;
    private int worldWidth;
    private int worldHeight;
    private Camera cam;
    private InputManager inputManager;
    
    //GENERAL
    private final static Trigger TRIGGER_ESC =  new KeyTrigger(KeyInput.KEY_ESCAPE);
    private final static String MAPPING_ESC  = "escape";
    
    //INGAME
    //--CAM MOVEMENT
    private final static Trigger TRIGGER_CAM_W  =  new KeyTrigger(KeyInput.KEY_LEFT);
    private final static Trigger TRIGGER_CAM_W2 =  new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_CAM_N  =  new KeyTrigger(KeyInput.KEY_UP);
    private final static Trigger TRIGGER_CAM_N2 =  new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_CAM_E  =  new KeyTrigger(KeyInput.KEY_RIGHT);
    private final static Trigger TRIGGER_CAM_E2 =  new KeyTrigger(KeyInput.KEY_D);
    private final static Trigger TRIGGER_CAM_S  =  new KeyTrigger(KeyInput.KEY_DOWN);
    private final static Trigger TRIGGER_CAM_S2 =  new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_CAM_ZOOM =  new KeyTrigger(KeyInput.KEY_C);
    private final static String MAPPING_CAM_W = "cam move west";
    private final static String MAPPING_CAM_N = "cam move north";
    private final static String MAPPING_CAM_E = "cam move east";
    private final static String MAPPING_CAM_S = "cam move south";
    private final static String MAPPING_CAM_ZOOM = "cam zoom";
    
    private final static MouseButtonTrigger TRIGGER_LCLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);

    
    
    public InputMapper(Application app){
        this.app = (SimpleApplication) app;
        this.cam = app.getCamera();
        this.inputManager = app.getInputManager();
    }
    
    
    public void loadInputMapping(int inputMode){
        inputManager.clearMappings();
        inputManager.setCursorVisible(true);
        
        switch (inputMode){
            case INPUT_MODE_INGAME:
                inputManager.addMapping(MAPPING_CAM_W, TRIGGER_CAM_W, TRIGGER_CAM_W2);
                inputManager.addMapping(MAPPING_CAM_N, TRIGGER_CAM_N, TRIGGER_CAM_N2);
                inputManager.addMapping(MAPPING_CAM_E, TRIGGER_CAM_E, TRIGGER_CAM_E2);
                inputManager.addMapping(MAPPING_CAM_S, TRIGGER_CAM_S, TRIGGER_CAM_S2);
                inputManager.addMapping(MAPPING_CAM_ZOOM, TRIGGER_CAM_ZOOM);
                inputManager.addMapping(MAPPING_ESC, TRIGGER_ESC);
                inputManager.addMapping("pick target", TRIGGER_LCLICK);
                inputManager.addListener(analogListener,
                        new String[]{
                            MAPPING_CAM_W,
                            MAPPING_CAM_N,
                            MAPPING_CAM_E,
                            MAPPING_CAM_S});
                //inputManager.addListener(analogListener,new String[]{MAPPING_CAM_N});
                //inputManager.addListener(analogListener,new String[]{MAPPING_CAM_E});
                //inputManager.addListener(analogListener,new String[]{MAPPING_CAM_S});
                inputManager.addListener(actionListener,new String[]{
                            MAPPING_ESC,
                            MAPPING_CAM_ZOOM,
                            "pick target"});
                break;
            case INPUT_MODE_MAINMENU:
                break;
            case INPUT_MODE_PAUSED:
                break;
            default: break;
        }
    }
    
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_ESC) && !isPressed)
                app.stop();
            if (name.equals(MAPPING_CAM_ZOOM) && !isPressed)
                cycleCamZoom();
            if (name.equals("pick target") && !isPressed) {
                // Reset results list.
                CollisionResults results = new CollisionResults();
                // Convert screen click to 3d position
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
                // Aim the ray from the clicked spot forwards.
                Ray ray = new Ray(click3d, dir);
                // Collect intersections between ray and all nodes in results list.
                app.getRootNode().collideWith(ray, results);
                // (Print the results so we see what is going on:)
                for (int i = 0; i < results.size(); i++) {
                    // (For each "hit", we know distance, impact point, geometry.)
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String target = results.getCollision(i).getGeometry().getName();
                    //System.out.println("Selection #" + i + ": " + target + " at " + pt + ", " + dist + " WU away.");
                }
                // Use the results -- we rotate the selected geometry.
                if (results.size() > 0) {
                    // The closest result is the target that the player picked:
                    Spatial target = results.getClosestCollision().getGeometry();
                    // Here comes the action:
                    target.scale(2);
                }
            } 
        }
    };
    
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals(MAPPING_CAM_W))
                moveCam(-tpf*5,0,0);
            if (name.equals(MAPPING_CAM_N))
                moveCam(0,0,-tpf*5);
            if (name.equals(MAPPING_CAM_E))
                moveCam(tpf*5,0,0);
            if (name.equals(MAPPING_CAM_S))
                moveCam(0,0,tpf*5);
        }
    };
    
    
     /** Pick a Target Using the Mouse Pointer. <ol><li>Map "pick target" action to a MouseButtonTrigger. <li>flyCam.setEnabled(false); <li>inputManager.setCursorVisible(true); <li>Implement action in AnalogListener (TODO).</ol>
    */
    
    
    private void moveCam(float x, float y, float z){
        cam.setLocation(
                new Vector3f(
                    cam.getLocation().x + x,
                    cam.getLocation().y + y,
                    cam.getLocation().z + z));
        /*cam.lookAtDirection(
                new Vector3f(
                    cam.getDirection().x + (-x/40),
                    cam.getDirection().y,
                    cam.getDirection().z),
                Vector3f.UNIT_Y);*/
    }
    
    
    private void cycleCamZoom(){
        float yPos = cam.getLocation().y;
        float zVec;
        
        if (yPos <= 5.1f){
            zVec = 10;
            yPos = 15f;
        } else if (yPos <= 10.1f){
            zVec = -5;
            yPos = 5f;
        } else {
           zVec = -5;
           yPos = 10;
        }
        
        cam.setLocation(
                new Vector3f(
                    cam.getLocation().x,
                    yPos,
                    cam.getLocation().z + zVec));
    }
    
}
