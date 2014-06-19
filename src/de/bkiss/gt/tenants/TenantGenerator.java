
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
        String name = gen.getName(male);
        String prof = gen.getProfession(socialClass);
        int budget = getRndBudget(socialClass);
        int minLux = getRndMinLux(budget);
        
        Tenant t = new Tenant(name, budget, prof, minLux, "imgPath");
        for (int i = 0; i < (socialClass+1)*2; i++)
            if (Math.random() > 0.3f)
                t.addNeed(gen.getRndExpansionFor(socialClass));
        
        return t;
    }
    
    
    private int getRndBudget(int socialClass){
        return (int)(
                ((400 + (400*((Math.random() - 0.5f)/2)))
                * (socialClass*2+1))
                + (socialClass*(Math.random()*(400*socialClass)))
                );
    }
    
    
    private int getRndMinLux(int budget){
        return (int)(150 * ((float)(budget-100)/4000));
    }
    
    
    
}
