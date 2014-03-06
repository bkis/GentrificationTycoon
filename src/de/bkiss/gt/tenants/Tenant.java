package de.bkiss.gt.tenants;

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

    
    
    public Tenant(String name, int budget, String profession, int minLuxury, String imgPath) {
        this.name = name;
        this.budget = budget;
        this.profession = profession;
        this.minLuxury = minLuxury;
        this.imgPath = imgPath;
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
        return "TENANT - Name :" + name + " - Prof.: " + profession;
    }
    
}
