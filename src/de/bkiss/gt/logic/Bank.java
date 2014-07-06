package de.bkiss.gt.logic;

/**
 *
 * @author boss
 */
public class Bank {
    
    public static final int MIN_TRANS = 100000;
    
    private static final float INTEREST_POS = 0.02f;
    private static final float INTEREST_NEG = 0.05f;
    public static final int MAX_DEBTS = 5000000;
    
    private long account;
    
    
    
    public Bank(){
        account = 0;
    }
    
    
    public long put(long amount){
        account += amount;
        return amount;
    }
    
    
    public long take(long amount){
        account -= amount;
        return amount;
    }
    
    
    public void setBalanceToMin(){
        account = -5000000;
    }
    
    
    public boolean canTake(long amount){
        if (account - amount > -MAX_DEBTS)
            return true;
        else
            return false;
    }
    
    
    public void applyInterest(float ownedRatio){
        account += getCurrentInterest(ownedRatio);
    }
    
    
    public long getCurrentInterest(float ownedRatio){
        float i;
        if (account > 0){
            i = (INTEREST_POS * (0.8f + (ownedRatio * 2)));
        } else {
            i = INTEREST_NEG;
        }
        return (long)(account*i);
    }
    
    
    public long getBalance(){
        return account;
    }
    
    
}
