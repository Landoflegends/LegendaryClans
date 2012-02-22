/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans;

import com.raphfrk.bukkit.loginqueue2.LoginQueue;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import me.blackhawklex.donations.DonationManager;
import me.blackhawklex.legendaryclans.clans.ClanManager;
import me.blackhawklex.legendaryclans.commands.CommandExecutor_AdminClanCommands;
import me.blackhawklex.legendaryclans.commands.CommandExecutor_Chat;
import org.bukkit.plugin.PluginDescriptionFile;
import me.blackhawklex.legendaryclans.commands.CommandExecutor_Clan;
import me.blackhawklex.legendaryclans.commands.CommandExecutor_Donations;
import me.blackhawklex.legendaryclans.commands.CommandExecutor_Party;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import me.blackhawklex.legendaryclans.party.PartyManager;
import me.znickq.spoutmaterials.SpoutMaterials;
import net.citizensnpcs.api.CitizensManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.virtuallyabstract.minecraft.DungeonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.sound.SoundEffect;
import org.ss.SpoutShopPlugin;

public class LegendaryClans extends JavaPlugin {
    private Logger log;

    private PluginDescriptionFile description;

    private String prefix;

    private ClanManager clanManager;

    private PlayerManager playerManager;

    private PartyManager partyManager;

    private SpoutManager spoutManager;

    private DonationManager donationManager;

    private WorldGuardPlugin worldGuard;

    private SpoutShopPlugin spoutShop;

    private SpoutMaterials spoutMaterials;

    private LoginQueue loginQueue;

    private DungeonBuilder dungeonBuilder;

    private Config cfg;

//    private iConomy iConomy = null;
    private static final int[] distributableBlocks = {41, 42, 57, 22};

    private static final int[] blockCreditPrices = {65, 55, 90, 100};

    private static final String FILE_LOGINQUEUE = "." + File.separator + "plugins" + File.separator + "LoginQueue2" + File.separator + "reservelist.txt";

    private static final int soulPrice = 1;

    public static Permission permission = null;

    public static Economy economy = null;

