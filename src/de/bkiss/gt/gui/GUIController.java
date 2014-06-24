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
import de.bkiss.gt.objects.Expansion;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.objects.House;
import de.bkiss.gt.objects.Land;
import de.bkiss.gt.objects.PublicBuilding;
import de.bkiss.gt.states.MainState;
import de.bkiss.gt.tenants.Tenant;
import de.bkiss.gt.utils.ModelLoader;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    private static final int PRICE_BUILD_H1 = 200000;
    private static final int PRICE_BUILD_H2 = 500000;
    private static final int PRICE_BUILD_H3 = 1500000;
    private static final int PRICE_BUILD_E1 = 450000;   //CLUB
    private static final int PRICE_BUILD_E2 = 1000000;  //GALLERY
    private static final int PRICE_BUILD_E3 = 3000000;  //SCHOOL
    
    private static final String HUD_DEFAULT_INFO_ICON = "defaultIconImg.png";
    
    private static final Color COL_RED = new Color("#ff5555ff");
    private static final Color COL_GREEN = new Color("#55ff55ff");
    

    private SimpleApplication app;
    private MainState mainState;
    private District district;
    private Nifty nifty;
    private Screen screen;    
    private NiftyJmeDisplay niftyDisplay;
    private Map<String, Element> popups;
    private Spatial marker;
    private Game game;
    private Player player;
    private String currBuildSelection;
    private List<Tenant> currTenants;
    private int currTenantIndex;
    
    private boolean btnBuildActive;
    private boolean btnDestroyActive;
    
    
    public GUIController(Application app, MainState mainState,
            District district){
        this.app = (SimpleApplication) app;
        this.mainState = mainState;
        popups = new HashMap<String, Element>();
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
        
        this.currTenantIndex = 0;
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
        nifty.showPopup(screen, popup("loading").getId(), null);
        
        mainState.loadState(MainState.STATE_INGAME);
    }
    
    
    public void quitGame(){
        app.stop();
    }
    
    
    private Element popup(String key){
        if (popups.get("popup_"+key) == null){
            popups.put("popup_"+key, nifty.createPopup("popup_"+key));
            return popup(key);
        } else {
            return popups.get("popup_"+key);
        }
    }
    
    
    public void openPopup(String key){
        //select popup
        if (key.startsWith("build")){
            if (!btnBuildActive) return;
            if (!district.getSelected().isOwnedByPlayer()){
                prepareBuyPopup();
                nifty.showPopup(screen, popup("buy").getId(), null);
            } else if (district.getSelected() instanceof Land){
                prepareBuildPopup();
                nifty.showPopup(screen, popup("build").getId(), null);
            } else if (district.getSelected() instanceof House){
                prepareEditPopup();
                nifty.showPopup(screen, popup("edit").getId(), null);
            }
        } else if (key.startsWith("destroy")){
            if (!btnDestroyActive) return;
            prepareDestroyPopup();
            nifty.showPopup(screen, popup("destroy").getId(), null);
        } else if (key.startsWith("tenant")){
            prepareTenantsPopup();
            nifty.showPopup(screen, popup("tenants").getId(), null);
        }
    }
    
    
    private void prepareBuyPopup(){
        //set popup title
        setLabelText(popup("buy").findElementByName("popup_buy_window_title"),
                        "Buy '" + district.getSelected().getName() + "'?");
        
        //check preconditions
        if (player.getMoney() >= district.getSelected().getValue()){
            setLabelText(popup("buy").findElementByName("popup_buy_text"),
                    "Would you like to buy '" + district.getSelected().getName()
                    + "' (" + moneyFormat(district.getSelected().getValue()) + "$)?");
            popup("buy").findElementByName("button_popup_buy_ok").setVisible(true);
        } else {
            setLabelText(popup("buy").findElementByName("popup_buy_text"),
                    "You don't have enough money to buy '" + district.getSelected().getName()
                    + "' (" + moneyFormat(district.getSelected().getValue()) + "$).");
            popup("buy").findElementByName("button_popup_buy_ok").setVisible(false);
        }
    }
    
    
    private void prepareBuildPopup(){
        //set popup title
        setLabelText(popup("build").findElementByName("popup_build_window_title"),
                        "Build on '" + district.getSelected().getName() + "'?");
        
        //set prices display
        setLabelText(popup("build").findElementByName("popup_build_price_h1"),
                        moneyFormat(PRICE_BUILD_H1) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h2"),
                        moneyFormat(PRICE_BUILD_H2) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h3"),
                        moneyFormat(PRICE_BUILD_H3) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h21"),
                        moneyFormat(PRICE_BUILD_E1) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h22"),
                        moneyFormat(PRICE_BUILD_E2) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h23"),
                        moneyFormat(PRICE_BUILD_E3) + "");
        
        //color price tags
        if (game.getPlayer().getMoney() < PRICE_BUILD_H1)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h1"), COL_RED);
        if (game.getPlayer().getMoney() < PRICE_BUILD_H2)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h2"), COL_RED);
        if (game.getPlayer().getMoney() < PRICE_BUILD_H3)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h3"), COL_RED);
        if (game.getPlayer().getMoney() < PRICE_BUILD_E1)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h21"), COL_RED);
        if (game.getPlayer().getMoney() < PRICE_BUILD_E2)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h22"), COL_RED);
        if (game.getPlayer().getMoney() < PRICE_BUILD_E3)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h23"), COL_RED);
    }
    
    
    private void prepareEditPopup(){
        House h = (House) district.getSelected();
        
        //set popup title
        setLabelText(popup("edit").findElementByName("popup_edit_window_title"),
                        "Properties of '" + h.getName() + "'...");
        
        //set object info
        displayObjectInfoEdit(h);
        
        //set buttons
        if (h.isOccupied()) popup("edit")
                .findElementByName("button_popup_edit_tenants").setVisible(false);
        
        
        //set tenant data
        if (h.isOccupied()){
            String needs = "";
            for (Expansion e  : h.getTenant().getNeeds())
                needs += e.getName() + ", ";
            if (needs.length() == 0){
                needs = "-";
            } else {
                needs = needs.concat("END");
                needs = needs.replaceFirst(", END", "");
            }

            String publicCond = h.getTenant().getPublicCondition();
            if (!publicCond.contains("nothing")){
                publicCond = h.getTenant().getPublicConditionCount() + "x " + publicCond;
            } else {
                publicCond = "-";
            }

            setLabelText(popup("edit").findElementByName("edit_tenant_name"), "Name: " + h.getTenant().getName());
            setLabelText(popup("edit").findElementByName("edit_tenant_prof"), "Job: " + h.getTenant().getProfession());
            setLabelText(popup("edit").findElementByName("edit_tenant_budget"), "Budget: " + moneyFormat(h.getTenant().getBudget()) + " $");
            setLabelText(popup("edit").findElementByName("edit_tenant_occupied"), "");
        } else {
            setLabelText(popup("edit").findElementByName("edit_tenant_name"), "");
            setLabelText(popup("edit").findElementByName("edit_tenant_prof"), "");
            setLabelText(popup("edit").findElementByName("edit_tenant_budget"), "");
            setLabelText(popup("edit").findElementByName("edit_tenant_occupied"), "not occupied...");
            setLabelTextColor(popup("edit").findElementByName("edit_tenant_occupied"), COL_RED);
        }
        
    }
    
    
    private void prepareDestroyPopup(){
        //set popup title
        setLabelText(popup("destroy").findElementByName("popup_destroy_window_title"),
                        "Really destroy '" + district.getSelected().getName() + "'?");
        
        //and
        if (district.getSelected().isOwnedByPlayer()){
            setLabelText(popup("destroy").findElementByName("popup_destroy_text"),
                        "Do you want to destroy '" + district.getSelected().getName() + "'?");
        } else {
            setLabelText(popup("destroy").findElementByName("popup_destroy_text"),
                        "Impossible! There are still people living in '" + district.getSelected().getName() + "'!");
            popup("destroy").findElementByName("button_popup_destroy_ok").setVisible(false);
        }
    }
    
    
    private void prepareTenantsPopup(){
        currTenants = game.getTenants();
        
        setLabelText(popup("tenants").findElementByName("popup_tenants_window_title"),
                    "View potential tenants for '" + district.getSelected().getName() + "'...");
        
        for (int i = 0; i < currTenants.size(); i++) {
            setLabelText(popup("tenants").findElementByName("tenant" + i),
                    currTenants.get(i).getName());
            if (district.getSelected() instanceof House
                    && currTenants.get(i).acceptsHouse((House)district.getSelected())){
                setLabelTextColor(popup("tenants").findElementByName("tenant" + i), COL_GREEN);
            } else {
                setLabelTextColor(popup("tenants").findElementByName("tenant" + i), COL_RED);
            }
        }
        
        popup("tenants").findElementByName("button_popup_tenants_ok").setVisible(false);
    }
    
    
    public void closePopup(String key){
        nifty.closePopup(popups.get("popup_" + key).getId());
    }

    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }
    
    
    public void buyHouse(){
        player.reduceMoney(district.getSelected().getValue());
        district.getSelected().setOwned(true);
        refreshPlayerMoneyDisplay();
        closePopup("buy");
        refreshButtonStates();
    }
    
    
    public void buildHouse(){
        String type;
        if      (currBuildSelection.equals("H1")) type = House.TYPE_HOUSE_1;
        else if (currBuildSelection.equals("H2")) type = House.TYPE_HOUSE_2;
        else if (currBuildSelection.equals("H3")) type = House.TYPE_HOUSE_3;
        else if (currBuildSelection.equals("E1")) type = GameObject.PUBLIC_CLUB;
        else if (currBuildSelection.equals("E2")) type = GameObject.PUBLIC_GALLERY;
        else if (currBuildSelection.equals("E3")) type = GameObject.PUBLIC_SCHOOL;
        else                                      type = House.TYPE_HOUSE_1;
        
        player.reduceMoney(district.buildHouse(type, (Land) district.getSelected()).getValue());
        refreshPlayerMoneyDisplay();
        district.setSelected(null);
        clearObjectInfo();
        refreshButtonStates();
        closePopup("build");
    }
    
    
    public void selectBuild(String selectionKey){
        currBuildSelection = selectionKey;
        
        if (player.getMoney() >= getDefPrice(currBuildSelection)){
            setLabelText("popup_build_selection", "Build for " + moneyFormat(getDefPrice(currBuildSelection)) + "$ ?");
            popup("build").findElementByName("button_popup_build_ok").setVisible(true);
            setLabelTextColor(popup("build").findElementByName("popup_build_selection"), COL_GREEN);
        } else {
            setLabelText("popup_build_selection", "You don't have enough money (" + moneyFormat(getDefPrice(currBuildSelection)) + "$).");
            popup("build").findElementByName("button_popup_build_ok").setVisible(false);
            setLabelTextColor(popup("build").findElementByName("popup_build_selection"), COL_RED);
        }
    }
    
    
    public void selectTenant(String tenant){
        unhighlightTenant(currTenantIndex);
        int index = Integer.parseInt(tenant);
        currTenantIndex = index;
        highlightTenant(index);
        
        String needs = "";
        for (Expansion e  : currTenants.get(index).getNeeds())
            needs += e.getName() + ", ";
        if (needs.length() == 0){
            needs = "-";
        } else {
            needs = needs.concat("END");
            needs = needs.replaceFirst(", END", "");
        }
        
        String publicCond = currTenants.get(index).getPublicCondition();
        if (!publicCond.contains("nothing")){
            publicCond = currTenants.get(index).getPublicConditionCount() + "x " + publicCond;
        } else {
            publicCond = "-";
        }
        
        setLabelText("tenant_name", "Name: " + currTenants.get(index).getName());
        setLabelText("tenant_prof", "Job: " + currTenants.get(index).getProfession());
        setLabelText("tenant_budget", "Budget: " + moneyFormat(currTenants.get(index).getBudget()) + " $");
        setLabelText("tenant_minlux", "min. Luxury: " + currTenants.get(index).getMinLuxury());
        setLabelText("tenant_needs", "Wants: " + needs);
        setLabelText("tenant_public", "District: " + publicCond);
        
        unmarkProblems();
        
        //mark problems
        boolean match = true;
        if (!currTenants.get(index).checkMatchBudget((House)district.getSelected())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_budget"), COL_RED); match = false;}
        if (!currTenants.get(index).checkMatchLuxury((House)district.getSelected())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_minlux"), COL_RED); match = false;}
        if (!currTenants.get(index).checkMatchNeeds((House)district.getSelected())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_needs"), COL_RED); match = false;}
        if (!currTenants.get(index).checkMatchDistrict((House)district.getSelected())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_public"), COL_RED); match = false;}
        
        popup("tenants").findElementByName("button_popup_tenants_ok").setVisible(match);
    }
    
    
    private void highlightTenant(int i){
        setLabelText("tenant"+i, ">>> " + currTenants.get(i).getName());
    }
    
    
    private void unhighlightTenant(int i){
        setLabelText("tenant"+i, currTenants.get(i).getName());
    }
    
    
    private void unmarkProblems(){
        setLabelTextColor(popup("tenants").findElementByName("tenant_budget"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_minlux"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_needs"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_public"), COL_GREEN);
    }
    
    
    public void destroyBuilding(){
        district.destroyBuilding(district.getSelected());
        closePopup("destroy");
        refreshButtonStates();
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
    
    
    private void setLabelTextColor(Element e, Color col){
        TextRenderer eRenderer = e.getRenderer(TextRenderer.class);
        eRenderer.setColor(col);
    }
    
    
    private Element getElement(final String id) {
	return nifty.getScreen(SCREEN_INGAME).findElementByName(id);
    }
    
    
    public void clicked(Geometry clicked){
        if (clicked == null) return;
        if (district.getGameObject(clicked.getParent().getParent())
                == district.getSelected()){
            highlight(null);
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
            if (district.getSelected() instanceof PublicBuilding){
                setIconImage("button_build", HUD_IMG_PATH + BTN_BUILD_OFF);
                this.btnBuildActive = false;
            } else {
                setIconImage("button_build", HUD_IMG_PATH + BTN_BUILD_ON);
                this.btnBuildActive = true;
            }
            if (district.getSelected().isOwnedByPlayer() && !(district.getSelected() instanceof Land)){
                setIconImage("button_destroy", HUD_IMG_PATH + BTN_DESTROY_ON);
                this.btnDestroyActive = true;
            } else {
                setIconImage("button_destroy", HUD_IMG_PATH + BTN_DESTROY_OFF);
                this.btnDestroyActive = false;
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
    
    
    private void displayObjectInfoEdit(House go){
        //properties
        setLabelText(popup("edit").findElementByName("popup_edit_info_1"), go.getName());
        setLabelText(popup("edit").findElementByName("popup_edit_info_2"), go.getLuxury() + "");
        setLabelText(popup("edit").findElementByName("popup_edit_info_3"), go.getNeighborhoodValue()+ "");
        setLabelText(popup("edit").findElementByName("popup_edit_info_4"), go.getValue() + " $");
        setLabelText(popup("edit").findElementByName("popup_edit_info_5"), moneyFormat(go.getRent()) + " $/m.");
        setIconImage(popup("edit").findElementByName("popup_edit_info_image"), go.getImagePath());
        
        //categories
        setLabelText(popup("edit").findElementByName("popup_edit_cat_1"), "Name:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_2"), "Luxury:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_3"), "Neighbourh.:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_4"), "Value:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_5"), "Rent:");
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
        Element e = nifty.getScreen(SCREEN_INGAME).findElementByName(imageID);
        setIconImage(e, imagePath);
    }
    
    
    private void setIconImage(Element e, String imagePath){
        NiftyImage img = nifty.getRenderEngine().createImage(screen, imagePath, false);
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

    
    public void displayPlayerData(String playerName, long playerMoney, String playerIconPath){
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
    
    
    private String moneyFormat(long money){
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
