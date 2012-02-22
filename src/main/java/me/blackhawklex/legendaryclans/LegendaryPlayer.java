/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.blackhawklex.gui.TeleportationBar;
import me.blackhawklex.legendaryclans.clans.Clan;
import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.AppearanceManager;

public class LegendaryPlayer {
    private String playerPath;
    
    private static final String PATH_CLAN ="Player.Clan";
    private static final String PATH_DEATH="Player.Deaths";
    private static final String PATH_KILLS ="Player.Kills";
    
    private static final String PATH_LASTLOGIN ="Player.LastLogin";
    
    private static final String PATH_DONATIONTIME ="Donation.Time";
    private static final String PATH_DONATIONTYPE ="Donation.Type";
    private static final String PATH_DONATIONREMAINING_EXTRATIME = "Donation.Extratime";
    
    
    private LegendaryClans plugin;
    private boolean online = false;
    private boolean ignoreChat = false;
    private long lastLoginTime = 0;
    
//    private Clan clan;
    private Player player;
    private String name;
    private double kdr;
    private double deaths;
    private double kills;
    
    // 0 = no donation 
    // 1 = DT1            ;
    // 2 = DT2          
    // 3 = DT3
    // 4 = DT4
    // 5 = DT5
    
    private int donationType = 0;
    private long donationTime = 0;
    private long donationExtraTime = 0;
    // 0 global:
    // 1 local 100 blocks
    // 2 party 
    // 3 clan;
    private int chat = 1;
    
    //destination 0 = nothing;
    //destination 1 = home stone 
    //destination 2 = clan base 
    
    private int destination = 0;
    
    private int teleportingStatus = 100;
    private boolean teleporting;
    private TeleportationBar teleportingBar;
    
    public LegendaryPlayer(Player player){
        name = player.getName();
//        clan = null;
        this.player = player;
        online = true;
        deaths = 0;
        kills = 0;
        kdr = 0;
        playerPath = playerPath = "." + File.separator + "plugins" + File.separator + "LegendaryClans" + File.separator + "Players" + File.separator + name+ ".yml";
        save();        
        
    }

    public LegendaryPlayer(Player player, String name, double kdr, int deaths, int kills,LegendaryClans plugin) {   
        this.plugin = plugin;
//        this.clan = clan;
        this.player = player;
        this.name = name;
        this.kdr = kdr;
        this.deaths = deaths;
        this.kills = kills;       
        
        playerPath = playerPath = "." + File.separator + "plugins" + File.separator + "LegendaryClans" + File.separator + "Players" + File.separator + name+ ".yml";
    }
    
    public LegendaryPlayer(String name,LegendaryClans plugin){
        this.plugin=plugin;
        this.name=name;
        
        playerPath = playerPath = "." + File.separator + "plugins" + File.separator + "LegendaryClans" + File.separator + "Players" + File.separator + name + ".yml";
        
        load(new File(playerPath));
//        if(deaths != 0){
//            kdr = kills/deaths;
//        }
//        else{
//            kdr = 0;
//        }
    }
    
