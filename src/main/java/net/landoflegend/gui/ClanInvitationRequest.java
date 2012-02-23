/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.gui;

import net.landoflegend.legendaryclans.LegendaryClans;
import net.landoflegend.legendaryclans.LegendaryPlayer;
import net.landoflegend.legendaryclans.clans.Clan;
import net.landoflegend.legendaryclans.party.Invitation;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClanInvitationRequest {
    private Clan clan;
    private LegendaryClans pluginLeg;
    private LegendaryPlayer askedPlayer;

    public ClanInvitationRequest(LegendaryClans plugin, SpoutPlayer splayer, Clan clan) {
        this.clan = clan;
        pluginLeg = plugin;
        askedPlayer = plugin.getPlayerManager().searchPlayerByName(splayer.getName());
        open(pluginLeg, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) { // method used to open the GUI for a player       

        GenericPopup popup = new GenericPopup();
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();
        pluginLeg.log("aight" + width + "" + height);

//        int width = splayer.getCurrentScreen().getWidth();
//        int height = splayer.getCurrentScreen().getHeight();

        //title of the screen
        GenericLabel topLabel = new GenericLabel("Clan-Invitation");
        topLabel.setScale(1);

        topLabel.setWidth(topLabel.getText().length() * 8 * (int) topLabel.getScale()).setHeight((int) topLabel.getScale() * 15);

        topLabel.setX(width / 2 - topLabel.getWidth() / 2);
        topLabel.setY(topLabel.getHeight() / 2 + 5);
        popup.attachWidget(plugin, topLabel);

        //title of the screen
        String questionString = "&2You are invited to "+clan.getName()+". Do you want to join?";
        GenericLabel questionLabel = new GenericLabel(LegendaryClans.parseColor(questionString));
        questionLabel.setScale(1);
        questionLabel.setWidth(questionLabel.getText().length() * 5 * (int) questionLabel.getScale());
        questionLabel.setHeight((int) questionLabel.getScale() * 15);

        questionLabel.setX(width / 2 - questionLabel.getWidth() / 2);
        questionLabel.setY(height / 2 - questionLabel.getHeight() / 2);
        popup.attachWidget(plugin, questionLabel);

        //Accept Button
        LegendaryButton acceptButton = new LegendaryButton(LegendaryClans.parseColor("&2Accept!"), popup) {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                if (pluginLeg.getClanManager().getClanByPlayer(clan.getLeaders().get(0)) != null && askedPlayer != null) {
                    clan.addMember(askedPlayer);
                } 
                getPopup().close();
            }
        };


        acceptButton.setWidth(acceptButton.getText().length() * 8 * (int) acceptButton.getScale()).setHeight((int) acceptButton.getScale() * 15);;
        acceptButton.setX(width / 2 + width / 4 - acceptButton.getWidth() / 2).setY(height / 2 + height / 4 - acceptButton.getHeight() / 2);


        popup.attachWidget(plugin, acceptButton);
        //deny button
        LegendaryButton denieButton = new LegendaryButton(LegendaryClans.parseColor("&4Deny!"), popup) {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                clan.broadcastMessage("&4" + askedPlayer.getName() + " denied invitation!");
                getPopup().close();
            }
        };

        denieButton.setWidth(acceptButton.getText().length() * 8 * (int) acceptButton.getScale());
        denieButton.setHeight((int) denieButton.getScale() * 15);
//         
//         ((LegendaryClans)plugin).log(""+width+","+height);
//        denieButton.setX(denieButton.getX()+denieButton.getWidth()/2 +10);

        denieButton.setX(width / 4 - denieButton.getWidth() / 2);
        denieButton.setY(height / 2 + height / 4 - denieButton.getHeight() / 2);
        popup.attachWidget(plugin, denieButton);

        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popup);
    }
}
