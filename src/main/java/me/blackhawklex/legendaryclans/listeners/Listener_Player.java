/*
 * LegendaryClans - by BlackHawkLex
 * http://
 *
 * powered by Kickstarter
 */
package me.blackhawklex.legendaryclans.listeners;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import me.blackhawklex.gui.TeleportationBar;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import me.blackhawklex.legendaryclans.party.Party;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Listener_Player extends PlayerListener {
    private LegendaryClans plugin;

    private static final String WATCHSTONE_PERM = "LegendaryClans.donator.watchstone";

    public Listener_Player(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    @Override
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

    @Override
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

    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        super.onPlayerKick(event);
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

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        super.onPlayerMove(event);
        LegendaryPlayer playerLeg = plugin.getPlayerManager().searchPlayerByName(event.getPlayer().getName());
        if (playerLeg.isTeleporting()) {
            playerLeg.stopTeleporting();
            LegendaryClans.coloredOutput((CommandSender) event.getPlayer(), "&4Stoped teleportation. DonÂ´t move during teleportation!");
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        super.onPlayerInteract(event);
        regulateWatchPort(event);

    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        super.onPlayerChat(event);
        regulateChatSystem(event);
    }

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        super.onPlayerPickupItem(event);
        //Turned off soul pickup temporarily - dionnsai
        //Soul drop creation also disabled in Listener_Entity
        //regulateSoulPickUp(event);
    }

    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        super.onPlayerInteractEntity(event);
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