    /**
     * Saves a session to a file
     */
    public void save() {
        try {
            File file = new File(playerPath);
            YamlConfiguration playerFile = new YamlConfiguration();

            //setting values
//            if(clan != null){
//              playerFile.set(PATH_CLAN, clan.getName());  
//            }
//            else{
//                playerFile.set(PATH_CLAN, null);
//            }
            playerFile.set(PATH_DEATH, deaths);
            playerFile.set(PATH_KILLS, kills);
            playerFile.set(PATH_LASTLOGIN,lastLoginTime);
            playerFile.set(PATH_DONATIONTIME, donationTime);
            playerFile.set(PATH_DONATIONTYPE, donationType);
            playerFile.set(PATH_DONATIONREMAINING_EXTRATIME, donationExtraTime);

            playerFile.save(new File(playerPath));
        } catch (IOException ex) {
            Logger.getLogger(LegendaryPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public boolean load(File file) {
        try {
            YamlConfiguration playerFile = new YamlConfiguration();
            playerFile.load(file);

            //getting values
            String clanName = playerFile.getString(PATH_CLAN);
//            clan = plugin.getClanManager().searchClansByName(clanName);
            
            deaths = playerFile.getInt(PATH_DEATH);
            kills = playerFile.getInt(PATH_KILLS);
            lastLoginTime = playerFile.getLong(PATH_LASTLOGIN);
            donationTime = playerFile.getLong(PATH_DONATIONTIME);
            donationType = playerFile.getInt(PATH_DONATIONTYPE);
            donationExtraTime = playerFile.getLong(PATH_DONATIONREMAINING_EXTRATIME);
            
            recalcKdr();
            

            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LegendaryPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LegendaryPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(LegendaryPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public double getDeaths() {
        return deaths;
    }

    public double getKdr() {
        if (deaths == 0) {
            kdr = kills;
            
        }
        else{
            kdr = (kills/deaths);
        }
        return roundTwoDecimals(kdr);
    }
    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public double getKills() {
        return kills;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }

    public Player getPlayer() {
        return player;
    }
    
//    public Clan getClan(){
//        return plugin.getClanManager().getClanByPlayer(this);
//    }
    
//    public boolean isLeader(){
//        if(getClan().isLeader(this)){
//            return true;
//        }
//        return false;
//    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public void setPlayer (Player player){
        this.player=player;
    }
    
    public String getStatus(){
        if(!online){
            return "&4Offline";
        }
        return "&2Online";
    }
    
    public void getKilled(){
        deaths++;
        //recalcKdr();
    }
    
    public void kill(){
        kills++;
        //recalcKdr();
    }
    
    public void recalcKdr(){
        if(deaths >0){
            kdr = kills/deaths;
        }
        else{
            kdr = kills;
        }
    }

    public boolean isTeleporting() {
        return teleporting;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    public TeleportationBar getTeleportingBar() {
        return teleportingBar;
    }

    public void setTeleportingBar(TeleportationBar teleportingBar) {
        this.teleportingBar = teleportingBar;
    }
    
    public void stopTeleporting(){
        teleportingBar.deleteAll();
        teleportingBar = null;
        teleporting  = false;
        SpoutManager.getPlayer(player).closeActiveWindow();
    }
    
//    public void startTeleporting(){
//        teleportingStatus = 0;
//        if (getTeleportingBar() != null) {
//            SpoutManager.getPlayer(getPlayer()).getMainScreen().attachWidget(plugin,teleportingBar);         
//        }
//    }

    public int getDestination() {
        return destination;
    }
    
    public void setDestination (int dest){
        destination = dest;
    }
    
    public void teleportToDestination(){
        if(destination != 0){
            if (getDestination() == 1) {
                 HumanNPC watchStoneHolder = (HumanNPC) CitizensManager.getNPC(2);
                 if(watchStoneHolder != null){
                     Location loc = new Location(watchStoneHolder.getWorld(), watchStoneHolder.getBaseLocation().getX()-2, watchStoneHolder.getBaseLocation().getY(), watchStoneHolder.getBaseLocation().getZ());
                     plugin.getServer().getPlayer(name).teleport(loc);
                     LegendaryClans.coloredOutput((CommandSender)player, "&2 Successfully warped to watch stone. Welcome at Salva!");
                 }
                 
            }
            if (getDestination() == 2) {
                Clan clan = plugin.getClanManager().getClanByPlayer(this);
                if(clan != null){
                    if(clan.getTerritory().isSetup()){
                        Location loc = new Location(plugin.getServer().getWorld("wild"), clan.getTerritory().getClanBaseMid().getBlockX()+2, clan.getTerritory().getClanBaseMid().getBlockY(), clan.getTerritory().getClanBaseMid().getBlockZ()+2);
                        plugin.getServer().getPlayer(name).teleport(loc);
                        LegendaryClans.coloredOutput((CommandSender)player, "&2 Successfully warped to clan base.");
                    }
                }               
            }
            teleporting = false;
        }
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }  
    
    public void setCape(Clan clan) {
//        Clan clan = plugin.getClanManager().getClanByPlayer(this);
        if (clan != null) {
            String url = "http://dl.dropbox.com/u/26007278/Textures/Capes/" + clan.getName() + ".png";
            if (LegendaryClans.URLexists(url)){
                AppearanceManager am = SpoutManager.getAppearanceManager();
                am.setGlobalCloak(player, url);
            }
        }

    }

    public int getChat() {
        return chat;
    }

    public void setChat(int chat) {
        this.chat = chat;
    }

    public long getDonationTime() {
        return donationTime;
    }

    public int getDonationType() {
        return donationType;
    }

    public void setDonationTime(long donationTime) {
        this.donationTime = donationTime;
    }

    public void setDonationType(int donationType) {
        this.donationType = donationType;
    }

    public long getDonationExtraTime() {
        return donationExtraTime;
    }

    public void addDonationExtraTime(long amount) {
        donationExtraTime = donationExtraTime+ amount;
    }
    
    public void setDonationExtraTime(long amount) {
        donationExtraTime = amount;
    }

    public boolean isIgnoreChat() {
        return ignoreChat;
    }

    public void setIgnoreChat(boolean ignoreChat) {
        this.ignoreChat = ignoreChat;
    }
    
    
    
    
    
}