    private boolean emergencyDisable = false;

//        private Spout spout;
    @Override
    public void onEnable() {
        log = Logger.getLogger("Minecraft");
        description = getDescription();
        prefix = "[" + description.getName() + "] ";

        log("loading " + description.getFullName());

        //set up config!
//        getConfig().options().copyDefaults(true);
//        saveConfig();
        cfg = new Config(this);

        worldGuard = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null) {
            emergencyDisable = true;
            log("Not WorldGuard installed!");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            log("Hooked into WorldGuard.");
        }

        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens != null) {
            log("Successfully hooked into Citizens!");
        } else {
            emergencyDisable = true;
            log("Not Citizens installed!");
            getServer().getPluginManager().disablePlugin(this);
        }

        Plugin spoutShops = getServer().getPluginManager().getPlugin("SpoutShops");
        if (citizens != null) {
            spoutShop = (SpoutShopPlugin) spoutShops;
            log("Successfully hooked into SpoutShops!");
        } else {
            emergencyDisable = true;
            log("Not SpoutShops installed!");
            getServer().getPluginManager().disablePlugin(this);
        }

        Plugin spoutMats = getServer().getPluginManager().getPlugin("SpoutMaterials");
        if (citizens != null) {
            spoutMaterials = (SpoutMaterials) spoutMats;
            log("Successfully hooked into SpoutMats!");
        } else {
            emergencyDisable = true;
            log("Not SpoutMats installed!");
            getServer().getPluginManager().disablePlugin(this);
        }

        Plugin loginQueues = getServer().getPluginManager().getPlugin("LoginQueue");
        if (citizens != null) {
            loginQueue = (LoginQueue) loginQueues;
            log("Successfully hooked into LoginQueue!");
        } else {
            emergencyDisable = true;
            log("Not LoginQueue installed!");
            getServer().getPluginManager().disablePlugin(this);
        }

        Plugin dunBuild = getServer().getPluginManager().getPlugin("DungeonBuilder");
        if (dunBuild != null) {
            dungeonBuilder = (DungeonBuilder) dunBuild;
            log("Successfully hooked into DungeonBuilder!");
        } else {
            emergencyDisable = true;
            log("DungeonBuilder not installed!");
            getServer().getPluginManager().disablePlugin(this);
        }

        setupEconomy();
        setupPermissions();

        playerManager = new PlayerManager(this);
        partyManager = new PartyManager(this);
        clanManager = new ClanManager(this);
        donationManager = new DonationManager(this);

        //Register events
        Bukkit.getPluginManager().registerEvents(new LegendaryListener(this), this);

        getCommand("clan").setExecutor(new CommandExecutor_Clan(this));
        getCommand("party").setExecutor(new CommandExecutor_Party(this));
        getCommand("aclan").setExecutor(new CommandExecutor_AdminClanCommands(this));
        getCommand("ch").setExecutor(new CommandExecutor_Chat(this));
        getCommand("don").setExecutor(new CommandExecutor_Donations(this));
    }

    @Override
    public void onDisable() {
        if (playerManager == null || partyManager == null || clanManager == null || donationManager == null) {
            emergencyDisable = true;
        }

        if (!emergencyDisable) {
            clanManager.saveAllClans();
            playerManager.saveAllPlayers();
        }
        log("disabled " + description.getFullName());

    }

    public void log(String message) {
        log.info(prefix + message);
    }

    public boolean checkPlugin(String name) {
        if (getServer().getPluginManager().isPluginEnabled(name)) {
            return true;
        }
        return false;
    }

    public Plugin getPlugin(String name) {
        if (checkPlugin(name)) {
            return getServer().getPluginManager().getPlugin(name);
        }
        return null;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void enableSpoutSupport() {
        if (checkPlugin("Spout")) {
            spoutManager = SpoutManager.getInstance();
        }
    }

    public SpoutManager getSpoutManager() {
        return spoutManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public static void coloredOutput(CommandSender sender, String message) {
        sender.sendMessage(parseColor(message));
    }

    /**
     * Parses color in text
     *
     * @param message The message to parse
     * @return The parsed string
     */
    public static String parseColor(String message) {
        return message.replaceAll("(&([a-f0-9]))", "\u00A7$2");
    }

    public Economy getiConomy() {
        return economy;
    }

    public WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    private Boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    private Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public static Permission getPermission() {
        return permission;
    }

    public static boolean checkNPC(Entity entity) {
        if (CitizensManager.isNPC(entity)) {
            return true;
        }
        return false;
    }

    public static int[] getBlockCreditPrices() {
        return blockCreditPrices;
    }

    public static int[] getDistributableBlocks() {
        return distributableBlocks;
    }

    public static boolean checkIfBlockIsDistributable(int id) {
        for (int i : distributableBlocks) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    //  Iron: 30-55
//Gold: 40-65
//Lapis: 35-120
//Diamond: 55-120
    public static int getBlockPrice(int id) {
        int p = 0;
        for (int i : distributableBlocks) {
            if (i == id) {
                Random generator = new Random();
                int num = generator.nextInt(15);
                return blockCreditPrices[p] - num;
            }
            p++;
        }
        return -1000;
    }

    public void addPlayerToRegionAsMember(String worldName, String regionName, LegendaryPlayer player) {
        RegionManager regionManager = worldGuard.getRegionManager(getServer().getWorld(worldName));
        ProtectedRegion region = regionManager.getRegion(regionName);
        if (region != null) {
            DefaultDomain domain = region.getMembers();
            domain.addPlayer(player.getName());
            region.setMembers(domain);
            try {
                regionManager.save();
            } catch (ProtectionDatabaseException ex) {
                Logger.getLogger(LegendaryClans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void removePlayerFromRegionAsMember(String worldName, String regionName, LegendaryPlayer player) {
        RegionManager regionManager = worldGuard.getRegionManager(getServer().getWorld(worldName));
        ProtectedRegion region = regionManager.getRegion(regionName);
        if (region != null) {
            DefaultDomain domain = region.getMembers();
//            if(domain.contains(worldGuard.wrapPlayer(player.getName()))){
            domain.removePlayer(player.getName());
//            }
            region.setMembers(domain);
            try {
                regionManager.save();
            } catch (ProtectionDatabaseException ex) {
                Logger.getLogger(LegendaryClans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static boolean URLexists(String url) {
        int code = 0;
        try {
            URL u = new URL(url);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            code = huc.getResponseCode();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (code == 200) {
            return true;
        } else {
            return false;
        }
    }

    public SpoutShopPlugin getSpoutShop() {
        return spoutShop;
    }

    public static void playSound(Player p, SoundEffect e) {
        SpoutManager.getSoundManager().playSoundEffect(SpoutManager.getPlayer(p), e);
    }

    public static int getSoulPrice() {
        return soulPrice;
    }

    public SpoutMaterials getSpoutMaterials() {
        return spoutMaterials;
    }

    public Config getCfg() {
        return cfg;
    }

    public void reloadLoginQueue() {
        getServer().getPluginManager().disablePlugin(getServer().getPluginManager().getPlugin("LoginQueue"));
        getServer().getPluginManager().enablePlugin(getServer().getPluginManager().getPlugin("LoginQueue"));
    }

    public void updateLoginQueueFile() {
        List<LegendaryPlayer> donsHighEnough = getPlayerManager().getDonsOver1();
        String text = "";
        StringBuilder builder = new StringBuilder(text);
        for (LegendaryPlayer p : donsHighEnough) {
            builder.append(p.getName()).append("\n");
        }
        try {
            Writer output = null;
            File file = new File(FILE_LOGINQUEUE);
            output = new BufferedWriter(new FileWriter(file));
            output.write(builder.toString());
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(LegendaryClans.class.getName()).log(Level.SEVERE, null, ex);
        }
//        reloadLoginQueue();
    }

    public DonationManager getDonationManager() {
        return donationManager;
    }

    public DungeonBuilder getDungeonBuilder() {
        return dungeonBuilder;
    }

}
