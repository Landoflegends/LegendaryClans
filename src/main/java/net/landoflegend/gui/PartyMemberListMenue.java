/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.gui;

import net.landoflegend.legendaryclans.LegendaryClans;
import net.landoflegend.legendaryclans.LegendaryPlayer;
import net.landoflegend.legendaryclans.party.Invitation;
import net.landoflegend.legendaryclans.party.Party;
import net.landoflegend.legendaryclans.party.PartyManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PartyMemberListMenue {

    private LegendaryClans pluginLeg;
    private Party party;
    private GenericPopup popup;
    private SpoutPlayer spplayer;
    private GenericListWidget partyMemberList;

    public PartyMemberListMenue(LegendaryClans plugin, SpoutPlayer splayer, Party party) {
        pluginLeg = plugin;
        this.party = party;
        spplayer  = splayer;
        open(pluginLeg, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) {
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();
        popup = new GenericPopup();
        popup.setDirty(true);
        partyMemberList = new GenericListWidget();
        for(LegendaryPlayer p:party.getMembers()){
            if(p.equals(party.getLeader())){
               ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&5Leader")); 
               partyMemberList.addItem(item);
            }
            else{
               ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&2Member")); 
               partyMemberList.addItem(item);
            }
            
        }
        partyMemberList.setWidth(75).setHeight(height);
//        pluginLeg.log(width+""+height);
        //  
        partyMemberList.setX(width/2-partyMemberList.getWidth()/2 ).setY(height/2-partyMemberList.getHeight()/2);
        popup.attachWidget(plugin, partyMemberList);
        
        
        
        if(pluginLeg.getPartyManager().checkIfIsLeader(
            pluginLeg.getPlayerManager().searchPlayerByName(splayer.getName()))){
            
            //transfer lead button
            LegendaryButton button = new LegendaryButton("Transfer Lead",popup){

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    Widget widgets[] = popup.getAttachedWidgets();
                    if (partyMemberList.getSelectedItem() != null) {
                        LegendaryPlayer player = pluginLeg.getPlayerManager().searchPlayerByName(partyMemberList.getSelectedItem().getTitle());
                        if(player != null){
                            pluginLeg.getPartyManager().getPartyByPlayer(player).transferLead(player);
                            popup.close();
                        }
                        else{
                            CommandSender sender = (CommandSender)spplayer.getPlayer();
                            String noSuchPlayerOnline = "&6No such player in party!";
                            LegendaryClans.coloredOutput(sender, noSuchPlayerOnline);
                        }
                    }
                }

            };
            button.setWidth(button.getText().length()*8*(int)button.getScale()).setHeight(15);
            //-button.getWidth()/2
            button.setX(width/2+width/4 - button.getWidth()/2).setY(height/2-button.getHeight()/2);
            
            // extra invite button to add more members
            LegendaryButton inviteButton = new LegendaryButton("Invite", popup) {

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    Widget widgets[] = popup.getAttachedWidgets();
                    String playerName = "";
                    for (Widget widget : widgets) {
                        if (widget instanceof GenericTextField) {
                            playerName = ((GenericTextField) widget).getText();
                        }
                    }
                    LegendaryPlayer player = pluginLeg.getPlayerManager().searchPlayerByName(playerName);
                    if (player != null && !player.getName().equalsIgnoreCase(spplayer.getName()) && pluginLeg.getPartyManager().getPartyByPlayer(player) == null) {
                        //TODO invite Player
                        LegendaryPlayer leader = pluginLeg.getPlayerManager().searchPlayerByName(spplayer.getName());
                        popup.close();
                        Invitation invite = new Invitation(pluginLeg.getPartyManager().getPartyByLeader(leader), player, leader, pluginLeg);
                        
                    } else {
                        CommandSender sender = (CommandSender) spplayer.getPlayer();
                        String noSuchPlayerOnline = "&6No such player online or player already in party!";
                        LegendaryClans.coloredOutput(sender, noSuchPlayerOnline);
                    }
                }
            };
            
            //place invite button correctly
            inviteButton.setWidth(inviteButton.getText().length()*8*(int)inviteButton.getScale()).setHeight(15);
            //-button.getWidth()/2
            inviteButton.setX(width/2+width/4 - inviteButton.getWidth()/2).setY(height/2-inviteButton.getHeight()/2+15+button.getHeight());
            
            //kick Button to kick members from the party
            LegendaryButton kickButton = new LegendaryButton(LegendaryClans.parseColor("&4Kick"), popup) {

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    Widget widgets[] = popup.getAttachedWidgets();
//                    String playerName = "";
                    if (partyMemberList.getSelectedItem() != null) {
                        LegendaryPlayer player = pluginLeg.getPlayerManager().searchPlayerByName(partyMemberList.getSelectedItem().getTitle());
                        if (player != null && party.getMemberCount() > 2) {
                            pluginLeg.getPartyManager().getPartyByPlayer(player).deleteMember(player);
                            String kickMessage = "&1Kicked from " + party.getName();
                            LegendaryClans.coloredOutput((CommandSender) player.getPlayer(), kickMessage);
                            popup.close();
                        } else if (party.getMemberCount() == 2 && player != null) {
                            party.broadcastMessage("&8Party got dissolved, not enough members!");
                            String kickMessage = "&1Kicked from " + party.getName();
                            LegendaryClans.coloredOutput((CommandSender) player.getPlayer(), kickMessage);
                            pluginLeg.getPartyManager().removeParty(party);
                            popup.close();
                        }
                        if (player == null) {
                            CommandSender sender = (CommandSender) spplayer.getPlayer();
                            String noSuchPlayerOnline = "&6No such player in party!";
                            LegendaryClans.coloredOutput(sender, noSuchPlayerOnline);
                        }
                        }
                    }
                
            };
            
            kickButton.setWidth(kickButton.getText().length()*8*(int)kickButton.getScale()).setHeight(15);
            //-button.getWidth()/2
            kickButton.setX(width/2+width/4 - kickButton.getWidth()/2).setY(height/2-kickButton.getHeight()/2+30+inviteButton.getHeight()*2);
            
            //set up textfield
            GenericTextField textField = new GenericTextField();
            textField.setWidth(100).setHeight(15);
            textField.setX(width/2+width/4-textField.getWidth()/2).setY(height/2-button.getHeight()*2);
            
            LegendaryButton leaveButton = new LegendaryButton(LegendaryClans.parseColor("&4Leave"), popup) {

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    LegendaryPlayer player = pluginLeg.getPlayerManager().searchPlayerByName(spplayer.getName());
                    Party party = pluginLeg.getPartyManager().getPartyByPlayer(player);
                    if (player != null && party != null) {
                        if (party.getMemberCount() > 2) {
                            party.deleteMember(player);
                            party.broadcastMessage("&8" + player.getName() + " left the party.");
                            popup.close();
                        }
                        else if (party.getMemberCount() <= 2) {
                            party.broadcastMessage("&8" + player.getName() + " left the party.");
                            party.broadcastMessage("&8Party got dissolved, not enough members anymore!");
                            pluginLeg.getPartyManager().removeParty(party);
                            popup.close();
                        }
                    }
                }
            };

            leaveButton.setWidth(leaveButton.getText().length() * 8 * (int) leaveButton.getScale()).setHeight(15);
            //-button.getWidth()/2
            leaveButton.setX(width / 2 + width / 4 - leaveButton.getWidth() / 2).setY(kickButton.getY() + leaveButton.getHeight()/2+15);
            
            popup.attachWidget(plugin, leaveButton); 
            popup.attachWidget(plugin,inviteButton);
            popup.attachWidget(plugin,kickButton);
            popup.attachWidget(plugin, button);
            popup.attachWidget(plugin, textField);
        }
        else{
            LegendaryButton leaveButton = new LegendaryButton(LegendaryClans.parseColor("&4Leave"), popup) {

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    LegendaryPlayer player = pluginLeg.getPlayerManager().searchPlayerByName(spplayer.getName());
                    Party party = pluginLeg.getPartyManager().getPartyByPlayer(player);
                    if (player != null && party !=null) {
                        if(party.getMemberCount()>2){
                            party.deleteMember(player);
                            party.broadcastMessage("&8"+player.getName()+" left the party.");
                            popup.close();
                        }
                        else if(party.getMemberCount()<=2){
                            party.broadcastMessage("&8"+player.getName()+" left the party.");
                            party.broadcastMessage("&8Party got dissolved, not enough members anymore!");
                            pluginLeg.getPartyManager().removeParty(party);
                            popup.close();
                        }
                    }
                }
            };

            leaveButton.setWidth(leaveButton.getText().length() * 8 * (int) leaveButton.getScale()).setHeight(15);
            //-button.getWidth()/2
            leaveButton.setX(width/2+width/4 - leaveButton.getWidth()/2).setY(height/2-leaveButton.getHeight()/2);
            popup.attachWidget(plugin, leaveButton);
        }
        
        // level 
        GenericLabel levelLabel = new GenericLabel(LegendaryClans.parseColor("&9Level: " + party.getLevel()+ "/"+PartyManager.getMaxLevel()));
        levelLabel.setWidth(levelLabel.getText().length()*8*(int)levelLabel.getScale()).setHeight((int)levelLabel.getScale()*15);
        
        levelLabel.setX(width/4- levelLabel.getWidth()/2);
        levelLabel.setY( -25 + height/2 + levelLabel.getHeight()/2);
        popup.attachWidget(plugin, levelLabel);
        
        // level 
        GenericLabel expLabel = new GenericLabel(LegendaryClans.parseColor("&cEXP: " + party.getExp() + "/" + PartyManager.getRequiredXpPerLevel()[party.getLevel()-1]));
        expLabel.setWidth(levelLabel.getText().length() * 8 * (int) levelLabel.getScale()).setHeight((int) levelLabel.getScale() * 15);
        expLabel.setX(width / 4 - levelLabel.getWidth() / 2);
        expLabel.setY(25 + levelLabel.getY());
        popup.attachWidget(plugin, expLabel);
        
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popup);   
    }
}
