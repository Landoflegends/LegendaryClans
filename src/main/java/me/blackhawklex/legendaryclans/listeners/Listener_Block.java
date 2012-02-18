/*
 * LegendaryClans - by BlackHawkLex
 * http://
 *
 * powered by Kickstarter
 */

package me.blackhawklex.legendaryclans.listeners;


import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;

import org.bukkit.event.block.BlockDamageEvent;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.BlockPlaceEvent;


public class Listener_Block extends BlockListener {
	private LegendaryClans plugin;

	public Listener_Block(LegendaryClans plugin){
		this.plugin = plugin;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event){
		// TODO handle that event
	}

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        super.onBlockPlace(event);
        Block placedBlock = event.getBlockPlaced();
        RegionManager manager = plugin.getWorldGuard().getRegionManager(plugin.getServer().getWorld("salva"));
        Vector vector = new Vector(placedBlock.getX(),placedBlock.getY(),placedBlock.getZ());
        if(manager.getRegion("blocksacrification") != null){
            if(manager.getRegion("blocksacrification").contains(vector)){
                if(LegendaryClans.checkIfBlockIsDistributable(placedBlock.getTypeId())){
                    LegendaryPlayer legPlayer = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
                    if(legPlayer != null){
                        Clan clan = plugin.getClanManager().getClanByPlayer(legPlayer);
                        if(clan != null){
                            int amount = LegendaryClans.getBlockPrice(placedBlock.getTypeId());
                            clan.changeCredits(amount);
                            placedBlock.setTypeId(0);
                            clan.broadcastMessage("&9" + legPlayer.getName() + " distributed " + amount + " credits. Total credits now: "+clan.getCredits()+".");
                            clan.save();
                        }
                        else{
                            LegendaryClans.coloredOutput((CommandSender)legPlayer.getPlayer(), "&4You don´t have a clan to distribute to.");
                            event.setCancelled(true);
                        }
                    }
                }
                else{
                    LegendaryClans.coloredOutput((CommandSender)event.getPlayer(), "&4You can´t sacrifice this block!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        super.onBlockCanBuild(event);      
    }
    
        
        


}
