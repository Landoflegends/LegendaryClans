/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans.clans;

import com.sk89q.worldedit.BlockVector;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.party.PartyManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;



/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class ClanManager {
    
    private LegendaryClans plugin;
    private static final String directoryPath = "." + File.separator + "plugins" + File.separator + "LegendaryClans" + File.separator +"Clans"+File.separator;
    private List <Clan> clans = new ArrayList<Clan>();
    
	// Clan levels and level configurations:
	// Testing, added 6th level here -dionnsai
    private static final int[] creditPrices = {2000,6000,11000,19000,36000}; 
    private static final int[] moneyPrices = {2600,7800,14300,24700,45000}; 
    private static final int[] maxTerrRange = {40,54,64,70,84,104};
    private static final int[] maxInnerClanBaseRange ={26,40,50,56,70,90};
    private static final int[] maxMembers ={10,20,30,35,50,65};
    private static final int maxLvl = 6;
   
    public ClanManager (LegendaryClans plugin){
        this.plugin = plugin;
        addAllExistingClans();
    }
    
    public void addAllExistingClans() {
        File dir = new File(directoryPath);
        File[] allClanFiles = dir.listFiles();
        if (allClanFiles != null) {
            for (File f : allClanFiles) {
                if (!f.isDirectory()) {
                    Clan clan = new Clan(f.getName().replace(".yml",""),plugin);
                    addClan(clan);
                }
            }
        }
    }
    
    public int getRank(Clan clan){
        return getRankedListKd().indexOf(clan)+1;
    }

    public void addClan(Clan clan) {
        clans.add(clan);
    }

    public Clan searchClansByName(String name) {
        for (Clan c : clans) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public List<Clan> getClans() {
        return clans;
    }
     
    public void saveAllClans(){
        for(Clan clan: clans){
            clan.save();
        }
    }
    
    public Clan getClanByPlayer(LegendaryPlayer player) {
        for (Clan p : clans) {
            for (LegendaryPlayer player2 : p.getMembers()) {
                if (player2.getName().equalsIgnoreCase(player.getName())) {
                    return p;
                }
            }
            for (LegendaryPlayer player2 : p.getLeaders()) {
                if (player2.getName().equalsIgnoreCase(player.getName())) {
                    return p;
                }
            }
        }
        return null;
        
    }
    
    public List<Clan> getRankedListLevel(){
        List <Clan> printableClans = new ArrayList<Clan>(); 
        printableClans.addAll(clans);
        Collections.sort(printableClans,  
                new Comparator() {

                    @Override
                    public int compare(Object o1, Object o2) {
                        int level1 = ((Clan) o1).getLvl();
                        int level2 = ((Clan) o2).getLvl();
                        if(level1<level2){
                            return 1;
                        }
                        else if(level2<level1){
                            return -1;
                        }
                        return 0;
                    }       
                });
        return printableClans;
    }
    
    public List<Clan> getRankedListKd() {
        List<Clan> printableClans = new ArrayList<Clan>();
        printableClans.addAll(clans);
        Collections.sort(printableClans,
                new Comparator() {

                    @Override
                    public int compare(Object o1, Object o2) {
                        double kdr1 = ((Clan) o1).getKdr();
                        double kdr2 = ((Clan) o2).getKdr();
                        if (kdr1 < kdr2) {
                            return 1;
                        }
                        if (kdr1 > kdr2) {
                            return -1;
                        }
                        return 0;
                    }
                });
        return printableClans;
    }
    
    public List<ClanTerr> getAllClanTerritories(){
        List<ClanTerr> territories = new ArrayList <ClanTerr>();
        for(Clan clan: clans){
            territories.add(clan.getTerritory());
        }
        return territories;
    }
    
    public void dissolveClan(Clan clan){
        if(clans.contains(clan)){
            int index = clans.indexOf(clan);
            String dissolveMessage = "&4"+clan.getName()+" has been dissolved!";
            plugin.getPlayerManager().broadcastMessage(dissolveMessage);
            clan.getTerritory().wipeBaseAndTerritory();
            clans.remove(clan);
            clan.getFile().delete();
        }
    }
    
    public Clan getClanByName(String name) {
        for (Clan clan : clans) {
            if (clan.getName().equalsIgnoreCase(name)) {
                return clan;
            }
        }
        return null;
    }
    
    public boolean isNameAlreadyInUse(String name){
        for(Clan clan:clans){
            if(clan.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
    
	//Clan creation 
    public void foundClan(LegendaryPlayer leader, String clanName){
        PartyManager pManager = plugin.getPartyManager();
        if(pManager.checkIfIsLeader(leader)){
	//Check party members and souls
	//Reduced party requirement to 3 for beta purposes, raise back to 5 when we finish beta.
		if(pManager.getPartyByLeader(leader).getMemberCount() > 2 && plugin.getiConomy().has(leader.getName(), 1000)){
                boolean anyoneGotClan = false;
                for(LegendaryPlayer player:pManager.getPartyByLeader(leader).getMembers()){
                    if(plugin.getClanManager().getClanByPlayer(player) != null){
                        anyoneGotClan = true;
                    }
                }
                if(!anyoneGotClan){
                    List<LegendaryPlayer> members = pManager.getPartyByLeader(leader).getMembers();
                    LegendaryPlayer realLeader = null;
                    for(LegendaryPlayer posLeader: members){
                        if(pManager.getPartyByLeader(posLeader) != null){
                            realLeader = posLeader;
                        }
                    }
                    if(realLeader != null){
                        members.remove(realLeader);
                    }
                    Clan clan = new Clan(clanName, leader, plugin,members);
                    addClan(clan);
                    plugin.getiConomy().withdrawPlayer(leader.getName(), 1000);
                    LegendaryClans.coloredOutput((CommandSender)leader.getPlayer(), "&2You lost 1000 souls for creating this clan!");
                    clan.broadcastMessage("&6You are now in the new founded clan: "+clan.getName());
                    plugin.getPlayerManager().broadcastMessage("&6Clan "+clan.getName()+ " has been founded by "+leader.getName()+"!");
                    pManager.removeParty(pManager.getPartyByLeader(leader));
                }
                else{
                    LegendaryClans.coloredOutput((CommandSender)leader.getPlayer(), "&4Some of the players in you party already have a clan.");
                }
            }
            else{
                LegendaryClans.coloredOutput((CommandSender)leader.getPlayer(), "&4You don´t have enough party members to found a clan. You need at least 4. Or you don´t have enough souls. You need at least 1000 souls.");
            }
            
        }
        else{
            LegendaryClans.coloredOutput((CommandSender) leader.getPlayer(), "&4You are not a party leader. To create a clan you have to be the party leader");
        }
    }

	//Checking distance to other clans.
    public boolean isConflictingOtherArea(LegendaryPlayer setter) {
        Location loc = setter.getPlayer().getLocation();
        BlockVector midPoint = new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        List<ClanTerr> territories = getAllClanTerritories();
        for (ClanTerr terr : territories) {
            if (terr.isSetup()) {
                BlockVector midPointTerr = terr.getClanBaseMid();
//                int x = midPoint.getBlockX() - midPointTerr.getBlockX();
//                int y = midPoint.getBlockY() - midPointTerr.getBlockY();
//                int z = midPoint.getBlockZ() - midPointTerr.getBlockZ();
//                BlockVector difVector = new BlockVector(x,y,z);
//					Raised distance to 300 from 100 - dionnsai
                if (midPoint.distance(midPointTerr) < 300) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int[] getCreditPrices() {
        return creditPrices;
    }

    public static int[] getMoneyPrices() {
        return moneyPrices;
    }

    public static int[] getMaxInnerClanBaseRange() {
        return maxInnerClanBaseRange;
    }

    public static int[] getMaxTerrRange() {
        return maxTerrRange;
    }

    public static int getMaxLvl() {
        return maxLvl;
    }

    public static int[] getMaxMembers() {
        return maxMembers;
    }
    
    
    
    
    
}
