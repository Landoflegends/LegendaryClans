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
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClanMemberMoneyDeposit {
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private GenericPopup oldPopup;
    private GenericLabel clanSoulsLabel;
    private SpoutPlayer splayerLeg;
    private LegendaryPlayer submitter;
    private Clan clan;

    public ClanMemberMoneyDeposit(LegendaryClans legPlugin, SpoutPlayer splayer, Clan clan, GenericPopup oldPopup) {
        this.legPlugin = legPlugin;
        this.clan = clan;
        this.oldPopup = oldPopup;
        splayerLeg = splayer;
        submitter = legPlugin.getPlayerManager().searchPlayerByName(splayer.getName());

        open(legPlugin, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) {
//        splayer.closeActiveWindow();
        popupp = new GenericPopup();

        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();
        
        // clan Name  -done
        GenericLabel topLabel = new GenericLabel("Clan-Hall");
        topLabel.setWidth(topLabel.getText().length() * 6 * (int) topLabel.getScale()).setHeight(15);
        topLabel.setX(width / 2 - topLabel.getWidth() / 2).setY(5 + topLabel.getHeight() / 2);
        popupp.attachWidget(plugin, topLabel);
        
        // clan Name  -done
        double currentClanSouls = 0;
        currentClanSouls = legPlugin.getiConomy().bankBalance(clan.getName()).balance;
        clanSoulsLabel = new GenericLabel(LegendaryClans.parseColor("&6Soul Bank: "+ currentClanSouls));
        clanSoulsLabel.setWidth(clanSoulsLabel.getText().length() * 6 * (int) clanSoulsLabel.getScale()).setHeight(15);
        clanSoulsLabel.setX(width / 4 - clanSoulsLabel.getWidth() / 2).setY(height/2 - clanSoulsLabel.getHeight() / 2);
        popupp.attachWidget(plugin, clanSoulsLabel);
        
        
        GenericButton sacrificePartyButton1 = new GenericButton("25") {  
            int amount = 25;
            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if(legPlugin.getiConomy().has(splayerLeg.getName(), amount)){
                    legPlugin.getiConomy().bankDeposit(clan.getName(), amount);
                    legPlugin.getiConomy().withdrawPlayer(splayerLeg.getName(), amount);
                    LegendaryClans.coloredOutput((CommandSender)splayerLeg.getPlayer(), "&2Successfully deposited " + amount + " at the clan bank!");
                    clan.broadcastMessage("&9"+splayerLeg.getName() + " deposited " + amount + " at the clan bank.");
                    clanSoulsLabel.setText("&6Souls: "+ legPlugin.getiConomy().bankBalance(clan.getName()).balance);
                }
                else {
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&4You do not have enough souls to do this.");
                }
            }
        };
        sacrificePartyButton1.setWidth(topLabel.getText().length() * 8 * (int) sacrificePartyButton1.getScale()).setHeight(15);
        sacrificePartyButton1.setX(width / 2 + width/4 - sacrificePartyButton1.getWidth() / 2).setY(25+height / 4 - sacrificePartyButton1.getHeight() / 2);

        popupp.attachWidget(plugin, sacrificePartyButton1);
        
        GenericButton sacrificePartyButton2 = new GenericButton("50") {
            int amount = 50;
            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (legPlugin.getiConomy().has(splayerLeg.getName(), amount)) {
                    legPlugin.getiConomy().bankDeposit(clan.getName(), amount);
                    legPlugin.getiConomy().withdrawPlayer(splayerLeg.getName(), amount);
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&2Successfully deposited " + amount + " at the clan bank!");
                    clan.broadcastMessage("&9" + splayerLeg.getName() + "deposited " + amount + "at the clan bank.");
                    clanSoulsLabel.setText("&6Souls: "+ legPlugin.getiConomy().bankBalance(clan.getName()).balance);
                }
                else {
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&4You do not have enough souls to do this.");
                }
            }
        };
        sacrificePartyButton2.setWidth(topLabel.getText().length() * 8 * (int) sacrificePartyButton1.getScale()).setHeight(15);
        sacrificePartyButton2.setX(width / 2 + width / 4 - sacrificePartyButton1.getWidth() / 2).setY(25 + sacrificePartyButton1.getY() - sacrificePartyButton1.getHeight() / 2);

        popupp.attachWidget(plugin, sacrificePartyButton2);
        
        GenericButton sacrificePartyButton3 = new GenericButton("100") {

            int amount = 100;

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (legPlugin.getiConomy().has(splayerLeg.getName(), amount)) {
                    legPlugin.getiConomy().bankDeposit(clan.getName(), amount);
                    legPlugin.getiConomy().withdrawPlayer(splayerLeg.getName(), amount);
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&2Successfully deposited " + amount + " at the clan bank!");
                    clan.broadcastMessage("&9" + splayerLeg.getName() + "deposited " + amount + "at the clan bank.");
                    clanSoulsLabel.setText("&6Souls: "+ legPlugin.getiConomy().bankBalance(clan.getName()).balance);
                }
                else {
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&4You do not have enough souls to do this.");
                }
            }
        };
        sacrificePartyButton3.setWidth(topLabel.getText().length() * 8 * (int) sacrificePartyButton1.getScale()).setHeight(15);
        sacrificePartyButton3.setX(width / 2 + width / 4 - sacrificePartyButton1.getWidth() / 2).setY(25 + sacrificePartyButton2.getY() - sacrificePartyButton1.getHeight() / 2);

        popupp.attachWidget(plugin, sacrificePartyButton3);
        
        GenericButton sacrificePartyButton4 = new GenericButton("250") {

            int amount = 250;

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (legPlugin.getiConomy().has(splayerLeg.getName(), amount)) {
                    legPlugin.getiConomy().bankDeposit(clan.getName(), amount);
                    legPlugin.getiConomy().withdrawPlayer(splayerLeg.getName(), amount);
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&2Successfully deposited " + amount + " at the clan bank!");
                    clan.broadcastMessage("&9" + splayerLeg.getName() + "deposited " + amount + "at the clan bank.");
                    clanSoulsLabel.setText("&6Souls: "+ legPlugin.getiConomy().bankBalance(clan.getName()).balance);
                }
                else {
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&4You do not have enough souls to do this.");
                }
            }
        };
        sacrificePartyButton4.setWidth(topLabel.getText().length() * 8 * (int) sacrificePartyButton1.getScale()).setHeight(15);
        sacrificePartyButton4.setX(width / 2 + width / 4 - sacrificePartyButton1.getWidth() / 2).setY(25 + sacrificePartyButton3.getY() - sacrificePartyButton1.getHeight() / 2);

        popupp.attachWidget(plugin, sacrificePartyButton4);
        
        GenericButton sacrificePartyButton5 = new GenericButton("500") {

            int amount = 500;

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (legPlugin.getiConomy().has(splayerLeg.getName(), amount)) {
                    legPlugin.getiConomy().bankDeposit(clan.getName(), amount);
                    legPlugin.getiConomy().withdrawPlayer(splayerLeg.getName(), amount);
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&2Successfully deposited " + amount + " at the clan bank!");
                    clan.broadcastMessage("&9" + splayerLeg.getName() + "deposited " + amount + "at the clan bank.");
                    clanSoulsLabel.setText("&6Souls: "+ legPlugin.getiConomy().bankBalance(clan.getName()).balance);
                }
                else{
                    LegendaryClans.coloredOutput((CommandSender) splayerLeg.getPlayer(), "&4You do not have enough souls to do this.");
                }
            }
        };
        sacrificePartyButton5.setWidth(topLabel.getText().length() * 8 * (int) sacrificePartyButton1.getScale()).setHeight(15);
        sacrificePartyButton5.setX(width / 2 + width / 4 - sacrificePartyButton1.getWidth() / 2).setY(25 + sacrificePartyButton4.getY() - sacrificePartyButton1.getHeight() / 2);

        popupp.attachWidget(plugin, sacrificePartyButton5);
        
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
        
        splayer.getMainScreen().attachPopupScreen(popupp);
    }
}
