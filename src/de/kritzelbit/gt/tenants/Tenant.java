package de.kritzelbit.gt.tenants;

import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import de.kritzelbit.gt.objects.Expansion;
import de.kritzelbit.gt.objects.House;
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
    private int publicConditionCount;
    private int minStudentsRatio;
    private int minAverageBudget;
    
    
    public Tenant(String name, int budget, String profession, int minLuxury, String imgPath) {
        this.name = name;
        this.budget = budget;
        this.profession = profession;
        this.minLuxury = minLuxury;
        this.imgPath = imgPath;
        this.needs = new HashSet<Expansion>();
        this.publicCondition = "nothing";
        this.publicConditionCount = 0;
        
        this.minStudentsRatio = 0;
        this.minAverageBudget = 0;
    }
    
    public void addNeed(Expansion need){
        for (Expansion n : needs)
            if (n.getName().equals(need.getName()))
                return;
        needs.add(need);
    }
    
    
    public void setPublicCondition(String type, int count){
        publicCondition = type;
        publicConditionCount = count;
    }
    
    
    public String getPublicCondition(){
        return publicCondition;
    }
    
    
    public int getPublicConditionCount(){
        return publicConditionCount;
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

    public int getMinStudentsRatio() {
        return minStudentsRatio;
    }

    public void setMinStudentsRatio(int minStudentsRatio) {
        this.minStudentsRatio = minStudentsRatio;
    }

    public int getMinAverageBudget() {
        return minAverageBudget;
    }

    public void setMinAverageBudget(int minAverageBudget) {
        this.minAverageBudget = minAverageBudget;
    }
    
    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    
    
    public boolean checkMatchLuxury(House house){
        if (house.getLuxury() >= minLuxury)
            return true;
        else
            return false;
    }
    
    
    public boolean checkMatchBudget(House house){
        if (house.getRent() <= budget)
            return true;
        else
            return false;
    }
    
    
    public void setOnlyNeed(Expansion need){
        needs.clear();
        needs.add(need);
    }
    
    
    public void setNeedsFor(House h){
        setBudget(h.getRent() + 11);
        setMinAverageBudget(0);
        publicConditionCount = 0;
        setMinLuxury(h.getLuxury() - 1);
        setMinStudentsRatio(0);
        setOnlyNeed((Expansion)h.getExpansions().toArray()[0]);
    }
    
    
    public boolean checkMatchNeeds(House house){
        for (Expansion e : needs)
                if (!house.hasExpansion(e.getName()))
                    return false;
        return true;
    }
    
    
    public boolean checkMatchDistrict(House house){
        if (house.getDistrict().getNrOfBuildings("public_"
                    + getPublicCondition()) < getPublicConditionCount())
                return false;
        
        return true;
    }
    
    
    public boolean checkMatchStudentRatio(House house){
        if (house.getDistrict().getStudentsRatio() < minStudentsRatio)
            return false;
        
        return true;
    }
    
    
    public boolean checkMatchAvgBudget(House house){
        if (house.getDistrict().getAverageBudget() < minAverageBudget)
            return false;
        
        return true;
    }
    
    
    public boolean acceptsHouse(House house){
        if (checkMatchBudget(house)
            && checkMatchLuxury(house)
            && checkMatchDistrict(house)
            && checkMatchNeeds(house)
            && checkMatchStudentRatio(house)
            && checkMatchAvgBudget(house))
            return true;
        else 
            return false;
    }
    
    
    @Override
    public String toString(){
        return "[TENANT] "
                + "Name:'" + name
                + "' Prof:'" + profession
                + "' MinLux:'" + minLuxury
                + "' Budget:'" + budget
                + "' Needs: " + needs
                + "' Public: " + publicCondition;
    }
    
}
