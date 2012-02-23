/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans;

import org.bukkit.Location;
import org.bukkit.World;

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
