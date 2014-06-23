package de.bkiss.gt.tenants;

import de.bkiss.gt.objects.Expansion;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author boss
 */
public class Tenant {
    
    private String name;
    private int budget;
    private String profession;
    private int minLuxury;
    private String imgPath;
    private Set<Expansion> needs;
    private String publicCondition;
    
    
    public Tenant(String name, int budget, String profession, int minLuxury, String imgPath) {
        this.name = name;
        this.budget = budget;
        this.profession = profession;
        this.minLuxury = minLuxury;
        this.imgPath = imgPath;
        this.needs = new HashSet<Expansion>();
    }
    
    public void addNeed(Expansion need){
        for (Expansion n : needs)
            if (n.getName().equals(need.getName()))
                return;
        needs.add(need);
    }
    
    
    public void setPublicCondition(String type){
        publicCondition = type;
    }
    
    
    public String getPublicCondition(){
        return publicCondition;
    }
    
    
    public Set<Expansion> getNeeds(){
        return needs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getMinLuxury() {
        return minLuxury;
    }

    public void setMinLuxury(int minLuxury) {
        this.minLuxury = minLuxury;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    
    
    @Override
    public String toString(){
        return "[TENANT] "
                + "Name:'" + name
                + "' Prof:'" + profession
                + "' MinLux:'" + minLuxury
                + "' Budget:'" + budget
                + "' Needs: " + needs;
    }
    
}
