package de.bkiss.gt.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.bkiss.gt.District;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.objects.House;
import de.bkiss.gt.states.MainState;
import de.bkiss.gt.utils.ModelLoader;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private District district;
    private Nifty nifty;
    private Screen screen;    
    private NiftyJmeDisplay niftyDisplay;
    private Element popup;
    private Spatial marker;
    
    
    public GUIController(Application app, MainState mainState, District district){
        this.app = (SimpleApplication) app;
        this.mainState = mainState;
        niftyDisplay = new NiftyJmeDisplay(
            app.getAssetManager(),
            app.getInputManager(),
            app.getAudioRenderer(),
            app.getViewPort());
        this.district = district;
        nifty = niftyDisplay.getNifty();
        app.getGuiViewPort().addProcessor(niftyDisplay);
        
        //create selection marker
        marker = ModelLoader.createSelectionMarker(app.getAssetManager());
        this.app.getRootNode().attachChild(marker);
        
        //set logging level
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
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
//            
//        }
        
        nifty.showPopup(screen, popup.getId(), null);
    }
    
    
    public void closePopup(){
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
    
    
    public String getDefaultImgPath(){
        return "Interface/hud/defaultIconImg.png";
    }
    
    
    private void setLabelText(String id, String text){
        getElement(id).getRenderer(TextRenderer.class).setText(text);
        getElement(id).setWidth(getElement(id).getRenderer(TextRenderer.class).getTextWidth());
    }
    
    
    public void setDebugText(String text){
        setLabelText("info_5", text);
    }
    
    
    private Element getElement(final String id) {
	return nifty.getCurrentScreen().findElementByName(id);
    }
    
    
    public void clicked(Geometry clicked){
        if (clicked == null
                || district.getGameObject(clicked.getParent().getParent())
                == district.getSelected()) return;
        
        System.out.println("CLICKED: " + clicked.getParent().getName());
        
        GameObject go = district.getGameObject(clicked.getParent().getParent());
        highlight((go == null ? null : clicked));
        district.setSelected(go);
        
        if (go == null)
            clearObjectInfo();
        else
            displayObjectInfo(go);
    }
    
    
    private void displayObjectInfo(GameObject go){
        setLabelText("info_1", go.getName());
        setLabelText("info_2", (go instanceof House ? ((House)go).getCondition() + "" : ""));
        setLabelText("info_3", district.getNeighborhoodValue(go)+ "");
        setLabelText("info_4", go.getPrice() + "$");
        setLabelText("info_5", "??? $/m.");
        setIconImage(go.getImagePath());
    }
    
    
    private void clearObjectInfo(){
        setLabelText("info_1", "");
        setLabelText("info_2", "");
        setLabelText("info_3", "");
        setLabelText("info_4", "");
        setLabelText("info_5", "");
    }
    
    
    private void setIconImage(String imagePath){
        NiftyImage img = nifty.getRenderEngine().createImage(screen, imagePath, false);
        Element e = nifty.getCurrentScreen().findElementByName("panel_hud_info_image");
        e.getRenderer(ImageRenderer.class).setImage(img);
    }
    
    
    private void highlight(Geometry geom){
        //set new highlight
        if (geom != null){
            geom.getMaterial().setColor("Ambient", ColorRGBA.Green);
            setMarker(geom.getParent().getParent().getLocalTranslation());
        } else {
            setMarker(null);
        }
        
        //clear previous highlight
        if (district.getSelected() != null && district.getSelected()
                != district.getGameObject(geom)){
            geom = (Geometry) ((Node)((Node)district.getSelected().getSpatial()).getChild(0)).getChild(0);
            geom.getMaterial().setColor("Ambient", new ColorRGBA(1, 1, 1, 1));
        }
    }
    
    
    private void setMarker(Vector3f target){
        if (target != null)
            marker.setLocalTranslation(target.x, 2, target.z);
        else
            marker.setLocalTranslation(100, 2, 100);
    }
    
    
    public void toggleObjectMarkers(){
        district.toogleObjectMarkers();
    }
    
}
