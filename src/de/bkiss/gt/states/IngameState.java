package de.bkiss.gt.states;

import de.bkiss.gt.controls.CarControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
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
import de.bkiss.gt.utils.InputMapper;

/**
 *
 * @author boss
 */
public class IngameState extends AbstractAppState{
    
    private Camera cam;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    
    private static final String STH = "street_h";
    private static final String STV = "street_v";
    private static final String STX = "street_x";
    private static final String H01 = "house_01";
    private static final String H02 = "house_02";
    private static final String H03 = "house_03";
    private static final String CON = "construction";
    private static final String LAN = "land";
    
    private static final String C1A = "car_1a";
    private static final String C1B = "car_1b";
    private static final String C1C = "car_1c";
    
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //initialize fields
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        
        //set input mapping
        InputMapper im = new InputMapper(app);
        im.loadInputMapping(InputMapper.INPUT_MODE_INGAME);
        
        //construct game world
        constructWorld();
        
        //add ground planes
        rootNode.attachChild(getGroundPlane());
        
        //add cars
        rootNode.attachChild(makeCar('a'));
        rootNode.attachChild(makeCar('b'));
        rootNode.attachChild(makeCar('c'));
        
        //lights
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -1f)));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun); 
        
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1f));
        rootNode.addLight(al);
        
        //set cam
        cam.setLocation(new Vector3f(0, 10, 9));
        cam.setRotation(new Quaternion(8.377186E-4f, 0.9033154f, -0.42897254f, 0.0017641005f));
    }

    
    @Override
    public void update(float tpf) {
        
    }
    
    
    @Override
    public void cleanup() {
        rootNode.detachAllChildren();
    }
    
    
    private Spatial getSpatial(String path, String key){
        Spatial s = null;
        
        try {
            s = app.getAssetManager().loadModel(
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
                    spatial = getSpatial("Models/tiles/", "street_x");
                } else if (curr.equals(STH)){
                    spatial = getSpatial("Models/tiles/", "street_s");
                } else if (curr.equals(STV)){
                    spatial = getSpatial("Models/tiles/", "street_s");
                    spatial.rotate(0, FastMath.DEG_TO_RAD*90, 0);
                } else if (curr.equals(H01)){
                    spatial = getSpatial("Models/buildings/", "house_1" + randomABC());
                } else if (curr.equals(H02)){
                    spatial = getSpatial("Models/buildings/", "house_2" + randomABC());
                    spatial.rotate(0, FastMath.DEG_TO_RAD*180, 0);
                } else if (curr.equals(H03)){
                    spatial = getSpatial("Models/buildings/", "house_3" + randomABC());
                    spatial.rotate(0, FastMath.DEG_TO_RAD*180, 0);
                } else if (curr.equals(CON)){
                    spatial = getSpatial("Models/buildings/", "construction");
                    spatial.rotate(0, FastMath.DEG_TO_RAD*90, 0);
                } else if (curr.equals(LAN)){
                    spatial = getSpatial("Models/tiles/", "land");
                }
                
                spatial.move(i-(matrix.length/2), 0, j-(matrix[0].length/2));
                rootNode.attachChild(spatial);
            }
        }
    }
    
    
    private char randomABC(){
        int i = FastMath.rand.nextInt(9);
        if (i < 3) return 'a';
        if (i < 6) return 'b';
        if (i < 9) return 'c';
        return 'a';
    }
    
    
    private Node makeCar(char key){
        //make car
        Spatial car;
        switch (key){
            case 'a': car = getSpatial("Models/mobiles/", C1A); break;
            case 'b': car = getSpatial("Models/mobiles/", C1B); break;
            case 'c': car = getSpatial("Models/mobiles/", C1C); break;
            default:  car = getSpatial("Models/mobiles/", C1A); break;
        }
        
        car.scale(0.3f, 0.4f, 0.7f);
        
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
        Node carNode = new Node();
        carNode.attachChildAt(car, 0);
        carNode.attachChild(exhaustEffect);
        carNode.addControl(new CarControl(
                getBoardMatrix().length, getBoardMatrix()[0].length));
        return carNode;
    }
    
    
    private Geometry getGroundPlane(){
        Quad plane = new Quad(50,50);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/world/plane.jpg"));
        Geometry geom = new Geometry("plane", plane);
        geom.setMaterial(mat);
        geom.rotate(FastMath.DEG_TO_RAD*270, 0, 0);
        geom.setLocalTranslation(-plane.getWidth()/2,-0.52f,plane.getHeight()/2);
        return geom;
    }
    
    
}
