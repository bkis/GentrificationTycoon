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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import de.bkiss.gt.gui.GUIController;
import de.bkiss.gt.objects.Car;
import de.bkiss.gt.objects.House;
import de.bkiss.gt.utils.InputMapper;
import de.bkiss.gt.utils.ModelLoader;

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
    
    private static final String STH = "street_h";
    private static final String STV = "street_v";
    private static final String STX = "street_x";
    private static final String H01 = "house_01";
    private static final String H02 = "house_02";
    private static final String H03 = "house_03";
    private static final String CON = "construction";
    private static final String LAN = "land";
    
    
    
    public IngameState(InputMapper inputMapper,
                       GUIController guiController){
        this.inputMapper = inputMapper;
        this.guiController = guiController;
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
        
        //construct game world
        constructWorld();
        
        //add ground planes
        rootNode.attachChild(getGroundPlane());
        
        //add cars
        rootNode.attachChild(new Car(assetManager, 'a',
                getBoardMatrix().length,
                getBoardMatrix()[0].length)
                .getSpatial());
        rootNode.attachChild(new Car(assetManager, 'b',
                getBoardMatrix().length,
                getBoardMatrix()[0].length)
                .getSpatial());
        rootNode.attachChild(new Car(assetManager, 'c',
                getBoardMatrix().length,
                getBoardMatrix()[0].length)
                .getSpatial());
        
        //lights
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -1f)));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun); 
        
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1f));
        rootNode.addLight(al);
        
        //load gui
        guiController.loadScreen(GUIController.SCREEN_INGAME);
        
        //set cam
        cam.setLocation(new Vector3f(0, 10, 9));
        cam.setRotation(new Quaternion(8.377186E-4f, 0.9033154f, -0.42897254f, 0.0017641005f));
    }

    
    @Override
    public void update(float tpf) {
        
    }
    
    
    @Override
    public void cleanup() {
        
    }

    
    private String[][] getBoardMatrix(){
        String[][] matrix = {
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV,CON,STV,H01,STV,H01,STV,H03,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV,H01,STV,CON,STV,CON,STV,H01,STV,H03,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV,H01,STV,LAN,STV,H02,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV,H01,STV,H01,STV,LAN,STV,H02,STV,CON,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX},
            {STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV,CON,STV,LAN,STV,H01,STV,H02,STV,H02,STV},
            {STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX,STH,STX}
        };
        return matrix;
    }
    
    
    private void constructWorld(){
        String[][] matrix = getBoardMatrix();
        String curr;
        Spatial spatial = null;
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                
                curr = matrix[i][j];
                
                if        (curr.equals(STX)){
                    spatial = getStreetTile(STX);
                } else if (curr.equals(STH)){
                    spatial = getStreetTile(STH);
                } else if (curr.equals(STV)){
                    spatial = getStreetTile(STV);
                } else if (curr.equals(H01)){
                    spatial = new House(app,
                            House.TYPE_HOUSE_1, "Haus Nr." + i + "" + j)
                            .getSpatial();
                } else if (curr.equals(H02)){
                    spatial = new House(app,
                            House.TYPE_HOUSE_2, "Haus Nr." + i + "" + j)
                            .getSpatial();
                    spatial.rotate(0, FastMath.DEG_TO_RAD*180, 0);
                } else if (curr.equals(H03)){
                    spatial = new House(app,
                            House.TYPE_HOUSE_3, "Haus Nr." + i + "" + j)
                            .getSpatial();
                    spatial.rotate(0, FastMath.DEG_TO_RAD*180, 0);
                } else if (curr.equals(CON)){
                    spatial = ModelLoader.loadSpatial(assetManager,
                            "Models/buildings/", "construction");
                    spatial.rotate(0, FastMath.DEG_TO_RAD*90, 0);
                } else if (curr.equals(LAN)){
                    spatial = ModelLoader.loadSpatial(assetManager,
                            "Models/tiles/", "land");
                }
                
                spatial.move(i-(matrix.length/2), 0, j-(matrix[0].length/2));
                rootNode.attachChild(spatial);
            }
        }
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
    
    
    private Geometry getStreetTile(String key){
        Quad tile = new Quad(1,1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        if (key.equals(STX)){
            mat.setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/streets/street_fx.png"));
        } else if (key.equals(STH)) {
            mat.setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/streets/street_fv.png"));
        } else if (key.equals(STV)) {
            mat.setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/streets/street_fh.png"));
        }
        
        Geometry geom = new Geometry("street", tile);
        geom.setMaterial(mat);
        geom.rotate(FastMath.DEG_TO_RAD*270, 0, 0);
        geom.move(-0.5f, -0.5f, 0.5f);
        return geom;
    }
    
    
}
