/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans.clans;

import com.sk89q.worldedit.BlockVector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Clan {
    
    private String name;
    private double kdr;
    private int lvl;
    private int credits;
    private ClanTerr territory;
    private String clanFilePath;
    private LegendaryClans plugin;
    private BlockVector innerClanBaseMid;       
    private List <LegendaryPlayer> leaders = new ArrayList <LegendaryPlayer>();
    
    private List <LegendaryPlayer> members = new ArrayList<LegendaryPlayer>();
    
    private LegendaryPlayer founder;
    
    
    private static final String PATH_LEVEL = "Clan.Data.Level";
    private static final String PATH_CREDITS= "Clan.Data.Credits";
    
    private static final String PATH_TERRITORY_BLOCK1 = "Clan.Data.TerritoryBlock1";
    private static final String PATH_TERRITORY_BLOCK2 = "Clan.Data.TerritoryBlock2";
    private static final String PATH_INNERCLANBASE_MID = "Clan.Data.InnerClanBaseMid";
    
    private static final String PATH_KDR = "Clan.Data.KDR";
    private static final String PATH_MEMBERS = "Clan.Data.Members";
    private static final String PATH_LEADERS = "Clan.Data.Leaders";
    private static final String PATH_FOUNDER = "Clan.Data.Founder";

    //constructor for reading from File
    public Clan(String name, LegendaryClans plugin) {
        this.name = name;
        this.plugin=plugin;
        clanFilePath = "." + File.separator + "plugins" + File.separator + "LegendaryClans" + File.separator + "Clans" + File.separator + name+".yml";
        load(new File(clanFilePath));       
    }
    
    //create Clan out of Party
    public Clan(String name, LegendaryPlayer leader, LegendaryClans plugin, List<LegendaryPlayer> members){
        this.name = name;
        this.plugin = plugin;
        clanFilePath = "." + File.separator + "plugins" + File.separator + "LegendaryClans" + File.separator + "Clans" + File.separator + name+".yml";
        this.members = members;
        leaders.add(leader);
        founder = leader;
        lvl = 1;
        territory = new ClanTerr(this, plugin);
        innerClanBaseMid = new BlockVector(0,0,0);
        territory.setMidPoint(innerClanBaseMid);  
        int kills = 0;
        int deaths = 0;
        for(LegendaryPlayer p: members){
            kills += p.getKills();
            deaths += p.getDeaths();           
        }
        if(deaths >0){
            kdr = kills/deaths;
        }
        else {
            kdr = kills;
        }
        credits =0;
        save();
    }
       
    
    
    public boolean load(File file) {
        try {
            YamlConfiguration configFile = new YamlConfiguration();
            configFile.load(file);
            
            //getting values
            lvl = configFile.getInt(PATH_LEVEL);
            credits = configFile.getInt(PATH_CREDITS);
            kdr = configFile.getDouble(PATH_KDR);
            founder = plugin.getPlayerManager().searchPlayerByName(configFile.getString(PATH_FOUNDER));
            
            territory = new ClanTerr(this, plugin);
//            setupClanTerritory(configFile.getString(PATH_TERRITORY_BLOCK1), configFile.getString(PATH_TERRITORY_BLOCK2)); 
            fillMembers(configFile.getStringList(PATH_MEMBERS));
            fillLeaders(configFile.getStringList(PATH_LEADERS));
            setupMidPointFromConfig(configFile.getString(PATH_INNERCLANBASE_MID));
            recalcKdr();
            
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Clan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Clan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(Clan.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;   
    }
    
    /**
     * Saves a a clan to a file
     */
    public void save() {
        try {
            File file = new File(clanFilePath);
            YamlConfiguration configFile = new YamlConfiguration();

            //setting values
            configFile.set(PATH_LEVEL, lvl);
            configFile.set(PATH_KDR, kdr);
            configFile.set(PATH_CREDITS, credits);
            configFile.set(PATH_FOUNDER,founder.getName());

//            ProtectedRegion terr = territory.getClanTerritory();
//            configFile.set(PATH_TERRITORY_BLOCK1, terr.getMinimumPoint().getBlockX()+","+terr.getMinimumPoint().getBlockY()+","+terr.getMinimumPoint().getBlockZ());
//            configFile.set(PATH_TERRITORY_BLOCK2, terr.getMaximumPoint().getBlockX()+","+terr.getMaximumPoint().getBlockY()+","+terr.getMaximumPoint().getBlockZ());

            int x = territory.getClanBaseMid().getBlockX();
            int y = territory.getClanBaseMid().getBlockY();
            int z = territory.getClanBaseMid().getBlockZ();

            configFile.set(PATH_INNERCLANBASE_MID, x + "," + y + "," + z);

            List<String> names = new ArrayList<String>();
            List<String> names2 = new ArrayList<String>();

            for (LegendaryPlayer p : members) {
                names.add(p.getName());
            }

            for (LegendaryPlayer p : leaders) {
                names2.add(p.getName());
            }
            configFile.set(PATH_MEMBERS, names);
            configFile.set(PATH_LEADERS, names2);

            configFile.save(file);
        } catch (IOException ex) {
            Logger.getLogger(Clan.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
    
    /**
     * Sets the midpoint of the clan base, from what is written in the clan file
     * @param pos position in the file, will be split
     */
    public void setupMidPointFromConfig(String pos){
        int pos1X = Integer.parseInt(pos.split(",")[0]);
        int pos1Y = Integer.parseInt(pos.split(",")[1]);
        int pos1Z = Integer.parseInt(pos.split(",")[2]);
        innerClanBaseMid = new BlockVector(pos1X,pos1Y,pos1Z);
        territory.setMidPoint(innerClanBaseMid);   
    }
    
    /**
     * Needed for clan establishment.
     * @param pos1 minimum Position
     * @param pos2 maximum posistion, both not really needed atm.
     */
    public void setupClanTerritory(String pos1, String pos2){
//        int pos1X = Integer.parseInt(pos1.split(",")[0]);
//        int pos1Y = Integer.parseInt(pos1.split(",")[1]);
//        int pos1Z = Integer.parseInt(pos1.split(",")[2]);
//        
//        int pos2X = Integer.parseInt(pos2.split(",")[0]);
//        int pos2Y = Integer.parseInt(pos2.split(",")[1]);
//        int pos2Z = Integer.parseInt(pos2.split(",")[2]);
//        
//        BlockVector vector1 = new BlockVector(pos1X, pos1Y, pos1Z);
//        BlockVector vector2 = new BlockVector(pos2X, pos2Y, pos2Z);  
        
        territory = new ClanTerr(this, plugin);
        }
    
    
    public void fillMembers(List <String> memberListFromFile){
        for(String playerName: memberListFromFile){
            for(LegendaryPlayer player :plugin.getPlayerManager().getPlayers()){       
                if(player.getName().equals(playerName)){
                    members.add(player);
                }
            }
        }
    }
    
    public void fillLeaders(List <String> leaderListFromFile){
        for (String playerName : leaderListFromFile) {
            for (LegendaryPlayer player : plugin.getPlayerManager().getPlayers()) {
                if (player.getName().equals(playerName)) {
                    leaders.add(player);
                }
            }
        }
    }
    
    public boolean isLeader(LegendaryPlayer p){
        if(leaders.contains(p)){
            return true;
        }
        return false;
    }
        

    public int getCredits() {
        return credits;
    }

    public double getKdr() {
        double kills = 0;
        double deaths = 0;
        for (LegendaryPlayer p : members) {
            kills = kills+ p.getKills();
            deaths = deaths + p.getDeaths();
        }
        for (LegendaryPlayer p : leaders) {
            kills = kills + p.getKills();
            deaths = deaths + p.getDeaths();
        }
        double localKdr;
        if (deaths > 0) {
            localKdr = kills / deaths;
        } else {
            localKdr = kills;
        }
        kdr = localKdr;
        return roundTwoDecimals(localKdr);
    }
    
    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public int getLvl() {
        return lvl;
    }

    public String getName() {
        return name;
    }

    public ClanTerr getTerritory() {
        return territory;
    }
    
    public void setTerritory(){
        territory = new ClanTerr(this, plugin);
    }

    public List<LegendaryPlayer> getMembers() {
        return members;
    }

    public List<LegendaryPlayer> getLeaders() {
        return leaders;
    }    

    public void recalcKdr() {
        double kills = 0;
        double deaths = 0;
        for (LegendaryPlayer p : members) {
            kills = kills+ p.getKills();
            deaths = kills + p.getDeaths();
        }
        if (deaths > 0) {
            kdr = kills / deaths;
        } else {
            kdr = kills;
        }
    }
    
    public void broadcastMessage(String message) {
        for (LegendaryPlayer p : members) {
            if(p.isOnline()){
                CommandSender sender = (CommandSender) p.getPlayer();
                LegendaryClans.coloredOutput(sender, message);
            }
        }
        for (LegendaryPlayer p : leaders) {
            if(p.isOnline()){
                CommandSender sender = (CommandSender) p.getPlayer();
                LegendaryClans.coloredOutput(sender, message);
            }
        }
    }
    
    public void broadcastRawMessage(String message) {
        for (LegendaryPlayer p : members) {
            if (p.isOnline()) {
                CommandSender sender = (CommandSender) p.getPlayer();
                p.getPlayer().sendRawMessage(message);
            }
        }
        for (LegendaryPlayer p : leaders) {
            if (p.isOnline()) {
                CommandSender sender = (CommandSender) p.getPlayer();
                p.getPlayer().sendRawMessage(message);
            }
        }
    }
    
    public String getInnerClanBaseName(){
        return getName()+"InnerClanBase";
    }
    public String getShrineName(){
        return getName()+"Shrine";
    }
    
    
    public String getTerritoryName() {
        return getName() + "ClanTerritory";
    }
    
    public void establishClanTerr(LegendaryPlayer player){
        if(!territory.isSetup()){
            territory = new ClanTerr(this, plugin, player);
        }
    }
    
    public void lvlup(LegendaryPlayer leader){
        Economy iconomy = plugin.getiConomy();        
        if(canLvlUp(leader)){  // && money appropiate  
            credits -= ClanManager.getCreditPrices()[lvl-1];
            iconomy.bankWithdraw(getName(), ClanManager.getMoneyPrices()[lvl-1]);
            lvl++;
            //Disable automatic territory expansion to max.
			//territory.changeInnerBase((ClanManager.getMaxInnerClanBaseRange()[lvl-1]/2)-(territory.getInnerClanBaseSeize()/2));
            territory.changeTerritory((ClanManager.getMaxTerrRange()[lvl-1]/2)-(territory.getTerritorySeize()/2));
            playLvlUpSoundToAllMembers();
            broadcastMessage("&aThe clan has leveled up. Congratulations, your clan is now level "+lvl+".");
        }
        else{
            LegendaryClans.coloredOutput((CommandSender)leader.getPlayer(), "&4You do not meet the requirements to level up your clan.");
        }
    }
    
    public boolean hasEnoughCreditsToLvlUp(){
        if (credits >= ClanManager.getCreditPrices()[lvl - 1]){
            return true;
        }
        return false;
    }
    
    public boolean hasEnoughMoneyToLvlUp(LegendaryPlayer leader){
        Economy iconomy = plugin.getiConomy();
//        if(iconomy.has(leader.getName(), ClanManager.getMoneyPrices()[lvl - 1]) && lvl < ClanManager.getMaxLvl()){
//           return true; 
//        }
//        else{
//            return false;
//        }
        boolean have = false;
        if (iconomy.bankHas(getName(), ClanManager.getMoneyPrices()[lvl - 1]).balance >= ClanManager.getMoneyPrices()[lvl - 1]) {
            return true;
        }
        return false;
    }
    
    public boolean canLvlUp(LegendaryPlayer leader){
        Economy iconomy = plugin.getiConomy();
        boolean have = false;
        if(iconomy.bankHas(getName(), ClanManager.getMoneyPrices()[lvl - 1]).balance >=ClanManager.getMoneyPrices()[lvl - 1]){
            have = true;
        }
        if (credits >= ClanManager.getCreditPrices()[lvl - 1] && have && lvl<ClanManager.getMaxLvl()) {  // && money appropiate
            return true;
        } 
        return false;
    }
    
    public File getFile(){
        return new File(clanFilePath);
    }
    
    public void giveCredits(int amount){
        credits +=amount;
        String message = "";
        if(amount <= 0){
            message = "&4Your clan lost "+ -amount +" credits.";
        }else{
            message = "&2Your clan received "+ amount +" credits.";
        }   
        broadcastMessage(message);    
            
    }
    
    public void dismissMember(LegendaryPlayer player, LegendaryPlayer submitter){
        Clan clan = plugin.getClanManager().getClanByPlayer(player);
        if(members.contains(player) && getMemberCount()>2){
            if (player.isOnline()) {
                LegendaryClans.coloredOutput((CommandSender) player.getPlayer(), "&4" + submitter.getName() + " dismissed you from your clan.");
            }
            if (getTerritory().isSetup()) {
                plugin.removePlayerFromRegionAsMember("wild", getInnerClanBaseName(), player);
            }
            clan.broadcastMessage("&e"+player.getName()+" left the clan.");
            members.remove(player);
        }
        else if(leaders.contains(player) && leaders.size()>1 && !isFounder(player)){ 
            if(player.isOnline()){
                LegendaryClans.coloredOutput((CommandSender)player.getPlayer(),"&4"+submitter.getName()+" dismissed you from your clan."); 
            }
            if (getTerritory().isSetup()) {
                plugin.removePlayerFromRegionAsMember("wild", getInnerClanBaseName(), player);
            }
            clan.broadcastMessage("&e"+player.getName()+" left the clan.");
            leaders.remove(player);
        }
        else if(leaders.contains(player) && leaders.size()==1 && !isFounder(player)){
            plugin.getClanManager().dissolveClan(this);       
        }
    }
    
    public void promotePlayer(LegendaryPlayer toBePromoted, LegendaryPlayer promoter){
        Clan clan = plugin.getClanManager().getClanByPlayer(promoter);
        if(!clan.isLeader(toBePromoted) && clan.isLeader(promoter)){
            if(members.contains(toBePromoted)){    
                members.remove(toBePromoted);
                leaders.add(toBePromoted);
                clan.broadcastMessage("&2"+toBePromoted.getName()+" got promoted. He is a leader now.");
            }
            else{
                LegendaryClans.coloredOutput((CommandSender) promoter.getPlayer(), "&4That player isn´t on your clan!");
            }
        }
        else{
            LegendaryClans.coloredOutput((CommandSender)promoter.getPlayer(), "&4You can´t promote a leader!");
        }
    }
    
    public void promotePlayerAdmin(LegendaryPlayer toBePromoted, LegendaryPlayer promoter){
        Clan clan = plugin.getClanManager().getClanByPlayer(toBePromoted);
        if (!clan.isLeader(toBePromoted)) {
            if (members.contains(toBePromoted)) {
                members.remove(toBePromoted);
                leaders.add(toBePromoted);
                clan.broadcastMessage("&2" + toBePromoted.getName() + " got promoted. He is a leader now.");
            } else {
                LegendaryClans.coloredOutput((CommandSender) promoter.getPlayer(), "&4That player isn´t on your clan!");
            }
        } else {
            LegendaryClans.coloredOutput((CommandSender) promoter.getPlayer(), "&4You can´t promote a leader!");
        }
    }
    
    public void demotePlayer(LegendaryPlayer toBeDemoted, LegendaryPlayer demoter) {
        Clan clan = plugin.getClanManager().getClanByPlayer(demoter);
        if (clan.isLeader(toBeDemoted) && clan.isLeader(demoter) && !clan.isFounder(toBeDemoted)) {
            if (leaders.contains(toBeDemoted)) {
                leaders.remove(toBeDemoted);
                members.add(toBeDemoted);
                clan.broadcastMessage("&2" + toBeDemoted.getName() + " got demoted. He is a member now.");
            } else {
                LegendaryClans.coloredOutput((CommandSender) demoter.getPlayer(), "&4That player isn´t on your clan!");
            }
        } else {
            LegendaryClans.coloredOutput((CommandSender) demoter.getPlayer(), "&4You can´t demote a member or a founder!");
        }
    }
    
    /**
     * Use this method if u wanna add a person to the clan!
     * @param toBeInvited The player that shall be added to the clan
     */
    
    public void addMember(LegendaryPlayer toBeInvited){
        members.add(toBeInvited);
        if(territory.isSetup()){
            plugin.addPlayerToRegionAsMember("wild", getInnerClanBaseName(), toBeInvited);
        }
        broadcastMessage("&2"+toBeInvited.getName() + " joined the clan!");
    }
    
    public void leaveClan(LegendaryPlayer leaver){
        if(members.contains(leaver) && getMemberCount()>1){
            if(getTerritory().isSetup()){
                plugin.removePlayerFromRegionAsMember("wild", getInnerClanBaseName(), leaver);
            }
            members.remove(leaver);
            broadcastMessage("&4"+leaver.getName()+" left the clan.");
            LegendaryClans.coloredOutput((CommandSender) leaver.getPlayer(), "&4You have now left your clan!!");
        }
        else if(leaders.contains(leaver) && leaders.size()>1){
            leaders.remove(leaver);
            if(isFounder(leaver)){
                founder =  leaders.get(0);
                broadcastMessage("&4" + leaders.get(0).getName() + " is new founder.");
            }
            if (getTerritory().isSetup()) {
                plugin.removePlayerFromRegionAsMember("wild", getInnerClanBaseName(), leaver);
            }
            broadcastMessage("&4" + leaver.getName() + " left the clan.");
            if(leaver.isOnline()){
                LegendaryClans.coloredOutput((CommandSender) leaver.getPlayer(), "&4You have now left your clan!!");
            }
        }
        else if(leaders.contains(leaver) && leaders.size()== 1){
            plugin.getClanManager().dissolveClan(this);
        }
    }
    
    public int getMemberCount(){
        return members.size()+leaders.size();
    }

    public LegendaryPlayer getFounder() {
        return founder;
    }

    public void setFounder(LegendaryPlayer founder) {
        this.founder = founder;
    }
    
    public boolean isFounder(LegendaryPlayer player){
        if(founder != null){
            if(founder.equals(player)){
                return true;
            }
        }
        return false;
    }
    
    public void changeCredits(int amount){
        credits += amount;
    }
    
    
    public void playLvlUpSoundToAllMembers(){
        String url = plugin.getCfg().getClanLvlUpSoundURL();
        if(LegendaryClans.URLexists(url)){
            for(LegendaryPlayer m:members){
                if(m.isOnline()){
                    SpoutPlayer splayer = SpoutManager.getPlayer(m.getPlayer());
                    SpoutManager.getSoundManager().playCustomSoundEffect(plugin, splayer,url, false);
                }
            }
            for(LegendaryPlayer l:leaders){
                if (l.isOnline()) {
                    SpoutPlayer splayer = SpoutManager.getPlayer(l.getPlayer());
                    SpoutManager.getSoundManager().playCustomSoundEffect(plugin, splayer, url, false);
                }
            }
        }
        else{
            plugin.log("Clan level up sound is not in a correct format. Inform Hawk!");
        }
    }
   
    
    
    
    
}
