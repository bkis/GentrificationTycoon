package de.kritzelbit.gt.sound;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class SoundManager {
    
    private static final String SOUND_PATH = "Sounds/";
    
    public static final String ALERT = SOUND_PATH + "alert.ogg";
    public static final String BANK = SOUND_PATH + "bank.ogg";
    public static final String BUILD = SOUND_PATH + "build.ogg";
    public static final String BUY = SOUND_PATH + "buy.ogg";
    public static final String CLUB = SOUND_PATH + "club.ogg";
    public static final String DESTROY = SOUND_PATH + "destroy.ogg";
    public static final String GALLERY = SOUND_PATH + "gallery.ogg";
    public static final String LOSE = SOUND_PATH + "lose.ogg";
    public static final String MONTH = SOUND_PATH + "month.ogg";
    public static final String MOVEOUT = SOUND_PATH + "moveout.ogg";
    public static final String POPUP = SOUND_PATH + "popup.ogg";
    public static final String SCHOOL = SOUND_PATH + "school.ogg";
    public static final String WIN = SOUND_PATH + "win.ogg";
    
    public static final String MUSIC_INGAME = SOUND_PATH + "music_ingame.ogg";
    public static final String MUSIC_MENU = SOUND_PATH + "music_menu.ogg";
    
    
    private boolean muted;
    private Map<String,AudioNode> sounds;
    private AudioNode musicMenu;
    private AudioNode musicIngame;
    private AssetManager assetManager;
    
    
    public SoundManager(AssetManager assetManager){
        this.assetManager = assetManager;
        muted = false;
        sounds = new HashMap<String,AudioNode>();
        loadSounds();
    }
    
    
    public void mute(boolean mute){
        muted = mute;
        
        if (muted){
            stopAll();
            stopMusic();
        } else {
            playIngameMusic();
        }
    }
    
    public boolean isMuted(){
        return muted;
    }
    
    
    public void play(String soundKey){
        if (muted) return;
        sounds.get(soundKey).playInstance();
    }
    
    
    private void loadSounds(){
        sounds.put(ALERT, new AudioNode(assetManager, ALERT));
        sounds.put(BUILD, new AudioNode(assetManager, BUILD));
        sounds.put(DESTROY, new AudioNode(assetManager, DESTROY));
        sounds.put(MONTH, new AudioNode(assetManager, MONTH));
        sounds.put(SCHOOL, new AudioNode(assetManager, SCHOOL));
        sounds.put(POPUP, new AudioNode(assetManager, POPUP));
        sounds.put(BANK, new AudioNode(assetManager, BANK));
        sounds.put(BUY, new AudioNode(assetManager, BUY));
        sounds.put(CLUB, new AudioNode(assetManager, CLUB));
        sounds.put(GALLERY, new AudioNode(assetManager, GALLERY));
        sounds.put(LOSE, new AudioNode(assetManager, LOSE));
        sounds.put(MOVEOUT, new AudioNode(assetManager, MOVEOUT));
        sounds.put(WIN, new AudioNode(assetManager, WIN));
        setupNodes();
        
        //music
        musicMenu =  new AudioNode(assetManager, MUSIC_MENU);
        musicMenu.setPositional(false);
        musicMenu.setDirectional(false);
        musicMenu.setLooping(true);
        musicMenu.setVolume(4.2f);
        musicIngame =  new AudioNode(assetManager, MUSIC_INGAME);
        musicIngame.setPositional(false);
        musicIngame.setDirectional(false);
        musicIngame.setLooping(true);
        musicIngame.setVolume(4.2f);
    }
    
    
    private void setupNodes(){
        for (Entry<String,AudioNode> e : sounds.entrySet()){
            e.getValue().setVolume(5.0f);
            e.getValue().setPositional(false);
            e.getValue().setDirectional(false);
        }
    }
    
    
    private void stopMusic(){
        musicMenu.stop();
        musicIngame.stop();
    }
    
    
    public void playMenuMusic(){
        stopMusic();
        musicMenu.play();
    }
    
    
    public void playIngameMusic(){
        stopMusic();
        musicIngame.play();
    }
    
    
    private void stopAll(){
        for (Entry<String,AudioNode> e : sounds.entrySet()){
            e.getValue().stop();
        }
    }
    
}
