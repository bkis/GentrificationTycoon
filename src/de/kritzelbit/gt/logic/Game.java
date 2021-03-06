package de.kritzelbit.gt.logic;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import de.kritzelbit.gt.gui.Alert;
import de.kritzelbit.gt.gui.GUIController;
import de.kritzelbit.gt.objects.GameObject;
import de.kritzelbit.gt.objects.House;
import de.kritzelbit.gt.tenants.Tenant;
import de.kritzelbit.gt.tenants.TenantGenerator;
import de.kritzelbit.gt.utils.Format;
import de.kritzelbit.gt.utils.RandomContentGenerator;
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
        
        //tutorial?
        if (month == 2) guiController.openPopup("tutorial");
        
        //new tenants?
        refreshTenantList();
        
        //move out?
        for (GameObject go : district.getObjectList()){
            if (go instanceof House && ((House)go).isOccupied() && ((House)go).isOwnedByPlayer()){
                if (!((House)go).getTenant().acceptsHouse((House)go)){
                    guiController.moveOut((House)go);
                }
            }
        }
        
        //pay rent / moving out
        for (GameObject go : district.getObjectList()){
            if (go instanceof House && ((House)go).isOccupied() && ((House)go).isOwnedByPlayer()){
                player.addMoney(((House)go).getRent());
            }
        }
        guiController.refreshPlayerMoneyDisplay();
        
        //BANK
        float owned = district.getOwnedRatio();
        if (bank.getCurrentInterestMoney(owned) < 0
                && !bank.canTake(Math.abs(bank.getCurrentInterestMoney(owned)))){
            long i = Math.abs(bank.getCurrentInterestMoney(owned));
            i -= bank.getBalance() + Bank.MAX_DEBTS;
            bank.setBalanceToMin();
            player.reduceMoney(i);
            guiController.bankAlert();
        } else {
            bank.applyInterest(owned);
        }
        
        /* EXECUTE
         * EVERY
         * MONTH !!!
         */
        
        
        //WIN? LOSE?
        if (player.getMoney() + bank.getBalance() <= -Bank.MAX_DEBTS) guiController.lose();
        if (2==3) guiController.win();
        
        guiController.refreshPlayerMoneyDisplay();
        guiController.nextMonth();
    }
    
    
    public void stopTimer(){
        timer.cancel();
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
    
    
    public String getMonths(){
        return month + "";
    }
    
}
