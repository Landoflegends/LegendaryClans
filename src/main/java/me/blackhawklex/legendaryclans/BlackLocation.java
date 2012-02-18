/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans;

import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class BlackLocation extends Location{
    
    private String name;

    public BlackLocation(World world, double x, double y, double z, String name) {
        super(world,x,y,z);
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    
}
