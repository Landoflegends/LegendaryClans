/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans.pvp;

import com.sk89q.worldedit.Vector;
import java.util.Random;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import me.blackhawklex.legendaryclans.clans.ClanManager;
import me.blackhawklex.legendaryclans.clans.ClanTerr;
import org.bukkit.Location;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class DeathManager {
    
    private LegendaryPlayer deadPlayer;
    private LegendaryPlayer killer;
    private LegendaryClans plugin;
    
    private String[] deathMessages = {"got his head chopped of by ", "was smashed in the back by ", "got his entrails put over his body by ", "was slain by "
                                     , "got brutally massacred by "};
    private String[] killMessages = {"chopped the head off of ","smashed ","massacred ","slained ","slaughtered "};
    
    private int[] credits = {1,2,3};
    
    public DeathManager(LegendaryPlayer killer, LegendaryPlayer deadPlayer, LegendaryClans plugin){
        this.deadPlayer = deadPlayer;
        this.killer = killer;
        this.plugin = plugin;
        managerKdrs();
    }
    
    public  void managerKdrs(){
        ClanManager clanManager = plugin.getClanManager();
        Clan clanOfDeadPlayer = clanManager.getClanByPlayer(deadPlayer);
        Clan clanOfKiller = clanManager.getClanByPlayer(killer);
        deadPlayer.getKilled();
        deadPlayer.save();
        killer.kill();
        killer.save();
        
        Random generator = new Random();
        int num = generator.nextInt(deathMessages.length-1);
        
        if(clanOfDeadPlayer != null){
            clanOfDeadPlayer.recalcKdr();
            clanOfDeadPlayer.save();
                if(clanOfKiller != null){
                    if(!clanOfKiller.equals(clanOfDeadPlayer)){
                        String deathMessage = "&9"+deadPlayer.getName() + " &c"+deathMessages[num] + "&d"+killer.getName() + " &cof Clan: &d" + clanOfKiller.getName();
                        clanOfDeadPlayer.broadcastMessage(deathMessage);
                    }
                    else{
                       String warning = "&4" + killer.getName() + " team-killed "+ deadPlayer.getName();
                       clanOfDeadPlayer.broadcastMessage(warning); 
                    }
                }
                else{
                    String deathMessage = "&9" + deadPlayer.getName() + " &c" + deathMessages[num] + "&d" + killer.getName() + " &cof Clan: &d" + "None";
                    clanOfDeadPlayer.broadcastMessage(deathMessage); 
                }
        }
        if(clanOfKiller !=null){
            clanOfKiller.recalcKdr();
            clanOfKiller.save();
            if(clanOfDeadPlayer != null){
                if(!clanOfKiller.equals(clanOfDeadPlayer)){
                    String killMessage = "&9"+killer.getName() + " &2"+killMessages[num] + "&d"+deadPlayer.getName() + " &2of Clan: &d" + clanOfDeadPlayer.getName();
                    clanOfKiller.broadcastMessage(killMessage);
                }
                else {
                    String warning = "&4" + killer.getName() + " team-killed " + deadPlayer.getName();
                    clanOfDeadPlayer.broadcastMessage(warning);
                }
            }
            else{
                String killMessage = "&9" + killer.getName() + " &2" + killMessages[num] + "&d" + deadPlayer.getName() + " &2of Clan: &d" + "None";
                clanOfKiller.broadcastMessage(killMessage);  
            }
        }
        
        if(clanOfKiller != null && clanOfDeadPlayer != null){
            manageTerritories();
            manageCredits();
        }
    }
    
    //manage Territory overtaking 
    public void manageTerritories(){
        ClanManager clanManager = plugin.getClanManager();
        Clan clanOfDeadPlayer = clanManager.getClanByPlayer(deadPlayer);
        Clan clanOfKiller = clanManager.getClanByPlayer(killer);
        
        ClanTerr terrOfDeadPlayerClan = clanOfDeadPlayer.getTerritory();
        ClanTerr terrOfKillerClan = clanOfKiller.getTerritory();
        
        if(terrOfDeadPlayerClan.isSetup() && terrOfKillerClan.isSetup()){
            
            Location killerLoc = killer.getPlayer().getLocation();
            Vector killerPos = new Vector(killerLoc.getBlockX(),killerLoc.getBlockY(),killerLoc.getBlockZ());
            
            Location deadPlayerLoc = deadPlayer.getPlayer().getLocation();
            Vector deadPlayerPos = new Vector(deadPlayerLoc.getBlockX(), deadPlayerLoc.getBlockY(), deadPlayerLoc.getBlockZ());
            
            if(terrOfDeadPlayerClan.getClanTerritory().contains(killerPos) && terrOfDeadPlayerClan.getClanTerritory().contains(deadPlayerPos)){
                terrOfDeadPlayerClan.addLoosingPoint(1);
            }
            
//            if(terrOfKillerClan.getClanTerritory().contains(killerPos) && terrOfKillerClan.getClanTerritory().contains(deadPlayerPos)){
            terrOfKillerClan.addDefendingPoint(1);
//            }
        }
    }
    
    public void manageCredits(){
        ClanManager clanManager = plugin.getClanManager();
        Clan clanOfDeadPlayer = clanManager.getClanByPlayer(deadPlayer);
        Clan clanOfKiller = clanManager.getClanByPlayer(killer);
        
        if(!clanOfDeadPlayer.equals(clanOfKiller)){
            Random generator = new Random();
            int num = generator.nextInt(credits.length - 1);

            clanOfDeadPlayer.giveCredits(-credits[num]);
            clanOfKiller.giveCredits(credits[num]);
        }
        else{
            clanOfDeadPlayer.giveCredits(-50);
        }
    }
    
}
