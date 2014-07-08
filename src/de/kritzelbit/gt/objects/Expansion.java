package de.kritzelbit.gt.objects;

/**
 *
 * @author boss
 */
public class Expansion implements Comparable<Expansion>{
    
    private int price;
    private int effect; // luxury bonus
    private String name;
    
    
    public Expansion(int price, int effect, String name) {
        this.price = price;
        this.effect = effect;
        this.name = name;
    }

    
    public int getPrice() {
        return price;
    }

    public int getEffect() {
        return effect;
    }

    
    public String getName() {
        return name;
    }

    
    @Override
    public int compareTo(Expansion t) {
        return t.getName().compareTo(getName());
    }
    
    
    @Override
    public String toString(){
        return name + "(" + price + "," + effect + ")";
    }
    
}
