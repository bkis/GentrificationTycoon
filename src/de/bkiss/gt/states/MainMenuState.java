package de.bkiss.gt.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.bkiss.gt.utils.InputMapper;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author boss
 */
public class MainMenuState extends AbstractAppState implements ScreenController{
    
    private SimpleApplication app;
    private Nifty nifty;
    private Screen screen;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //initialize fields
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        
        //set input mapping
        InputMapper im = new InputMapper(app);
        im.loadInputMapping(InputMapper.INPUT_MODE_MAINMENU);
        
        //load GUI
        loadGUI();
    }


    @Override
    public void update(float tpf) { 
      /** jME update loop! */ 
    }
    
    
    private void loadGUI(){
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
            app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/screen.xml", "start", this);
        app.getGuiViewPort().addProcessor(niftyDisplay);
    }
    
    
    public void startGame(){
        nifty.gotoScreen("hud");
        app.getStateManager().attach(new IngameState());
        app.getStateManager().detach(this);
    }
    
    
    public void quitGame(){
        app.stop();
    }

    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    
    public void onStartScreen() {
        
    }

    
    public void onEndScreen() {
        
    }
    
    
    public String getUserName(){
        return System.getProperty("user.name");
    }
    
    
    public String getDescriptionText(){
        return "A funniy-ish yet serious-ish 3D arcade "
                + "management simulation making you a mean "
                + "and greedy real estate agent in a "
                + "working-class district hit by gentrification!\n\n"
                + "Take from the poor - give to the rich!";
    }
    
    
}
