
package de.kritzelbit.gt.tenants;

import de.kritzelbit.gt.utils.RandomContentGenerator;
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
        int minLux = getRndMinLux(socialClass);
        
        if (socialClass == 0 && !prof.equalsIgnoreCase("student")) budget *= 0.7f;
        
        Tenant t = new Tenant(name, budget, prof, minLux, "imgPath");
        for (int i = 0; i < (socialClass+2)/2; i++)
            if (Math.random() > 0.1f)
                t.addNeed(gen.getRndExpansionFor(socialClass));
        
        int i = gen.rndNumber(1, 3);        // public condition count
        int s = gen.rndNumber(39, 68);      // min student ratio
        int b = gen.rndNumber(2413, 3611);  // min avg budget
        switch(socialClass){
            case 0: if (prof.equalsIgnoreCase("student")) t.setPublicCondition("club",i); break;
            case 1: t.setPublicCondition("school",i); t.setMinStudentsRatio(s); break;
            case 2: t.setPublicCondition("gallery",i); t.setMinAverageBudget(b); break;
            default: break;
        }
        
        t.setMinLuxury(t.getMinLuxury()/i);
        t.setMinAverageBudget(t.getMinAverageBudget()/i);
        t.setMinStudentsRatio(t.getMinStudentsRatio()/i);
        
        return t;
    }
    
    
    private int getRndBudget(int socialClass){
        return (int)(
                ((800 + (400*((Math.random() - 0.5f)/2)))
                * (socialClass*3+1))
                + (socialClass*(Math.random()*(600*socialClass)))
                );
    }
    
    
    private int getRndMinLux(int socialClass){
        return gen.rndNumber((socialClass*50)+40, (socialClass*50)+60);
    }
    
    
    
}
