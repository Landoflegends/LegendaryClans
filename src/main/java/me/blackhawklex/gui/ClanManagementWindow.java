/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.gui;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import me.blackhawklex.legendaryclans.clans.ClanManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class ClanManagementWindow {
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private SpoutPlayer splayerLeg;
    private LegendaryPlayer submitter;
    private GenericListWidget memberList;
    private Clan clan;

    public ClanManagementWindow(LegendaryClans legPlugin, SpoutPlayer splayer, Clan clan) {
        this.legPlugin = legPlugin;
        this.clan = clan;
        splayerLeg = splayer;
        submitter = legPlugin.getPlayerManager().searchPlayerByName(splayer.getName());
        
        open(legPlugin, splayer);
    }
    public void open(Plugin plugin, SpoutPlayer splayer) {
        splayer.closeActiveWindow();
        popupp = new GenericPopup();
        
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();

        // clan Name  -done
        GenericLabel topLabel = new GenericLabel("Clan-Management");
        topLabel.setWidth(topLabel.getText().length() * 6 * (int) topLabel.getScale()).setHeight(15);
        topLabel.setX(width / 2 - topLabel.getWidth() / 2).setY(5 + topLabel.getHeight() / 2);
        popupp.attachWidget(plugin, topLabel);
        
        // MemberList 
        memberList = new GenericListWidget();
        for (LegendaryPlayer p : clan.getLeaders()) {
            if(!clan.isFounder(p)){
                ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&5Leader"));
                memberList.addItem(item);
            }
            else{
                ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&6Founder"));
                memberList.addItem(item); 
            }
        }
        for (LegendaryPlayer p : clan.getMembers()) {
            ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&2Member"));
            memberList.addItem(item);
        }
        memberList.setWidth(75).setHeight(height - 40);
        memberList.setX(width / 2 - memberList.getWidth() / 2).setY(height / 2 - memberList.getHeight() / 2);
        popupp.attachWidget(plugin, memberList);

        //show info button for clan members
        GenericButton selectButton = new GenericButton("Show info!") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (memberList.getSelectedItem() != null) {
                    LegendaryPlayer legP = legPlugin.getPlayerManager().searchPlayerByName(memberList.getSelectedItem().getTitle());
                    if (legP != null) {
                        popupp.close();
                        PlayerInfoScreen screene = new PlayerInfoScreen(legPlugin, splayerLeg, popupp, legP);
                    }
                }
            }
        };

        selectButton.setWidth(selectButton.getText().length() * 8 * (int) selectButton.getScale()).setHeight(15);
        selectButton.setX(width / 2 - width / 4 - selectButton.getWidth() / 2);
        selectButton.setY(height / 2 - selectButton.getHeight() / 2);
        popupp.attachWidget(plugin, selectButton);
        
        //level up button
        GenericButton lvlButton = new GenericButton("Level-Up") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
//                LegendaryPlayer legP = legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName());
                popupp.close();
                if(clan.getLvl()<5){
                    ClanLevelUpScreen screeny = new ClanLevelUpScreen(legPlugin, splayerLeg, popupp, clan);
                }
                else{
                   LegendaryClans.coloredOutput((CommandSender) submitter.getPlayer(), "&4Your clan already is max level. Wait for a content update."); 
                }
            }
        };

        lvlButton.setWidth(selectButton.getText().length() * 8 * (int) selectButton.getScale()).setHeight(15);
        lvlButton.setX(width / 2 - width / 4 - selectButton.getWidth() / 2);
        lvlButton.setY(25+selectButton.getY() - selectButton.getHeight() / 2);
        popupp.attachWidget(plugin, lvlButton);
        
        //level up button
        GenericButton depositMoneyButton = new GenericButton("Deposit") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
