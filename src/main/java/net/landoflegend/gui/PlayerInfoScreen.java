/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.gui;

import net.landoflegend.legendaryclans.LegendaryClans;
import net.landoflegend.legendaryclans.LegendaryPlayer;
import net.landoflegend.legendaryclans.clans.Clan;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PlayerInfoScreen {
    
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private GenericPopup oldPopup;
    private SpoutPlayer splayerLeg;
    private LegendaryPlayer player;

    public PlayerInfoScreen(LegendaryClans legPlugin, SpoutPlayer splayer, GenericPopup oldPopup, LegendaryPlayer legPlayer) {
        this.legPlugin = legPlugin;
        this.oldPopup = oldPopup;
        splayerLeg = splayer;
        player = legPlayer;
        open(legPlugin, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) {
        popupp = new GenericPopup();
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();
        
        
        // player name
        GenericLabel clanNameLabel = new GenericLabel(player.getName());
        clanNameLabel.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        clanNameLabel.setX(width / 2 - clanNameLabel.getWidth() / 2).setY(5 + clanNameLabel.getHeight() / 2);
        popupp.attachWidget(plugin, clanNameLabel);

        // player kills
        GenericLabel rank = new GenericLabel(LegendaryClans.parseColor("&2Kills: " + player.getKills()));
        rank.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        rank.setX(rank.getWidth() / 2 + 15).setY(50 + rank.getHeight() / 2);
        popupp.attachWidget(plugin, rank);


        // player deaths
        GenericLabel kdr = new GenericLabel(LegendaryClans.parseColor("&9Deaths: " + player.getDeaths()));
        kdr.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        kdr.setX(kdr.getWidth() / 2 + 15).setY(25 + rank.getY() - kdr.getHeight() / 2);
        popupp.attachWidget(plugin, kdr);
        
        // player kdr
        GenericLabel level = new GenericLabel(LegendaryClans.parseColor("&2Kill-Death-Ratio: " + player.getKdr()));
        level.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        level.setX(level.getWidth() / 2 + 15).setY(25 + kdr.getY() - level.getHeight() / 2);
        popupp.attachWidget(plugin, level);
        
        // player Clan
        GenericLabel credits = new GenericLabel(LegendaryClans.parseColor("&9Clan: " + legPlugin.getClanManager().getClanByPlayer(player).getName()));
        credits.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        credits.setX(credits.getWidth() / 2 + 15).setY(25 + level.getY() - level.getHeight() / 2);
        popupp.attachWidget(plugin, credits);
        
        // player Status
        GenericLabel status = new GenericLabel(LegendaryClans.parseColor("&2Status: " + player.getStatus()));
        status.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        status.setX(status.getWidth() / 2 + 15).setY(25 + credits.getY() - status.getHeight() / 2);
        popupp.attachWidget(plugin, status);
        
        if (oldPopup != null) {
            //back button
            GenericButton backButton = new GenericButton(LegendaryClans.parseColor("&4Back")) {

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    popupp.close();
                    splayerLeg.getMainScreen().attachPopupScreen(oldPopup);
                }
            };
            backButton.setWidth(backButton.getText().length() * 8 * (int) backButton.getScale()).setHeight(15);
            backButton.setX(width / 2 - backButton.getWidth() / 2);
            backButton.setY(height - height / 4 - backButton.getHeight() / 2);
            popupp.attachWidget(plugin, backButton);
        }
        
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popupp);
    }
    
}
