/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans.clans;

//import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class ClanTerritory extends ProtectedCuboidRegion{
    
    public ClanTerritory(String id,BlockVector smallV, BlockVector bigV){       
        super(id,smallV, bigV);     
    }
    
}
