/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.gui;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericPopup;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class LegendaryButton extends GenericButton {
    
    private String text;
    private GenericPopup popup;
    
    public LegendaryButton(String text, GenericPopup popup){
        super(text);
        this.text = text;
        this.popup = popup;
    }

    public GenericPopup getPopup() {
        return popup;
    }
    
    
    
    
}
