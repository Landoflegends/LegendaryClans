/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.legendaryclans.commands;


import net.landoflegend.gui.ClanInvitationRequest;
import net.landoflegend.gui.ExtraClanScreen;
import net.landoflegend.gui.NoClanMenue;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.landoflegend.legendaryclans.LegendaryClans;
import net.landoflegend.legendaryclans.LegendaryPlayer;
import net.landoflegend.legendaryclans.PlayerManager;
import net.landoflegend.legendaryclans.clans.Clan;
import net.landoflegend.legendaryclans.clans.ClanManager;
import net.landoflegend.legendaryclans.party.PartyManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;


public class CommandExecutor_Clan implements CommandExecutor {
	private LegendaryClans plugin;

	public CommandExecutor_Clan(LegendaryClans plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (command.getName().equalsIgnoreCase("clan")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Y U NO PLAYER??!111");
				return true;
			}
                        if(args.length == 0){
                            showClanMenue(sender);
                        }
                        if(args.length == 1 && args[0].equalsIgnoreCase("establish")){
                            establishClan(sender);
                        }
                        if(args.length == 2 && args[0].equalsIgnoreCase("invite")){
                            manuallyInvite(sender, args);
                        }

			return true;
		}
		return false;
	}
        
        public void showClanMenue(CommandSender sender){
            ClanManager clanManager = plugin.getClanManager();
            PlayerManager playerManager = plugin.getPlayerManager();
            
            Player senderAsPlayer = (Player) sender;
            SpoutPlayer spoutPlayer = SpoutManager.getPlayer(senderAsPlayer);
            
            LegendaryPlayer legPlayer = playerManager.searchPlayerByName(spoutPlayer.getName());
            Clan clan = clanManager.getClanByPlayer(legPlayer);
            if(clan ==null){
                //TODO: show list of all clans
                NoClanMenue menue = new NoClanMenue(plugin, spoutPlayer);
            }
            else{
                //TODO: show own clan menue including all data: 
                ExtraClanScreen screen = new ExtraClanScreen(plugin, spoutPlayer, null, clan);
            }
            
            
        }
        
        public void establishClan(CommandSender sender){
            LegendaryPlayer setter = plugin.getPlayerManager().searchPlayerByName(sender.getName());
            Clan clan = plugin.getClanManager().getClanByPlayer(setter);
            if(clan != null){
                if(clan.isLeader(setter)){
                    if(setter.getPlayer().getWorld().getName().equalsIgnoreCase("wild")){
                        if(!clan.getTerritory().isSetup()){
                            ClanManager manager = plugin.getClanManager();
                            if(!manager.isConflictingOtherArea(setter)){
                                clan.establishClanTerr(setter);
                            }
                            else{
                               LegendaryClans.coloredOutput(sender, "You r too close to another clan base! Try somewhere else!");  
                            }
                        }
                        else{
                            LegendaryClans.coloredOutput(sender, "You already got a base!");                              
                        }
                    }
                    else{
                        LegendaryClans.coloredOutput(sender, "You have to be in the wild world"); 
                    }
                }
                else{
                   LegendaryClans.coloredOutput(sender, "You r not the leader"); 
                }
            }
            else{
                LegendaryClans.coloredOutput(sender, "You r in no clan!");
            }
            
        }

    //clan invite player
    public void manuallyInvite(CommandSender sender, String[] args) {
        LegendaryClans legPlugin = plugin;
        String playerName = args[1];
        LegendaryPlayer playerToBeInvited = legPlugin.getPlayerManager().searchPlayerByName(playerName);
        LegendaryPlayer senderP = legPlugin.getPlayerManager().searchPlayerByName(sender.getName());
        Clan clan = plugin.getClanManager().getClanByPlayer(senderP);
        if (clan != null) {
            if (clan.isLeader(senderP)) {
                Clan clanOfPlayerInvite = legPlugin.getClanManager().getClanByPlayer(legPlugin.getPlayerManager().searchPlayerByName(sender.getName()));
                Clan clanOfToBeInvited =  legPlugin.getClanManager().getClanByPlayer(playerToBeInvited);
                if (clanOfPlayerInvite.getMemberCount() < ClanManager.getMaxMembers()[clanOfPlayerInvite.getLvl() - 1]) {
                    if (playerToBeInvited != null && !playerToBeInvited.getName().equalsIgnoreCase(sender.getName()) && clanOfToBeInvited == null && playerToBeInvited.isOnline()) {

                        SpoutPlayer player = SpoutManager.getPlayer(playerToBeInvited.getPlayer());
                        if (player != null) {
                            ClanInvitationRequest request = new ClanInvitationRequest(legPlugin, player, clanOfPlayerInvite);
                            LegendaryClans.coloredOutput(sender, "&2Invitation sent to " + playerToBeInvited.getName());
                        }
                    } else {
                        LegendaryClans.coloredOutput(sender, "&4No invitation possible.");
                    }
                } else {
                    LegendaryClans.coloredOutput(sender, "&4Your clan has reached the max amount of members for its level. Level up to increase memberrestrictions.");
                }
            } else {
                LegendaryClans.coloredOutput(sender, "&4You are not a clan leader.");
            }
        } else {
            LegendaryClans.coloredOutput(sender, "&4You do not have a clan.");
        }
    }
}
