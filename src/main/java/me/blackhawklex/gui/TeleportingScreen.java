/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.gui;

//import java.awt.Color;
import me.blackhawklex.legendaryclans.LegendaryClans;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Orientation;
import org.getspout.spoutapi.player.SpoutPlayer;
import sun.awt.OrientableFlowLayout;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class TeleportingScreen {
    
    private LegendaryClans legPlugin;
    private GenericPopup popup;
    private SpoutPlayer splayerLeg;

    public TeleportingScreen(LegendaryClans legPlugin, SpoutPlayer splayer) {
        this.legPlugin = legPlugin;
        splayerLeg = splayer;
        open(legPlugin, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) { // method used to open the GUI for a player
//        int width = splayer.getMainScreen().getWidth();
//        int height = splayer.getMainScreen().getHeight();
//        
//        GenericTexture overAllBar = new GenericTexture();
//            
//        overAllBar.setWidth(width/2);
//        overAllBar.setHeight(15);
//        overAllBar.setX(width/2 -overAllBar.getWidth()/2).setY(height/2-overAllBar.getHeight()/2);
//        overAllBar.setBottomColor(new Color(0,0,0));
//        overAllBar.setTopColor(new Color(51,153,51));
//
//        popup = new GenericPopup();
    }
    
}
