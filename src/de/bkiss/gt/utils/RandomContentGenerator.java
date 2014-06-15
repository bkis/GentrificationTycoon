package de.bkiss.gt.utils;

import com.jme3.asset.AssetManager;
import java.util.Random;

/**
 *
 * @author boss
 */
public class RandomContentGenerator {
    
    public static final int PROFESSION_LOWER = 0;
    public static final int PROFESSION_MIDDLE = 1;
    public static final int PROFESSION_UPPER = 2;
    
    
    private static final String FIRST_M = "Text/names_first_m.txt";
    private static final String FIRST_F = "Text/names_first_f.txt";
    private static final String LAST = "Text/names_last.txt";
    private static final String PROF_LOW = "Text/prof_lower.txt";
    private static final String PROF_MID = "Text/prof_middle.txt";
    private static final String PROF_UP = "Text/prof_upper.txt";
    
    private Random rnd;
    private AssetManager assetManager;
    
    
    
    public RandomContentGenerator(AssetManager assetManager){
        this.rnd = new Random();
        this.assetManager = assetManager;
    }
    
    
    public String getName(boolean male){
        return getRndName(true, male) + " " + getRndName(false, false);
    }
    
    
    public String getName(boolean male, String lastName){
        return getRndName(true, male) + " " + lastName;
    }
    
    
    public String getProfession(int socialClass){
        String path;
        
        switch (socialClass){
            case PROFESSION_LOWER: path = PROF_LOW; break;
            case PROFESSION_MIDDLE: path = PROF_MID; break;
            case PROFESSION_UPPER: path = PROF_UP; break;
            default: path = PROF_LOW; break;
        }
        
        String[] list = assetManager.loadAsset(path).toString().split("\n");
        return list[rnd.nextInt(list.length)];
    }
    
    
    private String getRndName(boolean first, boolean male){
        String path = (first?(male?FIRST_M:FIRST_F):LAST);
        String raw = assetManager.loadAsset(path).toString();
        String[] list = raw.split("\n");
        return list[rnd.nextInt(list.length)];
    }
    
}
