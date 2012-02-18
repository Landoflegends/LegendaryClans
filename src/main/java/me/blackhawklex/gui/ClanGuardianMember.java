/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.gui;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class ClanGuardianMember {
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private SpoutPlayer splayerLeg;
    private LegendaryPlayer submitter;
    private Clan clan;

    public ClanGuardianMember(LegendaryClans legPlugin, SpoutPlayer splayer, Clan clan) {
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
        GenericLabel topLabel = new GenericLabel("Clan-Hall");
        topLabel.setWidth(topLabel.getText().length() * 6 * (int) topLabel.getScale()).setHeight(15);
        topLabel.setX(width / 2 - topLabel.getWidth() / 2).setY(5 + topLabel.getHeight() / 2);
        popupp.attachWidget(plugin, topLabel);
        
        GenericButton sacrificePartyButton =  new GenericButton("Coming soon!"){  //Sacrifice party

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                
            }
            
        };
        sacrificePartyButton.setWidth(sacrificePartyButton.getText().length() * 6 * (int) sacrificePartyButton.getScale()).setHeight(15);
        sacrificePartyButton.setX(width/2-sacrificePartyButton.getWidth()/2).setY(-25+height/2-sacrificePartyButton.getHeight()/2);
        
        //remove this when the feature is there!
        sacrificePartyButton.setEnabled(false);
        popupp.attachWidget(plugin, sacrificePartyButton);
        
        GenericButton depositMoneyButton = new GenericButton("Deposit souls!") {

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                popupp.close();
                ClanMemberMoneyDeposit depositScreen = new ClanMemberMoneyDeposit(legPlugin, splayerLeg, clan, popupp);
                
            }
        };
        depositMoneyButton.setWidth(sacrificePartyButton.getText().length() * 6 * (int) sacrificePartyButton.getScale()).setHeight(15);
        depositMoneyButton.setX(width / 2 - sacrificePartyButton.getWidth() / 2).setY(+25 + sacrificePartyButton.getY() - sacrificePartyButton.getHeight() / 2);
        popupp.attachWidget(plugin, depositMoneyButton);
        
//        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popupp);
        
    }
}
