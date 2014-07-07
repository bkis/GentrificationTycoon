package de.bkiss.gt.utils;

import com.jme3.asset.AssetManager;
import de.bkiss.gt.objects.Expansion;
import java.util.ArrayList;
import java.util.List;
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
    private static final String EXPANSIONS = "Text/expansions.txt";
    
    private Random rnd;
    private AssetManager assetManager;
    
    private String[] namesFemale;
    private String[] namesMale;
    private String[] namesLast;
    private String[] profLower;
    private String[] profMiddle;
    private String[] profUpper;
    private String[] expansions;
    
    
    
    
    public RandomContentGenerator(AssetManager assetManager){
        this.rnd = new Random();
        this.assetManager = assetManager;
        loadItems();
    }
    
    
    public String getName(boolean male){
        return getRndName(true, male) + " " + getRndName(false, false);
    }
    
    
    public String getName(boolean male, String lastName){
        return getRndName(true, male) + " " + lastName;
    }
    
    
    public String getProfession(int socialClass){
        switch (socialClass){
            case PROFESSION_LOWER: return profLower[rnd.nextInt(profLower.length)];
            case PROFESSION_MIDDLE: return profMiddle[rnd.nextInt(profMiddle.length)];
            case PROFESSION_UPPER: return profUpper[rnd.nextInt(profUpper.length)];
            default: return profLower[rnd.nextInt(profLower.length)];
        }
    }
    
    
    private String getRndName(boolean first, boolean male){
        if (first)
            if (male)
                return namesMale[rnd.nextInt(namesMale.length)];
            else
                return namesFemale[rnd.nextInt(namesFemale.length)];
        else
            return namesLast[rnd.nextInt(namesLast.length)];
    }
    
    
    public Expansion getRndExpansionFor(int socialClass){
        int i = expansions.length/3;
        i *= (socialClass+1);
        if (socialClass == PROFESSION_UPPER) i = expansions.length;
        
        int min = i - (expansions.length/3);
        String[] e = expansions[min+(int)(Math.random()*(i-min))].split("#");
        return new Expansion(
                Integer.parseInt(e[1]),
                Integer.parseInt(e[2]),
                e[0]);
    }
    
    
    public List<Expansion> getAllExpansions(){
        List<Expansion> list = new ArrayList<Expansion>();
        
        for (String s : expansions){
            String[] e = s.split("#");
            list.add(0, new Expansion(
                Integer.parseInt(e[1]),
                Integer.parseInt(e[2]),
                e[0]));
        }
        
        return list;
    }
    
    
    private String[] readContent(String path){
        return assetManager.loadAsset(path).toString().split("\n");
    }
    
    
    private void loadItems(){
        this.namesFemale = readContent(FIRST_F);
        this.namesMale = readContent(FIRST_M);
        this.namesLast = readContent(LAST);
        this.profLower = readContent(PROF_LOW);
        this.profMiddle = readContent(PROF_MID);
        this.profUpper = readContent(PROF_UP);
        this.expansions = readContent(EXPANSIONS);
    }
    
    
    public int rndNumber(int min, int max){
        return rnd.nextInt(max-min) + min;
    }
    
}
