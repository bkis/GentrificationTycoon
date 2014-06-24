
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

    
    public TenantGenerator(AssetManager assetManager, RandomContentGenerator gen){
        this.gen = gen;
    }
    
    
    public Tenant generateTenant(int socialClass){
        String name = gen.getName(Math.random() < 0.5f);
        String prof = gen.getProfession(socialClass);
        int budget = getRndBudget(socialClass);
        int minLux = getRndMinLux(budget);
        
        if (socialClass == 0 && !prof.equalsIgnoreCase("student")) budget *= 0.7f;
        
        Tenant t = new Tenant(name, budget, prof, minLux, "imgPath");
        for (int i = 0; i < (socialClass+2)/2; i++)
            if (Math.random() > 0.1f)
                t.addNeed(gen.getRndExpansionFor(socialClass));
        
        int i = (int)((Math.random() * 10) / 3) + 1;
        switch(socialClass){
            case 0: if (prof.equalsIgnoreCase("student")) t.setPublicCondition("club",i); break;
            case 1: t.setPublicCondition("gallery",i); break;
            case 2: t.setPublicCondition("school",i); break;
            default: break;
        }
        
        return t;
    }
    
    
    private int getRndBudget(int socialClass){
        return (int)(
                ((800 + (400*((Math.random() - 0.5f)/2)))
                * (socialClass*2+1))
                + (socialClass*(Math.random()*(400*socialClass)))
                );
    }
    
    
    private int getRndMinLux(int budget){
        return (int)(150 * ((float)(budget-100)/4000));
    }
    
    
    
}
