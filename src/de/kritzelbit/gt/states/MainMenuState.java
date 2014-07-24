package de.kritzelbit.gt.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.gt.gui.GUIController;
import de.kritzelbit.gt.utils.InputMapper;

/**
 *
 * @author boss
 */
public class MainMenuState extends AbstractAppState{
    
    private SimpleApplication app;
    private InputMapper inputMapper;
    private GUIController guiController;
    
    
    public MainMenuState(InputMapper inputMapper, GUIController guiController){
        this.inputMapper = inputMapper;
        this.guiController = guiController;
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //initialize fields
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        
        //set input mapping
        inputMapper.loadInputMapping(InputMapper.INPUT_MODE_MAINMENU);
        
        //load gui
        guiController.loadScreen(GUIController.SCREEN_MAINMENU);
    }


    @Override
    public void update(float tpf) { 
      /** jME update loop! */ 
    }
    
    
}
