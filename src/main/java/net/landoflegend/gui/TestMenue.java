/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.gui;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericScrollable;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;
//import org.spoutcraft.spoutcraftapi.gui.GenericTexture;
//import org.spoutcraft.spoutcraftapi.gui.WidgetAnchor;

public class TestMenue {
        
    public static void open(Plugin plugin, SpoutPlayer splayer) { // method used to open the GUI for a player
        GenericTexture face = new GenericTexture();
        
        int width = splayer.getCurrentScreen().getWidth();
        int height = splayer.getCurrentScreen().getHeight();
        face.setUrl("http://landoflegend.net/forums/images/CoderGreen/site_logo.png"); // this must be a png image
        face.setWidth(splayer.getCurrentScreen().getWidth()/8).setHeight(splayer.getCurrentScreen().getHeight()/8); // you can manipulate the size of most widgets
        //face.setAnchor(WidgetAnchor.CENTER_CENTER); // the enum type WidgetAnchor provides a convenience method for placing widgets
        face.setX(width/2 - face.getWidth()/2);
        face.setY(height - face.getHeight() -5);
        
//        GenericButton button = new GenericButton("The Drogans?!");
//        button.setWidth(80);
//        button.setHeight(20);
//        button.setX(width/4);
//        button.setY(height/2);
//        button.setAnchor(WidgetAnchor.CENTER_LEFT);
        
        GenericListWidget scrollTry = new GenericListWidget();
        ListWidgetItem item = new ListWidgetItem("Drogans","Drogans");
        scrollTry.addItem(item);
        
        scrollTry.setX(width/4);
        scrollTry.setY(height/2);
        
        GenericLabel label = new GenericLabel("Clans - First try");
        //label.setAnchor(WidgetAnchor.CENTER_RIGHT);
        label.setWidth(80);
        label.setHeight(40);
        label.setScale(1);
        label.setX(width/2 - label.getWidth()/2);
        label.setY(20);
        
        GenericPopup popup = new GenericPopup(); // create the popup (A popup will "free the mouse")
        popup.attachWidget(plugin, face);// attach any widgets we want to the popup
        popup.attachWidget(plugin, scrollTry);
        popup.attachWidget(plugin, label);
        
//        button.set
        
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popup); // attach the popup to the players screen
    }
    
}
