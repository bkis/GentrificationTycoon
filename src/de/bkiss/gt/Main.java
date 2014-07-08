package de.bkiss.gt;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.bkiss.gt.states.MainState;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * Gentrification Tycoon
 */
public class Main extends SimpleApplication {
    

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Gentrification Tycoon");
        settings.setVSync(true);
        settings.setFrameRate(60);
        //settings.setFullscreen(true);
        settings.setFullscreen(false);
        GraphicsDevice gDevice = GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getDefaultScreenDevice();
        //settings.setResolution(
        //            gDevice.getDisplayMode().getWidth(),
        //            gDevice.getDisplayMode().getHeight());
        
        try {
            settings.setIcons(new BufferedImage[]{
                ImageIO.read(new File("icons/16.png")),
                ImageIO.read(new File("icons/32.png")),
                ImageIO.read(new File("icons/64.png")),
                ImageIO.read(new File("icons/128.png"))
            });
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Icon files missing",e);
        }
        
        settings.setResolution(800,600);
        settings.setSettingsDialogImage("Interface/splash.jpg");
        Main app = new Main();
        app.setSettings(settings);
        app.setShowSettings(true);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);
        setDisplayFps(false);
        //attatch initial state
        stateManager.attach(new MainState());
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    
}
