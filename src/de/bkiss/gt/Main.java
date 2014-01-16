package de.bkiss.gt;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.bkiss.gt.states.MainMenuState;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


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
        settings.setResolution(800,600);
        settings.setSettingsDialogImage("Interface/splash.png");
        
        Main app = new Main();
        app.setSettings(settings);
        app.setShowSettings(true);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);
        
        //attatch initial state
        stateManager.attach(new MainMenuState());
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
