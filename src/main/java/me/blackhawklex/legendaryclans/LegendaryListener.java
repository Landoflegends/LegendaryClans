/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import me.blackhawklex.gui.*;
import me.blackhawklex.legendaryclans.clans.Clan;
import me.blackhawklex.legendaryclans.party.Party;
import me.blackhawklex.legendaryclans.pvp.DeathManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.resources.npclib.HumanNPC;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.event.input.RenderDistanceChangeEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.gui.ShopBuyPopup;
import org.ss.gui.ShopManagerPopup;
import org.ss.listeners.SSBlockListener;
import org.ss.serial.Coordinate;
import org.ss.shop.Shop;

/**
 * The listener of all listeners.
 */



public class LegendaryListener implements Listener {
    private static final String WATCHSTONE_PERM = "LegendaryClans.donator.watchstone";

    private final LegendaryClans plugin;

    public static float Round(float Rval, int Rpl) {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }
    
    public LegendaryListener(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        // TODO handle that event
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        RegionManager manager = plugin.getWorldGuard().getRegionManager(plugin.getServer().getWorld("salva"));
        Vector vector = new Vector(placedBlock.getX(), placedBlock.getY(), placedBlock.getZ());
        if (manager.getRegion("blocksacrification") != null) {
            if (manager.getRegion("blocksacrification").contains(vector)) {
                if (LegendaryClans.checkIfBlockIsDistributable(placedBlock.getTypeId())) {
                    LegendaryPlayer legPlayer = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
                    if (legPlayer != null) {
                        Clan clan = plugin.getClanManager().getClanByPlayer(legPlayer);
                        if (clan != null) {
                            int amount = LegendaryClans.getBlockPrice(placedBlock.getTypeId());
                            clan.changeCredits(amount);
                            placedBlock.setTypeId(0);
                            clan.broadcastMessage("&9" + legPlayer.getName() + " distributed " + amount + " credits. Total credits now: " + clan.getCredits() + ".");
                            clan.save();
                        } else {
                            LegendaryClans.coloredOutput((CommandSender) legPlayer.getPlayer(), "&4You don´t have a clan to distribute to.");
                            event.setCancelled(true);
                        }
                    }
                } else {
                    LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4You can´t sacrifice this block!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // TODO handle that event
    }

    @EventHandler
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
            float amount = 0;
            amount = random.nextInt(3);
            //if player has party, give it a bit more, at max 5 souls + rest;
            if (party != null) {
                amount = amount + (int) (party.getLevel() / 5);
            }
            amount = amount + 1;
            
            // Check if Player is a Clan Member     
            Clan clan = plugin.getClanManager().getClanByPlayer(killerLeg);
            if (clan != null) {
                
                float taxRate = (float) 0.1;
                float clanSouls = Round(amount*taxRate,1);
                float playerSouls = Round(amount - clanSouls,1);
                
                plugin.getiConomy().depositPlayer(killer.getName(), playerSouls);
                LegendaryClans.coloredOutput((CommandSender) killer, "&b" + playerSouls + " soul(s) have been absorbed to the total souls of: " + plugin.getiConomy().getBalance(killer.getName()));
                plugin.getiConomy().bankDeposit(clan.getName(), clanSouls);
                LegendaryClans.coloredOutput((CommandSender) killer, "&b" + clanSouls + " soul(s) were absorbed by your clan: ");
            }
            else {
                plugin.getiConomy().depositPlayer(killer.getName(), amount);
                LegendaryClans.coloredOutput((CommandSender) killer, "&b" + amount + " soul(s) have been absorbed to the total souls of: " + plugin.getiConomy().getBalance(killer.getName()));
            }
            
            // Disabled CustomItem and added soul direct deposit.
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
    
    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        //    super.onNPCRightClick(event);
        Player player = event.getPlayer();
        player.sendMessage("&4Clicky Clicky.");
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

    @EventHandler
    public void onKeyPressedEvent(KeyPressedEvent event) {
        if (event.getScreenType().compareTo(ScreenType.GAME_SCREEN) == 0) {
            if (event.getKey().compareTo(Keyboard.KEY_L) == 0) {
//               TestMenue.open(plugin,event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onKeyReleasedEvent(KeyReleasedEvent event) {
    }

    @EventHandler
    public void onRenderDistanceChange(RenderDistanceChangeEvent event) {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LegendaryPlayer joinedPlayer = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
        if (joinedPlayer != null) {
            joinedPlayer.setOnline(true);
            joinedPlayer.setPlayer(event.getPlayer());


            Clan clan = plugin.getClanManager().getClanByPlayer(joinedPlayer);
            if (clan != null) {
                event.setJoinMessage(LegendaryClans.parseColor("&6[" + clan.getName() + "]" + joinedPlayer.getName() + " joined the game. Welcome " + joinedPlayer.getName() + "!"));
                joinedPlayer.setCape(clan);
            } else {
                event.setJoinMessage(LegendaryClans.parseColor("&6[None]" + joinedPlayer.getName() + " joined the game. Welcome " + joinedPlayer.getName() + "!"));
            }

            long time = Calendar.getInstance().getTime().getTime();
            // 24 hours
            if (time - joinedPlayer.getLastLoginTime() >= 86400000) {
                if (clan != null) {
                    Random generator = new Random();
                    int num = generator.nextInt(6);
                    clan.giveCredits(8 - num);
                    joinedPlayer.setLastLoginTime(time);
                }
            }
            //check donations
            plugin.getDonationManager().checkDonations(joinedPlayer);
        } else {
            plugin.getPlayerManager().createNewPlayer(event.getPlayer());
            event.setJoinMessage(LegendaryClans.parseColor("&3A new hero has found his way to Salvana! Welcome " + event.getPlayer().getName() + "!"));
        }
        event.getPlayer().sendMessage("&4You are in local chat. Use /ch g to change to global chat!");

        playLogonSound();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LegendaryPlayer leavingPlayer = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
        if (leavingPlayer != null) {
            Party party = plugin.getPartyManager().getPartyByPlayer(leavingPlayer);
            if (party != null && party.getMemberCount() > 2) {
                if (party.getLeader().equals(leavingPlayer)) {
                    party.deleteMember(leavingPlayer);
                    party.transferLead(party.getMembers().get(0));
                    party.broadcastMessage("&8Leader " + leavingPlayer.getName() + " left the party.");
                    party.broadcastMessage("&3" + party.getMembers().get(0).getName() + " is new leader.");
                } else {
                    party.deleteMember(leavingPlayer);
                    party.broadcastMessage("&8" + leavingPlayer.getName() + " left the party.");
                }

            } else if (party != null && party.getMemberCount() <= 2) {
                party.broadcastMessage("&8" + leavingPlayer.getName() + " left the party.");
                party.broadcastMessage("&8Party got dissolved, not enough members!");
                plugin.getPartyManager().removeParty(party);
            }
            leavingPlayer.setOnline(false);
            leavingPlayer.setPlayer(null);
            leavingPlayer.save();
        }
        if (leavingPlayer.isTeleporting()) {
            leavingPlayer.stopTeleporting();
        }

    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        LegendaryPlayer leavingPlayer = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
        if (leavingPlayer != null) {
            Party party = plugin.getPartyManager().getPartyByPlayer(leavingPlayer);
            if (party != null && party.getMemberCount() > 2) {
                if (party.getLeader().equals(leavingPlayer)) {
                    party.deleteMember(leavingPlayer);
                    party.transferLead(party.getMembers().get(0));
                    party.broadcastMessage("&8Leader " + leavingPlayer.getName() + " left the party.");
                    party.broadcastMessage("&3" + party.getMembers().get(0).getName() + " is new leader.");
                } else {
                    party.deleteMember(leavingPlayer);
                    party.broadcastMessage("&8" + leavingPlayer.getName() + " left the party.");
                }

            } else if (party != null && party.getMemberCount() <= 2) {
                party.broadcastMessage("&8" + leavingPlayer.getName() + " left the party.");
                party.broadcastMessage("&8Party got dissolved, not enough members!");
                plugin.getPartyManager().removeParty(party);
            }
            leavingPlayer.setOnline(false);
            leavingPlayer.setPlayer(null);
            leavingPlayer.save();
        }
        if (leavingPlayer.isTeleporting()) {
            leavingPlayer.stopTeleporting();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        LegendaryPlayer playerLeg = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
        if (playerLeg.isTeleporting()) {
            playerLeg.stopTeleporting();
            LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4Stoped teleportation. DonÂ´t move during teleportation!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        regulateWatchPort(event);

    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        regulateChatSystem(event);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        //Turned off soul pickup temporarily - dionnsai
        //Soul drop creation also disabled in Listener_Entity
        //regulateSoulPickUp(event);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    }

    public void playLogonSound() {
        String url = plugin.getCfg().getLogonSoundURL();
        if (LegendaryClans.URLexists(url)) {
            SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false);
        } else {
            plugin.log("Login sound: " + url + " not found! Change the link in the config!");
        }
    }

    public void playSoulSound(SpoutPlayer player) {
        String url = plugin.getCfg().getSoulAbsorbSoundURL();
        if (LegendaryClans.URLexists(url)) {
            SpoutManager.getSoundManager().playCustomSoundEffect(plugin, player, url, false);
        } else {
            plugin.log("Soul sound: " + url + " not found! Change the link in the config!");
        }
    }

    //  public void regulateSoulPickUp(PlayerPickupItemEvent event){
    //      Item item = event.getItem();
    //      Player player = event.getPlayer();
    //      CustomItem soulItem = plugin.getSpoutMaterials().itemManager.getItem("Soul").getCustomItem();
    //      SMCustomItem soulItemName = plugin.getSpoutMaterials().itemManager.getItem("Soul");
    //              if(soulItem != null){
    //                  SpoutItemStack stack = new SpoutItemStack(soulItem);
    //                  if(item.getItemStack().getTypeId() == stack.getTypeId()){
    //                      if (item.getItemStack().getData().getData() == stack.getData().getData()) {
    //                          //                    SpoutManager.getMaterialManager().
    //                    System.out.println("penis");
    //                    SpoutManager.getMaterialManager().
    //                    if (((CustomItem)item).getName().equalsIgnoreCase(soulItemName.getName())) {   // item.getItemStack().getType() == stack.getType()
    //                          int amount = 0;
    //                         for (int i = 0; i < item.getItemStack().getAmount(); i++) {
    //                              int price = LegendaryClans.getSoulPrice();
    //                              plugin.getiConomy().depositPlayer(player.getName(), price);
    //                              amount++;
    //                          }
    //                          ItemStack newStack = new ItemStack(3, 1);
    //                          //                    item.getItemStack().setTypeId(0);
    //                          item.setItemStack(newStack);
    //                          LegendaryClans.coloredOutput((CommandSender) player, "&b" + amount + " soul(s) have been absorbed to the total souls of: " + plugin.getiConomy().getBalance(player.getName()));
    //                          playSoulSound(SpoutManager.getPlayer(player));
    //                      }
    //                  }
    //                }
    //              } else{
    //                 plugin.log("No custom block named Soul"); 
    //              }           
    //  }   
    public void regulateChatSystem(PlayerChatEvent event) {
        LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
        Clan clan = plugin.getClanManager().getClanByPlayer(player);
        if (clan != null) {
            if (clan.isLeader(player)) {
                event.setFormat("[" + clan.getName() + "-L] " + event.getPlayer().getName() + ": ");
            } else {
                event.setFormat("[" + clan.getName() + "-M] " + event.getPlayer().getName() + ": ");
            }
        } else {
            event.setFormat("[None] " + event.getPlayer().getName() + ": ");
        }
        if (LegendaryClans.getPermission().has(event.getPlayer(), "dt.colorchat")) {
            event.setMessage(LegendaryClans.parseColor(event.getMessage()));
        }
        // if chat not global
        if (player.getChat() != 0) {
            if (player.getChat() == 1) {
                if (event.getMessage().contains("&") && !LegendaryClans.getPermission().has(event.getPlayer(), "dt.colorchat")) {
                    event.getPlayer().sendMessage("&4You can´t write in colors. Donate to get this feature!");
                } else {
                    broadcastToPplInRange(100, event.getPlayer(), event.getFormat() + event.getMessage());
                }
                event.setCancelled(true);
            } else if (player.getChat() == 2) {
                Party party = plugin.getPartyManager().getPartyByPlayer(player);
                if (party != null) {
                    if (event.getMessage().contains("&") && !LegendaryClans.getPermission().has(event.getPlayer(), "dt.colorchat")) {
                        event.getPlayer().sendMessage("&4You can´t write in colors. Donate to get this feature!");
                    } else {
                        String color = LegendaryClans.parseColor("&9");
                        party.broadcastRawMessage(color + event.getFormat() + LegendaryClans.parseColor("&f") + event.getMessage());
                    }
                    event.setCancelled(true);
                }
            } else if (player.getChat() == 3) {
                Clan clan2 = plugin.getClanManager().getClanByPlayer(player);
                if (clan != null) {
                    if (event.getMessage().contains("&") && !LegendaryClans.getPermission().has(event.getPlayer(), "dt.colorchat")) {
                        event.getPlayer().sendMessage("&4You can´t write in colors. Donate to get this feature!");
                    } else {
                        String color = LegendaryClans.parseColor("&6");
                        clan2.broadcastRawMessage(color + event.getFormat() + LegendaryClans.parseColor("&f") + event.getMessage());
                    }
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getMessage().contains("&") && !LegendaryClans.getPermission().has(event.getPlayer(), "dt.colorchat")) {
                event.getPlayer().sendMessage("&4You can´t write in colors. Donate to get this feature!");
                event.setCancelled(true);
            } else {
                String color = LegendaryClans.parseColor("&e");
                String finalMsg = color + event.getFormat() + LegendaryClans.parseColor("&f") + event.getMessage();
                plugin.getPlayerManager().broadcastMessageToNonIgnoring(finalMsg);
                event.setCancelled(true);
            }
        }
    }

    public void broadcastToPplInRange(int range, Player player, String message) {
        LegendaryPlayer legPlayer = plugin.getPlayerManager().searchPlayerByName(player.getName());
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        for (Entity ent : entities) {
            if (ent instanceof Player) {
                LegendaryPlayer entLeg = plugin.getPlayerManager().searchPlayerByName(((Player) ent).getName());
                if (entLeg != null) {
                    if (entLeg.isOnline()) {
                        entLeg.getPlayer().sendRawMessage(message);
                    }
                }
            }
        }
        legPlayer.getPlayer().sendMessage(message);
    }

    public void regulateWatchPort(PlayerInteractEvent event) {
        //if he clicks the air 
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            //if he has got the watch in his hand.
            if (event.getPlayer().getItemInHand().getTypeId() == 347) {
                LegendaryPlayer playerLeg = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
                if (playerLeg != null) {
                    if (!playerLeg.isTeleporting()) {
                        Clan clan = plugin.getClanManager().getClanByPlayer(playerLeg);
                        if (playerLeg.getDestination() == 0) {
                            if (!LegendaryClans.getPermission().has(event.getPlayer(), WATCHSTONE_PERM)) {
                                LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4Watch stone not available. Donate to get this feature.");
                            } else if (LegendaryClans.getPermission().has(event.getPlayer(), WATCHSTONE_PERM)) {
                                playerLeg.setDestination(1);
                                LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&2Switched destination to watch stone.");
                            }
                            if (clan != null && !LegendaryClans.getPermission().has(event.getPlayer(), WATCHSTONE_PERM)) {
                                if (clan.getLvl() > 2 && clan.getTerritory().isSetup()) {
                                    playerLeg.setDestination(2);
                                    LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&2Switched destination to clan base.");
                                } else if (clan.getLvl() < 2) {
                                    LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4Your clan is not high level enough to use this feature.");
                                }
                            } else if (clan == null && !LegendaryClans.getPermission().has(event.getPlayer(), WATCHSTONE_PERM)) {
                                LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4You donÂ´t have a clan.");
                            }
                        } else if (playerLeg.getDestination() == 1) {
                            if (clan != null) {
                                if (clan.getLvl() > 2 && clan.getTerritory().isSetup()) {
                                    playerLeg.setDestination(2);
                                    LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&2Switched destination to clan base.");
                                } else {
                                    LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4Your clan is not high level enough or has no territory.");
                                }
                            } else {
                                LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4You have no clan.");
                            }
                        } else if (playerLeg.getDestination() == 2) {
                            if (LegendaryClans.getPermission().has(event.getPlayer(), WATCHSTONE_PERM)) {
                                playerLeg.setDestination(1);
                                LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&2Switched destination to watch stone.");
                            } else {
                                LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4Watch stone not available. Donate to get this feature.");
                            }
                        }
                    } else {
                        LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4You are already porting.");
                    }
                }
            }
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            if (event.getPlayer().getItemInHand().getTypeId() == 347) {
                LegendaryPlayer playerLeg = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
                if (!playerLeg.isTeleporting()) {
                    if (playerLeg.getDestination() != 0) {
                        TeleportationBar bar = new TeleportationBar(plugin, SpoutManager.getPlayer(event.getPlayer()), playerLeg);
                        playerLeg.setTeleporting(true);
                        playerLeg.setTeleportingBar(bar);
                    } else if (playerLeg.getDestination() == 0) {
                        LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4Choose a destination first.");
                    }
                } else {
                    LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4You are already porting.");
                }
            }
        }
    }

}
