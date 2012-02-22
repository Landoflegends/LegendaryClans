/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.gui;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

public class FoundAClan {
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private SpoutPlayer splayerLeg;

    public FoundAClan(LegendaryClans legPlugin, SpoutPlayer splayer) {
        this.legPlugin = legPlugin;
        splayerLeg = splayer;
        open(legPlugin, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) {
        popupp = new GenericPopup();
        LegendaryPlayer posLeader = legPlugin.getPlayerManager().searchPlayerByName(splayer.getName());
    
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();
        
        // clan Name  -done
        GenericLabel clanNameLabel = new GenericLabel("Clan-Foundation");
        clanNameLabel.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        clanNameLabel.setX(width / 2 - clanNameLabel.getWidth() / 2).setY(5 + clanNameLabel.getHeight() / 2);
        popupp.attachWidget(plugin, clanNameLabel);
        
        // Textfield
        GenericTextField textField = new GenericTextField();
        textField.setWidth(100);
        textField.setHeight(15);

        textField.setX(width / 2 - textField.getWidth() / 2);
        textField.setY(height / 2 - textField.getHeight() / 2);
        popupp.attachWidget(plugin, textField);
        
        //show info button for clan members
        GenericButton foundClanButton = new GenericButton("Found the clan!") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                Widget widgets[] = popupp.getAttachedWidgets();
                String clanName = "";
                for (Widget widget : widgets) {
                    if (widget instanceof GenericTextField) {
                        clanName = ((GenericTextField) widget).getText();
                    }
                }
                if(clanName.length()>3 && clanName.length()<=10){
                    if(!legPlugin.getClanManager().isNameAlreadyInUse(clanName)){
                        LegendaryPlayer posLeader = legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName());
                        legPlugin.getClanManager().foundClan(posLeader, clanName);
                        popupp.close();
                    }
                    else{
                        LegendaryClans.coloredOutput((CommandSender)splayerLeg.getPlayer(), "&4That clan name is already in use, choose a different one.");
                    }
                }
                else{
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&4Clan name too short or too long. Max length is 10 characters");
                }
                
                
//                if (memberList.getSelectedItem() != null) {
//                    LegendaryPlayer legP = legPlugin.getPlayerManager().searchPlayerByName(memberList.getSelectedItem().getTitle());
//                    if (legP != null) {
//                        popupp.close();
//                        PlayerInfoScreen screene = new PlayerInfoScreen(legPlugin, splayerLeg, popupp, legP);
//                    }
//                }
            }
        };

        foundClanButton.setWidth(foundClanButton.getText().length() * 8 * (int) foundClanButton.getScale()).setHeight(15);
        foundClanButton.setX(width / 2 - foundClanButton.getWidth() / 2);
        foundClanButton.setY(25 + textField.getY() - foundClanButton.getHeight() / 2);
        popupp.attachWidget(plugin, foundClanButton);
   
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popupp);
    }
}
