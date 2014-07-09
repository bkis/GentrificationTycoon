package de.kritzelbit.gt.logic;

/**
 *
 * @author boss
 */
public class Bank {
    
    private static final float INTEREST_POS = 0.01f;
    private static final float INTEREST_NEG = 0.01f;
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
        account = -MAX_DEBTS;
    }
    
    
    public boolean canTake(long amount){
        if (account - amount > -MAX_DEBTS)
            return true;
        else
            return false;
    }
    
    
    public void applyInterest(float ownedRatio){
        account += getCurrentInterestMoney(ownedRatio);
    }
    
    
    public long getCurrentInterestMoney(float ownedRatio){
        return (long)(account*getCurrentInterest(ownedRatio));
    }
    
    
    public float getCurrentInterest(float ownedRatio){
        float i;
        if (account >= 0){
            i = (INTEREST_POS * ((0.04f + ownedRatio) * 1.8f));
        } else {
            i = INTEREST_NEG;
        }
        return i;
    }
    
    
    public long getBalance(){
        return account;
    }
    
    
}
