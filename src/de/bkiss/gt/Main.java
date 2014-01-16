package de.bkiss.gt;

import de.bkiss.gt.states.IngameState;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
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
        
        //text
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Gentrification Tycoon (Test Scence)");
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);
        
        //cam
        //flyCam.setZoomSpeed(20);
        
        //load GameState
        IngameState state = new IngameState();
        stateManager.attach(state);
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
