/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans.listeners;

import me.blackhawklex.gui.ClanGuardianMember;
import me.blackhawklex.gui.ClanManagementWindow;
import me.blackhawklex.gui.FoundAClan;
import me.blackhawklex.gui.TeleportationScreen;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
//import net.citizensnpcs.api.event.CitizensEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.resources.npclib.HumanNPC;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.gui.ShopBuyPopup;
import org.ss.gui.ShopManagerPopup;
import org.ss.listeners.SSBlockListener;
import org.ss.serial.Coordinate;
import org.ss.shop.Shop;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class Listener_NPC implements Listener {
    private LegendaryClans plugin;

    public Listener_NPC(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    //   @Override
    public void onNPCRightClick(NPCRightClickEvent event) {
        //       super.onNPCRightClick(event);
        Player player = event.getPlayer();
        HumanNPC npc = event.getNPC();
        if (npc != null) {
            LegendaryPlayer contacter = plugin.getPlayerManager().searchPlayerByName(player.getName());
            //is it the clan guardian and the contacter existing? 
            if (npc.getName().contains("Clan Guardian") && contacter != null) {
                Clan clan = plugin.getClanManager().getClanByPlayer(contacter);
                if (clan != null) {
                    //if he is the clan leader
                    if (clan.isLeader(contacter)) {
                        SpoutPlayer playerS = SpoutManager.getPlayer(contacter.getPlayer());
                        ClanManagementWindow managementWindow = new ClanManagementWindow(plugin, playerS, clan);
                    } else {
                        SpoutPlayer playerS = SpoutManager.getPlayer(contacter.getPlayer());
                        ClanGuardianMember memberWindow = new ClanGuardianMember(plugin, playerS, clan);
                    }
                } else {
                    // found a clan window
                    SpoutPlayer playerS = SpoutManager.getPlayer(contacter.getPlayer());
                    FoundAClan window = new FoundAClan(plugin, playerS);
                }
            } else if (npc.getName().contains("Trader")) {
                openShopWindow(event);
            } else if (npc.getName().contains("Captain")) {
                SpoutPlayer playerS = SpoutManager.getPlayer(contacter.getPlayer());
                TeleportationScreen telScreen = new TeleportationScreen(plugin, playerS);
            }
        }
    }

    public void openShopWindow(NPCRightClickEvent event) {
        SpoutPlayer player = SpoutManager.getPlayer(event.getPlayer());
        HumanNPC npc = event.getNPC();
        if (!LegendaryClans.getPermission().has(player.getPlayer(), "spoutshops.interact.npc")) {
            player.sendMessage("&4You do not have permission to interact with this.");
            event.setCancelled(true);
            return;
        } else {
            Location baseLoc = new Location(event.getNPC().getWorld(), npc.getBaseLocation().getX(), npc.getBaseLocation().getY() - 1, npc.getBaseLocation().getZ());
            Coordinate coord = new Coordinate(baseLoc);
            Shop shop = SSBlockListener.getInstance().getShop(coord);
            if (shop != null) {
                if (player.hasPermission("spoutshops.management")) {
                    new ShopManagerPopup(player, shop).show();
                } else {
                    new ShopBuyPopup(player, shop).show();
                }
            } else {
                LegendaryClans.coloredOutput((CommandSender) player.getPlayer(), "&4This shop is not setup correctly, inform an admin!");
            }
        }


    }

}
