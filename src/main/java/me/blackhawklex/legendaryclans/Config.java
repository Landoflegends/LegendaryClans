/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class Config {
    private LegendaryClans plugin;
    private FileConfiguration config;
    
    private static final String PATH_SOUNDS_LOGON="Sounds.Login";
    private static final String PATH_SOUNDS_CLANLEVELUP="Sounds.ClanLvlUp";
    private static final String PATH_SOUNDS_SOULABSORB="Sounds.SoulAbsorb";
    
    private static final String PATH_GUARDIAN_STONE_DESTINATION ="Teleporation.WatchStonePosition";
    private static final String PATH_TELEPORTATION_LOCS = "Teleporation.WildPorts";
    private static final String PATH_DUNGEON_DAMAGE_LIST = "Dungeons.List";
    
    private Location watchStoneLoc;
    private String logonSoundURL;
    private String clanLvlUpSoundURL;
    private String soulAbsorbSoundURL;
    
    private List <BlackLocation> teleportLocs = new ArrayList<BlackLocation>();
    
    public Config(LegendaryClans plugin){
        this.plugin = plugin;
        config = plugin.getConfig();
        setupWatchStoneLoc();
        setupTeleportLocs();
        logonSoundURL = config.getString(PATH_SOUNDS_LOGON);
        clanLvlUpSoundURL = config.getString(PATH_SOUNDS_CLANLEVELUP);
        soulAbsorbSoundURL = config.getString(PATH_SOUNDS_SOULABSORB);
    }
    
    public void setupWatchStoneLoc(){
        try {
            String watchStoneRawLoc = config.getString(PATH_GUARDIAN_STONE_DESTINATION);
            String[] loc = watchStoneRawLoc.split(":");
            watchStoneLoc = new Location(plugin.getServer().getWorld("salva"), Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2])); 
        } catch (Exception e) {
            plugin.log("ALERT: Watchstone location is not in a correct format! Inform Hawk!");
        }
  
    }
    
    public void saveChanges(){
        
    }

    public String getClanLvlUpSoundURL() {
        return clanLvlUpSoundURL;
    }

    public String getLogonSoundURL() {
        return logonSoundURL;
    }

    public String getSoulAbsorbSoundURL() {
        return soulAbsorbSoundURL;
    }

    public Location getWatchStoneLoc() {
        return watchStoneLoc;
    }
    
    public void setWatchStoneLoc(int x, int y, int z){
        watchStoneLoc = new Location(plugin.getServer().getWorld("salva"),x,y,z);
        plugin.getConfig().set(PATH_GUARDIAN_STONE_DESTINATION, x + ":" +y+":"+z);
        plugin.saveConfig();
    }
    
    // Penis:salva:15:15:15
    public void setupTeleportLocs(){
        List<String> rawLocs = config.getStringList(PATH_TELEPORTATION_LOCS);
        if(rawLocs != null){
            for(String s: rawLocs){
                String name = s.split(":")[0];
                String world = s.split(":")[1];
                try {
                    int x = Integer.parseInt(s.split(":")[2]);
                    int y = Integer.parseInt(s.split(":")[3]);
                    int z = Integer.parseInt(s.split(":")[4]);
                    BlackLocation loc = new BlackLocation(plugin.getServer().getWorld(world),x,y,z,name);
                    teleportLocs.add(loc);
                } catch (Exception e) {
                    plugin.log("Teleportation locations not setup correctly. Check the config and inform Hawk!");
                }
             }
        }
    }
    
    public void addTelLoc(BlackLocation loc){
        teleportLocs.add(loc);
        saveTelLocs();
    }
    
    public void removeTelLocByName(String name){
        BlackLocation delLoc = null;
        for(BlackLocation loc:teleportLocs){
            if(loc.getName().equalsIgnoreCase(name)){    
                delLoc = loc;
            }
        }
        teleportLocs.remove(delLoc);
    }

    public List<BlackLocation> getTeleportLocs() {
        return teleportLocs;
    }
    
    public BlackLocation getLocByName(String name){
        for(BlackLocation loc: teleportLocs){
            if(loc.getName().equalsIgnoreCase(name)){
                return loc;
            }
        }
        return null;
    }
    
    public void saveTelLocs(){
        List <String> locsString = new ArrayList <String>();
        for(BlackLocation loc: teleportLocs){
            locsString.add(loc.getName()+":"+loc.getWorld().getName()+":"+loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ());
        }
        plugin.getConfig().set(PATH_TELEPORTATION_LOCS,locsString);
        plugin.saveConfig();
    }
    
}
