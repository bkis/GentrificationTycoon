package de.bkiss.gt.logic;

import com.jme3.asset.AssetManager;
import de.bkiss.gt.gui.GUIController;
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
            RandomContentGenerator gen){
        
        this.day = 0;
        player = new Player(playerName, playerIconPath);
        timer = new GameTimer();
        this.district = district;
        this.guiController = guiController;
        this.tenants = new ArrayList<Tenant>();
        
        //launch tenant generator
        this.tenantGen = new TenantGenerator(assetManager, gen);
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
        day++;
        guiController.displayGameTime(day);
        guiController.displayGentrificationState();
        
        if (day % 30 == 0) refreshTenantList();
        
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
