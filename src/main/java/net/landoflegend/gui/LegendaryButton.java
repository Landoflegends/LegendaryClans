/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.gui;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericPopup;

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
