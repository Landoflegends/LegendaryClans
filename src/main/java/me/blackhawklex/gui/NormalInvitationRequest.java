/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.gui;

import java.util.ArrayList;
import java.util.List;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.party.Invitation;
import me.blackhawklex.legendaryclans.party.Party;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class NormalInvitationRequest {
    private Invitation invite;
    private LegendaryClans pluginLeg;
    
    public NormalInvitationRequest(LegendaryClans plugin, SpoutPlayer splayer, Invitation invite){
        this.invite = invite;
        pluginLeg = plugin;
        open(pluginLeg, splayer);
    }
    
    public void open(Plugin plugin, SpoutPlayer splayer) { // method used to open the GUI for a player       
        
        GenericPopup popup = new GenericPopup();
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();
        
//        int width = splayer.getCurrentScreen().getWidth();
//        int height = splayer.getCurrentScreen().getHeight();
        
        //title of the screen
        GenericLabel topLabel = new GenericLabel("Party-Invitation");
        topLabel.setScale(1);
        
        topLabel.setWidth(topLabel.getText().length()*8*(int)topLabel.getScale()).setHeight((int)topLabel.getScale()*15);
        
        topLabel.setX(width/2 - topLabel.getWidth()/2);
        topLabel.setY(topLabel.getHeight()/2+5);
        popup.attachWidget(plugin,topLabel);
        
        //title of the screen
        String questionString = "&2" + invite.getLeader().getName() + " invites you to his party!";
        GenericLabel questionLabel = new GenericLabel(LegendaryClans.parseColor(questionString));
        questionLabel.setScale(1);
        questionLabel.setWidth(questionLabel.getText().length()*5*(int)questionLabel.getScale());
        questionLabel.setHeight((int)questionLabel.getScale()*15);
        
        questionLabel.setX(width/2 - questionLabel.getWidth()/2);
        questionLabel.setY(height/2 - questionLabel.getHeight()/2);
        popup.attachWidget(plugin, questionLabel);
        
        //Accept Button
        LegendaryButton acceptButton = new LegendaryButton(LegendaryClans.parseColor("&2Accept!"), popup){
            @Override
            public void onButtonClick(ButtonClickEvent event) {
                if(invite.getParty() != null && pluginLeg.getPartyManager().checkIfIsLeader(invite.getLeader())){
                    invite.acceptInvite();
                }
                else if(invite.getParty() == null && pluginLeg.getPartyManager().checkIfIsLeader(invite.getLeader())){
                    invite.setParty(pluginLeg.getPartyManager().getPartyByLeader(invite.getLeader()));
                    invite.acceptInvite();
                }
                else if(invite.getParty() == null && !pluginLeg.getPartyManager().checkIfIsLeader(invite.getLeader())){
                    List <LegendaryPlayer> members = new ArrayList<LegendaryPlayer>();
                    members.add(invite.getAskedPlayer());
                    members.add(invite.getLeader());
                    Party newParty = new Party("", invite.getLeader(),members,pluginLeg.getPartyManager());
                    newParty.setName(invite.getLeader().getName()+"´s party");                        
                    invite.setParty(newParty);
                    
                    String message = "&2Party "+invite.getParty().getName()+" successfully created!";
                    invite.getParty().broadcastMessage(message);
                }
                getPopup().close();
            }
        };
        
        
        acceptButton.setWidth(acceptButton.getText().length()*8*(int)acceptButton.getScale()).setHeight((int)acceptButton.getScale()*15);;       
        acceptButton.setX(width/2+width/4-acceptButton.getWidth()/2).setY(height/2+height/4-acceptButton.getHeight()/2);
        
        
        popup.attachWidget(plugin,acceptButton);
        //deny button
        LegendaryButton denieButton = new LegendaryButton(LegendaryClans.parseColor("&4Deny!"), popup) {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                invite.denyInvite();
                getPopup().close();
            }
        };
       
        denieButton.setWidth(acceptButton.getText().length() * 8 * (int) acceptButton.getScale());
        denieButton.setHeight((int) denieButton.getScale() * 15);
//         
//         ((LegendaryClans)plugin).log(""+width+","+height);
//        denieButton.setX(denieButton.getX()+denieButton.getWidth()/2 +10);
        
        denieButton.setX(width/4-denieButton.getWidth()/2);
        denieButton.setY(height/2+height/4-denieButton.getHeight()/2);
        popup.attachWidget(plugin,denieButton);
        
        
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popup);

    }
}
