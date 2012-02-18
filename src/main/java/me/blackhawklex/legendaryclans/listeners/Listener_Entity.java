/*
 * LegendaryClans - by BlackHawkLex
 * http://
 *
 * powered by Kickstarter
 */
package me.blackhawklex.legendaryclans.listeners;

import java.util.Random;
import org.bukkit.event.entity.EntityListener;
//import org.getspout.spoutapi.inventory.ItemStack;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.party.Party;
import me.blackhawklex.legendaryclans.pvp.DeathManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Listener_Entity extends EntityListener {
    private LegendaryClans plugin;

    public Listener_Entity(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // TODO handle that event
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            LivingEntity playerr = (LivingEntity) event.getEntity();
            Player player = (Player) event.getEntity();
            Player killer = null;
            LegendaryPlayer realP = plugin.getPlayerManager().searchPlayerByName(player.getName());
            if (realP != null) {
                double amount = 10;
                plugin.getiConomy().withdrawPlayer(player.getName(), amount);
                LegendaryClans.coloredOutput((CommandSender) player, "&4You lost " + amount + " souls as u died!");

            }

            EntityDamageEvent e1 = event.getEntity().getLastDamageCause();
            EntityDamageByEntityEvent e2 = (e1 instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) e1 : null;
            Entity damager = (e2 != null) ? e2.getDamager() : null;
            if (e2 != null && damager instanceof Player) {
                killer = (Player) damager;
            }
            if (killer != null) {
                LegendaryPlayer deadPlayer = plugin.getPlayerManager().searchPlayerByName(player.getName());
                LegendaryPlayer killerLeg = plugin.getPlayerManager().searchPlayerByName(killer.getName());
                if (deadPlayer != null && killer != null) {
                    DeathManager dm = new DeathManager(killerLeg, deadPlayer, plugin);
                    ((PlayerDeathEvent) event).setDeathMessage("");
                }
            }
        } else {
            Entity deadMob = event.getEntity();
            Player killer = null;
            EntityDamageEvent e1 = event.getEntity().getLastDamageCause();
            EntityDamageByEntityEvent e2 = (e1 instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) e1 : null;
            Entity damager = (e2 != null) ? e2.getDamager() : null;
            if (e2 != null && damager instanceof Player) {
                killer = (Player) damager;
            }
            if (killer != null) {
                LegendaryPlayer killerLeg = plugin.getPlayerManager().searchPlayerByName(killer.getName());
                Party party = plugin.getPartyManager().getPartyByPlayer(killerLeg);
                if (party != null) {
                    addExpByType(deadMob, party, killerLeg);
                }
            }
        }

        //regulate soul drop by party level;
        Entity deadMob = event.getEntity();
        Player killer = null;
        EntityDamageEvent e1 = event.getEntity().getLastDamageCause();
        Party party = null;
        EntityDamageByEntityEvent e2 = (e1 instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) e1 : null;
        Entity damager = (e2 != null) ? e2.getDamager() : null;
        if (e2 != null && damager instanceof Player) {
            killer = (Player) damager;
        }
        if (killer != null) {
            LegendaryPlayer killerLeg = plugin.getPlayerManager().searchPlayerByName(killer.getName());
            party = plugin.getPartyManager().getPartyByPlayer(killerLeg);
            // drop a random amount of souls (0-3) everytime u kill "something"
            Random random = new Random();
            int amount = 0;
            amount = random.nextInt(3);
            //if player has party, give it a bit more, at max 5 souls + rest;
            if (party != null) {
                amount = amount + (int) (party.getLevel() / 5);
            }

            // Disabled CustomItem and added soul direct deposit.
            plugin.getiConomy().depositPlayer(killer.getName(), amount);
            // CustomItem soulItem = plugin.getSpoutMaterials().itemManager.getItem("Soul").getCustomItem();
            // try {
            // SpoutItemStack stack = new SpoutItemStack(soulItem, amount + 1);
            // plugin.getServer().getWorld(event.getEntity().getWorld().getName()).dropItem(event.getEntity().getLocation(), stack);
            // } catch (Exception e) {
            // plugin.log("Block Soul not existing. Custom drops will not work!");
            // }
        }


    }

    public void addExpByType(Entity ent, Party party, LegendaryPlayer killer) {
        int expMulti = -1;
        if (ent instanceof Blaze) {
            expMulti = 15;
        }
        if (ent instanceof CaveSpider) {
            expMulti = 7;
        }
        if (ent instanceof Creeper) {
            expMulti = 3;
        }
        if (ent instanceof EnderDragon) {
            expMulti = 150;
        }
        if (ent instanceof Enderman) {
            expMulti = 7;
        }
        if (ent instanceof Ghast) {
            expMulti = 15;
        }
        if (ent instanceof Giant) {
            expMulti = 30;
        }
        if (ent instanceof MagmaCube) {
            expMulti = 10;
        }
        if (ent instanceof PigZombie) {
            expMulti = 5;
        }
        if (ent instanceof Skeleton) {
            expMulti = 2;
        }
        if (ent instanceof Slime) {
            expMulti = 5;
        }
        if (ent instanceof Spider) {
            expMulti = 4;
        }
        if (ent instanceof Wolf) {
            expMulti = 2;
        }
        if (ent instanceof Zombie) {
            expMulti = 3;
        }
        if (ent instanceof Player) {
            Party party1 = plugin.getPartyManager().getPartyByPlayer(killer);
            Party party2 = plugin.getPartyManager().getPartyByPlayer(plugin.getPlayerManager().searchPlayerByName(((Player) ent).getName()));
            if (!party1.equals(party2)) {
                expMulti = 10;
            }
        }
        if (expMulti > 0) {
            int partyLevel = party.getLevel();
            int multiplier = (expMulti / 5);
            if (multiplier == 0) {
                multiplier = 1;
            }
            int realExp = multiplier * partyLevel * party.getMemberCount();
            party.regulateLevel(realExp);
        }
    }

}
