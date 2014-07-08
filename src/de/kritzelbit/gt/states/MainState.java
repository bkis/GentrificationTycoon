package de.kritzelbit.gt.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.gt.IngameState;
import de.kritzelbit.gt.logic.District;
import de.kritzelbit.gt.gui.GUIController;
import de.kritzelbit.gt.tenants.TenantGenerator;
import de.kritzelbit.gt.utils.InputMapper;
import de.kritzelbit.gt.utils.RandomContentGenerator;
import de.kritzelbit.gt.utils.TextLoader;

/**
 *
 * @author boss
 */
public class MainState extends AbstractAppState {
    
    public static final String STATE_MAINMENU = "mainmenu";
    public static final String STATE_INGAME   = "ingame";
    
    private SimpleApplication app;
    private AppStateManager stateManager;
    private InputMapper inputMapper;
    private GUIController guiController;
    private District district;
    
    private AbstractAppState mainMenuState;
    private AbstractAppState ingameState;
    private RandomContentGenerator gen;
    private TenantGenerator tenGen;
    
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //register text loader
        app.getAssetManager().registerLoader(TextLoader.class, "txt");
        
        //initialize fields
        super.initialize(stateManager, app);
        this.gen = new RandomContentGenerator(app.getAssetManager());
        this.tenGen = new TenantGenerator(app.getAssetManager(), gen);
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        this.district = new District(app, gen, tenGen);
        
        //init GUI controller
        guiController = new GUIController(app, this, district, gen);
        
        //init input mapper
        inputMapper = new InputMapper(app, guiController);
        
        //load main menu state
        loadState(STATE_MAINMENU, null, null);
    }


    @Override
    public void update(float tpf) { 
      /** jME update loop! */
    }

    
    public void loadState(String stateKey, String playerName, String playerImg){
        if (stateKey.equals(STATE_MAINMENU)){
            mainMenuState = new MainMenuState(inputMapper, guiController);
            stateManager.attach(mainMenuState);
        } else if (stateKey.equals(STATE_INGAME)){
            ingameState = new IngameState(inputMapper, guiController,
                    district, playerName, playerImg, gen, tenGen);
            stateManager.attach(ingameState);
            stateManager.detach(mainMenuState);
        }
    }
    

}
