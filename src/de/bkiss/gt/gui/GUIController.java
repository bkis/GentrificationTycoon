package de.bkiss.gt.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.bkiss.gt.logic.Bank;
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
import de.bkiss.gt.utils.Format;
import de.bkiss.gt.utils.ModelLoader;
import de.bkiss.gt.utils.RandomContentGenerator;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.textfield.filter.input.TextFieldInputFilter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String BTN_HOUSE_BUILD = HUD_IMG_PATH + "hud-button-house-build.png";
    private static final String BTN_HOUSE_BUY = HUD_IMG_PATH + "hud-button-house-buy.png";
    private static final String BTN_HOUSE_EDIT = HUD_IMG_PATH + "hud-button-house-edit.png";
    private static final String BTN_HOUSE_OFF = HUD_IMG_PATH + "hud-button-buil-off.png";
    private static final String BTN_DESTROY_ON = HUD_IMG_PATH + "hud-button-wrck.png";
    private static final String BTN_DESTROY_OFF = HUD_IMG_PATH + "hud-button-wrck-off.png";
    
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
    private List<Expansion> allExtras;
    private List<Expansion> availableExtras;
    private List<Expansion> selectedExtras;
    private List<Alert> alerts;
    private int selectedExtra;
    
    private boolean btnBuildActive;
    private boolean btnDestroyActive;
    private boolean alert;
    
    
    public GUIController(Application app, MainState mainState,
            District district, RandomContentGenerator gen){
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
        
        this.alerts = new ArrayList<Alert>();
        this.btnBuildActive = false;
        this.btnDestroyActive = false;
        this.allExtras = gen.getAllExpansions();
        this.availableExtras = new ArrayList<Expansion>();
        this.selectedExtras = new ArrayList<Expansion>();
        this.currTenants = new ArrayList<Tenant>();
        this.currBuildSelection = "H1";
        
        //create selection marker
        marker = ModelLoader.createSelectionMarker(app.getAssetManager());
        this.app.getRootNode().attachChild(marker);
        
        //set logging level
//        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
//        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
        
        this.currTenantIndex = 999;
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
        mainState.loadState(MainState.STATE_INGAME,
                screen.findNiftyControl("player_name_input", TextField.class).getRealText(),
                "Interface/hud/avatars/" + screen.findNiftyControl("player_avatar_select", ImageSelect.class).getSelectedImageIndex() + ".png");
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
            if (!sel().isOwnedByPlayer()){
                prepareBuyPopup();
                nifty.showPopup(screen, popup("buy").getId(), null);
            } else if (sel() instanceof Land){
                prepareBuildPopup();
                nifty.showPopup(screen, popup("build").getId(), null);
            } else if (sel() instanceof House){
                prepareEditPopup();
                nifty.showPopup(screen, popup("edit").getId(), null);
            }
        } else if (key.startsWith("destroy")){
            if (!btnDestroyActive) return;
            if (sel().isOccupied()){
                nifty.showPopup(screen, popup("nodestroy").getId(), null);
            } else {
                prepareDestroyPopup();
                nifty.showPopup(screen, popup("destroy").getId(), null);
            }
        } else if (key.startsWith("tenant")){
            prepareTenantsPopup();
            nifty.showPopup(screen, popup("tenants").getId(), null);
        } else if (key.startsWith("quit")){
            nifty.showPopup(screen, popup("quit").getId(), null);
        } else if (key.startsWith("sell")){
            prepareSellPopup();
            nifty.showPopup(screen, popup("sell").getId(), null);
        } else if (key.startsWith("rent")){
            popup("rent").findNiftyControl("rent_input", TextField.class).enableInputFilter(tif);
            popup("rent").findElementByName("button_popup_rent_cancel").setVisible(((House)sel()).isOccupied());
            nifty.showPopup(screen, popup("rent").getId(), null);
        } else if (key.startsWith("extras")){
            prepareExtrasPopup();
            nifty.showPopup(screen, popup("extras").getId(), null);
        } else if (key.startsWith("bank")){
            prepareBankPopup();
            nifty.showPopup(screen, popup("bank").getId(), null);
        }
    }
    
    
    public void showAlert(Alert a){
        if (alert){
            if (!alerts.contains(a)) queueAlert(a);
            return;
        }
        
        setLabelText(popup("note").findElementByName("popup_note_window_title"), a.getTitle());
        setLabelText(popup("note").findElementByName("popup_note_text1"), a.getLine1());
        setLabelText(popup("note").findElementByName("popup_note_text2"), a.getLine2());
        nifty.showPopup(screen, popup("note").getId(), null);
        alerts.remove(a);
        alert = true;
    }
    
    
    private void queueAlert(Alert alert){
        alerts.add(alert);
    }
    
    
    public void nextAlert(){
        if (alerts.size() > 0) showAlert(alerts.get(0));
    }
    
    
    public void closeAlert(){
        closePopup("note");
        alert = false;
    }
    
    
    private TextFieldInputFilter tif = new TextFieldInputFilter() {
        public boolean acceptInput(int i, char c) {
            if (!(c + "").matches("[0-9]")) return false;
            String s = popup("rent").findNiftyControl("rent_input", TextField.class).getRealText();
            if (s.length() >= 6) return false;
            return true;
        }

        public boolean acceptInput(int i, CharSequence cs) {
            if (!(cs + "").matches("[0-9]+")) return false;
            String s = popup("rent").findNiftyControl("rent_input", TextField.class).getRealText();
            if (s.length() >= 6) return false;
            return true;
        }
    };
    
    
    public void prepareBankPopup(){
        setLabelText(popup("bank").findElementByName("bank_balance"),
                "Balance: " + Format.money(game.getBank().getBalance()) + " $");
        
        float owned = district.getOwnedRatio();
        if (game.getBank().getBalance() < 0) {
            setLabelTextColor(popup("bank").findElementByName("bank_balance"), COL_RED);
            setLabelTextColor(popup("bank").findElementByName("bank_interest"), COL_RED);
            setLabelText(popup("bank").findElementByName("bank_interest"),
                        "Interest: " + Format.money(game.getBank().getCurrentInterestMoney(owned)) + " $/m. (" + Format.twoDecimals(game.getBank().getCurrentInterest(owned)*100) + "%)");
        } else {
            setLabelTextColor(popup("bank").findElementByName("bank_balance"), COL_GREEN);
            setLabelTextColor(popup("bank").findElementByName("bank_interest"), COL_GREEN);
            setLabelText(popup("bank").findElementByName("bank_interest"),
                        "Interest: " + Format.money(game.getBank().getCurrentInterestMoney(owned)) + " $/m. (" + Format.twoDecimals(game.getBank().getCurrentInterest(owned)*100) + "%)");
        }
    }
    
    
    public void bankAction(String action){
        int amount;
        if (action.startsWith("-")){
            amount = Integer.parseInt(action.substring(1));
            if (game.getBank().canTake(amount)){
                player.addMoney(game.getBank().take(amount));
                refreshPlayerMoneyDisplay();
            } else {
                showAlert(new Alert("I'm afraid we can't do that...", "The bank won't give you more money.",
                        "The maximum loan is " + Format.money(Bank.MAX_DEBTS) + "$."));
            }
        } else {
            amount = Integer.parseInt(action);
            if (player.getMoney() >= amount){
                player.reduceMoney(game.getBank().put(amount));
                refreshPlayerMoneyDisplay();
            } else {
                showAlert(new Alert("Sir, your pockets seem empty!", "You can't transfer this sum to your",
                        "account, as you don't have " + Format.money(amount) + "$ in cash."));
            }
        }
        prepareBankPopup();
    }
    
    
    private void prepareExtrasPopup(){
        availableExtras.clear();
        selectedExtras.clear();
        
        for (Expansion e : allExtras){
            if (!((House)sel()).hasExpansion(e.getName())){
                availableExtras.add(0, e);
            }
        }
        
        markExtras();
    }
    
    
    private void markExtras(){
        int p = 0;
        for (Expansion e : selectedExtras) p += e.getPrice();
        
        for (int i = 0; i < 10; i++) {
            if (availableExtras.size() > i){
                setLabelText(popup("extras").findElementByName("popup_extra"+i),
                    availableExtras.get(i).getName() + " ("
                        + availableExtras.get(i).getPrice() + "$, Luxury +"
                        + availableExtras.get(i).getEffect() + ")");
                if (player.getMoney() >= availableExtras.get(i).getPrice() + p)
                    setLabelTextColor(popup("extras").findElementByName("popup_extra"+i), COL_GREEN);
                else
                    setLabelTextColor(popup("extras").findElementByName("popup_extra"+i), COL_RED);
                
                if (selectedExtras.contains(availableExtras.get(i)))
                    setLabelTextColor(popup("extras").findElementByName("popup_extra"+i), Color.WHITE);
            } else {
                setLabelText(popup("extras").findElementByName("popup_extra"+i), "-");
                setLabelTextColor(popup("extras").findElementByName("popup_extra"+i), Color.WHITE);
            }
        }
        
        setLabelText(popup("extras").findElementByName("popup_extra_sum"),
                "Total Cost: " + Format.money(p) + " $");
    }
    
    
    public void selectExtra(String index){
        int p = 0;
        for (Expansion e : selectedExtras) p += e.getPrice();
        
        int i = Integer.parseInt(index);
        if (selectedExtras.contains(availableExtras.get(i))){
            selectedExtras.remove(availableExtras.get(i));
        } else if (player.getMoney() >= p + availableExtras.get(i).getPrice()){
            selectedExtras.add(availableExtras.get(i));
        }
        markExtras();
    }
    
    
    public void buyExtra(){
        for (Expansion e : selectedExtras){
            ((House)sel()).addExpansion(e);
            player.reduceMoney(e.getPrice());
        }
        
        availableExtras.clear();
        selectedExtras.clear();
        
        refreshExpansionList((House)sel());
        displayObjectInfoEdit((House)sel());
        refreshPlayerMoneyDisplay();
        closePopup("extras");
    }

    
    private void prepareSellPopup(){
        //set popup title
        setLabelText(popup("sell").findElementByName("popup_sell_window_title"),
                        "Sell '" + sel().getName().replaceAll("[0-9.]", "") + "'?");
        
        //set image
        setIconImage(popup("sell").findElementByName("popup_sell_img"),
                sel().getImagePath());
        
        //set price label
        setLabelText(popup("sell").findElementByName("popup_sell_price"),
                    Format.money(sel().getValue()) + " $");
        
        //check preconditions
        setLabelTextColor(popup("sell").findElementByName("popup_sell_price"), COL_GREEN);
        setLabelText(popup("sell").findElementByName("popup_sell_text"),
                "Do you want to sell '" + sel().getName().replaceAll("[0-9.]", "") + "'?");
    }
    
    
    private void prepareBuyPopup(){
        //set popup title
        setLabelText(popup("buy").findElementByName("popup_buy_window_title"),
                        "Buy '" + sel().getName().replaceAll("[0-9.]", "") + "'?");
        
        //set image
        setIconImage(popup("buy").findElementByName("popup_buy_img"),
                sel().getImagePath());
        
        //set price label
        setLabelText(popup("buy").findElementByName("popup_buy_price"),
                    Format.money(sel().getValue()) + " $");
        
        //check preconditions
        if (player.getMoney() >= sel().getValue()){
            setLabelTextColor(popup("buy").findElementByName("popup_buy_price"), COL_GREEN);
            setLabelText(popup("buy").findElementByName("popup_buy_text"),
                    "Would you like to buy '" + sel().getName().replaceAll("[0-9.]", "") + "'?");
            popup("buy").findElementByName("button_popup_buy_ok").setVisible(true);
        } else {
            setLabelTextColor(popup("buy").findElementByName("popup_buy_price"), COL_RED);
            setLabelText(popup("buy").findElementByName("popup_buy_text"),
                    "You don't have enough money to buy '" + sel().getName().replaceAll("[0-9.]", "") + "'.");
            popup("buy").findElementByName("button_popup_buy_ok").setVisible(false);
        }
    }
    
    
    private void prepareBuildPopup(){
        //set popup title
        setLabelText(popup("build").findElementByName("popup_build_window_title"),
                        "Build on '" + sel().getName().replaceAll("[0-9.]", "") + "'?");
        
        //set prices display
        setLabelText(popup("build").findElementByName("popup_build_price_h1"),
                        Format.money(PRICE_BUILD_H1) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h2"),
                        Format.money(PRICE_BUILD_H2) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h3"),
                        Format.money(PRICE_BUILD_H3) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h21"),
                        Format.money(PRICE_BUILD_E1) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h22"),
                        Format.money(PRICE_BUILD_E2) + "");
        setLabelText(popup("build").findElementByName("popup_build_price_h23"),
                        Format.money(PRICE_BUILD_E3) + "");
        
        //color price tags
        colorPriceTags();
    }
    
    
    private void colorPriceTags(){
        if (game.getPlayer().getMoney() < PRICE_BUILD_H1)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h1"), COL_RED);
        else
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h1"), COL_GREEN);
        
        if (game.getPlayer().getMoney() < PRICE_BUILD_H2)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h2"), COL_RED);
        else
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h2"), COL_GREEN);
        
        if (game.getPlayer().getMoney() < PRICE_BUILD_H3)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h3"), COL_RED);
        else
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h3"), COL_GREEN);
        
        if (game.getPlayer().getMoney() < PRICE_BUILD_E1)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h21"), COL_RED);
        else
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h21"), COL_GREEN);
        
        if (game.getPlayer().getMoney() < PRICE_BUILD_E2)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h22"), COL_RED);
        else
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h22"), COL_GREEN);
        
        if (game.getPlayer().getMoney() < PRICE_BUILD_E3)
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h23"), COL_RED);
        else
            setLabelTextColor(popup("build").findElementByName("popup_build_price_h23"), COL_GREEN);
    }
    
    
    private void prepareEditPopup(){
        House h = (House) sel();
        
        //set popup title
        setLabelText(popup("edit").findElementByName("popup_edit_window_title"),
                        "Properties of '" + h.getName().replaceAll("[0-9.]", "") + "'...");
        
        //set object info
        displayObjectInfoEdit(h);
        
        //set buttons
        popup("edit").findElementByName("button_popup_edit_tenants").setVisible(!h.isOccupied());
        
        //set tenant data
        if (h.isOccupied()){
            setLabelText(popup("edit").findElementByName("edit_tenant_name"), "Name: " + h.getTenant().getName());
            setLabelText(popup("edit").findElementByName("edit_tenant_prof"), "Job: " + h.getTenant().getProfession());
            setLabelText(popup("edit").findElementByName("edit_tenant_budget"), "Budget: " + Format.money(h.getTenant().getBudget()) + " $");
            setLabelText(popup("edit").findElementByName("edit_tenant_occupied"), "");
        } else {
            setLabelText(popup("edit").findElementByName("edit_tenant_name"), "");
            setLabelText(popup("edit").findElementByName("edit_tenant_prof"), "");
            setLabelText(popup("edit").findElementByName("edit_tenant_budget"), "");
            setLabelText(popup("edit").findElementByName("edit_tenant_occupied"), "not occupied...");
            setLabelTextColor(popup("edit").findElementByName("edit_tenant_occupied"), COL_RED);
        }
        
        //display expansions
        refreshExpansionList(h);
    }
    
    
    private void refreshExpansionList(House h){
        int i = 0;
        for (Expansion e : h.getExpansions()){
            setLabelText(popup("edit").findElementByName("popup_edit_exp" + i), e.getName());
            i++;
        }
        for (int j = i; j < 10; j++) {
            setLabelText(popup("edit").findElementByName("popup_edit_exp" + j), "-");
        }
    }
    
    
    private void prepareDestroyPopup(){
        //set popup title
        setLabelText(popup("destroy").findElementByName("popup_destroy_window_title"),
                        "Really destroy '" + sel().getName().replaceAll("[0-9.]", "") + "'?");
        
        //and
        if (sel().isOwnedByPlayer()){
            setLabelText(popup("destroy").findElementByName("popup_destroy_text"),
                        "Do you want to destroy '" + sel().getName().replaceAll("[0-9.]", "") + "'?");
        } else {
            setLabelText(popup("destroy").findElementByName("popup_destroy_text"),
                        "Impossible! There are still people living in '" + sel().getName().replaceAll("[0-9.]", "") + "'!");
            popup("destroy").findElementByName("button_popup_destroy_ok").setVisible(false);
        }
    }
    
    
    private void prepareTenantsPopup(){
        currTenants.clear();
        currTenants.addAll(game.getTenants());
        
        setLabelText(popup("tenants").findElementByName("popup_tenants_window_title"),
                    "View potential tenants for '" + sel().getName().replaceAll("[0-9.]", "") + "'...");
        
        
        setLabelText(popup("tenants").findElementByName("tenant_name"), "");
        setLabelText(popup("tenants").findElementByName("tenant_prof"), "");
        setLabelText(popup("tenants").findElementByName("tenant_budget"), "");
        setLabelText(popup("tenants").findElementByName("tenant_minlux"), "");
        setLabelText(popup("tenants").findElementByName("tenant_needs"), "");
        setLabelText(popup("tenants").findElementByName("tenant_public"), "");
        setLabelText(popup("tenants").findElementByName("tenant_students"), "");
        setLabelText(popup("tenants").findElementByName("tenant_avgbudget"), "");
        
        
        for (int i = 0; i < 10; i++) {
            if (currTenants.size() < i+1) break;
            setLabelText(popup("tenants").findElementByName("tenant" + i),
                    currTenants.get(i).getName() + " (" + Format.money(currTenants.get(i).getBudget()) + "$)");
            if (currTenants.get(i).acceptsHouse((House)sel())){
                setLabelTextColor(popup("tenants").findElementByName("tenant" + i), COL_GREEN);
            } else {
                setLabelTextColor(popup("tenants").findElementByName("tenant" + i), COL_RED);
            }
        }
        
        //popup("tenants").findElementByName("button_popup_tenants_ok").setVisible(false);
    }
    
    
    public void closePopup(String key){
        nifty.closePopup(popup(key).getId());
//        highlight(null);
//        clearObjectInfo();
//        highlightGameObject(sel());
        displayObjectInfo(sel());
        refreshButtonStates();
    }

    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }
    
    
    public void buyHouse(){
        player.reduceMoney(sel().getValue());
        sel().setOwned(true);
        refreshPlayerMoneyDisplay();
        refreshButtonStates();
        closePopup("buy");
    }
    
    
    public void acceptTenant(){
        if (currTenantIndex == 999){
            showAlert(new Alert("Well...", "You didn't select a tenant, yet.",
                    "Which one do you want to pick?"));
            return;
        }
        if (!tenantIsMatch()){
            showAlert(new Alert("No, thank's...", "This tenant is not interested in your house.",
                    "Maybe you should improve it a litte?"));
            return;
        }
        
        closePopup("tenants");
        ((House)sel()).setTenant(currTenants.get(currTenantIndex));
        ((House)sel()).setOccupied(true);
        
        setLabelText(popup("edit").findElementByName("edit_tenant_name"), "Name: " + ((House)sel()).getTenant().getName());
        setLabelText(popup("edit").findElementByName("edit_tenant_prof"), "Job: " + ((House)sel()).getTenant().getProfession());
        setLabelText(popup("edit").findElementByName("edit_tenant_budget"), "Budget: " + Format.money(((House)sel()).getTenant().getBudget()) + " $");
        setLabelText(popup("edit").findElementByName("edit_tenant_occupied"), "");
        popup("edit").findElementByName("button_popup_edit_tenants").setVisible(false);
        currTenantIndex = 999;
    }
    
    
    public void setRent(){
        int r = Integer.parseInt(popup("rent").findNiftyControl("rent_input",
                TextField.class).getRealText());
        ((House)sel()).setRent(r);
        closePopup("rent");
        setLabelText(popup("edit").findElementByName("popup_edit_info_5"), Format.money(((House)sel()).getRent())+" $/m");
    }
    
    
    public void maxRent(){
        ((House)sel()).setRent(((House)sel()).getTenant().getBudget());
        closePopup("rent");
        setLabelText(popup("edit").findElementByName("popup_edit_info_5"), Format.money(((House)sel()).getRent())+" $/m");
    }
    
    
    public void sellHouse(){
        player.addMoney(sel().getValue());
        sel().setOwned(false);
        refreshPlayerMoneyDisplay();
        closePopup("sell");
        closePopup("edit");
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
        
        //check max public builds
        if (type.contains("public") && district.getTypeCount(type) >= 3){
            showAlert(new Alert("Enough is enough!",
                    "You can only build up to three",
                    "community buildings of each type!"));
            return;
        }
        
        district.buildHouse(type, (Land) sel());
        player.reduceMoney(getDefPrice(currBuildSelection));
        refreshPlayerMoneyDisplay();
        closePopup("build");
    }
    
    
    public void selectBuild(String selectionKey){
        if (player.getMoney() >= getDefPrice(selectionKey)){
            setLabelText("popup_build_selection", "Build for " + Format.money(getDefPrice(selectionKey)) + "$ ?");
            popup("build").findElementByName("button_popup_build_ok").setVisible(true);
            setLabelTextColor(popup("build").findElementByName("popup_build_selection"), COL_GREEN);
        } else {
            setLabelText("popup_build_selection", "You don't have enough money (" + Format.money(getDefPrice(selectionKey)) + "$).");
            popup("build").findElementByName("button_popup_build_ok").setVisible(false);
            setLabelTextColor(popup("build").findElementByName("popup_build_selection"), COL_RED);
        }
        
        PanelRenderer pr = popup("build").findElementByName("popup_build_window_content_gallery_" + currBuildSelection).getRenderer(PanelRenderer.class);
        pr.setBackgroundColor(new Color("#000000ff"));
        
        pr = popup("build").findElementByName("popup_build_window_content_gallery_" + selectionKey).getRenderer(PanelRenderer.class);
        pr.setBackgroundColor(new Color("#232323ff"));
        
        currBuildSelection = selectionKey;
        colorPriceTags();
    }
    
    
    public void selectTenant(String tenant){
        if (currTenantIndex > 9) currTenantIndex = 0;
        
        unhighlightTenant(currTenantIndex);
        int index = Integer.parseInt(tenant);
        currTenantIndex = index;
        highlightTenant(index);
        
        String needs = "";
        for (Expansion e : currTenants.get(index).getNeeds())
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
        setLabelText("tenant_budget", "Budget: " + Format.money(currTenants.get(index).getBudget()) + " $");
        setLabelText("tenant_minlux", "min. Luxury: " + currTenants.get(index).getMinLuxury());
        setLabelText("tenant_needs", "Wants: " + needs);
        setLabelText("tenant_public", "District: " + publicCond);
        
        if (!currTenants.get(currTenantIndex).checkMatchStudentRatio((House)sel()))
            setLabelText("tenant_students", "\"I want more students around!\" ("+ currTenants.get(index).getMinStudentsRatio() + "%)");
        else
            setLabelText("tenant_students", "");
            
        if (!currTenants.get(currTenantIndex).checkMatchAvgBudget((House)sel()))
            setLabelText("tenant_avgbudget", "\"People are poor, here!\" (min. " + Format.money(currTenants.get(index).getMinAverageBudget()) + "$ avg.)");
        else
            setLabelText("tenant_avgbudget", "");
        
        unmarkProblems();
        
        //mark problems
        tenantIsMatch();
        
        //popup("tenants").findElementByName("button_popup_tenants_ok").setVisible(match);
    }
    
    
    private boolean tenantIsMatch(){
        boolean match = true;
        if (!currTenants.get(currTenantIndex).checkMatchBudget((House)sel())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_budget"), COL_RED); match = false;}
        if (!currTenants.get(currTenantIndex).checkMatchLuxury((House)sel())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_minlux"), COL_RED); match = false;}
        if (!currTenants.get(currTenantIndex).checkMatchNeeds((House)sel())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_needs"), COL_RED); match = false;}
        if (!currTenants.get(currTenantIndex).checkMatchDistrict((House)sel())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_public"), COL_RED); match = false;}
        if (!currTenants.get(currTenantIndex).checkMatchStudentRatio((House)sel())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_students"), COL_RED); match = false;}
        if (!currTenants.get(currTenantIndex).checkMatchAvgBudget((House)sel())){
            setLabelTextColor(popup("tenants").findElementByName("tenant_avgbudget"), COL_RED); match = false;}
        return match;
    }
    
    
    private void highlightTenant(int i){
        setLabelTextColor(popup("tenants").findElementByName("tenant"+i), Color.WHITE);
    }
    
    
    private void unhighlightTenant(int i){
        if (tenantIsMatch())
            setLabelTextColor(popup("tenants").findElementByName("tenant"+i), COL_GREEN);
        else
            setLabelTextColor(popup("tenants").findElementByName("tenant"+i), COL_RED);
    }
    
    
    private void unmarkProblems(){
        setLabelTextColor(popup("tenants").findElementByName("tenant_budget"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_minlux"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_needs"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_public"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_students"), COL_GREEN);
        setLabelTextColor(popup("tenants").findElementByName("tenant_avgbudget"), COL_GREEN);
    }
    
    
    public void destroyBuilding(){
        district.destroyBuilding(sel());
        closePopup("destroy");
    }
    
    
    private int getDefPrice(String buildingKey){
        if      (buildingKey.equalsIgnoreCase("H1")) return PRICE_BUILD_H1;
        else if (buildingKey.equalsIgnoreCase("H2")) return PRICE_BUILD_H2;
        else if (buildingKey.equalsIgnoreCase("H3")) return PRICE_BUILD_H3;
        else if (buildingKey.equalsIgnoreCase("E1")) return PRICE_BUILD_E1;
        else if (buildingKey.equalsIgnoreCase("E2")) return PRICE_BUILD_E2;
        else if (buildingKey.equalsIgnoreCase("E3")) return PRICE_BUILD_E3;
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
        return sel().getImagePath();
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
                == sel()){
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
        if (sel() == null){
            setIconImage("button_build", BTN_HOUSE_OFF);
            setIconImage("button_destroy", BTN_DESTROY_OFF);
            this.btnBuildActive = false;
            this.btnDestroyActive = false;
        } else {
            if (sel() instanceof PublicBuilding){
                setIconImage("button_build", BTN_HOUSE_OFF);
                this.btnBuildActive = false;
            } else {
                if (sel().isOwnedByPlayer()){
                    if (sel() instanceof Land) setIconImage("button_build", BTN_HOUSE_BUILD);
                    else setIconImage("button_build", BTN_HOUSE_EDIT);
                } else {
                    setIconImage("button_build", BTN_HOUSE_BUY);
                }
                this.btnBuildActive = true;
            }
            if (sel().isOwnedByPlayer() && !(sel() instanceof Land)){
                setIconImage("button_destroy", BTN_DESTROY_ON);
                this.btnDestroyActive = true;
            } else {
                setIconImage("button_destroy", BTN_DESTROY_OFF);
                this.btnDestroyActive = false;
            }
        }
        setMarkerState();
    }
    
    
    private void displayObjectInfo(GameObject go){
        if (go == null){
            clearObjectInfo();
            return;
        }
        
        //properties
        setLabelText("info_1", go.getName().replaceAll("[0-9.]", ""));
        setLabelText("info_2", (go instanceof House ? ((House)go).getLuxury() + "" : ""));
        setLabelText("info_3", go.getNeighborhoodValue()+ "");
        setLabelText("info_4", (go instanceof House || go instanceof Land ? Format.money(go.getValue()) + "$" : ""));
        setLabelText("info_5", (go instanceof House ? Format.money(((House)go).getRent()) + " $/m." : ""));
        setIconImage("panel_hud_info_image", go.getImagePath());
        
        //categories
        setLabelText("cat_1", "Type:");
        setLabelText("cat_2", (go instanceof House ? "Luxury:" : ""));
        setLabelText("cat_3", "Neighbourh.:");
        setLabelText("cat_4", (go instanceof House || go instanceof Land ? "Value:" : ""));
        setLabelText("cat_5", (go instanceof House ? "Rent:" : ""));
    }
    
    
    private void displayObjectInfoEdit(House go){
        //properties
        setLabelText(popup("edit").findElementByName("popup_edit_info_1"), go.getName().replaceAll("[0-9.]", ""));
        setLabelText(popup("edit").findElementByName("popup_edit_info_2"), go.getLuxury() + "");
        setLabelText(popup("edit").findElementByName("popup_edit_info_3"), go.getNeighborhoodValue()+ "");
        setLabelText(popup("edit").findElementByName("popup_edit_info_4"), Format.money(go.getValue()) + " $");
        setLabelText(popup("edit").findElementByName("popup_edit_info_5"), Format.money(go.getRent()) + " $/m");
        setIconImage(popup("edit").findElementByName("popup_edit_info_image"), go.getImagePath());
        
        //categories
        setLabelText(popup("edit").findElementByName("popup_edit_cat_1"), "Type:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_2"), "Luxury:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_3"), "Neighbourh.:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_4"), "Value:");
        setLabelText(popup("edit").findElementByName("popup_edit_cat_5"), "Rent:");
    }
    
    
    private void clearObjectInfo(){
        //properties
        setLabelText("info_1", "");
        setLabelText("info_2", "");
        setLabelText("info_3", "");
        setLabelText("info_4", "");
        setLabelText("info_5", "");
        
        //categories
        setLabelText("cat_1", "");
        setLabelText("cat_2", "");
        setLabelText("cat_3", "");
        setLabelText("cat_4", "");
        setLabelText("cat_5", "");
    }
    
    
    private void setMarkerState(){
        if (sel() == null) return;
        if (sel().isOwnedByPlayer()){
            ((Geometry)marker).getMaterial().setColor("Diffuse", ColorRGBA.Green);
            ((Geometry)marker).getMaterial().setColor("Ambient", ColorRGBA.Green);
        } else {
            ((Geometry)marker).getMaterial().setColor("Diffuse", ColorRGBA.Red);
            ((Geometry)marker).getMaterial().setColor("Ambient", ColorRGBA.Red);
        }
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
        if (sel() != null){
            current = (Geometry) ((Node)((Node)sel().getSpatial()).getChild(0)).getChild(0);
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
            district.setSelected(district.getGameObject(geom.getLocalTranslation()));
        } else {
            setMarker(null);
        }
    }
    
    
    private void setMarker(Vector3f target){
        if (target != null){
            app.getRootNode().attachChild(marker);
            marker.setLocalTranslation(target.x, 2, target.z);
        } else {
            app.getRootNode().detachChild(marker);
        }
    }
    
    
    public void toggleObjectMarkers(){
        district.toogleObjectMarkers();
    }
    
    
    public void displayGameTime(long months){
        setLabelText("game_stat_time",
                "Month: " + months);
    }

    
    public void displayPlayerData(String playerName, long playerMoney, String playerIconPath){
        setLabelText("player_name", playerName);
        setLabelText("player_money", Format.money(playerMoney) + " $");
        setIconImage("panel_hud_info_avatar", playerIconPath);
    }
    
    
    public String getSelectedRent(){
        return ((House)sel()).getRent() + "";
    }
    
    
    public void refreshPlayerMoneyDisplay(){
        setLabelText("player_money", Format.money(player.getMoney()) + "$ (+"
                + Format.money(district.getTotalRent()) + "$/m.)");
    }
    
    
    public String getCurrentScreenId(){
        return nifty.getCurrentScreen().getScreenId();
    }
    
    
    public void displayGentrificationState(){
        int objectCount = 0;
        int luxuryCount = 0;
        
        for (GameObject go : district.getObjectList()){
            objectCount++;
            if (go instanceof House){
                luxuryCount += ((House)go).getLuxury();
            }
        }
        
        setLabelText("game_stat_district_tot_luxury",
                "Avg. Luxury: " + (luxuryCount/objectCount));
        
        float g = (float)((float)(luxuryCount/objectCount) / 120) * 100;
        
        setLabelText("game_stat_gentrified",
                "Gentrified: " + Format.twoDecimals(g) + "%");
        
        if (g >= 100) win();
    }
    
    
    private GameObject sel(){
        return district.getSelected();
    }
    
    
    public void win(){
        //TODO
    } 
    
    
    public void lose(){
        //TODO
    } 
    
    
//    public void hightlightNeighborhoodRadius(){
//        Set<GameObject> nh = district.getNeighborhood(sel(), true);
//        
//        for (GameObject go : nh){
//            if (go instanceof House)
//                ((Geometry)((Node)((Node)go.getSpatial()).getChild(0)).getChild(0)).getMaterial().setColor("Diffuse", ColorRGBA.Yellow);
//        }
//    }
}
