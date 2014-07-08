package de.kritzelbit.gt.sound;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class SoundManager {
    
    private static final String SOUND_PATH = "Sounds/";
    
    public static final String BUILD = SOUND_PATH + "build.ogg";
    public static final String DESTROY = SOUND_PATH + "destroy.ogg";
    public static final String CASH = SOUND_PATH + "cash.ogg";
    public static final String ALERT = SOUND_PATH + "alert.ogg";
    public static final String SCHOOL = SOUND_PATH + "school.ogg";
    
    
    private boolean muted;
    private Map<String,AudioNode> sounds;
    private AssetManager assetManager;
    
    
    public SoundManager(AssetManager assetManager){
        this.assetManager = assetManager;
        muted = false;
        sounds = new HashMap<String,AudioNode>();
        loadSounds();
    }
    
    
    public void mute(boolean mute){
        muted = mute;
        
        if (muted) stopAll();
    }
    
    public boolean isMuted(){
        return muted;
    }
    
    
    public void play(String soundKey){
        if (muted) return;
        sounds.get(soundKey).playInstance();
    }
    
    
    private void loadSounds(){
        sounds.put(BUILD, new AudioNode(assetManager, BUILD));
        sounds.put(DESTROY, new AudioNode(assetManager, DESTROY));
        sounds.put(CASH, new AudioNode(assetManager, CASH));
        sounds.put(SCHOOL, new AudioNode(assetManager, SCHOOL));
        sounds.put(ALERT, new AudioNode(assetManager, ALERT));
        
        setupNodes();
    }
    
    
    private void setupNodes(){
        for (Entry<String,AudioNode> e : sounds.entrySet()){
            e.getValue().setVolume(0.4f);
            e.getValue().setPositional(false);
            e.getValue().setDirectional(false);
        }
    }
    
    
    private void stopAll(){
        for (Entry<String,AudioNode> e : sounds.entrySet()){
            e.getValue().stop();
        }
    }
    
}
