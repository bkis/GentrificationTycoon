package de.bkiss.gt.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.bkiss.gt.logic.District;
import de.bkiss.gt.logic.Game;
import de.bkiss.gt.logic.Player;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.objects.House;
import de.bkiss.gt.objects.Land;
import de.bkiss.gt.states.MainState;
import de.bkiss.gt.utils.ModelLoader;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.List;
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
    
    private static final String HUD_IMG_PATH = "/Interface/hud/";
    private static final String BTN_BUILD_ON = "hud-button-buil.png";
    private static final String BTN_BUILD_OFF = "hud-button-buil-off.png";
    private static final String BTN_DESTROY_ON = "hud-button-wrck.png";
    private static final String BTN_DESTROY_OFF = "hud-button-wrck-off.png";
    
    private static final int PRICE_BUILD_H1 = 100000;
    private static final int PRICE_BUILD_H2 = 300000;
    private static final int PRICE_BUILD_H3 = 800000;
    private static final int PRICE_BUILD_E1 = 500000;
    private static final int PRICE_BUILD_E2 = 1000000;
    private static final int PRICE_BUILD_E3 = 3000000;
    
    private static final String HUD_DEFAULT_INFO_ICON = "defaultIconImg.png";

    private SimpleApplication app;
    private MainState mainState;
    private District district;
    private Nifty nifty;
    private Screen screen;    
    private NiftyJmeDisplay niftyDisplay;
    private Element popup;
    private Spatial marker;
    private Game game;
    private Player player;
    private String currBuildSelection;
    
    private boolean btnBuildActive;
    private boolean btnDestroyActive;
    
    
    public GUIController(Application app, MainState mainState,
            District district){
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
        
        this.btnBuildActive = false;
        this.btnDestroyActive = false;
        
        //create selection marker
        marker = ModelLoader.createSelectionMarker(app.getAssetManager());
        this.app.getRootNode().attachChild(marker);
        
        //set logging level
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
    }
    
    public void loadScreen(String screenKey){
        if (screenKey.equals(SCREEN_MAINMENU)){
            nifty.fromXml("Interface/screen.xml", screenKey, this);
        } else if (screenKey.equals(SCREEN_INGAME)){
            nifty.gotoScreen(screenKey);
            clearObjectInfo();
        }
        
        this.screen = nifty.getCurrentScreen();
    }
    
    
    public void startGame(){
        popup = nifty.createPopup("popup_loading");
        nifty.showPopup(screen, popup.getId(), null);
        
        mainState.loadState(MainState.STATE_INGAME);
    }
    
    
    public void quitGame(){
        app.stop();
    }
    
    
    public void openPopup(String key){
        popup = null;
        
        //select popup
        if (key.startsWith("build")){
            if (!btnBuildActive) return;
            if (!district.getSelected().isOwnedByPlayer()){
                prepareBuyPopup();
            } else if (district.getSelected() instanceof Land){
                prepareBuildPopup();
            } else if (district.getSelected() instanceof House){
                popup = nifty.createPopup("popup_edit");
            }
        } else if (key.startsWith("destroy")){
            if (!btnDestroyActive) return;
            popup = nifty.createPopup("popup_destroy");
        }
        
        if (popup != null) nifty.showPopup(screen, popup.getId(), null);
    }
    
    
    private void prepareBuyPopup(){
        //choose popup layout
        popup = nifty.createPopup("popup_buy");
        
        //set popup title
        setLabelText(popup.findElementByName("popup_buy_window_title"),
                        "Buy '" + district.getSelected().getName() + "'?");
        
        //check preconditions
        if (player.getMoney() >= district.getSelected().getValue()){
            setLabelText(popup.findElementByName("popup_buy_text"),
                    "Would you like to buy '" + district.getSelected().getName()
                    + "' (" + moneyFormat(district.getSelected().getValue()) + "$)?");
            popup.findElementByName("button_popup_buy_ok").setVisible(true);
        } else {
            setLabelText(popup.findElementByName("popup_buy_text"),
                    "You don't have enough money to buy '" + district.getSelected().getName()
                    + "' (" + moneyFormat(district.getSelected().getValue()) + "$).");
            popup.findElementByName("button_popup_buy_ok").setVisible(false);
        }
    }
    
    
    private void prepareBuildPopup(){
        //choose popup layout
        popup = nifty.createPopup("popup_build");
        
        //set popup title
        setLabelText(popup.findElementByName("popup_build_window_title"),
                        "Build on '" + district.getSelected().getName() + "'?");
        
        //set prices display
        setLabelText(popup.findElementByName("popup_build_price_h1"),
                        moneyFormat(PRICE_BUILD_H1) + "");
        setLabelText(popup.findElementByName("popup_build_price_h2"),
                        moneyFormat(PRICE_BUILD_H2) + "");
        setLabelText(popup.findElementByName("popup_build_price_h3"),
                        moneyFormat(PRICE_BUILD_H3) + "");
        setLabelText(popup.findElementByName("popup_build_price_h21"),
                        moneyFormat(PRICE_BUILD_E1) + "");
        setLabelText(popup.findElementByName("popup_build_price_h22"),
                        moneyFormat(PRICE_BUILD_E2) + "");
        setLabelText(popup.findElementByName("popup_build_price_h23"),
                        moneyFormat(PRICE_BUILD_E3) + "");
    }
    
    
    public void closePopup(){
        nifty.closePopup(popup.getId());
    }

    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }
    
    
    public void buyHouse(){
        player.reduceMoney(district.getSelected().getValue());
        district.getSelected().setOwned(true);
        refreshPlayerMoneyDisplay();
        closePopup();
    }
    
    
    public void buildHouse(){
        String type;
        if      (currBuildSelection.equals("H1")) type = House.TYPE_HOUSE_1;
        else if (currBuildSelection.equals("H2")) type = House.TYPE_HOUSE_2;
        else if (currBuildSelection.equals("H3")) type = House.TYPE_HOUSE_3;
        else if (currBuildSelection.equals("E1")) type = House.TYPE_HOUSE_1;
        else if (currBuildSelection.equals("E2")) type = House.TYPE_HOUSE_2;
        else if (currBuildSelection.equals("E3")) type = House.TYPE_HOUSE_3;
        else                                      type = House.TYPE_HOUSE_1;
        
        player.reduceMoney(district.buildHouse(type, (Land) district.getSelected()).getValue());
        refreshPlayerMoneyDisplay();
        district.setSelected(null);
        clearObjectInfo();
        refreshButtonStates();
        closePopup();
    }
    
    
    public void selectBuild(String selectionKey){
        currBuildSelection = selectionKey;
        
        if (player.getMoney() >= getDefPrice(currBuildSelection)){
            setLabelText("popup_build_selection", "Build for " + moneyFormat(getDefPrice(currBuildSelection)) + "$ ?");
            popup.findElementByName("button_popup_build_ok").setVisible(true);
        } else {
            setLabelText("popup_build_selection", "You don't have enough money (" + moneyFormat(getDefPrice(currBuildSelection)) + "$).");
            popup.findElementByName("button_popup_build_ok").setVisible(false);
        }
    }
    
    
    private int getDefPrice(String buildingKey){
        if      (buildingKey.equals("H1")) return PRICE_BUILD_H1;
        else if (buildingKey.equals("H2")) return PRICE_BUILD_H2;
        else if (buildingKey.equals("H3")) return PRICE_BUILD_H3;
        else if (buildingKey.equals("E1")) return PRICE_BUILD_E1;
        else if (buildingKey.equals("E2")) return PRICE_BUILD_E2;
        else if (buildingKey.equals("E3")) return PRICE_BUILD_E3;
        else    return 0;
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
        return HUD_IMG_PATH + HUD_DEFAULT_INFO_ICON;
    }
    
    
    public String getCurrentImgPath(){
        return district.getSelected().getImagePath();
    }
    
    
    private void setLabelText(String id, String text){
        Element e = getElement(id);
        setLabelText(e, text);
    }
    
    
    private void setLabelText(Element e, String text){
        TextRenderer eRenderer = e.getRenderer(TextRenderer.class);
        eRenderer.setText(text);
        e.layoutElements();
        e.setConstraintWidth(new SizeValue(eRenderer.getTextWidth()+"px"));
        
        e.setWidth(e.getRenderer(TextRenderer.class).getTextWidth());
        
        int width = 0;
        for (Element el : e.getParent().getElements())
            if (width < el.getWidth())
                width = el.getWidth();
        
        e.getParent().setWidth(width);
    }
    
    
    private Element getElement(final String id) {
	return nifty.getScreen(SCREEN_INGAME).findElementByName(id);
    }
    
    
    public void clicked(Geometry clicked){
        if (clicked == null) return;
        if (district.getGameObject(clicked.getParent().getParent())
                == district.getSelected()){
            highlight(new Geometry("null"));
            clearObjectInfo();
            district.setSelected(null);
            refreshButtonStates();
            return;
        }
        
        //System.out.println("CLICKED: " + clicked.getParent().getName());
        
        GameObject go = district.getGameObject(clicked.getParent().getParent());
        highlight((go == null ? null : clicked));
        district.setSelected(go);
        
        if (go == null)
            clearObjectInfo();
        else
            displayObjectInfo(go);
        
        refreshButtonStates();
    }
    
    
    public void setGame(Game game){
        this.game = game;
        this.player = game.getPlayer();
    }
    
    
    private void refreshButtonStates(){
        if (district.getSelected() == null){
            setIconImage("button_build", HUD_IMG_PATH + BTN_BUILD_OFF);
            setIconImage("button_destroy", HUD_IMG_PATH + BTN_DESTROY_OFF);
            this.btnBuildActive = false;
            this.btnDestroyActive = false;
        } else {
            setIconImage("button_build", HUD_IMG_PATH + BTN_BUILD_ON);
            this.btnBuildActive = true;
            if (district.getSelected().isOwnedByPlayer() && district.getSelected() instanceof House){
                setIconImage("button_destroy", HUD_IMG_PATH + BTN_DESTROY_ON);
                this.btnDestroyActive = true;
            }
        }
    }
    
    
    private void displayObjectInfo(GameObject go){
        //properties
        setLabelText("info_1", go.getName());
        setLabelText("info_2", (go instanceof House ? ((House)go).getLuxury() + "" : ""));
        setLabelText("info_3", go.getNeighborhoodValue()+ "");
        setLabelText("info_4", (go instanceof House || go instanceof Land ? moneyFormat(go.getValue()) + "$" : ""));
        setLabelText("info_5", (go instanceof House ? moneyFormat(((House)go).getRent()) + "$/m." : ""));
        setIconImage("panel_hud_info_image", go.getImagePath());
        
        //categories
        setLabelText("cat_1", "Name:");
        setLabelText("cat_2", (go instanceof House ? "Luxury:" : ""));
        setLabelText("cat_3", "Neighbourh.:");
        setLabelText("cat_4", (go instanceof House || go instanceof Land ? "Value:" : ""));
        setLabelText("cat_5", (go instanceof House ? "Rent:" : ""));
    }
    
    
    private void clearObjectInfo(){
        //properties
        setLabelText("info_1", "");
        setLabelText("info_2", "select an");
        setLabelText("info_3", "object to");
        setLabelText("info_4", "view its stats");
        setLabelText("info_5", "");
        
        //categories
        setLabelText("cat_1", "");
        setLabelText("cat_2", "");
        setLabelText("cat_3", "");
        setLabelText("cat_4", "");
        setLabelText("cat_5", "");
    }
    
    
    private void setIconImage(String imageID, String imagePath){
        NiftyImage img = nifty.getRenderEngine().createImage(screen, imagePath, false);
        Element e = nifty.getScreen(SCREEN_INGAME).findElementByName(imageID);
        e.getRenderer(ImageRenderer.class).setImage(img);
    }
    
    
    private void highlight(Geometry geom){
        //clear previous highlight
        Geometry current;
        if (district.getSelected() != null){
            current = (Geometry) ((Node)((Node)district.getSelected().getSpatial()).getChild(0)).getChild(0);
            current.getMaterial().setColor("Ambient", new ColorRGBA(1, 1, 1, 1));
            setMarker(null);
            setIconImage("panel_hud_info_image", getDefaultImgPath());
            district.setSelected(null);
            if (geom == null) return;
        }
        
        //set new highlight
        if (geom != null){
            geom.getMaterial().setColor("Ambient", ColorRGBA.Green);
            setMarker(geom.getParent().getParent().getLocalTranslation());
            district.setSelected(district.getGameObject(geom));
        } else {
            setMarker(null);
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
    
    
    public void displayGameTime(long days){
        setLabelText("game_stat_time",
                "Day: " + days
                + " (next month in " + (30 - (days % 30))
                + " days)");
    }

    
    public void displayPlayerData(String playerName, int playerMoney, String playerIconPath){
        setLabelText("player_name", playerName);
        setLabelText("player_money", moneyFormat(playerMoney) + "$");
        setIconImage("panel_hud_info_avatar", playerIconPath);
    }
    
    
    private void refreshPlayerMoneyDisplay(){
        setLabelText("player_money", moneyFormat(player.getMoney()) + "$");
    }
    
    
    public String getCurrentScreenId(){
        return nifty.getCurrentScreen().getScreenId();
    }
    
    
    private String moneyFormat(int money){
        return String.format("%,d", money);
    }
    
    
    public void displayGentrificationState(){
        List<GameObject> list = district.getObjectList();
        int houseCount = 0;
        int luxuryCount = 0;
        
        for (GameObject go : list){
            if (go instanceof House){
                houseCount++;
                luxuryCount += ((House)go).getLuxury();
            }
        }
        
        setLabelText("game_stat_district_tot_luxury",
                "District Luxury: " + luxuryCount
                + " (Average: " + (luxuryCount/houseCount) + ")");
    }
 
    
    
//    public void hightlightNeighborhoodRadius(){
//        Set<GameObject> nh = district.getNeighborhood(district.getSelected(), true);
//        
//        for (GameObject go : nh){
//            if (go instanceof House)
//                ((Geometry)((Node)((Node)go.getSpatial()).getChild(0)).getChild(0)).getMaterial().setColor("Diffuse", ColorRGBA.Yellow);
//        }
//    }
}
