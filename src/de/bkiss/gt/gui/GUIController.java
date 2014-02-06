package de.bkiss.gt.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.bkiss.gt.states.MainState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author boss
 */
public class GUIController implements ScreenController {
    
    public static int HUD_INFO_PANEL_HEIGHT   = 115;
    public static int HUD_BUTTON_PANEL_HEIGHT = 32;
    
    public static final String SCREEN_MAINMENU = "start";
    public static final String SCREEN_INGAME   = "hud";

    private SimpleApplication app;
    private MainState mainState;
    private Nifty nifty;
    private Screen screen;    
    private NiftyJmeDisplay niftyDisplay;
    private Element popup;
    
    
    public GUIController(Application app, MainState mainState){
        this.app = (SimpleApplication) app;
        this.mainState = mainState;
        niftyDisplay = new NiftyJmeDisplay(
            app.getAssetManager(),
            app.getInputManager(),
            app.getAudioRenderer(),
            app.getViewPort());
        nifty = niftyDisplay.getNifty();
        app.getGuiViewPort().addProcessor(niftyDisplay);
        
        //set logging level
        //Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
        //Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
    }
    
    public void loadScreen(String screenKey){
        if (screenKey.equals("start"))
            nifty.fromXml("Interface/screen.xml", screenKey, this);
        else
            nifty.gotoScreen(screenKey);
        
        this.screen = nifty.getCurrentScreen();
    }
    
    
    public void startGame(){
        mainState.loadState(MainState.STATE_INGAME);
    }
    
    
    public void quitGame(){
        app.stop();
    }
    
    
    public void openPopup(String key){
        popup = nifty.createPopup("popup_layer");
        
//        if (key.startsWith("info")){
//            popup.findControl("window", WindowControl.class).setTitle("Inforrrrmazzionné");
//        }
        
        nifty.showPopup(screen, popup.getId(), null);
    }
    
    
    public void closeWindow(){
        nifty.closePopup(popup.getId());
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
