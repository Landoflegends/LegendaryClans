/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.gui;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import me.blackhawklex.legendaryclans.clans.ClanManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClanLevelUpScreen {
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private GenericPopup oldPopup;
    private SpoutPlayer splayerLeg;
    private GenericListWidget memberList;
    private Clan clan;

    public ClanLevelUpScreen(LegendaryClans legPlugin, SpoutPlayer splayer, GenericPopup oldPopup, Clan clan) {
        this.legPlugin = legPlugin;
        this.oldPopup = oldPopup;
        this.clan = clan;
        splayerLeg = splayer;
        open(legPlugin, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) {
        splayer.closeActiveWindow();
        popupp = new GenericPopup();
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();

        // clan Name  -done
        GenericLabel clanNameLabel = new GenericLabel(LegendaryClans.parseColor("&6Level-Up-Menu"));
        clanNameLabel.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        clanNameLabel.setX(width / 2 - clanNameLabel.getWidth() / 2).setY(5 + clanNameLabel.getHeight() / 2);
        popupp.attachWidget(plugin, clanNameLabel);
        
        
        GenericLabel reqCreditsLabel = new GenericLabel();
        if(clan.hasEnoughCreditsToLvlUp()){
            reqCreditsLabel.setText(LegendaryClans.parseColor("&2Credits: "+clan.getCredits() +"/" + ClanManager.getCreditPrices()[clan.getLvl()-1]));
        }
        else{
           reqCreditsLabel.setText(LegendaryClans.parseColor("&4Credits: "+clan.getCredits() +"/" + ClanManager.getCreditPrices()[clan.getLvl()-1])); 
        }
        reqCreditsLabel.setWidth(reqCreditsLabel.getText().length() * 5 * (int) reqCreditsLabel.getScale()).setHeight(15);
        reqCreditsLabel.setX(width / 2 - width/4 - reqCreditsLabel.getWidth() / 2).setY(height/4 + reqCreditsLabel.getHeight() / 2);
        popupp.attachWidget(plugin, reqCreditsLabel);
        
        GenericLabel reqMoneyLabel = new GenericLabel();
        LegendaryPlayer leader = legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName());
        Economy iconomy = legPlugin.getiConomy();
        if (clan.hasEnoughMoneyToLvlUp(leader)) {
            reqMoneyLabel.setText(LegendaryClans.parseColor("&2Money: " + iconomy.bankBalance(clan.getName()).balance + "/" + ClanManager.getMoneyPrices()[clan.getLvl() - 1]));
        } else {
            reqMoneyLabel.setText(LegendaryClans.parseColor("&4Money: " +  iconomy.bankBalance(clan.getName()).balance + "/" + ClanManager.getMoneyPrices()[clan.getLvl() - 1]));
        }
        reqMoneyLabel.setWidth(reqCreditsLabel.getText().length() * 5 * (int) reqCreditsLabel.getScale()).setHeight(15);
        reqMoneyLabel.setX(width / 2 - width / 4 - reqMoneyLabel.getWidth() / 2).setY(25 + reqCreditsLabel.getY() + reqMoneyLabel.getHeight() / 2);
        popupp.attachWidget(plugin, reqMoneyLabel);
        
        
        //levelUp button
        GenericButton levelUpButton = new GenericButton(LegendaryClans.parseColor("&aLevelUp")) {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                LegendaryPlayer leader = legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName());
                clan.lvlup(leader);
                popupp.close();
            }
        };
        levelUpButton.setWidth(levelUpButton.getText().length() * 8 * (int) levelUpButton.getScale()).setHeight(15);
        levelUpButton.setX(width / 2 + width/4 - levelUpButton.getWidth() / 2);
        levelUpButton.setY(height/2 - levelUpButton.getHeight() / 2);
        popupp.attachWidget(plugin, levelUpButton);
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
            backButton.setX(width / 2 + width/4 - backButton.getWidth() / 2);
            backButton.setY(50+  height/2 - backButton.getHeight() / 2);
            popupp.attachWidget(plugin, backButton);
        }  
        
        splayer.getMainScreen().attachPopupScreen(popupp);
    }
    
}
