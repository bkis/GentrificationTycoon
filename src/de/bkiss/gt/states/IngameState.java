package de.bkiss.gt.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import de.bkiss.gt.logic.District;
import de.bkiss.gt.logic.Game;
import de.bkiss.gt.logic.Player;
import de.bkiss.gt.gui.GUIController;
import de.bkiss.gt.objects.Car;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.tenants.Tenant;
import de.bkiss.gt.tenants.TenantGenerator;
import de.bkiss.gt.utils.InputMapper;
import java.util.List;
import java.util.Set;

/**
 *
 * @author boss
 */
public class IngameState extends AbstractAppState{
    
    private Camera cam;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    private InputMapper inputMapper;
    private GUIController guiController;
    private Game game;
    private District district;
    
    
    public IngameState(InputMapper inputMapper,
                       GUIController guiController,
                       District district,
                       String playerName,
                       String playerIconPath){
        this.inputMapper = inputMapper;
        this.guiController = guiController;
        this.district = district;
        this.game = new Game(playerName, playerIconPath, district, guiController);
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //initialize fields
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        
        //set input mapping
        inputMapper.loadInputMapping(InputMapper.INPUT_MODE_INGAME);
        
        //construct district
        this.district.construct();
        
        //load district
        addObjects(district.getObjectList());
        
        //add ground plane
        rootNode.attachChild(getGroundPlane());
        
        //add cars
        rootNode.attachChild(new Car(assetManager, 'a',
                district.getBoardWidth(),
                district.getBoardHeight())
                .getSpatial());
        rootNode.attachChild(new Car(assetManager, 'b',
                district.getBoardWidth(),
                district.getBoardHeight())
                .getSpatial());
        rootNode.attachChild(new Car(assetManager, 'c',
                district.getBoardWidth(),
                district.getBoardHeight())
                .getSpatial());
        
        //lights and shadows
        addLightsAndShadows();
        
        //load gui and game
        guiController.loadScreen(GUIController.SCREEN_INGAME);
        guiController.displayPlayerData(game.getPlayer().getName(), game.getPlayer().getMoney(), game.getPlayer().getIconPath());
        
        //set cam
        cam.setLocation(new Vector3f(0, 10, 9));
        cam.setRotation(new Quaternion(8.377186E-4f, 0.9033154f, -0.42897254f, 0.0017641005f));
        
        //start day timer ( calls game.nextDay() )
        game.getTimer().addDayTimeTask(game, 2000);
        
    }

    
    @Override
    public void update(float tpf) {
        
    }
    
    
    @Override
    public void cleanup() {
        
    }
    
    
    private Geometry getGroundPlane(){
        Quad plane = new Quad(50,50);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/world/plane.jpg"));
        Geometry geom = new Geometry("plane", plane);
        geom.setMaterial(mat);
        geom.rotate(FastMath.DEG_TO_RAD*270, 0, 0);
        geom.setLocalTranslation(-plane.getWidth()/2,-0.52f,plane.getHeight()/2);
        return geom;
    }
    
    
    private void addObjects(List<GameObject> objectList){
        for (GameObject go : objectList)
            rootNode.attachChild(go.getSpatial());
    }

    
    private void addLightsAndShadows(){
        // directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1f, 0f, -2f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun); 
        
        // ambient light
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1f));
        rootNode.addLight(al);
        
        // drop shadows
//        final int SHADOWMAP_SIZE=1024;
//        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
//        dlsr.setLight(sun);
//        app.getViewPort().addProcessor(dlsr);
// 
//        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
//        dlsf.setLight(sun);
//        dlsf.setEnabled(true);
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        fpp.addFilter(dlsf);
//        app.getViewPort().addProcessor(fpp);
    }
}
