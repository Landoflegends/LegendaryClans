/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.gui;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.party.Invitation;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class NoPartyMenue {
    
    private LegendaryClans legPlugin;
    private GenericPopup popup;
    private SpoutPlayer splayerLeg;
    
    public NoPartyMenue(LegendaryClans legPlugin, SpoutPlayer splayer){
        this.legPlugin = legPlugin;
        splayerLeg=splayer;
        open(legPlugin,splayer);
    }
    
    public void open(Plugin plugin, SpoutPlayer splayer) { // method used to open the GUI for a player
        int width = splayer.getMainScreen().getWidth();
        int height = splayer.getMainScreen().getHeight();
        
        popup = new GenericPopup();
        
        //title
        GenericLabel topLabel = new GenericLabel("Party-Menu");
        topLabel.setWidth(topLabel.getText().length()*5*(int)topLabel.getScale()).setHeight(15);
        
        topLabel.setX(width/2 - topLabel.getWidth()/2);
        topLabel.setY(5+topLabel.getHeight()/2);
        
        popup.attachWidget(plugin, topLabel);
        
        // Textfield
        GenericTextField textField = new GenericTextField();
        textField.setWidth(100);
        textField.setHeight(15);
        
        textField.setX(width/2-textField.getWidth()/2);
        textField.setY(height/2-textField.getHeight()/2);
        popup.attachWidget(plugin, textField);


        
        // invite button
        LegendaryButton inviteButton = new LegendaryButton("Invite!", popup) {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                Widget widgets[] = popup.getAttachedWidgets();
                String playerName ="";
                for(Widget widget:widgets){
                    if(widget instanceof GenericTextField){
                        playerName = ((GenericTextField)widget).getText();
                    }
                }
                LegendaryPlayer playerToBeInvited = legPlugin.getPlayerManager().searchPlayerByName(playerName); 
                
                if(playerToBeInvited != null && !playerToBeInvited.getName().equalsIgnoreCase(splayerLeg.getName()) && playerToBeInvited.isOnline() && legPlugin.getPartyManager().getPartyByPlayer(playerToBeInvited) == null){
                    LegendaryPlayer leader = legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName());
                    popup.close();
                    Invitation invite = new Invitation(null,playerToBeInvited,leader, legPlugin);             
                }
                        
                else{
                    CommandSender sender = (CommandSender)splayerLeg.getPlayer();
                    String message = "&6No such player online or player already in party!";
                    LegendaryClans.coloredOutput(sender, message);
                }

            }
        };
        inviteButton.setWidth(inviteButton.getText().length() * 6 * (int) inviteButton.getScale());
        inviteButton.setHeight((int) inviteButton.getScale() * 15);       
        inviteButton.setX(width/2-inviteButton.getWidth()/2);
        inviteButton.setY(height/2+height/4-inviteButton.getHeight()/2);
        
        popup.attachWidget(plugin, inviteButton);
        
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popup);

        

    }
    
}
