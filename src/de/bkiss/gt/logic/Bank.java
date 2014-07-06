package de.bkiss.gt.logic;

/**
 *
 * @author boss
 */
public class Bank {
    
    public static final int MIN_TRANS = 100000;
    
    private static final float INTEREST_POS = 1.01f;
    private static final float INTEREST_NEG = 1.05f;
    private static final int MAX_DEBTS = 5000000;
    
    private long account;
    
    
    
    public Bank(){
        account = 0;
    }
    
    
    public void put(long amount){
        account += amount;
    }
    
    
    public long take(long amount){
        account -= amount;
        return amount;
    }
    
    
    public boolean canTake(long amount){
        if (account - amount > MAX_DEBTS)
            return true;
        else
            return false;
    }
    
    
    public void applyInterest(){
        if (account > 0){
            account *= INTEREST_POS;
        } else {
            account *= INTEREST_NEG;
        }
    }
    
    
    public long getBalance(){
        return account;
    }
    
    
}
