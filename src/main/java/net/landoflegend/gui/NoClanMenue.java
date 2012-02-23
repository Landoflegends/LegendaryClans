package me.blackhawklex.gui;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

public class NoClanMenue {
    
    private LegendaryClans legPlugin;
    private GenericPopup popup;
    private SpoutPlayer splayerLeg;
    private GenericListWidget clanList1;

    public NoClanMenue(LegendaryClans legPlugin, SpoutPlayer splayer) {
        this.legPlugin = legPlugin;
        splayerLeg = splayer;
        open(legPlugin, splayer);
    }

    public void open(Plugin plugin, SpoutPlayer splayer) {
        /*
         * This shall include:
         * List with all clans 
         * and a select button that makes it possible to show the data of a certain clan
         *
         */                            
        int width = splayer.getMainScreen().getWidth();
        int height = splayer.getMainScreen().getHeight();
        popup = new GenericPopup();
        
        //title
        GenericLabel topLabel = new GenericLabel("Clan-Menu");
        topLabel.setScale(1);
        topLabel.setWidth(topLabel.getText().length()*8*(int)topLabel.getScale()).setHeight((int)topLabel.getScale()*15);
        
        topLabel.setX(width/2-topLabel.getWidth()/2).setY(5+topLabel.getHeight()/2);
        popup.attachWidget(plugin, topLabel);
        
        //clan list
        GenericListWidget clanList = new GenericListWidget();
        for (Clan clan : legPlugin.getClanManager().getRankedListKd()) {
            int rank = legPlugin.getClanManager().getRankedListKd().indexOf(clan) + 1;
            ListWidgetItem item = new ListWidgetItem(clan.getName(), LegendaryClans.parseColor("&9Rank: " + rank));
            clanList.addItem(item);
        }
        clanList.setWidth(75).setHeight(height - 40);

        clanList.setX(width / 4 - clanList.getWidth() / 2).setY(height / 2 - clanList.getHeight() / 2);
        popup.attachWidget(plugin, clanList);
        clanList1 = clanList;
        
        GenericButton selectButton = new GenericButton("Show info!"){

            @Override
            public void onButtonClick(ButtonClickEvent event) {
                super.onButtonClick(event);
                if(clanList1.getSelectedItem() != null){
                    Clan clan = legPlugin.getClanManager().searchClansByName(clanList1.getSelectedItem().getTitle());
                    if(clan != null){
                        popup.close();
                        ExtraClanScreen screen = new ExtraClanScreen(legPlugin, splayerLeg, popup, clan);
                    }
                }
            }
            
        };
        
        selectButton.setWidth(selectButton.getText().length()*8*(int)selectButton.getScale()).setHeight(15);
        selectButton.setX(width/2-selectButton.getWidth()/2+width/4);
        selectButton.setY(height/2 - selectButton.getHeight()/2);
        popup.attachWidget(plugin, selectButton); 
            

        
        splayer.closeActiveWindow();
        //attach popup to player screen
        splayer.getMainScreen().attachPopupScreen(popup);
    }
}