//                LegendaryPlayer legP = legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName());
                popupp.close();
                ClanMemberMoneyDeposit deposit = new ClanMemberMoneyDeposit(legPlugin, splayerLeg, clan, popupp);        
            }
        };

        depositMoneyButton.setWidth(selectButton.getText().length() * 8 * (int) selectButton.getScale()).setHeight(15);
        depositMoneyButton.setX(width / 2 - width / 4 - selectButton.getWidth() / 2);
        depositMoneyButton.setY(25 + lvlButton.getY() - selectButton.getHeight() / 2);
        popupp.attachWidget(plugin, depositMoneyButton);
        
        
        //kick button
        GenericButton kickButton = new GenericButton("Kick!") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (memberList.getSelectedItem() != null) {
                    LegendaryPlayer legP = legPlugin.getPlayerManager().searchPlayerByName(memberList.getSelectedItem().getTitle());
                    if (legP != null) {
                        popupp.close();
                        clan.dismissMember(legP,submitter);
                    }
                }
                else{
                    LegendaryClans.coloredOutput((CommandSender) submitter.getPlayer(), "&4No player selected.");
                }
            }
        };

        kickButton.setWidth(kickButton.getText().length() * 8 * (int) kickButton.getScale()).setHeight(15);
        kickButton.setX(width / 2 + width / 4 - kickButton.getWidth() / 2);
        kickButton.setY(height / 2 - kickButton.getHeight() / 2);
        popupp.attachWidget(plugin, kickButton);
        
        //promote button
        GenericButton promoteButton = new GenericButton("Promote!") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (memberList.getSelectedItem() != null) {
                    LegendaryPlayer legP = legPlugin.getPlayerManager().searchPlayerByName(memberList.getSelectedItem().getTitle());
                    if (legP != null) {
                        popupp.close();
                        clan.promotePlayer(legP,submitter);
                    }
                }
                else{
                LegendaryClans.coloredOutput((CommandSender) submitter.getPlayer(), "&4No player selected.");
            }
            }
        };

        promoteButton.setWidth(promoteButton.getText().length() * 8 * (int) promoteButton.getScale()).setHeight(15);
        promoteButton.setX(width / 2 + width / 4 - promoteButton.getWidth() / 2);
        promoteButton.setY(25+ kickButton.getY() - selectButton.getHeight() / 2);
        popupp.attachWidget(plugin, promoteButton);
        
        //promote button
        GenericButton demoteButton = new GenericButton("Demote!") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if (memberList.getSelectedItem() != null) {
                    LegendaryPlayer legP = legPlugin.getPlayerManager().searchPlayerByName(memberList.getSelectedItem().getTitle());
                    if (legP != null) {
                        popupp.close();
                        clan.demotePlayer(legP, submitter);
                    }
                } else {
                    LegendaryClans.coloredOutput((CommandSender) submitter.getPlayer(), "&4No player selected.");
                }
            }
        };

        demoteButton.setWidth(promoteButton.getText().length() * 8 * (int) promoteButton.getScale()).setHeight(15);
        demoteButton.setX(width / 2 + width / 4 - demoteButton.getWidth() / 2);
        demoteButton.setY(25 + promoteButton.getY() - selectButton.getHeight() / 2);
        popupp.attachWidget(plugin, demoteButton);
        
        // Textfield
        GenericTextField textField = new GenericTextField();
        textField.setWidth(100);
        textField.setHeight(15);

        textField.setX(width / 2 + width/4 - textField.getWidth() / 2);
        textField.setY(-25 + kickButton.getY() - textField.getHeight() / 2);
        popupp.attachWidget(plugin, textField);
        
        //invite button
        GenericButton inviteButton = new GenericButton("Invite!") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                Widget widgets[] = popupp.getAttachedWidgets();
                String playerName = "";
                for (Widget widget : widgets) {
                    if (widget instanceof GenericTextField) {
                        playerName = ((GenericTextField) widget).getText();
                    }
                }
                LegendaryPlayer playerToBeInvited = legPlugin.getPlayerManager().searchPlayerByName(playerName);
                Clan clanOfPlayerInvite = legPlugin.getClanManager().getClanByPlayer(legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName()));
                if(clanOfPlayerInvite.getMemberCount() < ClanManager.getMaxMembers()[clanOfPlayerInvite.getLvl()-1]){
                    if (playerToBeInvited != null && !playerToBeInvited.getName().equalsIgnoreCase(splayerLeg.getName())&& clanOfPlayerInvite == null && playerToBeInvited.isOnline()) {

                        SpoutPlayer player = SpoutManager.getPlayer(playerToBeInvited.getPlayer());
                        if(player  != null){
                            ClanInvitationRequest request = new ClanInvitationRequest(legPlugin, player, clan);
                            LegendaryClans.coloredOutput((CommandSender)submitter.getPlayer(), "&2Invitation sent to "+playerToBeInvited.getName());
                        }
                    }
                    else{
                        LegendaryClans.coloredOutput((CommandSender) submitter.getPlayer(), "&4No invitation possible.");
                    }
                }
                else{
                    LegendaryClans.coloredOutput((CommandSender) submitter.getPlayer(), "&4Your clan has reached the max amount of members for its level. Level up to increase memberrestrictions.");
                }
            }
        };

        inviteButton.setWidth(promoteButton.getText().length() * 8 * (int) promoteButton.getScale()).setHeight(15);
        inviteButton.setX(width / 2 + width / 4 - inviteButton.getWidth() / 2);
        inviteButton.setY(25 + demoteButton.getY() - inviteButton.getHeight() / 2);
        popupp.attachWidget(plugin, inviteButton);
        
        
        splayer.getMainScreen().attachPopupScreen(popupp); 
    }
}
