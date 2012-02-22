/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans.clans;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public class ClanTerr {
    private ProtectedRegion innerClanBase;

    private ProtectedRegion clanTerritory;

    private Clan clan;

    private LegendaryClans plugin;

    private World world;

    private BlockVector clanBaseMid;

    private int defendingPoints;

    private int loosingPoints;

    private boolean setup;

    public ClanTerr(Clan clan, LegendaryClans plugin, World world) {
        this.clan = clan;
        this.plugin = plugin;
        this.world = world;
        innerClanBase = plugin.getWorldGuard().getRegionManager(world).getRegion(clan.getName() + "InnerClanBase");
        clanTerritory = plugin.getWorldGuard().getRegionManager(world).getRegion(clan.getName() + "ClanTerritory");
        if (innerClanBase != null && clanTerritory != null) {
            setup = true;
        }

    }

    public ClanTerr(Clan clan, LegendaryClans plugin, LegendaryPlayer leader) {
        this.clan = clan;
        this.plugin = plugin;
        world = plugin.getServer().getWorld("wild");

        setupClanTerritory(leader, world);
        setupInnerClanBase(leader, world);

        if (innerClanBase != null && clanTerritory != null) {
            setup = true;
            LegendaryClans.coloredOutput((CommandSender) leader.getPlayer(), "&2Your clan base has been successfully set up! Congratulations.");
        }
    }

    public ClanTerr(Clan clan, LegendaryClans plugin) {
        this.clan = clan;
        this.plugin = plugin;
        world = plugin.getServer().getWorld("wild");
        innerClanBase = plugin.getWorldGuard().getRegionManager(world).getRegion(clan.getInnerClanBaseName());
        clanTerritory = plugin.getWorldGuard().getRegionManager(world).getRegion(clan.getTerritoryName());
        if (innerClanBase != null && clanTerritory != null) {
            setup = true;
        }
    }

    //shrink the inner Base by 1;
    public void changeInnerBase(int amount) {
        RegionManager manager = plugin.getWorldGuard().getRegionManager(world);
        BlockVector maxPoint = innerClanBase.getMaximumPoint();
        BlockVector minPoint = innerClanBase.getMinimumPoint();

        //CLAN INNER BASE 
        //change the min and max points to smaller seize
//         maxPoint.setX(maxPoint.getX()+amount);
//         maxPoint.setZ(maxPoint.getZ()+amount);
//         
//         minPoint.setX(minPoint.getX()+amount);
//         minPoint.setZ(minPoint.getZ()+amount);
//         
        BlockVector newMaxPoint = new BlockVector(maxPoint.getBlockX() + amount, maxPoint.getBlockY(), maxPoint.getBlockZ() + amount);
        BlockVector newMinPoint = new BlockVector(minPoint.getBlockX() - amount, minPoint.getBlockY(), minPoint.getBlockZ() - amount);;

        //remove the old region
        manager.removeRegion(innerClanBase.getId());

        //create a new innerClanBaseRegion with new points
        ClanTerritory innerClanBaseRegion = new ClanTerritory(clan.getName() + "InnerClanBase", newMinPoint, newMaxPoint);
        innerClanBase = innerClanBaseRegion;

        //add the new region
        manager.addRegion(innerClanBase);
        addPplToInnerClanBase();
        try {
            manager.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(ClanTerr.class.getName()).log(Level.SEVERE, null, ex);
        }




    }

    // shrink the clan territory by 1
    public void changeTerritory(int amount) {
        RegionManager manager = plugin.getWorldGuard().getRegionManager(world);
        BlockVector maxPoint = clanTerritory.getMaximumPoint();
        BlockVector minPoint = clanTerritory.getMinimumPoint();

        //CLAN TERRITORY
        //change the min and max points to smaller seize
//        maxPoint.setX(maxPoint.getX() + amount);
//        maxPoint.setZ(maxPoint.getZ() + amount);
//
//        minPoint.setX(minPoint.getX() + amount);
//        minPoint.setZ(minPoint.getZ() + amount);

        BlockVector newMaxPoint = new BlockVector(maxPoint.getBlockX() + amount, maxPoint.getBlockY(), maxPoint.getBlockZ() + amount);
        BlockVector newMinPoint = new BlockVector(minPoint.getBlockX() - amount, minPoint.getBlockY(), minPoint.getBlockZ() - amount);;

        //remove the old region
        manager.removeRegion(clanTerritory.getId());

        //create a new innerClanBaseRegion with new points
        ClanTerritory territoryRegion = new ClanTerritory(clan.getName() + "ClanTerritory", newMinPoint, newMaxPoint);
        clanTerritory = territoryRegion;

        //add the new region
        manager.addRegion(clanTerritory);
        addPplToTerritory();
        try {
            manager.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(ClanTerr.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean isSetup() {
        return setup;
    }

    public void changeSetup(boolean value) {
        setup = value;
    }

    public World getWorld() {
        return world;
    }

    public ProtectedRegion getClanTerritory() {
        return clanTerritory;
    }

    public ProtectedRegion getInnerClanBase() {
        return innerClanBase;
    }

    public void addPplToInnerClanBase() {
        RegionManager manager = plugin.getWorldGuard().getRegionManager(world);
        DefaultDomain memberDomain = new DefaultDomain();
        for (LegendaryPlayer member : clan.getMembers()) {
            memberDomain.addPlayer(member.getName());
        }
        for (LegendaryPlayer leader : clan.getLeaders()) {
            memberDomain.addPlayer(leader.getName());
        }

        manager.getRegion(clan.getInnerClanBaseName()).setMembers(memberDomain);
//        DefaultDomain leaderDomain = new DefaultDomain();
//        for (LegendaryPlayer leader : clan.getLeaders()) {
//            leaderDomain.addPlayer(leader.getName());
//        }
//        manager.getRegion(clan.getInnerClanBaseName()).setOwners(leaderDomain);
    }

    public void addPplToTerritory() {
        RegionManager manager = plugin.getWorldGuard().getRegionManager(world);
        DefaultDomain memberDomain = new DefaultDomain();
        for (LegendaryPlayer player : plugin.getPlayerManager().getPlayers()) {
            memberDomain.addPlayer(player.getName());
        }
//        for (LegendaryPlayer member : clan.getMembers()) {
//            memberDomain.addPlayer(member.getName());
//        }
//        for (LegendaryPlayer leader : clan.getLeaders()) {
//            memberDomain.addPlayer(leader.getName());
//        }

        manager.getRegion(clan.getTerritoryName()).setMembers(memberDomain);

//        DefaultDomain leaderDomain = new DefaultDomain();
//        for (LegendaryPlayer leader : clan.getLeaders()) {
//            leaderDomain.addPlayer(leader.getName());
//        }
//        manager.getRegion(clan.getTerritoryName()).setOwners(leaderDomain);
    }

    public void setupClanTerritory(LegendaryPlayer player, World world) {
        Location playerLocation = player.getPlayer().getLocation();
        int minX = playerLocation.getBlockX() - 19;
        int minY = playerLocation.getBlockY() - 1000; //height
        int minZ = playerLocation.getBlockZ() - 19;

        int maxX = playerLocation.getBlockX() + 19;
        int maxY = playerLocation.getBlockY() + 1000;
        int maxZ = playerLocation.getBlockZ() + 19;

        BlockVector minPoint = new BlockVector(minX, minY, minZ);
        BlockVector maxPoint = new BlockVector(maxX, maxY, maxZ);

        RegionManager manager = plugin.getWorldGuard().getRegionManager(world);
        ClanTerritory territory = new ClanTerritory(clan.getTerritoryName(), minPoint, maxPoint);
        clanTerritory = territory;

        manager.addRegion(clanTerritory);
        addPplToTerritory();
        try {
            manager.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(ClanTerr.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setupInnerClanBase(LegendaryPlayer player, World world) {

        Location playerLocation = player.getPlayer().getLocation();
        clanBaseMid = new BlockVector(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ());
        int minX = playerLocation.getBlockX() - 12;
        int minY = playerLocation.getBlockY() - 1000; //height
        int minZ = playerLocation.getBlockZ() - 12;

        int maxX = playerLocation.getBlockX() + 12;
        int maxY = playerLocation.getBlockY() + 1000;
        int maxZ = playerLocation.getBlockZ() + 12;

        BlockVector minPoint = new BlockVector(minX, minY, minZ);
        BlockVector maxPoint = new BlockVector(maxX, maxY, maxZ);

        RegionManager manager = plugin.getWorldGuard().getRegionManager(world);
        ClanTerritory innerBase = new ClanTerritory(clan.getInnerClanBaseName(), minPoint, maxPoint);
//            BooleanFlag flag = new BooleanFlag("chest-access");
////            flag.
//            innerBase.setFlag(flag,false);
        innerClanBase = innerBase;

        manager.addRegion(innerClanBase);
        addPplToInnerClanBase();
        try {
            manager.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(ClanTerr.class.getName()).log(Level.SEVERE, null, ex);
        }

        changeBlocksAtClanBaseMid(clanBaseMid);

    }

    public void changeBlocksAtClanBaseMid(BlockVector mid) {
        Block block0 = plugin.getServer().getWorld("wild").getBlockAt(mid.getBlockX(), mid.getBlockY(), mid.getBlockZ());
        Block block = plugin.getServer().getWorld("wild").getBlockAt(mid.getBlockX(), mid.getBlockY() + 1, mid.getBlockZ());
        Block block2 = plugin.getServer().getWorld("wild").getBlockAt(mid.getBlockX(), mid.getBlockY() + 2, mid.getBlockZ());
        RegionManager manager = plugin.getWorldGuard().getRegionManager(world);

        BlockVector vector1 = new BlockVector(block0.getX(), block0.getY(), block0.getZ());
        BlockVector vector2 = new BlockVector(block2.getX(), block2.getY(), block2.getZ());
        ClanTerritory shrine = new ClanTerritory(getClan().getShrineName(), vector1, vector2);
        manager.addRegion(shrine);
        block0.setTypeId(87);
        block.setTypeId(87);
        block2.setTypeId(51);
        try {
            manager.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(ClanTerr.class.getName()).log(Level.SEVERE, null, ex);
        }

        BlockVector minPoint = innerClanBase.getMinimumPoint();
        BlockVector maxPoint = innerClanBase.getMaximumPoint();

        minPoint.setY(mid.getBlockY());
        maxPoint.setY(mid.getBlockY());

//        for(int i = 0; i<12;i++){
//            Block b1 = plugin.getServer().getWorld("wild").getBlockAt(minPoint.getBlockX()-i,mid.getBlockY(),minPoint.getBlockZ());
//            Block b2 = plugin.getServer().getWorld("wild").getBlockAt(maxPoint.getBlockX()+i,mid.getBlockY(),maxPoint.getBlockZ());
//            Block b3 = plugin.getServer().getWorld("wild").getBlockAt(minPoint.getBlockX(),mid.getBlockY(),minPoint.getBlockZ()-i);
//            Block b4 = plugin.getServer().getWorld("wild").getBlockAt(maxPoint.getBlockX(),mid.getBlockY(),maxPoint.getBlockZ()+i);
//            System.out.println(b1.getX() + b1.getY() + b1.getZ());
//            System.out.println(b2.getX() + b2.getY() + b2.getZ());
//            System.out.println(b3.getX() + b3.getY() + b3.getZ());
//            System.out.println(b4.getX() + b4.getY() + b4.getZ());
//            b1.setTypeId(87);
//            b2.setTypeId(87);
//            b3.setTypeId(87);
//            b4.setTypeId(87);

        int minX = minPoint.getBlockX();
        int minY = mid.getBlockY();
        int minZ = minPoint.getBlockZ();
        int maxX = maxPoint.getBlockX();
        int maxY = mid.getBlockY();
        int maxZ = maxPoint.getBlockZ();

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                plugin.getServer().getWorld("wild").getBlockAt(x, y, minZ).setTypeId(85);
                plugin.getServer().getWorld("wild").getBlockAt(x, y, maxZ).setTypeId(85);
            }

        }

        for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
                plugin.getServer().getWorld("wild").getBlockAt(minX, y, z).setTypeId(85);
                plugin.getServer().getWorld("wild").getBlockAt(maxX, y, z).setTypeId(85);
            }
        }
    }

    public Clan getClan() {
        return clan;
    }

    public void setMidPoint(BlockVector mid) {
        clanBaseMid = mid;
    }

    public BlockVector getClanBaseMid() {
        return clanBaseMid;
    }

    public int getDefendingPoints() {
        return defendingPoints;
    }

    public int getLoosingPoints() {
        return loosingPoints;
    }

    public void addDefendingPoint(int amount) {
        if (defendingPoints == 0 && loosingPoints == 0) {
            defendingPoints += amount;
        } else if (defendingPoints == 0 && loosingPoints == 1) {
            loosingPoints = 0;
        } else if (defendingPoints == 1 && loosingPoints == 0) {
            defendingPoints = 0;
            int maxSeizeInnerClanBase = ClanManager.getMaxInnerClanBaseRange()[clan.getLvl() - 1];
            int maxSeizeTerritory = ClanManager.getMaxTerrRange()[clan.getLvl() - 1];
            if (getInnerClanBaseSeize() < maxSeizeInnerClanBase) {
                changeInnerBase(1);
                String terrIncreased = "&2" + clan.getName() + "'s inner base has been increased to " + getInnerClanBaseSeize() + "/" + ClanManager.getMaxInnerClanBaseRange()[clan.getLvl() - 1];
                plugin.getPlayerManager().broadcastMessage(terrIncreased);
//                killer.getClan().broadcastMessage(terrIncreased);
//                deadPlayer.getClan().broadcastMessage(terrIncreased);
            } else if (getInnerClanBaseSeize() >= maxSeizeInnerClanBase) {
                clan.broadcastMessage("Your inner clan base is already at maximum seize.");
            }

            if (getTerritorySeize() < maxSeizeTerritory) {
                changeTerritory(1);
                String terrIncreased = "&2" + clan.getName() + "'s territory has been increased to " + getTerritorySeize() + "/" + ClanManager.getMaxTerrRange()[clan.getLvl() - 1];
                plugin.getPlayerManager().broadcastMessage(terrIncreased);
//                killer.getClan().broadcastMessage(terrIncreased);
//                deadPlayer.getClan().broadcastMessage(terrIncreased);
            } else if (getTerritorySeize() >= maxSeizeTerritory) {
                clan.broadcastMessage("Your territory is already at maximum seize.");
            }
        }
    }

    public void addLoosingPoint(int amount) {
        if (defendingPoints == 0 && loosingPoints == 0) {
            loosingPoints += amount;
        } else if (defendingPoints == 0 && loosingPoints == 1) {
            loosingPoints = 0;
            int maxSeizeInnerClanBase = ClanManager.getMaxInnerClanBaseRange()[clan.getLvl() - 1];
            int maxSeizeTerritory = ClanManager.getMaxTerrRange()[clan.getLvl() - 1];
            if (getInnerClanBaseSeize() > 11) {
                changeInnerBase(-1);
                changeTerritory(-1);
                String terrIncreased = "&4" + clan.getName() + "'s inner base and territory has been shrinked to " + getInnerClanBaseSeize() + "/" + ClanManager.getMaxInnerClanBaseRange()[clan.getLvl() - 1] + " and " + getTerritorySeize() + "/" + ClanManager.getMaxTerrRange()[clan.getLvl() - 1];
                plugin.getPlayerManager().broadcastMessage(terrIncreased);
//                killer.getClan().broadcastMessage(terrIncreased);
//                deadPlayer.getClan().broadcastMessage(terrIncreased);
            } else {
                //wipe out the clan 
                plugin.getClanManager().dissolveClan(clan);
            }
        } else if (defendingPoints == 1 && loosingPoints == 0) {
            defendingPoints = 0;
        }
    }

    public int getTerritorySeize() {
        if (setup) {
            int i = clanTerritory.getMaximumPoint().getBlockZ() - clanTerritory.getMinimumPoint().getBlockZ();
            if (i < 0) {
                i = -i;
            }
            return i;
        }
        return 0;
    }

    public int getInnerClanBaseSeize() {
        if (setup) {
            int i = innerClanBase.getMaximumPoint().getBlockZ() - innerClanBase.getMinimumPoint().getBlockZ();
            if (i < 0) {
                i = -i;
            }
            return i;
        }
        return 0;
    }

    public void wipeBaseAndTerritory() {
        if (setup) {
            RegionManager manager = plugin.getWorldGuard().getRegionManager(world);
            manager.removeRegion(clan.getInnerClanBaseName());
            manager.removeRegion(clan.getTerritoryName());
            manager.removeRegion(clan.getShrineName());
            try {
                manager.save();
            } catch (ProtectionDatabaseException ex) {
                Logger.getLogger(ClanTerr.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
