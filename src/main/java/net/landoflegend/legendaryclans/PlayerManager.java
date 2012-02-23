/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.legendaryclans;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.landoflegend.legendaryclans.clans.Clan;
import net.landoflegend.legendaryclans.clans.ClanTerr;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerManager {
    
    private List <LegendaryPlayer> players = new ArrayList <LegendaryPlayer>();
    private LegendaryClans plugin;
    private static final String directoryPath = "." + File.separator + "plugins" + File.separator + "LegendaryClans" + File.separator + "Players" + File.separator;
    
    public PlayerManager(LegendaryClans plugin){
        this.plugin=plugin;
        
        addAllExistingPlayers();
        checkWhoIsOnline();
    }
    
   

    public void addAllExistingPlayers() {
        File dir = new File(directoryPath);
        File[] allPlayerFiles = dir.listFiles();
        if (allPlayerFiles != null) {
            for (File f : allPlayerFiles) {
                if (!f.isDirectory()) {
                    LegendaryPlayer player = new LegendaryPlayer(f.getName().replace(".yml",""), plugin);
                    addPlayer(player); //UNSURE!!!!!!!!!
                }
            }
        }
    }
    
    public void checkWhoIsOnline(){
        for(LegendaryPlayer p:players){
            for(Player player: plugin.getServer().getOnlinePlayers()){
                if(p.getName().equals(player.getName())){
                    p.setOnline(true);
                    p.setPlayer(player);
                }
            }
        }
    }

    public void addPlayer(LegendaryPlayer player) {
        players.add(player);
    }

    public LegendaryPlayer searchPlayerByName(String name) {
        for (LegendaryPlayer p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;

    }

    public List<LegendaryPlayer> getPlayers() {
        return players;
    }
        
    public void createNewPlayer(Player player) {
        LegendaryPlayer newPlayer = new LegendaryPlayer(player);
        newPlayer.setOnline(true);
        newPlayer.setPlayer(player);
        addPlayer(newPlayer);
        
        //add the new player to all existing clan territories, so that he can build there
        List<ClanTerr> allTerrs = plugin.getClanManager().getAllClanTerritories();
        for(ClanTerr terr:allTerrs){
            if(terr.isSetup()){
                plugin.addPlayerToRegionAsMember("wild", terr.getClan().getTerritoryName(), newPlayer);
            }
        }
        
        //add the new player to the clansacrification zone.
        plugin.addPlayerToRegionAsMember("salva", "blocksacrification", newPlayer);
        
        plugin.log("Successfully created player "+player.getName());
    }
    
    public void saveAllPlayers() {
        for (LegendaryPlayer legPlayer : players) {
            legPlayer.save();
        }
    }
    
    public void broadcastMessage(String message){
        for(LegendaryPlayer player:players){
            if(player.isOnline()){
                LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), message);
            }
        }
    }
    
    public void broadcastMessageToNonIgnoring(String message) {
        for (LegendaryPlayer player : players) {
            if (player.isOnline()) {
                if(!player.isIgnoreChat()){
                    LegendaryClans.coloredOutput((CommandSender) player.getPlayer(), message);
                }
            }
        }
    }
    
    public List<LegendaryPlayer> getDonsOver1(){
        List <LegendaryPlayer> playerDons = new ArrayList <LegendaryPlayer>();
        for(LegendaryPlayer p:players){
            if(p.getDonationType()>1){
                playerDons.add(p);
            }
        }
        return playerDons;
    }
    
}
