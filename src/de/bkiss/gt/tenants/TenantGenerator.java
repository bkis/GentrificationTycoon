
package de.bkiss.gt.tenants;

import de.bkiss.gt.utils.RandomContentGenerator;
import com.jme3.asset.AssetManager;

/**
 *
 * @author boss
 */
public class TenantGenerator {
    
    public static final int PROFESSION_LOWER = 0;
    public static final int PROFESSION_MIDDLE = 1;
    public static final int PROFESSION_UPPER = 2;
    
    private RandomContentGenerator gen;

    
    public TenantGenerator(AssetManager assetManager){
        this.gen = new RandomContentGenerator(assetManager);
    }
    
    
    public Tenant generateTenant(boolean male, int socialClass){
        Tenant tenant = new Tenant(
                    gen.getName(male),
                    getRndBudget(socialClass),
                    gen.getProfession(socialClass),
                    getRndMinLux(socialClass),
                    "imgPath");
        return tenant;
    }
    
    
    private int getRndBudget(int socialClass){
        return (int)(
                (500 + (800*(Math.random() - 0.5f)))
                * (socialClass+1)
                );
    }
    
    
    private int getRndMinLux(int socialClass){
        return (int)(
                (socialClass * 30)
                + ((socialClass+1)*3)
                + ((Math.random() - 0.5f) * 10)
                );
    }
    
    
    
}
