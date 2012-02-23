/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.gui;

import net.landoflegend.legendaryclans.LegendaryClans;
import net.landoflegend.legendaryclans.LegendaryPlayer;
import net.landoflegend.legendaryclans.clans.Clan;
import net.landoflegend.legendaryclans.clans.ClanManager;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ExtraClanScreen {
    private LegendaryClans legPlugin;
    private GenericPopup popupp;
    private GenericPopup oldPopup;
    private SpoutPlayer splayerLeg;
    private GenericListWidget memberList;
    private Clan clan;

    public ExtraClanScreen(LegendaryClans legPlugin, SpoutPlayer splayer, GenericPopup oldPopup, Clan clan){
        this.legPlugin = legPlugin;
        this.oldPopup = oldPopup;
        this.clan = clan;
        splayerLeg = splayer;
        open(legPlugin, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) {
        popupp = new GenericPopup();
        int width = splayer.getMainScreen().getMaxWidth();
        int height = splayer.getMainScreen().getMaxHeight();
        
        // clan Name  -done
        GenericLabel clanNameLabel = new GenericLabel(clan.getName());
        clanNameLabel.setWidth(clanNameLabel.getText().length()*5*(int)clanNameLabel.getScale()).setHeight(15);
        clanNameLabel.setX(width/2-clanNameLabel.getWidth()/2).setY(5+clanNameLabel.getHeight()/2);
        popupp.attachWidget(plugin, clanNameLabel);
        
        // clan rank -done
        GenericLabel rank = new GenericLabel(LegendaryClans.parseColor("&2Rank: "+legPlugin.getClanManager().getRank(clan)));
        rank.setWidth(clanNameLabel.getText().length()*5*(int)clanNameLabel.getScale()).setHeight(15);
        rank.setX(rank.getWidth()/2+15).setY(50+rank.getHeight()/2);
        popupp.attachWidget(plugin, rank);
        
        
        // clan KDr -done
        GenericLabel kdr = new GenericLabel(LegendaryClans.parseColor("&9Kill-Death-Ratio: "+clan.getKdr()));
        kdr.setWidth(clanNameLabel.getText().length()*5*(int)clanNameLabel.getScale()).setHeight(15);
        kdr.setX(kdr.getWidth()/2+15).setY(25 + rank.getY()- kdr.getHeight()/2);
        popupp.attachWidget(plugin, kdr);
        
        
        // clan Level -done
        GenericLabel level = new GenericLabel(LegendaryClans.parseColor("&2Level: "+clan.getLvl()));
        level.setWidth(clanNameLabel.getText().length()*5*(int)clanNameLabel.getScale()).setHeight(15);
        level.setX(level.getWidth()/2+15).setY(25 + kdr.getY()- level.getHeight()/2);
        popupp.attachWidget(plugin, level);
        
        //Inner Clan Base seize:
        GenericLabel clanBaseSeize = new GenericLabel(LegendaryClans.parseColor("&9Innerbase seize: " + clan.getTerritory().getInnerClanBaseSeize()));
        clanBaseSeize.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
        clanBaseSeize.setX(clanBaseSeize.getWidth() / 2 + 15).setY(25 + level.getY() - level.getHeight() / 2);
        popupp.attachWidget(plugin, clanBaseSeize);
        
        //if own clan
        LegendaryPlayer legPlayer = legPlugin.getPlayerManager().searchPlayerByName(splayer.getName());
        Clan clan2 = legPlugin.getClanManager().getClanByPlayer(legPlayer);
        if (clan.equals(clan2)) {
            //clan credits -done
            GenericLabel credits = new GenericLabel(LegendaryClans.parseColor("&2Credits: " + clan.getCredits()));
            credits.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
            credits.setX(credits.getWidth() / 2 + 15).setY(25 + clanBaseSeize.getY() - clanBaseSeize.getHeight() / 2);
            popupp.attachWidget(plugin, credits);
            
            GenericLabel memberRest = new GenericLabel(LegendaryClans.parseColor("&9Member count: " + clan.getMemberCount() + "/"+ ClanManager.getMaxMembers()[clan.getLvl()-1]));
            memberRest.setWidth(clanNameLabel.getText().length() * 5 * (int) clanNameLabel.getScale()).setHeight(15);
            memberRest.setX(memberRest.getWidth() / 2 + 15).setY(25 + credits.getY() - memberRest.getHeight() / 2);
            popupp.attachWidget(plugin, memberRest);
        }
        
        // MemberList 
        memberList = new GenericListWidget();
        for(LegendaryPlayer p: clan.getLeaders()){
            if (!clan.isFounder(p)) {
                ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&5Leader"));
                memberList.addItem(item);
            } else {
                ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&6Founder"));
                memberList.addItem(item);
            }
        }
        for (LegendaryPlayer p : clan.getMembers()) {
            ListWidgetItem item = new ListWidgetItem(p.getName(), LegendaryClans.parseColor("&2Member"));
            memberList.addItem(item);
        }
        memberList.setWidth(75).setHeight(height-40);
        memberList.setX(width / 2 + width/4 - memberList.getWidth() / 2).setY(height / 2 - memberList.getHeight() / 2);
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
                        PlayerInfoScreen screene = new PlayerInfoScreen(legPlugin,splayerLeg,popupp,legP);
                    }
                }
            }
        };

        selectButton.setWidth(selectButton.getText().length() * 8 * (int) selectButton.getScale()).setHeight(15);
        selectButton.setX(width /2 - selectButton.getWidth()/2);
        selectButton.setY(height / 2 - selectButton.getHeight() / 2);
        popupp.attachWidget(plugin, selectButton);
        
        //if this has an apppropriate old popup make a back-button
        if(oldPopup != null){
            //back button
            GenericButton backButton = new GenericButton(LegendaryClans.parseColor("&4Back")){

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                        popupp.close();
                    splayerLeg.getMainScreen().attachPopupScreen(oldPopup);
                }

            };
            backButton.setWidth(backButton.getText().length()*8*(int)backButton.getScale()).setHeight(15);
            backButton.setX(width/2 - backButton.getWidth()/2);
            backButton.setY(height-height/4-backButton.getHeight()/2);
            popupp.attachWidget(plugin, backButton);
        }
        else{
            GenericButton backButton = new GenericButton(LegendaryClans.parseColor("&6Clan List")) {

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    popupp.close();
                    NoClanMenue menue = new NoClanMenue(legPlugin, splayerLeg);
                }
            };
            backButton.setWidth(backButton.getText().length() * 8 * (int) backButton.getScale()).setHeight(15);
            backButton.setX(width / 2 - backButton.getWidth() / 2);
            backButton.setY(height - height / 4 - backButton.getHeight() / 2);
            popupp.attachWidget(plugin, backButton);
            
            GenericButton leaveButton = new GenericButton(LegendaryClans.parseColor("&4Leave Clan")) {

                @Override
                public void onButtonClick(ButtonClickEvent event) {
                    super.onButtonClick(event);
                    popupp.close();
                    LegendaryPlayer player = legPlugin.getPlayerManager().searchPlayerByName(splayerLeg.getName());
                    if(player != null){
                        clan.leaveClan(player);
                    }
                }
            };
            leaveButton.setWidth(leaveButton.getText().length() * 8 * (int) leaveButton.getScale()).setHeight(15);
            leaveButton.setX(width / 2 - leaveButton.getWidth() / 2);
            leaveButton.setY(25 + backButton.getY() - leaveButton.getHeight() / 2);
            popupp.attachWidget(plugin, leaveButton);
        }
        splayer.closeActiveWindow();
        splayer.getMainScreen().attachPopupScreen(popupp);                     
    }
}
