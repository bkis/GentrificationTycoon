package de.bkiss.gt.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.bkiss.gt.IngameState;
import de.bkiss.gt.logic.District;
import de.bkiss.gt.gui.GUIController;
import de.bkiss.gt.utils.InputMapper;
import de.bkiss.gt.utils.RandomContentGenerator;
import de.bkiss.gt.utils.TextLoader;

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
    
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //register text loader
        app.getAssetManager().registerLoader(TextLoader.class, "txt");
        
        //initialize fields
        super.initialize(stateManager, app);
        this.gen = new RandomContentGenerator(app.getAssetManager());
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        this.district = new District(app, gen);
        
        //init GUI controller
        guiController = new GUIController(app, this, district);
        
        //init input mapper
        inputMapper = new InputMapper(app, guiController);
        
        //load main menu state
        loadState(STATE_MAINMENU);
    }


    @Override
    public void update(float tpf) { 
      /** jME update loop! */
    }

    
    public void loadState(String stateKey){
        if (stateKey.equals(STATE_MAINMENU)){
            mainMenuState = new MainMenuState(inputMapper, guiController);
            stateManager.attach(mainMenuState);
        } else if (stateKey.equals(STATE_INGAME)){
            ingameState = new IngameState(inputMapper, guiController,
                    district, "Rupert", "Interface/hud/avatars/player.png", gen);
            stateManager.attach(ingameState);
            stateManager.detach(mainMenuState);
        }
    }
    

}
