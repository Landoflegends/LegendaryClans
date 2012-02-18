/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans.listeners;

import me.blackhawklex.gui.TestMenue;
import me.blackhawklex.legendaryclans.LegendaryClans;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.event.input.RenderDistanceChangeEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;
//import org.spoutcraft.spoutcraftapi.gui.Keyboard;
//import org.spoutcraft.spoutcraftapi.gui.ScreenType;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class Listener_Input extends InputListener {
    
    private LegendaryClans plugin;

    public Listener_Input(LegendaryClans plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onKeyPressedEvent(KeyPressedEvent event) {
        if(event.getScreenType().compareTo(ScreenType.GAME_SCREEN) == 0){
            if(event.getKey().compareTo(Keyboard.KEY_L)== 0){
//               TestMenue.open(plugin,event.getPlayer());
            }
        }
    }

    @Override
    public void onKeyReleasedEvent(KeyReleasedEvent event) {
    }

    @Override
    public void onRenderDistanceChange(RenderDistanceChangeEvent event) {
    }
    
    
    
}
