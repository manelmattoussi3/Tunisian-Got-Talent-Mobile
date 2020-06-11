package com.mycompany.myapp;


import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Toolbar;
import java.io.IOException;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.io.NetworkEvent;
import com.codename1.maps.Coord;
import com.mycompany.gui.AjoutEvenementForm;
import com.mycompany.gui.AjoutPublicationForm;
import com.mycompany.gui.MapUI;
import com.mycompany.gui.MapUIAdmin;
import com.mycompany.gui.NewsfeedForm;
import com.mycompany.gui.PublicationForm;
import com.mycompany.gui.SplashScreenForm;
import com.mycompany.gui.StatistiqueForm;
import com.mycompany.gui.WalkthruForm;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class MyApplication {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme_1");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });        
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        //new FormAutoCompleteGoogle().show();
        try{
      new SplashScreenForm(theme).show();
        }catch(Exception ex) {
            
        }
      /* MapUI le = new MapUI();
        Coord src = new Coord(36.8065, 10.1815);
        Coord dest = new Coord(35.8256, 10.6084);
        le.start(src,dest);  

*/
}
    public void stop() {
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
    }

}