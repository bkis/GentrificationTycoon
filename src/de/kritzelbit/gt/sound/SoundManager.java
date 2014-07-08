package de.kritzelbit.gt.sound;


public class SoundManager {
    
    private boolean muted;
    
    
    public SoundManager(){
        muted = false;
    }
    
    
    public void mute(boolean mute){
        muted = mute;
    }
    
    public boolean isMuted(){
        return muted;
    }
    
}
