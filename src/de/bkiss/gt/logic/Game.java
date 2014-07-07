package de.bkiss.gt.logic;

import com.jme3.app.Application;
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
    
    private static final int MONTH_LENGTH_IN_MS = 5000;
    
    private Player player;
    private Bank bank;
    private District district;
    private GameTimer timer;
    private GUIController guiController;
    private TenantGenerator tenantGen;
    private List<Tenant> tenants;
    
    private long month;
    
    public Game(Application app,
            String playerName,
            String playerIconPath,
            District district,
            GUIController guiController,
            AssetManager assetManager,
            RandomContentGenerator gen,
            TenantGenerator tenGen){
        
        this.month = 1;
        this.bank = new Bank();
        player = new Player(playerName, playerIconPath);
        timer = new GameTimer(app, MONTH_LENGTH_IN_MS, this);
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
    
    public Bank getBank(){
        return bank;
    }
    
    public GameTimer getTimer(){
        return timer;
    }
    
    public District getDistrict(){
        return district;
    }
    
    public void nextMonth(){
        //time
        month++;
        guiController.displayGameTime(month);
        guiController.displayGentrificationState();
        
        //new tenants?
        refreshTenantList();
        
        //pay rent / moving out
        for (GameObject go : district.getObjectList()){
            if (go instanceof House && ((House)go).isOccupied() && ((House)go).isOwnedByPlayer()){
                if (((House)go).getRent() > ((House)go).getTenant().getBudget()){
                    ((House)go).removeTenant();
                } else {
                    player.addMoney(((House)go).getRent());
                }
            }
        }
        guiController.refreshPlayerMoneyDisplay();
        
        //BANK
        if (bank.getCurrentInterest(district.getOwnedRatio()) < 0
                && !bank.canTake(Math.abs(bank.getCurrentInterest(district.getOwnedRatio())))){
            long i = Math.abs(bank.getCurrentInterest(district.getOwnedRatio()));
            i -= bank.getBalance() + Bank.MAX_DEBTS;
            bank.setBalanceToMin();
            player.reduceMoney(i);
        } else {
            bank.applyInterest(district.getOwnedRatio());
        }
        
        /* EXECUTE
         * EVERY
         * MONTH !!!
         */
        
        
        //WIN? LOSE?
        if (player.getMoney() + bank.getBalance() <= -Bank.MAX_DEBTS) guiController.lose();
        if (2==3) guiController.win();
        
        guiController.refreshPlayerMoneyDisplay();
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
