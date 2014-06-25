package de.bkiss.gt.logic;

import com.jme3.asset.AssetManager;
import de.bkiss.gt.gui.GUIController;
import de.bkiss.gt.objects.GameObject;
import de.bkiss.gt.objects.House;
import de.bkiss.gt.tenants.Tenant;
import de.bkiss.gt.tenants.TenantGenerator;
import de.bkiss.gt.utils.RandomContentGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author boss
 */
public class Game {
    
    private static final int DAY_LENGTH_IN_MS = 500;
    
    private Player player;
    private District district;
    private GameTimer timer;
    private GUIController guiController;
    private TenantGenerator tenantGen;
    private List<Tenant> tenants;
    
    private long day;
    
    public Game(String playerName,
            String playerIconPath,
            District district,
            GUIController guiController,
            AssetManager assetManager,
            RandomContentGenerator gen,
            TenantGenerator tenGen){
        
        this.day = 0;
        player = new Player(playerName, playerIconPath);
        timer = new GameTimer(DAY_LENGTH_IN_MS, this);
        this.district = district;
        this.guiController = guiController;
        this.tenants = new ArrayList<Tenant>();
        
        //launch tenant generator
        this.tenantGen = tenGen;
        refreshTenantList();
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public GameTimer getTimer(){
        return timer;
    }
    
    public District getDistrict(){
        return district;
    }
    
    public void nextDay(){
        //time
        day++;
        guiController.displayGameTime(day);
        guiController.displayGentrificationState();
        
        //new tenants?
        if (day % 30 == 0) refreshTenantList();
        
        //pay rent / moving out
        int c = 0;
        if (day % 30 == 0){
            for (GameObject go : district.getObjectList()){
                if (go instanceof House && ((House)go).isOccupied()){
                    if (((House)go).getRent() > ((House)go).getTenant().getBudget()){
                        ((House)go).removeTenant();
                        c++;
                    } else {
                        player.addMoney(((House)go).getRent());
                    }
                }
            }
            guiController.refreshPlayerMoneyDisplay();
            if (c != 0)
                guiController.showAlert("Boohoo...",
                        "This month, " + c + " tenants moved out",
                        "because they couldn't afford their rent.");
        }

        /* EXECUTE
         * EVERY
         * DAY !!!
         */
    }
    
    private void refreshTenantList(){
        tenants.clear();
        for (int i = 0; i < 10; i++) {
            tenants.add(tenantGen.generateTenant(new Random().nextInt(3)));
        }
        
//        //DEBUG
//        for (int i = 0; i < tenants.size(); i++) {
//            System.out.println(tenants.get(i));
//        }
    }
    
    
    public List<Tenant> getTenants(){
        return tenants;
    }
    
}
