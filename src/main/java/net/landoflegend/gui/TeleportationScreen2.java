/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.gui;

import net.landoflegend.legendaryclans.BlackLocation;
import net.landoflegend.legendaryclans.LegendaryClans;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TeleportationScreen2 {
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private SpoutPlayer splayerLeg;
    private GenericListWidget locList;

    public TeleportationScreen2(LegendaryClans legPlugin, SpoutPlayer splayer) {
        this.legPlugin = legPlugin;
        splayerLeg = splayer;
        open(legPlugin, splayer);
    }
    public void open(Plugin plugin, SpoutPlayer splayer) {
        splayer.closeActiveWindow();
        popupp = new GenericPopup();

        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();

        // name label
        GenericLabel topLabel = new GenericLabel("Teleportation");
        topLabel.setWidth(topLabel.getText().length() * 6 * (int) topLabel.getScale()).setHeight(15);
        topLabel.setX(width / 2 - topLabel.getWidth() / 2).setY(5 + topLabel.getHeight() / 2);
        popupp.attachWidget(plugin, topLabel);
        
        locList = new GenericListWidget();
        
        for(BlackLocation loc: legPlugin.getCfg().getTeleportLocs()){
            ListWidgetItem item = new ListWidgetItem(loc.getName(), LegendaryClans.parseColor("&9"+loc.getWorld().getName()));
            locList.addItem(item);
        }
        
        locList.setWidth(width/4);
        locList.setHeight(height - 20);
        
        locList.setX(width/4 - locList.getWidth()/2);
        locList.setY(height/2 - locList.getHeight()/2);
        popupp.attachWidget(plugin, locList);
        
        GenericButton telButton = new GenericButton("Teleport!"){

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                ListWidgetItem sel = locList.getSelectedItem();
                if(sel != null){
                    BlackLocation loc = legPlugin.getCfg().getLocByName(sel.getTitle());
                    if(loc != null){
                        popupp.close();
                        splayerLeg.getPlayer().teleport(loc);
                        LegendaryClans.coloredOutput((CommandSender)splayerLeg.getPlayer(), "&2Successfully warped to " + loc.getName()+".");
                    }
                }
                
            }
        };
        telButton.setWidth(telButton.getText().length() * 8 * (int) telButton.getScale()).setHeight(15);
        telButton.setX(width / 2 + width/4 - telButton.getWidth() / 2);
        telButton.setY(height/2  - telButton.getHeight() / 2);
        popupp.attachWidget(plugin, telButton);
        
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popupp);
    }
}
