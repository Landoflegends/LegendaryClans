/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans.clans;

//import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class ClanTerritory extends ProtectedCuboidRegion{
    
    public ClanTerritory(String id,BlockVector smallV, BlockVector bigV){       
        super(id,smallV, bigV);     
    }
    
}
